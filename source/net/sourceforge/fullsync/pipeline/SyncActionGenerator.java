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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.fullsync.Action;
import net.sourceforge.fullsync.BufferUpdate;
import net.sourceforge.fullsync.Location;
import net.sourceforge.fullsync.State;
import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.util.SmartQueue;

public class SyncActionGenerator {
	private final SmartQueue<Task> queue;
	private final SyncActionGeneratorTask sourceTask;
	private final SyncActionGeneratorTask destinationTask;
	SyncActionGenerator(TaskletWorkNotificationTarget _workNotificationTarget, SmartQueue<File> _sourceQueue, SmartQueue<File> _destinationQueue) {
		queue = new SmartQueue<Task>();
		sourceTask = new SyncActionGeneratorTask(_workNotificationTarget, this, Origin.Source, _sourceQueue);
		destinationTask = new SyncActionGeneratorTask(_workNotificationTarget, this, Origin.Destination, _destinationQueue);
		sourceTask.setOther(destinationTask);
		destinationTask.setOther(sourceTask);
	}

	public void doDecideOne(File srcParent, File src, File dstParent, File dst) {
		//FIXME: make an actual decision (state decider, check source / destination, ...)
		Task t = new Task(src, dst, State.InSync, new Action[] { new Action(Action.Nothing, Location.Destination, BufferUpdate.None, "") });
		getOutput().offer(t);
	}

	public SmartQueue<Task> getOutput() {
		return queue;
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
			getOutput().shutdown();
		}
	}
}

enum Origin {
	Source,
	Destination
}

class SyncActionGeneratorTask extends SyncTasklet<File, Task> {
	enum ParentState {
		Pending,
		ParentsMatch,
		ParentsDiffer
	}
	private class WorkingSet {
		private HashMap<String, File> map = new HashMap<String, File>();
		private boolean isActive = true;

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
	}
	private final SyncActionGenerator generator;
	private final Origin origin;
	private final List<File> backlog;
	private final List<File> possibleParents;
	private File currentParent;
	private final HashMap<String, WorkingSet> workingSetMap;
	private SyncActionGeneratorTask other;
	private ParentState parentState;
	private volatile boolean running;
	public SyncActionGeneratorTask(TaskletWorkNotificationTarget _workNotificationTarget, SyncActionGenerator _generator, Origin _origin, SmartQueue<File> _inputQueue) {
		super(_workNotificationTarget, _inputQueue);
		generator = _generator;
		origin = _origin;
		backlog = new LinkedList<File>();
		possibleParents = new LinkedList<File>();
		currentParent = null;
		workingSetMap = new HashMap<String, WorkingSet>();
		parentState = ParentState.Pending;
		running = true;
	}

	@Override
	protected void processItem(File item) throws Exception {
		synchronized (generator) {
			if (ParentState.Pending == parentState) {
				currentParent = item.getParent();
				parentState = ParentState.ParentsMatch;
			}
			if (item.getParent() != currentParent) {
				backlog.add(item);
				parentState = ParentState.ParentsDiffer;
				WorkingSet workingSet = workingSetMap.get(currentParent.getPath());
				if (null != workingSet) {
					workingSet.setActive(false);
				}
			}
			else {
				File twin = other.poll(currentParent.getPath(), item.getName());
				if (null != twin) {
					doDecideOne(item, twin);
				}
				else {
					String parentPath = item.getParent().getPath();
					WorkingSet workingSet = workingSetMap.get(parentPath);
					if (null == workingSet) {
						workingSet = new WorkingSet();
						workingSetMap.put(parentPath, workingSet);
					}
					workingSet.put(item.getName(), item);
				}
			}
			/*
			if (ParentState.ParentsDiffer == parentState && ParentState.ParentsDiffer == other.getState()) {
				nextWorkingSet(true);
			}
			*/
		}
	}
/*
	private void nextWorkingSet(boolean notifyOther) {
		for (Entry<String, File> entry : workingSet.entrySet()) {
			doDecideOne(entry.getValue(), other.poll(entry.getKey()));
		}
		workingSet.clear();
		other.nextWorkingSet(false);
		if (!backlog.isEmpty()) {
			currentParent = backlog.get(0).getParent();
			while (!backlog.isEmpty() && backlog.get(0).getParent() == currentParent) {
				File item = backlog.remove(0);
				workingSet.put(item.getName(), item);
			}
			parentState = ParentState.ParentsMatch;
		}
		else {
			currentParent = null;
			parentState = ParentState.Pending;
		}
	}
*/
	private void doDecideOne(File item, File twin) {
		if (Origin.Source == origin) {
			generator.doDecideOne(currentParent, item, other.getParent(), twin);
		}
		else {
			generator.doDecideOne(other.getParent(), twin, currentParent, item);
		}
	}

	public void setOther(SyncActionGeneratorTask _other) {
		other = _other;
	}

	private ParentState getState() {
		return parentState;
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

	private File getParent() {
		return currentParent;
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

	private void flushWorkingset(String path, WorkingSet workingSet) {
		for (String name : workingSet.keySet()) {
			doDecideOne(workingSet.remove(name), other.poll(path, name));
		}
	}

	@Override
	protected void cleanup() {
		synchronized (generator) {
			cleanup(true);
			generator.notifyEnd();
		}
	}

	void cleanup(boolean callOther) {
		boolean otherSideEnded = other.hasEnded();
		parentState = ParentState.ParentsDiffer;
		for (Map.Entry<String, WorkingSet> entry : workingSetMap.entrySet()) {
			entry.getValue().setActive(false);
			if (otherSideEnded) {
				flushWorkingset(entry.getKey(), entry.getValue());
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
