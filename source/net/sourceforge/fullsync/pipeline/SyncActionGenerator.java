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
package net.sourceforge.fullsync.pipeline;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import net.sourceforge.fullsync.ActionDecider;
import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.Location;
import net.sourceforge.fullsync.StateDecider;
import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.util.SmartQueue;

public class SyncActionGenerator {
	private final SmartQueue<Task> outputQueue;
	private final SyncActionGeneratorTask sourceTask;
	private final SyncActionGeneratorTask destinationTask;
	private final StateDecider stateDecider;
	private final ActionDecider actionDecider;
	SyncActionGenerator(TaskletWorkNotificationTarget _workNotificationTarget, SmartQueue<File> _sourceQueue, SmartQueue<File> _destinationQueue, StateDecider _stateDecider, ActionDecider _actionDecider) {
		outputQueue = new SmartQueue<Task>();
		sourceTask = new SyncActionGeneratorTask(_workNotificationTarget, this, Location.Source, _sourceQueue);
		destinationTask = new SyncActionGeneratorTask(_workNotificationTarget, this, Location.Destination, _destinationQueue);
		sourceTask.setOther(destinationTask);
		destinationTask.setOther(sourceTask);
		stateDecider = _stateDecider;
		actionDecider = _actionDecider;
	}

	public void doDecideOne(File srcParent, File src, File dstParent, File dst) {
		try {
			Task t = actionDecider.getTask(src, dst, stateDecider, null);
			outputQueue.offer(t);
		}
		catch (DataParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public SmartQueue<Task> getOutput() {
		return outputQueue;
	}

	public SyncTasklet<File, Task> getSourceTask() {
		return sourceTask;
	}

	public SyncTasklet<File, Task> getDestinationTask() {
		return destinationTask;
	}

	public synchronized void pause() {
		getOutput().pause();
	}

	public synchronized void resume() {
		getOutput().resume();
	}

	public synchronized void cancel() {
		getOutput().cancel();
	}

	public synchronized void notifyEnd() {
		if (sourceTask.hasEnded() && destinationTask.hasEnded()) {
			try {
				getOutput().shutdown();
			}
			catch (IllegalStateException iex) {
				// ignore, Queue might have been cancelled already
			}
		}
	}
}

class WorkingSet {
	private final File parent;
	private final String path;
	private HashMap<String, File> map = new HashMap<String, File>();
	private boolean isActive = true;

	public WorkingSet(File _parent) {
		parent = _parent;
		if (null != parent) {
			path = parent.getPath();
		}
		else {
			path = "";
		}
	}

	public void put(String name, File file) {
		map.put(name, file);
	}

	public File remove(String name) {
		return map.remove(name);
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean active) {
		isActive = active;
	}

	public Set<String> keySet() {
		return map.keySet();
	}

	public String getPath() {
		return path;
	}

	public File getParent() {
		return parent;
	}
}

class SyncActionGeneratorTask extends SyncTasklet<File, Task> {
	private final SyncActionGenerator generator;
	private final Location location;
	private WorkingSet currentWorkingSet;
	private final HashMap<String, WorkingSet> workingSetMap;
	private SyncActionGeneratorTask other;
	private volatile boolean running;
	public SyncActionGeneratorTask(TaskletWorkNotificationTarget _workNotificationTarget, SyncActionGenerator _generator, Location _location, SmartQueue<File> _inputQueue) {
		super(_workNotificationTarget, _inputQueue);
		generator = _generator;
		location = _location;
		workingSetMap = new HashMap<String, WorkingSet>();
		running = true;
	}

	public void setOther(SyncActionGeneratorTask _other) {
		other = _other;
	}

	@Override
	protected void processItem(File item) throws Exception {
		synchronized (generator) {
			// first node to be processed, store it's parent as the root
			final File currentParent = item.getParent();
			if ((null != currentWorkingSet) && (currentParent != currentWorkingSet.getParent())) {
				currentWorkingSet.setActive(false);
				if (other.isWorkingsetInactive(currentWorkingSet.getPath())) {
					flushWorkingset(currentWorkingSet);
					other.flushWorkingset(currentWorkingSet.getPath());
				}
				currentWorkingSet = null;
			}
			if (null == currentWorkingSet) {
				currentWorkingSet = new WorkingSet((null == currentParent) ? item : currentParent);
				workingSetMap.put(currentWorkingSet.getPath(), currentWorkingSet);
			}
			File twin = other.poll(currentWorkingSet.getPath(), item.getName());
			if (null != twin) {
				doDecideOne(item, twin);
				currentWorkingSet.remove(item.getName());
			}
			else {
				currentWorkingSet.put(item.getName(), item);
			}
		}
	}

	private void doDecideOne(File item, File twin) {
		final File twinParent = (null != twin) ? twin.getParent() : null;
		if (Location.Source == location) {
			generator.doDecideOne(item.getParent(), item, twinParent, twin);
		}
		else {
			generator.doDecideOne(twinParent, twin, item.getParent(), item);
		}
	}

	private File poll(String path, String name) {
		File f = null;
		WorkingSet workingSet = workingSetMap.get(path);
		if (null != workingSet) {
			f = workingSet.remove(name);
		}
		return f;
	}

	private boolean isWorkingsetInactive(String path) {
		boolean active = false;
		WorkingSet workingSet = workingSetMap.get(path);
		if (null != workingSet) {
			active = !workingSet.isActive();
		}
		return active;
	}

	@Override
	public void pause() {
		generator.pause();
	}

	@Override
	public void resume() {
		generator.resume();
	}

	@Override
	public void cancel() {
		synchronized (generator) {
			generator.cancel();
			running = false;
		}
	}

	private void flushWorkingset(WorkingSet workingSet) {
		final String path = workingSet.getPath();
		for (String name : workingSet.keySet()) {
			File srcFile = workingSet.remove(name);
			File dstFile = other.poll(path, name);
			if (null == dstFile) {
				dstFile = other.createFile(path, name, srcFile.isDirectory());
			}
			doDecideOne(srcFile, dstFile);
		}
	}

	// path must be normalized and start with a /
	public File createFile(String path, String name, boolean directory) {
		WorkingSet workingSet = workingSetMap.get(path);
		if (null == workingSet) {
			String[] dirs = path.split("/");
			String parentDir = "";
			WorkingSet grandparentWorkingSet = workingSetMap.get("");
			if (null == grandparentWorkingSet) {

			}
			for (String dir : dirs) {
				parentDir += (parentDir.isEmpty() ? "" : "/") + dir;
				workingSet = workingSetMap.get(parentDir);
				if (null == workingSet) {
					File parent = grandparentWorkingSet.getParent().buildChildNode(dir, true, false);
					workingSet = new WorkingSet(parent);
					workingSetMap.put(parentDir, workingSet);
				}
				grandparentWorkingSet = workingSet;
			}
		}
		if (null != workingSet) {
			return workingSet.getParent().buildChildNode(name, directory, false);
		}
		return null;
	}

	private void flushWorkingset(String path) {
		flushWorkingset(workingSetMap.get(path));
	}

	@Override
	protected void cleanup() {
		synchronized (generator) {
			cleanup(true);
			generator.notifyEnd();
		}
	}

	private void cleanup(boolean callOther) {
		boolean otherSideEnded = other.hasEnded();
		for (WorkingSet workingSet : workingSetMap.values()) {
			workingSet.setActive(false);
			if (otherSideEnded) {
				flushWorkingset(workingSet);
			}
		}
		running = false;
		if (otherSideEnded && callOther) {
			other.cleanup(false);
		}
	}

	public boolean hasEnded() {
		return !running;
	}
}
