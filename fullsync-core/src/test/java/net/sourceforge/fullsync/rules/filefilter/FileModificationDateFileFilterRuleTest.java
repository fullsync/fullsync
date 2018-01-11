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

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Before;
import org.junit.Test;

import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.rules.filefilter.values.DateValue;

import static org.junit.Assert.*;

public class FileModificationDateFileFilterRuleTest {
	private File root = new TestNode("root", null, true, true, 0, 0);
	private File testNode;
	private long oldtime;
	private long newtime;
	private SimpleDateFormat dateFormat;

	@Before
	public void setUp() throws ParseException {
		dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		oldtime = dateFormat.parse("01/06/2005 06:00:00").getTime();
		newtime = dateFormat.parse("02/06/2005 06:00:00").getTime();

		testNode = new TestNode("foobar.txt", root, true, false, 1000, oldtime);
	}

	@Test
	public void testOpIs() throws ParseException, FilterRuleNotAppliableException {
		FileModificationDateFileFilterRule filterRule = new FileModificationDateFileFilterRule(new DateValue(oldtime),
			FileModificationDateFileFilterRule.OP_IS);

		assertTrue(filterRule.match(testNode));
		testNode.setLastModified(newtime);

		assertFalse(filterRule.match(testNode));
	}

	@Test
	public void testOpIsnt() throws ParseException, FilterRuleNotAppliableException {
		FileModificationDateFileFilterRule filterRule = new FileModificationDateFileFilterRule(new DateValue(oldtime),
			FileModificationDateFileFilterRule.OP_ISNT);

		testNode.setLastModified(newtime);
		assertTrue(filterRule.match(testNode));
	}

	@Test
	public void testOpIsAfter() throws ParseException, FilterRuleNotAppliableException {
		FileModificationDateFileFilterRule filterRule = new FileModificationDateFileFilterRule(new DateValue(newtime),
			FileModificationDateFileFilterRule.OP_IS_AFTER);

		testNode.setLastModified(oldtime);

		assertNotEquals(oldtime, newtime);
		assertTrue(filterRule.match(testNode));
	}

	@Test
	public void testOpIsBefore() throws ParseException, FilterRuleNotAppliableException {
		FileModificationDateFileFilterRule filterRule = new FileModificationDateFileFilterRule(new DateValue(oldtime),
			FileModificationDateFileFilterRule.OP_IS_BEFORE);

		testNode.setLastModified(newtime);
		assertNotEquals(oldtime, newtime);
		assertTrue(filterRule.match(testNode));
	}

	@Test
	public void testOpDefault() throws ParseException, FilterRuleNotAppliableException {
		FileModificationDateFileFilterRule filterRule = new FileModificationDateFileFilterRule(new DateValue(newtime), -1);

		assertFalse(filterRule.match(testNode));
	}

	@Test(expected = FilterRuleNotAppliableException.class)
	public void throwFilterRuleNotAppliableExceptionAll() throws FilterRuleNotAppliableException {
		FileModificationDateFileFilterRule filterRule = new FileModificationDateFileFilterRule(new DateValue(newtime),
			FileModificationDateFileFilterRule.OP_IS);

		testNode.setLastModified(-1);

		assertTrue(filterRule.match(testNode));
	}
}
