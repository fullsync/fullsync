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

import net.sourceforge.fullsync.Action;
import net.sourceforge.fullsync.ActionDecider;
import net.sourceforge.fullsync.ActionType;
import net.sourceforge.fullsync.BufferUpdate;
import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.FileSystemException;
import net.sourceforge.fullsync.FileSystemManager;
import net.sourceforge.fullsync.FullSync;
import net.sourceforge.fullsync.IgnoreDecider;
import net.sourceforge.fullsync.Location;
import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.RuleSet;
import net.sourceforge.fullsync.State;
import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.TaskGenerator;
import net.sourceforge.fullsync.TaskTree;
import net.sourceforge.fullsync.events.TaskGenerationStarted;
import net.sourceforge.fullsync.events.TaskTreeStarted;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.Site;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskGeneratorImpl implements TaskGenerator {
	private static final Logger logger = LoggerFactory.getLogger(TaskGeneratorImpl.class.getSimpleName());
	private FileSystemManager fsm;

	public TaskGeneratorImpl() {
		this.fsm = new FileSystemManager();
	}

	@Override
	public TaskTree execute(Profile profile, boolean interactive) throws FileSystemException, URISyntaxException, DataParseException, IOException {
		Site src = null, dst = null;

		RuleSet rules = profile.getRuleSet().createRuleSet();

		ActionDecider actionDecider;
		if (profile.getSynchronizationType().equals("Publish/Update")) {
			actionDecider = new PublishActionDecider();
		}
		else if (profile.getSynchronizationType().equals("Publish/Update Overwrite")) {
			actionDecider = new PublishOverwriteActionDecider();
		}
		else if (profile.getSynchronizationType().equals("Backup Copy")) {
			actionDecider = new BackupActionDecider();
		}
		else if (profile.getSynchronizationType().equals("Exact Copy")) {
			actionDecider = new ExactCopyActionDecider();
		}
		else if (profile.getSynchronizationType().equals("Two Way Sync")) {
			actionDecider = new TwoWaySyncActionDecider();
		}
		else {
			throw new IllegalArgumentException("Profile has unknown synchronization type.");
		}

		try {
			ConnectionDescription srcDesc = profile.getSource();
			ConnectionDescription dstDesc = profile.getDestination();
			if (interactive) {
				srcDesc.setParameter("interactive", "true");
				dstDesc.setParameter("interactive", "true");
			}
			src = fsm.createConnection(srcDesc);
			dst = fsm.createConnection(dstDesc);
			return execute(src, dst, actionDecider, rules);
		}
		catch (FileSystemException ex) {
			if (src != null) {
				src.close();
			}
			if (dst != null) {
				dst.close();
			}
			throw ex;
		}
	}

	private TaskTree execute(Site source, Site destination, ActionDecider actionDecider, RuleSet rules) throws DataParseException, FileSystemException, IOException {
		if (!source.isAvailable()) {
			throw new FileSystemException("source is unavailable");
		}
		if (!destination.isAvailable()) {
			throw new FileSystemException("destination is unavailable");
		}

		TaskTree tree = new TaskTree(source, destination);
		Task root = new Task(null, null, State.InSync, new Action[] { new Action(ActionType.Nothing,
				Location.None, BufferUpdate.None, "Root") });
		tree.setRoot(root);

		FullSync.publish(new TaskTreeStarted(tree));

		// TODO use syncnodes here [?]
		// TODO get traversal type and start correct traversal action
		synchronizeDirectories(source.getRoot(), destination.getRoot(), rules, root, actionDecider);

		FullSync.publish(tree);
		return tree;
	}

	private void recurse(File src, File dst, RuleSet rules, Task parent, ActionDecider actionDecider) throws DataParseException, IOException {
		if (src.isDirectory() && dst.isDirectory()) {
			synchronizeDirectories(src, dst, rules, parent, actionDecider);
		}
		// TODO [DirHereFileThere, ?]
		// handle case where src is dir but dst is file
	}

	private void synchronizeNodes(File src, File dst, SyncDeciders deciders, Task parent) throws DataParseException, IOException {
		if (!deciders.takeIgnoreDecider.isNodeIgnored(src)) {
			FullSync.publish(new TaskGenerationStarted(src,  dst));

			Task task = deciders.actionDecider.getTask(src, dst, deciders.stateDecider, deciders.bufferStateDecider);
			logger.debug(src.getName() + ": " + task);

			FullSync.publish(task);

			if (deciders.rules.isUsingRecursion()) {
				recurse(src, dst, deciders.rules, task, deciders.actionDecider);
			}
			parent.addChild(task);
		}
	}

	/*
	 * we could updateRules in synchronizeNodes and apply synchronizeDirectories
	 * to the given src and dst if they are directories
	 */
	private void synchronizeDirectories(File src, File dst, RuleSet oldrules, Task parent, ActionDecider actionDecider) throws DataParseException, IOException {
		SyncDeciders deciders = new SyncDeciders(actionDecider, oldrules.createChild(src, dst));
		Collection<File> srcFiles = src.getChildren();
		Collection<File> dstFiles = new ArrayList<File>(dst.getChildren());

		for (File sfile : srcFiles) {
			File dfile = dst.getChild(sfile.getName());
			if (dfile == null) {
				dfile = dst.createChild(sfile.getName(), sfile.isDirectory());
			}
			else {
				dstFiles.remove(dfile);
			}

			synchronizeNodes(sfile, dfile, deciders, parent);
		}

		for (File dfile : dstFiles) {
			File sfile = src.getChild(dfile.getName());
			if (sfile == null) {
				sfile = src.createChild(dfile.getName(), dfile.isDirectory());
			}

			synchronizeNodes(sfile, dfile, deciders, parent);
		}
	}
}

class SyncDeciders {
	public RuleSet rules;
	public IgnoreDecider takeIgnoreDecider;
	public StateDecider stateDecider;
	public BufferStateDecider bufferStateDecider;
	public ActionDecider actionDecider;
	public SyncDeciders(ActionDecider _actionDecider, RuleSet _rules) {
		rules = _rules;
		takeIgnoreDecider = _rules;
		stateDecider = new StateDecider(_rules);
		bufferStateDecider = new BufferStateDecider(_rules);
		actionDecider = _actionDecider;
	}
}
