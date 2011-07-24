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

import net.sourceforge.fullsync.IoStatistics;
import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.TaskExecutor;
import net.sourceforge.fullsync.TaskFinishedListener;
import net.sourceforge.fullsync.TaskTree;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class DebugTaskExecutor implements TaskExecutor {
	public DebugTaskExecutor() {
	}

	public IoStatistics createStatistics(TaskTree tree) {
		// TODO Auto-generated method stub
		return null;
	}

	public void enqueue(TaskTree tree) {

	}

	public void enqueue(Task task) {
		System.out.println(task.getSource() + ": " + task.getCurrentAction());
	}

	public boolean isActive() {
		return true;
	}

	public void resume() {
	}

	public void suspend() {
	}

	public void cancel() {
	}

	public void flush() {

	}

	public void addTaskFinishedListener(TaskFinishedListener listener) {
	}

	public void removeTaskFinishedListener(TaskFinishedListener listener) {
	}
}
