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

public class SizeValueTest {
	@Test
	public void parseFromStringExtractsValueAndUnit() throws Exception {
		var size = new SizeValue("10 MBYTES");
		assertEquals(10.0, size.getValue());
		assertEquals(SizeValue.Unit.MBYTES, size.getUnit());
	}

	@Test
	public void getBytesForBytes() {
		assertEquals(42L, new SizeValue(42, SizeValue.Unit.BYTES).getBytes());
	}

	@Test
	public void getBytesForKilobytes() {
		assertEquals(1024L, new SizeValue(1, SizeValue.Unit.KBYTES).getBytes());
	}

	@Test
	public void getBytesForMegabytes() {
		assertEquals(1024L * 1024, new SizeValue(1, SizeValue.Unit.MBYTES).getBytes());
	}

	@Test
	public void getBytesForGigabytes() {
		assertEquals(1024L * 1024 * 1024, new SizeValue(1, SizeValue.Unit.GBYTES).getBytes());
	}

	@Test
	public void parseIsCaseInsensitive() throws Exception {
		var size = new SizeValue("1024 bytes");
		assertEquals(SizeValue.Unit.BYTES, size.getUnit());
	}

	@Test
	public void parseInvalidUnitThrowsDataParseException() {
		assertThrows(DataParseException.class, () -> new SizeValue("5 TBYTES"));
	}

	@Test
	public void parseStringWithNoSeparatorThrowsDataParseException() {
		assertThrows(DataParseException.class, () -> new SizeValue("1024BYTES"));
	}

	@Test
	public void toStringReturnsValueAndUnit() {
		assertEquals("10.0 MBYTES", new SizeValue(10, SizeValue.Unit.MBYTES).toString());
	}
}
