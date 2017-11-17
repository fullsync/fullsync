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
package net.sourceforge.fullsync;

import java.util.List;

import net.sourceforge.fullsync.schedule.ScheduleTask;
import net.sourceforge.fullsync.schedule.SchedulerChangeListener;

public interface ProfileManager {

	boolean loadProfiles();

	boolean loadProfiles(String profilesFileName);

	void addProfile(Profile profile);

	void removeProfile(Profile profile);

	List<Profile> getProfiles();

	Profile getProfile(String name);

	void addProfilesChangeListener(ProfileListChangeListener listener);

	void removeProfilesChangeListener(ProfileListChangeListener listener);

	void save();

	void startScheduler();

	void stopScheduler();

	boolean isSchedulerEnabled();

	void addSchedulerListener(ProfileSchedulerListener listener);

	void removeSchedulerListener(ProfileSchedulerListener listener);

	void addSchedulerChangeListener(SchedulerChangeListener listener);

	void removeSchedulerChangeListener(SchedulerChangeListener listener);

	ScheduleTask getNextScheduleTask();
}
