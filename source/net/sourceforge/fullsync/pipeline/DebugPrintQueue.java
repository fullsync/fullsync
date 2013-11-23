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

public class DebugPrintQueue<QueueItem> extends SyncTasklet<QueueItem, QueueItem> {
	private final String queueName;

	public DebugPrintQueue(ProfileSyncTask _task, SmartQueue<QueueItem> _inputQueue, String _queueName) {
		super(_task, _inputQueue);
		queueName = _queueName;
	}

	@Override
	protected void processItem(QueueItem item) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append(queueName);
		sb.append(": ");
		sb.append(item);
		System.out.println(sb.toString());
		getOutput().offer(item);
	}

	@Override
	public void pause() {
		getInput().pause();
	}

	@Override
	public void resume() {
		getInput().resume();
	}

	@Override
	public void cancel() {
		getInput().cancel();
	}
}
