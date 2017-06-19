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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.rules.filefilter.values.SizeValue;

public class FileSizeFileFilterRuleTest {
	private File root = new TestNode("root", null, true, true, 0, 0);
	private File foobarTxt;

	@Before
	public void setUp() {
		foobarTxt = new TestNode("foobar.txt", root, true, false, 1000, 0);
	}

	@Test
	public void testOpIs() throws FilterRuleNotAppliableException {
		FileSizeFileFilterRule filterRule = new FileSizeFileFilterRule(new SizeValue("1000 Bytes"), FileSizeFileFilterRule.OP_IS);

		assertTrue(filterRule.match(foobarTxt));

		foobarTxt.setSize(2000);
		assertFalse(filterRule.match(foobarTxt));
	}

	@Test
	public void testOpIsnt() throws FilterRuleNotAppliableException {
		FileSizeFileFilterRule filterRule = new FileSizeFileFilterRule(new SizeValue("1000 Bytes"), FileSizeFileFilterRule.OP_ISNT);
		assertFalse(filterRule.match(foobarTxt));

		foobarTxt.setSize(2000);
		assertTrue(filterRule.match(foobarTxt));
	}

	@Test
	public void testOpIsGreaterThan() throws FilterRuleNotAppliableException {
		FileSizeFileFilterRule filterRule = new FileSizeFileFilterRule(new SizeValue("1000 Bytes"),
			FileSizeFileFilterRule.OP_IS_GREATER_THAN);

		assertFalse(filterRule.match(foobarTxt));

		foobarTxt.setSize(2000);
		assertTrue(filterRule.match(foobarTxt));

		foobarTxt.setSize(999);
		assertFalse(filterRule.match(foobarTxt));
	}

	@Test
	public void testOpIsLessThan() throws FilterRuleNotAppliableException {
		FileSizeFileFilterRule filterRule = new FileSizeFileFilterRule(new SizeValue("1000 Bytes"), FileSizeFileFilterRule.OP_IS_LESS_THAN);

		assertFalse(filterRule.match(foobarTxt));

		foobarTxt.setSize(2000);
		assertFalse(filterRule.match(foobarTxt));

		foobarTxt.setSize(999);
		assertTrue(filterRule.match(foobarTxt));
	}
}
