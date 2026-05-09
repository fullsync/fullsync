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
package net.sourceforge.fullsync.rules.filefilter.values;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import net.sourceforge.fullsync.DataParseException;

import org.junit.jupiter.api.Test;

public class AgeValueTest {
	@Test
	public void parseFromStringExtractsValueAndUnit() throws Exception {
		var age = new AgeValue("5 DAYS");
		assertEquals(5.0, age.getValue());
		assertEquals(AgeValue.Unit.DAYS, age.getUnit());
	}

	@Test
	public void getSecondsForSeconds() {
		assertEquals(7, new AgeValue(7, AgeValue.Unit.SECONDS).getSeconds());
	}

	@Test
	public void getSecondsForMinutes() {
		assertEquals(120, new AgeValue(2, AgeValue.Unit.MINUTES).getSeconds());
	}

	@Test
	public void getSecondsForHours() {
		assertEquals(7200, new AgeValue(2, AgeValue.Unit.HOURS).getSeconds());
	}

	@Test
	public void getSecondsForDays() {
		assertEquals(86400, new AgeValue(1, AgeValue.Unit.DAYS).getSeconds());
	}

	@Test
	public void parseIsCaseInsensitive() throws Exception {
		var age = new AgeValue("3 hours");
		assertEquals(AgeValue.Unit.HOURS, age.getUnit());
	}

	@Test
	public void parseInvalidUnitThrowsDataParseException() {
		assertThrows(DataParseException.class, () -> new AgeValue("5 FORTNIGHTS"));
	}

	@Test
	public void parseStringWithNoSeparatorThrowsDataParseException() {
		assertThrows(DataParseException.class, () -> new AgeValue("5DAYS"));
	}

	@Test
	public void toStringReturnsValueAndUnit() {
		assertEquals("5.0 DAYS", new AgeValue(5, AgeValue.Unit.DAYS).toString());
	}
}
