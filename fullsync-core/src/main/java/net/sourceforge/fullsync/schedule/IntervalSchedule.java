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

import java.util.Objects;

public class IntervalSchedule extends Schedule {
	public static final String SCHEDULE_TYPE = "interval"; //$NON-NLS-1$

	private static final String ATTRIBUTE_TYPE = "type"; //$NON-NLS-1$
	private static final String ATTRIBUTE_DISPLAY_UNIT = "displayUnit"; //$NON-NLS-1$
	private static final String ATTRIBUTE_INTERVAL = "interval"; //$NON-NLS-1$
	private static final String ATTRIBUTE_FIRSTINTERVAL = "firstinterval"; //$NON-NLS-1$

	private final long firstInterval;
	private final long interval;
	private final String displayUnit;
	private final long creationTime;

	public IntervalSchedule(Element element) {
		String firstIntervalAttribute = null;
		if (element.hasAttribute(ATTRIBUTE_FIRSTINTERVAL)) {
			firstIntervalAttribute = element.getAttribute(ATTRIBUTE_FIRSTINTERVAL);
		}
		String intervalAttribute = null;
		if (element.hasAttribute(ATTRIBUTE_INTERVAL)) {
			intervalAttribute = element.getAttribute(ATTRIBUTE_INTERVAL);
		}
		this.firstInterval = null != firstIntervalAttribute ? Long.parseLong(firstIntervalAttribute) : 0;
		this.interval = null != intervalAttribute ? Long.parseLong(intervalAttribute) : 0;
		this.displayUnit = element.getAttribute(ATTRIBUTE_DISPLAY_UNIT);
		this.creationTime = System.currentTimeMillis();
	}

	public IntervalSchedule(long firstInterval, long interval, String displayUnit) {
		this.firstInterval = firstInterval;
		this.interval = interval;
		this.displayUnit = displayUnit;
		this.creationTime = System.currentTimeMillis();
	}

	@Override
	public final Element serialize(final Element element) {
		element.setAttribute(ATTRIBUTE_TYPE, SCHEDULE_TYPE);
		element.setAttribute(ATTRIBUTE_FIRSTINTERVAL, Long.toString(firstInterval));
		element.setAttribute(ATTRIBUTE_INTERVAL, Long.toString(interval));
		element.setAttribute(ATTRIBUTE_DISPLAY_UNIT, displayUnit);
		return element;
	}

	@Override
	public long getNextOccurrence(long lastOccurence, long now) {
		long next = 0 == lastOccurence ? creationTime + firstInterval : lastOccurence + interval;
		return next > now ? next : now;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		IntervalSchedule that = (IntervalSchedule) o;
		return firstInterval == that.firstInterval && interval == that.interval && displayUnit.equals(that.displayUnit);
	}

	@Override
	public int hashCode() {
		return Objects.hash(firstInterval, interval, displayUnit);
	}

	@Override
	public String toString() {
		return "IntervalSchedule{"
			+ "firstInterval="
			+ firstInterval
			+ ", interval="
			+ interval
			+ ", displayUnit='"
			+ displayUnit
			+ '\''
			+ ", creationTime="
			+ creationTime
			+ '}';
	}
}
