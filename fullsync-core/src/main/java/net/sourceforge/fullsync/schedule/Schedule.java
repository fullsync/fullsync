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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class Schedule {
	private static final String ELEMENT_NAME = "Schedule"; //$NON-NLS-1$
	private static final String ATTRIBUTE_TYPE = "type"; //$NON-NLS-1$

	public static final Schedule unserialize(final Element element) {
		if (null != element) {
			switch (element.getAttribute(ATTRIBUTE_TYPE)) {
				case IntervalSchedule.SCHEDULE_TYPE:
					return new IntervalSchedule(element);
				case CrontabSchedule.SCHEDULE_TYPE:
					return new CrontabSchedule(element);
			}
		}
		return null;
	}

	public static final Element serialize(Schedule sch, Document doc) {
		return sch.serialize(doc.createElement(ELEMENT_NAME));
	}

	public abstract Element serialize(Element element);

	public abstract long getNextOccurrence(long now);

	public abstract void setLastOccurrence(long now);
}
