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

import java.util.List;

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
	private boolean sourceRunning;
	private boolean destinationRunning;
	private List<File> sourceList;
	private List<File> destinationList;
	private File currentSourceParent;
	private File currentDestinationParent;
	SyncActionGenerator(ProfileSyncTask _task, SmartQueue<File> _sourceQueue, SmartQueue<File> _destinationQueue) {
		queue = new SmartQueue<Task>();
		sourceTask = new SyncActionGeneratorTask(_task, this, Origin.Source, _sourceQueue);
		destinationTask = new SyncActionGeneratorTask(_task, this, Origin.Destination, _destinationQueue);
		sourceRunning = true;
		destinationRunning = true;
	}

	synchronized void doProcessItem(Origin origin, File item) {
		if (null == currentSourceParent && Origin.Source == origin) {
			currentSourceParent = item;
		}
		if (null == currentDestinationParent && Origin.Destination == origin) {
			currentDestinationParent = item;
		}
		switch(origin) {
			case Destination:
				break;
			case Source:
				break;
			default:
				break;
		}
		//FIXME: make an actual decision
		if (Origin.Source == origin) {
			Task t = new Task(item, null, new State(State.Orphan, Location.Source), new Action[] { new Action(Action.Add, Location.Destination, BufferUpdate.None, "") });
			queue.offer(t);
		}
	}

	synchronized void notifyEnd(Origin origin) {
		switch (origin) {
			case Destination:
				destinationRunning = false;
				break;
			case Source:
				sourceRunning = false;
				break;
		}
		if (false == sourceRunning && false == destinationRunning) {
			//TODO: flush parent lookup lists
			queue.shutdown();
		}
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
}

enum Origin {
	Source,
	Destination
}

class OriginState {
	private boolean running;
	private List<File> backlog;
	private File currentParent;

	public void processItem(File item) {
		if (null == currentParent) {
			currentParent = item;
		}
		item.getPath();
	}

}

class SyncActionGeneratorTask extends SyncTasklet<File, Task> {
	private SyncActionGenerator generator;
	private Origin origin;
	public SyncActionGeneratorTask(ProfileSyncTask _task, SyncActionGenerator _generator, Origin _origin, SmartQueue<File> _inputQueue) {
		super(_task, _inputQueue);
		origin = _origin;
	}

	@Override
	protected void processItem(File item) throws Exception {
		generator.doProcessItem(origin, item);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void cancel() {
	}

	@Override
	protected void cleanup() {
		super.cleanup();
		generator.notifyEnd(origin);
	}
}
