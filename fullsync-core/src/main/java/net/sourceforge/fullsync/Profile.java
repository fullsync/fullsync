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

import java.util.Comparator;
import java.util.Date;

import net.sourceforge.fullsync.schedule.Schedule;

public interface Profile {
	class SortByNameAndIdComparator implements Comparator<Profile> {
		@Override
		public int compare(Profile o1, Profile o2) {
			var ret = o1.getName().compareTo(o2.getName());
			if (0 == ret) {
				ret = o1.getId().compareTo(o2.getId());
			}
			return ret;
		}
	}

	String getId();

	String getName();

	String getSynchronizationType();

	String getDescription();

	ConnectionDescription getDestination();

	ConnectionDescription getSource();

	Date getLastUpdate();

	void setLastUpdate(Date lastUpdate);

	String getLastUpdateText();

	// FIXME: this needs updates!
	String getNextUpdateText();

	RuleSetDescriptor getRuleSet();

	boolean isSchedulingEnabled();

	Schedule getSchedule();

	void setLastError(int lastErrorLevel, String lastErrorString);

	long getLastScheduleTime();

	void setLastScheduleTime(long lastScheduleTime);

	int getLastErrorLevel();

	String getLastErrorString();
}
