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
package net.sourceforge.fullsync.rules.filefilter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.jupiter.api.Test;

import net.sourceforge.fullsync.rules.filefilter.values.DateValue;

public class DateValueTest {
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	private long parseDate(String date) throws ParseException {
		return dateFormat.parse(date).getTime();
	}

	@Test
	public void testEquals() throws Exception {
		DateValue value = new DateValue("10/08/1994");

		assertTrue(value.isEqualTo(parseDate("10/08/1994 10:00:00")));
		assertTrue(value.isEqualTo(parseDate("10/08/1994 23:59:59")));
		assertTrue(value.isEqualTo(parseDate("10/08/1994 00:00:00")));
		assertFalse(value.isEqualTo(parseDate("09/08/1994 23:59:59")));
		assertFalse(value.isEqualTo(parseDate("11/08/1994 00:00:00")));
	}

	@Test
	public void testIsBefore() throws Exception {
		DateValue value = new DateValue("10/08/1994");

		assertTrue(value.isBefore(parseDate("11/08/1994 00:00:00")));
		assertTrue(value.isBefore(parseDate("11/08/1994 23:59:59")));
		assertTrue(value.isBefore(parseDate("10/09/1994 23:59:59")));
		assertTrue(value.isBefore(parseDate("10/08/1995 23:59:59")));
		assertTrue(value.isBefore(parseDate("09/08/2005 23:59:59")));
	}

	@Test
	public void testIsAfter() throws Exception {
		DateValue value = new DateValue("10/08/1994");

		assertTrue(value.isAfter(parseDate("09/08/1994 00:00:00")));
		assertTrue(value.isAfter(parseDate("09/08/1994 23:59:59")));
		assertTrue(value.isAfter(parseDate("10/07/1994 23:59:59")));
		assertTrue(value.isAfter(parseDate("10/08/1993 23:59:59")));
		assertTrue(value.isAfter(parseDate("10/08/1990 23:59:59")));
	}
}
