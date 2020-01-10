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

import javax.inject.Inject;
import javax.inject.Singleton;

import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.ProfileManager;
import net.sourceforge.fullsync.ProfileManagerSchedulerTaskFactory;
import net.sourceforge.fullsync.ScheduleTask;
import net.sourceforge.fullsync.ScheduleTaskSource;
import net.sourceforge.fullsync.schedule.Schedule;

@Singleton
public class ScheduleTaskSourceImpl implements ScheduleTaskSource {
	private final ProfileManagerSchedulerTaskFactory profileManagerSchedulerTaskFactory;
	private final ProfileManager profileManager;

	@Inject
	public ScheduleTaskSourceImpl(ProfileManager profileManager, ProfileManagerSchedulerTaskFactory profileManagerSchedulerTaskFactory) {
		this.profileManager = profileManager;
		this.profileManagerSchedulerTaskFactory = profileManagerSchedulerTaskFactory;
	}

	@Override
	public ScheduleTask getNextScheduleTask(long referenceTime) {
		long nextTime = Long.MAX_VALUE;
		Profile nextProfile = null;
		for (Profile profile : profileManager.getProfiles()) {
			Schedule s = profile.getSchedule();
			if (profile.isSchedulingEnabled() && (null != s)) {
				long o = s.getNextOccurrence(profile.getLastScheduleTime(), referenceTime);
				if (nextTime > o) {
					nextTime = o;
					nextProfile = profile;
				}
			}
		}

		ScheduleTask scheduleTask = null;
		if (null != nextProfile) {
			scheduleTask = profileManagerSchedulerTaskFactory.create(nextProfile, nextTime);
		}
		return scheduleTask;
	}
}
