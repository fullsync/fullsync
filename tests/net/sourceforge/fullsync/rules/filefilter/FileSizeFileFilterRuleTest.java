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
/*
 * Created on May 29, 2005
 */
package net.sourceforge.fullsync.rules.filefilter;

import junit.framework.TestCase;
import net.sourceforge.fullsync.rules.filefilter.values.SizeValue;

/**
 * @author Michele Aiello
 */
public class FileSizeFileFilterRuleTest extends TestCase {

	public void testOpIs() throws FilterRuleNotAppliableException {
		FileSizeFileFilterRule filterRule = new FileSizeFileFilterRule(new SizeValue(1000, SizeValue.BYTES), FileSizeFileFilterRule.OP_IS);
		TestNode file = new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1000, 0);

		assertTrue(filterRule.match(file));

		file.setSize(2000);
		assertTrue(!filterRule.match(file));
	}

	public void testOpIsnt() throws FilterRuleNotAppliableException {
		FileSizeFileFilterRule filterRule = new FileSizeFileFilterRule(new SizeValue(1000, SizeValue.BYTES), FileSizeFileFilterRule.OP_ISNT);
		TestNode file = new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1000, 0);
		assertTrue(!filterRule.match(file));

		file.setSize(2000);
		assertTrue(filterRule.match(file));
	}

	public void testOpIsGreaterThan() throws FilterRuleNotAppliableException {
		FileSizeFileFilterRule filterRule = new FileSizeFileFilterRule(new SizeValue(1000, SizeValue.BYTES),
				FileSizeFileFilterRule.OP_IS_GREATER_THAN);
		TestNode file = new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1000, 0);

		assertTrue(!filterRule.match(file));

		file.setSize(2000);
		assertTrue(filterRule.match(file));

		file.setSize(999);
		assertTrue(!filterRule.match(file));
	}

	public void testOpIsLessThan() throws FilterRuleNotAppliableException {
		FileSizeFileFilterRule filterRule = new FileSizeFileFilterRule(new SizeValue(1000, SizeValue.BYTES),
				FileSizeFileFilterRule.OP_IS_LESS_THAN);
		TestNode file = new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1000, 0);

		assertTrue(!filterRule.match(file));

		file.setSize(2000);
		assertTrue(!filterRule.match(file));

		file.setSize(999);
		assertTrue(filterRule.match(file));
	}

}
