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
package net.sourceforge.fullsync.changelog;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class VersionComparatorTest {

	@Test
	public void testVersionEquality() {
		VersionComparator vc = new VersionComparator();
		assertTrue(0 == vc.compare("0.0.1", "0.0.1"), "0.0.1 == 0.0.1");
		assertTrue(0 == vc.compare("0.0.1", "0.0.1.0"), "0.0.1 == 0.0.1.0");
		assertTrue(0 == vc.compare("1", "1.0.0.0.0"), "1 == 1.0.0.0.0");
		assertTrue(0 == vc.compare("", ""), "- == -");
	}

	@Test
	public void testVersionOlder() {
		VersionComparator vc = new VersionComparator();
		assertTrue(-1 == vc.compare("0.0.1", "0.0.2"), "0.0.1 < 0.0.2");
		assertTrue(-1 == vc.compare("0.0.1", "0.1.1"), "0.0.1 < 0.1.1");
		assertTrue(-1 == vc.compare("0.0.1", "1.1.1"), "0.0.1 < 1.1.1");
		assertTrue(-1 == vc.compare("0.0.1", "1.1.1"), "0.0.1 < 1.1.1");
		assertTrue(-1 == vc.compare("0.0.1", "1.1.1"), "0.0.1 < 1.1.1");
		assertTrue(-1 == vc.compare("", "0.0.1"), "0.0.1 < -");
	}

	@Test
	public void testVersionNewer() {
		VersionComparator vc = new VersionComparator();
		assertTrue(1 == vc.compare("0.1.0", "0.0.1"), "0.1.0 > 0.0.1");
		assertTrue(1 == vc.compare("0.10.0", "0.1.1"), "0.10.0 > 0.1.1");
		assertTrue(1 == vc.compare("0.1.1", "0.1.0"), "0.1.1 > 0.1.0");
		assertTrue(1 == vc.compare("1.0.0", "0.5.9"), "1.0.0 > 0.5.9");
		assertTrue(1 == vc.compare("1.0.0", "0.5.9"), "1.0.0 > 0.5.9");
		assertTrue(1 == vc.compare("1.0.0", ""), "1.0.0 > -");
	}
}
