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
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import net.sourceforge.fullsync.TaskGenerationListener;
import net.sourceforge.fullsync.TaskGenerator;
import net.sourceforge.fullsync.TaskTree;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.Site;

@Singleton
public class TaskGeneratorImpl implements TaskGenerator {
	private static final Logger logger = LoggerFactory.getLogger(TaskGeneratorImpl.class.getSimpleName());
	private final List<TaskGenerationListener> taskGenerationListeners = new ArrayList<>();
	// TODO this should be execution local so the class
	// itself is multithreadable
	// so maybe just put them all into a inmutable
	// state container
	private IgnoreDecider takeIgnoreDecider;
	private StateDeciderImpl stateDecider;
	private BufferStateDeciderImpl bufferStateDecider;
	private final FullSync fullsync;

	@Inject
	public TaskGeneratorImpl(FullSync fullsync) {
		this.fullsync = fullsync;
	}

	protected RuleSet updateRules(File src, File dst, RuleSet rules) throws DataParseException, IOException {
		rules = rules.createChild(src, dst);

		/* HACK OMG, that is utterly wrong !! */
		this.takeIgnoreDecider = rules;
		this.stateDecider = new StateDeciderImpl(rules);
		this.bufferStateDecider = new BufferStateDeciderImpl(rules);

		return rules;
	}

	protected void recurse(File src, File dst, RuleSet rules, Task parent, ActionDecider actionDecider)
		throws DataParseException, IOException {
		if (src.isDirectory() && dst.isDirectory()) {
			synchronizeDirectories(src, dst, rules, parent, actionDecider);
		}
		// TODO [DirHereFileThere, ?]
		// handle case where src is dir but dst is file
	}

	public void synchronizeNodes(File src, File dst, RuleSet rules, Task parent, ActionDecider actionDecider)
		throws DataParseException, IOException {
		if (!takeIgnoreDecider.isNodeIgnored(src)) {
			for (TaskGenerationListener listener : taskGenerationListeners) {
				listener.taskGenerationStarted(src, dst);
			}

			Task task = actionDecider.getTask(src, dst, stateDecider, bufferStateDecider);
			logger.debug("{}: {}", src.getName(), task);

			for (TaskGenerationListener listener : taskGenerationListeners) {
				listener.taskGenerationFinished(task);
			}

			if (rules.isUsingRecursion()) {
				recurse(src, dst, rules, task, actionDecider);
			}
			parent.addChild(task);
		}
	}

	/*
	 * we could updateRules in synchronizeNodes and apply synchronizeDirectories
	 * to the given src and dst if they are directories
	 */
	public void synchronizeDirectories(File src, File dst, RuleSet oldrules, Task parent, ActionDecider actionDecider)
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

			synchronizeNodes(sfile, dfile, rules, parent, actionDecider);
		}

		for (File dfile : dstFiles) {
			File sfile = src.getChild(dfile.getName());
			if (null == sfile) {
				sfile = src.createChild(dfile.getName(), dfile.isDirectory());
			}

			synchronizeNodes(sfile, dfile, rules, parent, actionDecider);
		}

		/* HACK OMG, that is utterly wrong !! */
		this.takeIgnoreDecider = oldrules;
		this.stateDecider = new StateDeciderImpl(oldrules);
		this.bufferStateDecider = new BufferStateDeciderImpl(oldrules);
	}

	@Override
	public void addTaskGenerationListener(TaskGenerationListener listener) {
		taskGenerationListeners.add(listener);
	}

	@Override
	public void removeTaskGenerationListener(TaskGenerationListener listener) {
		taskGenerationListeners.remove(listener);
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
			throw new IllegalArgumentException("Profile has unknown synchronization type.");
		}

		ConnectionDescription srcDesc = profile.getSource();
		ConnectionDescription dstDesc = profile.getDestination();
		final FileSystemManager fsm = new FileSystemManager();
		try (
			Site d1 = fsm.createConnection(fullsync, srcDesc, interactive);
			Site d2 = fsm.createConnection(fullsync, dstDesc, interactive)) {
			return execute(d1, d2, actionDecider, rules);
		}
		catch (Exception ex) {
			throw new FileSystemException(ex);
		}
	}

	@Override
	public TaskTree execute(Site source, Site destination, ActionDecider actionDecider, RuleSet rules)
		throws DataParseException, FileSystemException, IOException {
		if (!source.isAvailable()) {
			throw new FileSystemException("source is unavailable");
		}
		if (!destination.isAvailable()) {
			throw new FileSystemException("destination is unavailable");
		}

		TaskTree tree = new TaskTree(source, destination);
		Action rootAction = new Action(ActionType.NOTHING, Location.NONE, BufferUpdate.NONE, "Root");
		Task root = new Task(null, null, State.IN_SYNC, new Action[] { rootAction });
		tree.setRoot(root);

		for (TaskGenerationListener listener : taskGenerationListeners) {
			listener.taskTreeStarted(tree);
		}

		// TODO use syncnodes here [?]
		// TODO get traversal type and start correct traversal action
		synchronizeDirectories(source.getRoot(), destination.getRoot(), rules, root, actionDecider);

		for (TaskGenerationListener listener : taskGenerationListeners) {
			listener.taskTreeFinished(tree);
		}

		return tree;
	}
}
