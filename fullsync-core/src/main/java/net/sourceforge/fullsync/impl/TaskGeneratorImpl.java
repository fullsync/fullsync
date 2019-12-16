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
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;

import net.sourceforge.fullsync.Action;
import net.sourceforge.fullsync.ActionDecider;
import net.sourceforge.fullsync.ActionType;
import net.sourceforge.fullsync.BufferUpdate;
import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.FileSystemException;
import net.sourceforge.fullsync.FileSystemManager;
import net.sourceforge.fullsync.IgnoreDecider;
import net.sourceforge.fullsync.Location;
import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.RuleSet;
import net.sourceforge.fullsync.State;
import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.TaskGenerator;
import net.sourceforge.fullsync.TaskTree;
import net.sourceforge.fullsync.event.TaskGenerationFinished;
import net.sourceforge.fullsync.event.TaskTreeFinished;
import net.sourceforge.fullsync.event.TaskTreeStarted;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.Site;

@Singleton // effectively a singleton because the Synchronizer is a singleton
public class TaskGeneratorImpl implements TaskGenerator {
	private static final Logger logger = LoggerFactory.getLogger(TaskGeneratorImpl.class.getSimpleName());
	private final FileSystemManager fileSystemManager;
	private final EventBus eventBus;
	// TODO this should be execution local so the class
	// itself is multithreadable
	// so maybe just put them all into a inmutable
	// state container
	private IgnoreDecider takeIgnoreDecider;
	private StateDeciderImpl stateDecider;
	private BufferStateDeciderImpl bufferStateDecider;

	@Inject
	public TaskGeneratorImpl(FileSystemManager fileSystemManager, EventBus eventBus) {
		this.fileSystemManager = fileSystemManager;
		this.eventBus = eventBus;
	}

	protected RuleSet updateRules(File src, File dst, RuleSet rules) throws DataParseException, IOException {
		rules = rules.createChild(src, dst);

		/* HACK OMG, that is utterly wrong !! */
		this.takeIgnoreDecider = rules;
		this.stateDecider = new StateDeciderImpl(rules);
		this.bufferStateDecider = new BufferStateDeciderImpl(rules);

		return rules;
	}

	protected void recurse(TaskTree taskTree, File src, File dst, RuleSet rules, Task parent, ActionDecider actionDecider)
		throws DataParseException, IOException {
		if (src.isDirectory() && dst.isDirectory()) {
			synchronizeDirectories(taskTree, src, dst, rules, parent, actionDecider);
		}
		// TODO [DirHereFileThere, ?]
		// handle case where src is dir but dst is file
	}

	private void synchronizeNodes(TaskTree taskTree, File src, File dst, RuleSet rules, Task parent, ActionDecider actionDecider)
		throws DataParseException, IOException {
		if (!takeIgnoreDecider.isNodeIgnored(src)) {
			Task task = actionDecider.getTask(src, dst, stateDecider, bufferStateDecider);
			logger.debug("{}: {}", src.getName(), task); //$NON-NLS-1$

			eventBus.post(new TaskGenerationFinished(taskTree, task));

			if (rules.isUsingRecursion()) {
				recurse(taskTree, src, dst, rules, task, actionDecider);
			}
			parent.addChild(task);
		}
	}

	/*
	 * we could updateRules in synchronizeNodes and apply synchronizeDirectories
	 * to the given src and dst if they are directories
	 */
	public void synchronizeDirectories(TaskTree taskTree, File src, File dst, RuleSet oldrules, Task parent, ActionDecider actionDecider)
		throws DataParseException, IOException {
		// update rules to current directory
		RuleSet rules = updateRules(src, dst, oldrules);

		Collection<File> srcFiles = src.getChildren();
		Collection<File> dstFiles = new ArrayList<>(dst.getChildren());

		for (File sfile : srcFiles) {
			File dfile = dst.getChild(sfile.getName());
			if (null == dfile) {
				dfile = dst.createChild(sfile.getName(), sfile.isDirectory());
			}
			else {
				dstFiles.remove(dfile);
			}

			synchronizeNodes(taskTree, sfile, dfile, rules, parent, actionDecider);
		}

		for (File dfile : dstFiles) {
			File sfile = src.getChild(dfile.getName());
			if (null == sfile) {
				sfile = src.createChild(dfile.getName(), dfile.isDirectory());
			}

			synchronizeNodes(taskTree, sfile, dfile, rules, parent, actionDecider);
		}

		/* HACK OMG, that is utterly wrong !! */
		this.takeIgnoreDecider = oldrules;
		this.stateDecider = new StateDeciderImpl(oldrules);
		this.bufferStateDecider = new BufferStateDeciderImpl(oldrules);
	}

	@Override
	public TaskTree execute(Profile profile, boolean interactive)
		throws FileSystemException, URISyntaxException, DataParseException, IOException {

		RuleSet rules = profile.getRuleSet().createRuleSet();

		ActionDecider actionDecider;
		if ("Publish/Update".equals(profile.getSynchronizationType())) {
			actionDecider = new PublishActionDecider();
		}
		else if ("Publish/Update Overwrite".equals(profile.getSynchronizationType())) {
			actionDecider = new PublishOverwriteActionDecider();
		}
		else if ("Backup Copy".equals(profile.getSynchronizationType())) {
			actionDecider = new BackupActionDecider();
		}
		else if ("Exact Copy".equals(profile.getSynchronizationType())) {
			actionDecider = new ExactCopyActionDecider();
		}
		else if ("Two Way Sync".equals(profile.getSynchronizationType())) {
			actionDecider = new TwoWaySyncActionDecider();
		}
		else {
			throw new IllegalArgumentException("Profile has unknown synchronization type."); //$NON-NLS-1$
		}

		ConnectionDescription srcDesc = profile.getSource();
		ConnectionDescription dstDesc = profile.getDestination();
		try (Site d1 = fileSystemManager.createConnection(srcDesc, interactive)) {
			try (Site d2 = fileSystemManager.createConnection(dstDesc, interactive)) {
				return execute(d1, d2, actionDecider, rules);
			}
		}
		catch (Exception ex) {
			throw new FileSystemException(ex);
		}
	}

	public TaskTree execute(Site source, Site destination, ActionDecider actionDecider, RuleSet rules)
		throws DataParseException, FileSystemException, IOException {
		if (!source.isAvailable()) {
			throw new FileSystemException("source is unavailable");
		}
		if (!destination.isAvailable()) {
			throw new FileSystemException("destination is unavailable");
		}

		TaskTree tree = new TaskTree(source, destination);
		Action rootAction = new Action(ActionType.NOTHING, Location.NONE, BufferUpdate.NONE, "Root"); //$NON-NLS-1$
		Task root = new Task(null, null, State.IN_SYNC, new Action[] {
			rootAction
		});
		tree.setRoot(root);

		try {
			eventBus.post(new TaskTreeStarted(tree));

			// TODO use syncnodes here [?]
			// TODO get traversal type and start correct traversal action
			synchronizeDirectories(tree, source.getRoot(), destination.getRoot(), rules, root, actionDecider);
		}
		finally {
			eventBus.post(new TaskTreeFinished(tree));
		}

		return tree;
	}
}
