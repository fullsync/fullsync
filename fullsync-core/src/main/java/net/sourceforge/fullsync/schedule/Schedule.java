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
package net.sourceforge.fullsync.schedule;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sourceforge.fullsync.ExceptionHandler;

public abstract class Schedule implements Serializable {
	private static final long serialVersionUID = 2L;
	private static final String ELEMENT_NAME = "Schedule";

	private static final Map<String, Function<Element, Schedule>> scheduleRegister;

	static {
		scheduleRegister = new HashMap<>(2);
		scheduleRegister.put(IntervalSchedule.SCHEDULE_TYPE, IntervalSchedule::new);
		scheduleRegister.put(CrontabSchedule.SCHEDULE_TYPE, CrontabSchedule::new);
	}

	public static final Schedule unserialize(final Element element) {
		Schedule sched = null;
		if (null != element) {
			String scheduleType = element.getAttribute("type");
			Function<Element, Schedule> scheduleConstructor = scheduleRegister.get(scheduleType);

			if (null != scheduleConstructor) {
				try {
					sched = scheduleConstructor.apply(element);
				}
				catch (IllegalArgumentException iex) {
					ExceptionHandler.reportException(iex);
				}
			}
		}
		return sched;
	}

	public static final Element serialize(Schedule sch, Document doc) {
		Element element = doc.createElement(Schedule.ELEMENT_NAME);
		return sch.serialize(element);
	}

	public abstract Element serialize(Element element);

	public abstract long getNextOccurrence(long now);

	public abstract void setLastOccurrence(long now);
}
