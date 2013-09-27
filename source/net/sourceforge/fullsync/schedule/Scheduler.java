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
/*
 * Created on 16.10.2004
 */
package net.sourceforge.fullsync.schedule;

public interface Scheduler {
	public void start();

	public void refresh();

	public void stop();

	public boolean isRunning();

	public boolean isEnabled();

	public void setSource(ScheduleTaskSource source);

	public ScheduleTaskSource getSource();

	public void addSchedulerChangeListener(SchedulerChangeListener listener);
}
