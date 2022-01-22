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

import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CrontabScheduleTest {
	private Calendar now;
	private Calendar expectedResult;

	@BeforeEach
	public void setUp() throws Exception {
		now = Calendar.getInstance();
		expectedResult = Calendar.getInstance();
	}

	private void assertNextOccurence(String pattern) throws Exception {
		Schedule schedule = new CrontabSchedule(pattern);

		var res = schedule.getNextOccurrence(0, now.getTimeInMillis());

		assertEquals(new Date(expectedResult.getTimeInMillis()), new Date(res));
	}

	@Test
	public void testGetNextOccurrenceMilliseconds() throws Exception {
		now.set(2004, 0, 1, 0, 0, 0);
		now.set(Calendar.MILLISECOND, 0);
		expectedResult.set(2004, 0, 1, 0, 0, 0);
		expectedResult.set(Calendar.MILLISECOND, 0);

		assertNextOccurence("0 0 * * *");

		now.set(2004, 0, 1, 0, 0, 0);
		now.set(Calendar.MILLISECOND, 1);
		expectedResult.set(2004, 0, 2, 0, 0, 0);
		expectedResult.set(Calendar.MILLISECOND, 0);

		assertNextOccurence("0 0 * * *");
	}

	@Test
	public void testGetNextOccurrenceTwice() throws Exception {
		now.set(2004, 0, 1, 0, 0, 0);
		now.set(Calendar.MILLISECOND, 0);
		expectedResult.set(2004, 0, 1, 0, 0, 0);
		expectedResult.set(Calendar.MILLISECOND, 0);

		Schedule schedule = new CrontabSchedule("0 0 * * *");
		long res;

		res = schedule.getNextOccurrence(0, now.getTimeInMillis());
		assertEquals(new Date(expectedResult.getTimeInMillis()), new Date(res));

		expectedResult.set(2004, 0, 2, 0, 0, 0);
		expectedResult.set(Calendar.MILLISECOND, 0);

		res = schedule.getNextOccurrence(now.getTimeInMillis(), now.getTimeInMillis());
		assertEquals(new Date(expectedResult.getTimeInMillis()), new Date(res));
	}

	@Test
	public void testGetNextOccurrenceHour1() throws Exception {
		now.set(2004, 0, 1, 0, 0, 0);
		expectedResult.set(2004, 0, 1, 10, 0, 0);
		expectedResult.set(Calendar.MILLISECOND, 0);

		assertNextOccurence("0 10,20 * * *");
	}

	@Test
	public void testGetNextOccurrenceHour2() throws Exception {
		now.set(2004, 0, 1, 10, 0, 1);
		expectedResult.set(2004, 0, 1, 20, 0, 0);
		expectedResult.set(Calendar.MILLISECOND, 0);

		assertNextOccurence("0 10,20 * * *");
	}

	@Test
	public void testGetNextOccurrenceDayOfMonth1() throws Exception {
		now.set(2004, 0, 1, 0, 0, 0);
		expectedResult.set(2004, 0, 10, 10, 0, 0);
		expectedResult.set(Calendar.MILLISECOND, 0);

		assertNextOccurence("0 10,20 10,20 * *");
	}

	@Test
	public void testGetNextOccurrenceDayOfMonth2() throws Exception {
		now.set(2004, 0, 1, 15, 0, 0);
		expectedResult.set(2004, 0, 10, 10, 0, 0);
		expectedResult.set(Calendar.MILLISECOND, 0);

		assertNextOccurence("0 10,20 10,20 * *");
	}

	@Test
	public void testGetNextOccurrenceDayOfMonth3() throws Exception {
		now.set(2004, 0, 10, 20, 0, 1);
		expectedResult.set(2004, 0, 20, 10, 0, 0);
		expectedResult.set(Calendar.MILLISECOND, 0);

		assertNextOccurence("0 10,20 10,20 * *");
	}

	@Test
	public void testGetNextOccurrenceDayOfWeek() throws Exception {
		now.set(2004, 0, 1, 0, 0, 1);
		expectedResult.set(2004, 0, 4, 10, 0, 0); // saturday
		expectedResult.set(Calendar.MILLISECOND, 0);

		assertNextOccurence("0 10,20 * * 7");
	}
}
