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
package net.sourceforge.fullsync.event;

import net.sourceforge.fullsync.Task;

public class TaskFinishedEvent {
	private final Task task;
	private final boolean successful;
	private final String errorMsg;
	private final int bytesTransferred;

	public TaskFinishedEvent(Task task, int bytesTransferred) {
		this.task = task;
		this.successful = true;
		this.errorMsg = null;
		this.bytesTransferred = bytesTransferred;
	}

	public TaskFinishedEvent(Task task, String errorMsg) {
		this.task = task;
		this.successful = false;
		this.errorMsg = errorMsg;
		this.bytesTransferred = 0;
	}

	public int getBytesTransferred() {
		return bytesTransferred;
	}

	public String getErrorMessage() {
		return errorMsg;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public Task getTask() {
		return task;
	}
}
