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

import java.util.Date;

import net.sourceforge.fullsync.schedule.Schedule;

public interface ProfileBuilder {

	ProfileBuilder setId(String id);

	ProfileBuilder setName(String name);

	ProfileBuilder setDescription(String description);

	ProfileBuilder setSynchronizationType(String synchronizationType);

	ProfileBuilder setSource(ConnectionDescription source);

	ProfileBuilder setDestination(ConnectionDescription destination);

	ProfileBuilder setRuleSet(RuleSetDescriptor ruleSet);

	ProfileBuilder setSchedulingEnabled(boolean schedulingEnabled);

	ProfileBuilder setSchedule(Schedule schedule);

	ProfileBuilder setLastError(int i, String lastErrorString);

	ProfileBuilder setLastScheduleTime(long lastScheduleTime);

	ProfileBuilder setLastUpdate(Date lastUpdate);

	Profile build();
}
