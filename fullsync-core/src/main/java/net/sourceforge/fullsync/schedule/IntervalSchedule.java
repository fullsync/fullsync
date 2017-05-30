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

import org.w3c.dom.Element;

public class IntervalSchedule extends Schedule {
	public static final String SCHEDULE_TYPE = "interval";

	private static final long serialVersionUID = 2L;

	long firstInterval;
	long interval;
	long next;
	String displayUnit;

	public IntervalSchedule(Element element) {
		if (element.hasAttribute("firstinterval")) {
			this.firstInterval = Long.parseLong(element.getAttribute("firstinterval"));
		}
		if (element.hasAttribute("interval")) {
			this.interval = Long.parseLong(element.getAttribute("interval"));
		}
		if (element.hasAttribute("displayUnit")) {
			this.displayUnit = element.getAttribute("displayUnit");
		}
		this.next = System.currentTimeMillis() + firstInterval;
	}

	public IntervalSchedule(long firstInterval, long interval, String displayUnit) {
		this.firstInterval = firstInterval;
		this.interval = interval;
		this.displayUnit = displayUnit;

		this.next = System.currentTimeMillis() + firstInterval;
	}

	@Override
	public final Element serialize(final Element element) {
		element.setAttribute("type", SCHEDULE_TYPE);
		element.setAttribute("firstinterval", "" + this.firstInterval);
		element.setAttribute("interval", "" + this.interval);
		element.setAttribute("displayUnit", this.displayUnit);
		return element;
	}

	@Override
	public long getNextOccurrence(long now) {
		return next > now ? next : now;
	}

	@Override
	public void setLastOccurrence(long now) {
		this.next = now + interval;
	}

	public long getFirstInterval() {
		return firstInterval;
	}

	public long getInterval() {
		return interval;
	}

	public String getIntervalDisplayUnit() {
		return displayUnit;
	}
}
