/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA 02110-1301, USA.
 *
 * For information about the authors of this project Have a look
 * at the AUTHORS file in the root of this project.
 */
package net.sourceforge.fullsync.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;

import net.sourceforge.fullsync.Action;
import net.sourceforge.fullsync.ActionDecider;
import net.sourceforge.fullsync.ActionType;
import net.sourceforge.fullsync.BufferStateDecider;
import net.sourceforge.fullsync.BufferUpdate;
import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.FSFile;
import net.sourceforge.fullsync.FileSystemConnection;
import net.sourceforge.fullsync.FileSystemException;
import net.sourceforge.fullsync.FileSystemManager;
import net.sourceforge.fullsync.Location;
import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.RuleSet;
import net.sourceforge.fullsync.State;
import net.sourceforge.fullsync.StateDecider;
import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.TaskGenerator;
import net.sourceforge.fullsync.TaskTree;
import net.sourceforge.fullsync.event.TaskGenerationFinished;
import net.sourceforge.fullsync.event.TaskTreeFinished;
import net.sourceforge.fullsync.event.TaskTreeStarted;

public class TaskGeneratorImpl implements TaskGenerator {
	private static final Logger logger = LoggerFactory.getLogger(TaskGeneratorImpl.class.getSimpleName());
	private final FileSystemManager fileSystemManager;
	private final EventBus eventBus;

	@Inject
	public TaskGeneratorImpl(FileSystemManager fileSystemManager, EventBus eventBus) {
		this.fileSystemManager = fileSystemManager;
		this.eventBus = eventBus;
	}

	private void recurse(TaskTree taskTree, FSFile src, FSFile dst, Task parent, Deciders deciders) throws DataParseException, IOException {
		if (src.isDirectory() && dst.isDirectory()) {
			synchronizeDirectories(taskTree, src, dst, parent, deciders);
		}
		// TODO [DirHereFileThere, ?]
		// handle case where src is dir but dst is file
	}

	private void synchronizeNodes(TaskTree taskTree, FSFile src, FSFile dst, Task parent, Deciders deciders)
		throws DataParseException, IOException {
		if (!deciders.getRules().isNodeIgnored(src)) {
			Task task = deciders.getActionDecider().getTask(src, dst, deciders.getStateDecider(), deciders.getBufferStateDecider());
			logger.debug("{}: {}", src.getName(), task); //$NON-NLS-1$

			eventBus.post(new TaskGenerationFinished(taskTree, task));

			if (deciders.getRules().isUsingRecursion()) {
				recurse(taskTree, src, dst, task, deciders);
			}
			parent.addChild(task);
		}
	}

	private static class Deciders {
		private final RuleSet rules;
		private final ActionDecider actionDecider;
		private final StateDecider stateDecider;
		private final BufferStateDecider bufferStateDecider;

		public Deciders(RuleSet rules, ActionDecider actionDecider) {
			this.rules = rules;
			this.actionDecider = actionDecider;
			this.stateDecider = new StateDeciderImpl(rules);
			this.bufferStateDecider = new BufferStateDeciderImpl(rules);
		}

		public RuleSet getRules() {
			return rules;
		}

		public ActionDecider getActionDecider() {
			return actionDecider;
		}

		public StateDecider getStateDecider() {
			return stateDecider;
		}

		public BufferStateDecider getBufferStateDecider() {
			return bufferStateDecider;
		}

		public Deciders createChild(FSFile src, FSFile dst) throws IOException, DataParseException {
			return new Deciders(rules.createChild(src, dst), actionDecider);
		}
	}

	/*
	 * we could updateRules in synchronizeNodes and apply synchronizeDirectories
	 * to the given src and dst if they are directories
	 */
	private void synchronizeDirectories(TaskTree taskTree, FSFile src, FSFile dst, Task parent, Deciders parentDeciders)
		throws DataParseException, IOException {
		Deciders deciders = parentDeciders.createChild(src, dst);
		Collection<FSFile> srcFiles = src.getChildren();
		Collection<FSFile> dstFiles = new ArrayList<>(dst.getChildren());
		for (FSFile sfile : srcFiles) {
			FSFile dfile = dst.getChild(sfile.getName());
			if (null == dfile) {
				dfile = dst.createChild(sfile.getName(), sfile.isDirectory());
			}
			else {
				dstFiles.remove(dfile);
			}
			synchronizeNodes(taskTree, sfile, dfile, parent, deciders);
		}

		for (FSFile dfile : dstFiles) {
			FSFile sfile = src.getChild(dfile.getName());
			if (null == sfile) {
				sfile = src.createChild(dfile.getName(), dfile.isDirectory());
			}
			synchronizeNodes(taskTree, sfile, dfile, parent, deciders);
		}
	}

	@Override
	public TaskTree execute(Profile profile, boolean interactive)
		throws FileSystemException, URISyntaxException, DataParseException, IOException {
		RuleSet rules = profile.getRuleSet().createRuleSet();
		ActionDecider actionDecider;
		switch (profile.getSynchronizationType()) {
			case "Publish/Update":
				actionDecider = new PublishActionDecider();
				break;
			case "Publish/Update Overwrite":
				actionDecider = new PublishOverwriteActionDecider();
				break;
			case "Backup Copy":
				actionDecider = new BackupActionDecider();
				break;
			case "Exact Copy":
				actionDecider = new ExactCopyActionDecider();
				break;
			case "Two Way Sync":
				actionDecider = new TwoWaySyncActionDecider();
				break;
			default:
				throw new IllegalArgumentException("Profile has unknown synchronization type."); //$NON-NLS-1$
		}

		ConnectionDescription srcDesc = profile.getSource();
		ConnectionDescription dstDesc = profile.getDestination();
		try (FileSystemConnection d1 = fileSystemManager.createConnection(srcDesc, interactive)) {
			try (FileSystemConnection d2 = fileSystemManager.createConnection(dstDesc, interactive)) {
				return execute(d1, d2, actionDecider, rules);
			}
		}
		catch (Exception ex) {
			throw new FileSystemException(ex);
		}
	}

	public TaskTree execute(FileSystemConnection source, FileSystemConnection destination, ActionDecider actionDecider, RuleSet rules)
		throws DataParseException, FileSystemException, IOException {
		if (!source.isAvailable()) {
			throw new FileSystemException("source is unavailable");
		}
		if (!destination.isAvailable()) {
			throw new FileSystemException("destination is unavailable");
		}

		Action rootAction = new Action(ActionType.NOTHING, Location.NONE, BufferUpdate.NONE, "Root"); //$NON-NLS-1$
		Task root = new Task(null, null, State.IN_SYNC, new Action[] {
			rootAction
		});
		TaskTree tree = new TaskTree(source, destination, root);

		try {
			eventBus.post(new TaskTreeStarted(tree));

			// TODO use syncnodes here [?]
			// TODO get traversal type and start correct traversal action
			synchronizeDirectories(tree, source.getRoot(), destination.getRoot(), root, new Deciders(rules, actionDecider));
		}
		finally {
			eventBus.post(new TaskTreeFinished(tree));
		}
		return tree;
	}
}
