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

import java.util.HashMap;
import java.util.Set;

import net.sourceforge.fullsync.Action;
import net.sourceforge.fullsync.ActionType;
import net.sourceforge.fullsync.BufferUpdate;
import net.sourceforge.fullsync.Location;
import net.sourceforge.fullsync.State;
import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.util.SmartQueue;

public class SyncActionGenerator {
	private final SmartQueue<Task> outputQueue;
	private final SyncActionGeneratorTask sourceTask;
	private final SyncActionGeneratorTask destinationTask;
	SyncActionGenerator(TaskletWorkNotificationTarget _workNotificationTarget, SmartQueue<File> _sourceQueue, SmartQueue<File> _destinationQueue) {
		outputQueue = new SmartQueue<Task>();
		sourceTask = new SyncActionGeneratorTask(_workNotificationTarget, this, Location.Source, _sourceQueue);
		destinationTask = new SyncActionGeneratorTask(_workNotificationTarget, this, Location.Destination, _destinationQueue);
		sourceTask.setOther(destinationTask);
		destinationTask.setOther(sourceTask);
	}

	public void doDecideOne(File srcParent, File src, File dstParent, File dst) {
		//FIXME: make an actual decision (state decider, check source / destination, ...)
		Task t = new Task(src, dst, State.InSync, new Action[] { new Action(ActionType.Nothing, Location.Destination, BufferUpdate.None, "") });
		outputQueue.offer(t);
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

class SyncActionGeneratorTask extends SyncTasklet<File, Task> {
	private class WorkingSet {
		private final File parent;
		private final String path;
		private HashMap<String, File> map = new HashMap<String, File>();
		private boolean isActive = true;

		public WorkingSet(File _parent) {
			parent = _parent;
			path = parent.getPath();
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
			final File currentParent = item.getParent();
			if ((null != currentWorkingSet) && (currentParent != currentWorkingSet.getParent())) {
				currentWorkingSet.setActive(false);
				if (other.isWorkingsetInactive(currentWorkingSet.getPath())) {
					flushWorkingset(currentWorkingSet);
					other.flushWorkingset(currentWorkingSet.getPath());
				}
			}
			if ((null == currentWorkingSet) || (currentParent != currentWorkingSet.getParent())) {
				currentWorkingSet = new WorkingSet(currentParent);
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
			if (workingSet.isEmpty()) {
				workingSetMap.remove(path);
			}
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
			doDecideOne(workingSet.remove(name), other.poll(path, name));
		}
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
