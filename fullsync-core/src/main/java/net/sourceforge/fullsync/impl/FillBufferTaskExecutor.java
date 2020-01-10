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
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.fullsync.Action;
import net.sourceforge.fullsync.BufferUpdate;
import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.FSFile;
import net.sourceforge.fullsync.IoStatistics;
import net.sourceforge.fullsync.Location;
import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.TaskExecutor;
import net.sourceforge.fullsync.TaskFinishedListener;
import net.sourceforge.fullsync.TaskTree;
import net.sourceforge.fullsync.buffer.ExecutionBuffer;
import net.sourceforge.fullsync.event.TaskFinishedEvent;

public class FillBufferTaskExecutor implements TaskExecutor {
	private final List<TaskFinishedListener> listeners = new ArrayList<>();
	private boolean statisticsOnly;
	private IoStatisticsImpl stats;
	private final ExecutionBuffer buffer;

	public FillBufferTaskExecutor(ExecutionBuffer buffer) {
		this.statisticsOnly = false;
		this.buffer = buffer;
		buffer.addEntryFinishedListener((entry, ioe) -> {
			Task task = entry.getTask();
			if (null != task) {
				if (null != ioe) {
					fireTaskFinished(new TaskFinishedEvent(task, ioe.getLocalizedMessage()));
				}
				else {
					fireTaskFinished(new TaskFinishedEvent(task, 0));
				}
			}
		});
	}

	@Override
	public IoStatistics createStatistics(TaskTree tree) {
		statisticsOnly = true;
		enqueue(tree);
		statisticsOnly = false;
		return stats;
	}

	@Override
	public void enqueue(TaskTree tree) {
		stats = new IoStatisticsImpl();
		enqueue(tree.getRoot());
	}

	protected void enqueueTaskChildren(Task task) {
		task.getChildren().stream().forEachOrdered(this::enqueue);
	}

	@Override
	public void enqueue(Task task) {
		if (!task.getCurrentAction().isBeforeRecursion()) {
			enqueueTaskChildren(task);
		}

		executeTask(task);

		if (task.getCurrentAction().isBeforeRecursion()) {
			enqueueTaskChildren(task);
		}
	}

	protected void storeDirCreation(Task task, FSFile subject) throws IOException {
		if (!statisticsOnly) {
			buffer.storeEntry(new DirCreationEntryDescriptor(task, subject));
		}
		stats.setDirsCreated(stats.getDirsCreated() + 1);
	}

	protected void storeFileCopy(Task task, FSFile source, FSFile destination) throws IOException {
		try {
			if (!statisticsOnly) {
				buffer.storeEntry(new FileCopyEntryDescriptor(task, source, destination));
			}
			stats.setFilesCopied(stats.getFilesCopied() + 1);
			stats.setBytesTransferred(stats.getBytesTransferred() + source.getSize());
		}
		catch (IOException ioe) {
			// FIXME that's not right, the task does not neccessarily be the one
			// that throws this exception as there are flushs involved
			fireTaskFinished(new TaskFinishedEvent(task, ioe.getMessage()));
		}
	}

	protected void storeDeleteNode(Task task, FSFile subject) throws IOException {
		if (!statisticsOnly) {
			buffer.storeEntry(new DeleteNodeEntryDescriptor(task, subject));
		}
		stats.setDeletions(stats.getDeletions() + 1);
	}

	protected void executeTask(Task task) {
		try {
			// TODO lock tasks here

			Action action = task.getCurrentAction();
			FSFile source = task.getSource();
			FSFile destination = task.getDestination();

			switch (action.getType()) {
				case ADD:
				case UPDATE:
					if (action.getLocation() == Location.DESTINATION) {
						if (source.isDirectory()) {
							storeDirCreation(task, destination);
						}
						else {
							storeFileCopy(task, source, destination);
						}
					}
					else if (action.getLocation() == Location.SOURCE) {
						if (destination.isDirectory()) {
							storeDirCreation(task, source);
						}
						else {
							storeFileCopy(task, destination, source);
						}
					}
					break;
				case DELETE:
					if (action.getLocation() == Location.DESTINATION) {
						storeDeleteNode(task, destination);
					}
					else if (action.getLocation() == Location.SOURCE) {
						storeDeleteNode(task, source);
					}

					break;
				default:
					break;
			}
			if ((action.getBufferUpdate() != BufferUpdate.NONE) && !statisticsOnly) {
				buffer.storeEntry(new BufferUpdateEntryDescriptor(source, destination, action.getBufferUpdate()));
			}
		}
		catch (IOException ioe) {
			ExceptionHandler.reportException(ioe);
		}
	}

	@Override
	public void flush() throws IOException {
		buffer.flush();
	}

	protected void fireTaskFinished(TaskFinishedEvent event) {
		for (TaskFinishedListener listener : listeners) {
			listener.taskFinished(event);
		}
	}

	@Override
	public void addTaskFinishedListener(TaskFinishedListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeTaskFinishedListener(TaskFinishedListener listener) {
		listeners.remove(listener);
	}
}
