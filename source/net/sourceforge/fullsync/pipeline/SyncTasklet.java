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

import net.sourceforge.fullsync.util.SmartQueue;


public abstract class SyncTasklet<InputQueueItem, OutputQueueItem> implements Runnable {
	private final ProfileSyncTask task;
	private final SmartQueue<InputQueueItem> inputQueue;
	private final SmartQueue<OutputQueueItem> outputQueue;

	public SyncTasklet(ProfileSyncTask _task, SmartQueue<InputQueueItem> _inputQueue) {
		task = _task;
		inputQueue = _inputQueue;
		outputQueue = new SmartQueue<OutputQueueItem>();
	}

	public SmartQueue<InputQueueItem> getInput() {
		return inputQueue;
	}

	public SmartQueue<OutputQueueItem> getOutput() {
		return outputQueue;
	}

	@Override
	public void run() {
		try {
			task.startWork();
			try {
				prepare(inputQueue);
			}
			finally {
				task.endWork();
			}
			for (;;) {
				InputQueueItem item = getNextItem(inputQueue);
				if (null == item) {
					return;
				}
				task.startWork();
				try {
					processItem(item);
				}
				finally {
					task.endWork();
				}
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		finally {
			cleanup();
		}
	}

	protected void prepare(SmartQueue<InputQueueItem> queue) {
	}

	protected void cleanup() {
		outputQueue.shutdown();
	}

	protected InputQueueItem getNextItem(final SmartQueue<InputQueueItem> queue) {
		return queue.take();
	}

	protected abstract void processItem(InputQueueItem item);

	public abstract void pause();

	public abstract void resume();

	public abstract void cancel();
}
