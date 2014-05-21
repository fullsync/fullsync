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
import java.util.Vector;

import net.sourceforge.fullsync.Action;
import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.IoStatistics;
import net.sourceforge.fullsync.Location;
import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.TaskExecutor;
import net.sourceforge.fullsync.TaskFinishedEvent;
import net.sourceforge.fullsync.TaskFinishedListener;
import net.sourceforge.fullsync.TaskTree;
import net.sourceforge.fullsync.buffer.ExecutionBuffer;
import net.sourceforge.fullsync.fs.File;

public class FillBufferTaskExecutor implements TaskExecutor {
	private Vector<TaskFinishedListener> listeners;
	private boolean statisticsOnly;
	private IoStatisticsImpl stats;
	private ExecutionBuffer buffer;

	public FillBufferTaskExecutor(ExecutionBuffer buffer) {
		this.listeners = new Vector<TaskFinishedListener>();
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
		for (Task t : task.getChildren()) {
			enqueue(t);
		}
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

	protected void storeDirCreation(Task task, File subject) throws IOException {
		if (!statisticsOnly) {
			buffer.storeEntry(new DirCreationEntryDescriptor(task, subject));
		}
		stats.dirsCreated++;
	}

	protected void storeFileCopy(Task task, File source, File destination) throws IOException {
		try {
			if (!statisticsOnly) {
				buffer.storeEntry(new FileCopyEntryDescriptor(task, source, destination));
			}
			stats.filesCopied++;
			stats.bytesTransferred += source.getSize();
		}
		catch (IOException ioe) {
			// FIXME that's not right, the task does not neccessarily be the one
			// that throws this exception as there are flushs involved
			fireTaskFinished(new TaskFinishedEvent(task, ioe.getMessage()));
		}
	}

	protected void storeDeleteNode(Task task, File subject) throws IOException {
		if (!statisticsOnly) {
			buffer.storeEntry(new DeleteNodeEntryDescriptor(task, subject));
		}
		stats.deletions++;
	}

	protected void executeTask(Task task) {
		try {
			// TODO lock tasks here

			Action action = task.getCurrentAction();
			File source = task.getSource();
			File destination = task.getDestination();

			switch (action.getType()) {
				case Add:
				case Update:
					if (action.getLocation() == Location.Destination) {
						if (source.isDirectory()) {
							storeDirCreation(task, destination);
						}
						else {
							storeFileCopy(task, source, destination);
						}
					}
					else if (action.getLocation() == Location.Source) {
						if (destination.isDirectory()) {
							storeDirCreation(task, source);
						}
						else {
							storeFileCopy(task, destination, source);
						}
					}
					break;
				case Delete:
					if (action.getLocation() == Location.Destination) {
						storeDeleteNode(task, destination);
					}
					else if (action.getLocation() == Location.Source) {
						storeDeleteNode(task, source);
					}

					break;
				default:
					break;
			}
			if ((action.getBufferUpdate() > 0) && !statisticsOnly) {
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
}
