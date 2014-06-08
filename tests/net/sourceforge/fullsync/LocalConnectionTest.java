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
package net.sourceforge.fullsync;

import org.junit.Before;
import org.junit.Test;

public class LocalConnectionTest extends BaseConnectionTest {

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		ConnectionDescription dst = new ConnectionDescription(testingDst.toURI());
		dst.setParameter("bufferStrategy", "");
		profile.setDestination(dst);
	}

	@Override
	@Test
	public void testSingleInSync() throws Exception {
		super.testSingleInSync();
	}

	@Override
	@Test
	public void testSingleSpaceMinus() throws Exception {
		super.testSingleSpaceMinus();
	}

	@Override
	@Test
	public void testSingleFileChange() throws Exception {
		super.testSingleFileChange();
	}
}