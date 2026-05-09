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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.jupiter.api.Test;

public class IntervalScheduleTest {
	private org.w3c.dom.Element createElement() throws Exception {
		var factory = DocumentBuilderFactory.newInstance();
		var doc = factory.newDocumentBuilder().newDocument();
		return doc.createElement("schedule");
	}

	@Test
	public void constructorWithParamsSetsGetters() {
		var schedule = new IntervalSchedule(5000L, 60000L, "minutes");
		assertEquals(5000L, schedule.getFirstInterval());
		assertEquals(60000L, schedule.getInterval());
		assertEquals("minutes", schedule.getIntervalDisplayUnit());
	}

	@Test
	public void constructorFromElementParsesAttributes() throws Exception {
		var el = createElement();
		el.setAttribute("firstinterval", "3000");
		el.setAttribute("interval", "30000");
		el.setAttribute("displayUnit", "seconds");

		var schedule = new IntervalSchedule(el);
		assertEquals(3000L, schedule.getFirstInterval());
		assertEquals(30000L, schedule.getInterval());
		assertEquals("seconds", schedule.getIntervalDisplayUnit());
	}

	@Test
	public void constructorFromElementWithMissingAttributesUsesZero() throws Exception {
		var el = createElement();
		var schedule = new IntervalSchedule(el);
		assertEquals(0L, schedule.getFirstInterval());
		assertEquals(0L, schedule.getInterval());
	}

	@Test
	public void getNextOccurrenceForFirstRunReturnsAtLeastNow() {
		var schedule = new IntervalSchedule(0L, 60_000L, "minutes");
		var now = System.currentTimeMillis();
		var next = schedule.getNextOccurrence(0, now);
		assertTrue(next >= now);
	}

	@Test
	public void getNextOccurrenceAddsIntervalToLastOccurrence() {
		var schedule = new IntervalSchedule(0L, 60_000L, "minutes");
		var now = System.currentTimeMillis();
		var lastOccurrence = now;
		var next = schedule.getNextOccurrence(lastOccurrence, now);
		assertEquals(now + 60_000L, next);
	}

	@Test
	public void getNextOccurrenceReturnsAtLeastNowWhenNextIsInPast() {
		var schedule = new IntervalSchedule(0L, 1000L, "seconds");
		var now = System.currentTimeMillis();
		var longAgo = now - 100_000L;
		var next = schedule.getNextOccurrence(longAgo, now);
		assertEquals(now, next);
	}

	@Test
	public void serializeRoundtrip() throws Exception {
		var original = new IntervalSchedule(5000L, 60000L, "minutes");
		var el = createElement();
		original.serialize(el);

		assertEquals(IntervalSchedule.SCHEDULE_TYPE, el.getAttribute("type"));
		assertEquals("5000", el.getAttribute("firstinterval"));
		assertEquals("60000", el.getAttribute("interval"));
		assertEquals("minutes", el.getAttribute("displayUnit"));

		var restored = new IntervalSchedule(el);
		assertEquals(original, restored);
	}

	@Test
	public void equalInstancesHaveSameHashCode() {
		var a = new IntervalSchedule(1000L, 5000L, "ms");
		var b = new IntervalSchedule(1000L, 5000L, "ms");
		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
	}

	@Test
	public void differentDisplayUnitBreaksEquality() {
		var a = new IntervalSchedule(1000L, 5000L, "ms");
		var b = new IntervalSchedule(1000L, 5000L, "seconds");
		assertNotEquals(a, b);
	}

	@Test
	public void differentIntervalBreaksEquality() {
		var a = new IntervalSchedule(1000L, 5000L, "ms");
		var b = new IntervalSchedule(1000L, 9000L, "ms");
		assertNotEquals(a, b);
	}
}
