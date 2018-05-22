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
package net.sourceforge.fullsync.ui;

import org.eclipse.swt.widgets.Display;

class ExecuteBackgroundJob implements Runnable {
	private final AsyncUIUpdate job;
	private final Display display;
	private boolean executed;
	private boolean succeeded;

	private ExecuteBackgroundJob(AsyncUIUpdate job, Display display) {
		this.job = job;
		this.display = display;
	}

	public static ExecuteBackgroundJob create(AsyncUIUpdate job, Display display) {
		return new ExecuteBackgroundJob(job, display);
	}

	@Override
	public void run() {
		if (!executed) {
			try {
				job.execute();
				succeeded = true;
			}
			catch (Throwable t) {
				t.printStackTrace();
			}
			executed = true;
			display.asyncExec(this);
		}
		else {
			job.updateUI(succeeded);
		}
	}
}
