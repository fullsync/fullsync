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

import java.io.Serializable;
import java.util.Hashtable;

import net.sourceforge.fullsync.ExceptionHandler;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public abstract class Schedule implements Serializable {
	private static final long serialVersionUID = 2L;
	public static String ELEMENT_NAME = "Schedule";

	public abstract long getNextOccurrence(long now);

	public abstract void setLastOccurrence(long now);

	public abstract Element serialize(Document doc);

	public abstract void unserializeSchedule(Element element);

	private static Hashtable<String, Class<? extends Schedule>> scheduleRegister;

	static {
		scheduleRegister = new Hashtable<String, Class<? extends Schedule>>(2);
		scheduleRegister.put(IntervalSchedule.SCHEDULE_TYPE, IntervalSchedule.class);
		scheduleRegister.put(CrontabSchedule.SCHEDULE_TYPE, CrontabSchedule.class);
	}


	public static final Schedule unserialize(Element element) {
		if (element == null) {
			return null;
		}
		String scheduleType = element.getAttribute("type");
		Class<? extends Schedule> scheduleClass = scheduleRegister.get(scheduleType);

		if (scheduleClass == null) {
			return null;
		}
		else {
			Schedule sched = null;

			try {
				sched = scheduleClass.newInstance();
			}
			catch (InstantiationException e) {
				ExceptionHandler.reportException(e);
				return null;
			}
			catch (IllegalAccessException e) {
				ExceptionHandler.reportException(e);
				return null;
			}

			sched.unserializeSchedule(element);

			return sched;
		}
	}

}
