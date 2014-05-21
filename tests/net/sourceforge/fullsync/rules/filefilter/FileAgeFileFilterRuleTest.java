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
 * Created on Jun 2, 2005
 */
package net.sourceforge.fullsync.rules.filefilter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import net.sourceforge.fullsync.SystemDate;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.rules.filefilter.values.AgeValue;

import org.junit.After;
import org.junit.Test;

public class FileAgeFileFilterRuleTest {

	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	private File root = new TestNode("root", null, true, true, 0, 0);

	@After
	public void tearDown() throws Exception {
		SystemDate.getInstance().setUseSystemTime();
	}

	@Test
	public void testOpIs() throws FilterRuleNotAppliableException, ParseException {
		SystemDate.getInstance().setTimeSpeed(0);
		SystemDate.getInstance().setCurrent(dateFormat.parse("01/01/2005 10:00:01").getTime());
		FileAgeFileFilterRule filterRule = new FileAgeFileFilterRule(new AgeValue(1, AgeValue.Unit.SECONDS), FileAgeFileFilterRule.OP_IS);
		assertTrue(filterRule.match(new TestNode("foobar.txt", root, true, false, 1024, dateFormat.parse(
				"01/01/2005 10:00:00").getTime())));
		assertFalse(filterRule.match(new TestNode("foobar.txt", root, true, false, 1024, dateFormat.parse(
				"01/01/2005 10:00:01").getTime())));
		assertFalse(filterRule.match(new TestNode("foobar.txt", root, true, false, 1024, dateFormat.parse(
				"01/01/2005 09:00:00").getTime())));
		assertFalse(filterRule.match(new TestNode("foobar.txt", root, true, false, 1024, dateFormat.parse(
				"01/02/2005 10:00:00").getTime())));
	}

	@Test
	public void testOpIsnt() throws FilterRuleNotAppliableException, ParseException {
		SystemDate.getInstance().setTimeSpeed(0);
		SystemDate.getInstance().setCurrent(dateFormat.parse("01/01/2005 10:00:01").getTime());
		FileAgeFileFilterRule filterRule = new FileAgeFileFilterRule(new AgeValue(1, AgeValue.Unit.SECONDS), FileAgeFileFilterRule.OP_ISNT);
		assertFalse(filterRule.match(new TestNode("foobar.txt", root, true, false, 1024, dateFormat.parse(
				"01/01/2005 10:00:00").getTime())));
		assertTrue(filterRule.match(new TestNode("foobar.txt", root, true, false, 1024, dateFormat.parse(
				"01/01/2005 10:00:01").getTime())));
		assertTrue(filterRule.match(new TestNode("foobar.txt", root, true, false, 1024, dateFormat.parse(
				"01/01/2005 09:00:00").getTime())));
		assertTrue(filterRule.match(new TestNode("foobar.txt", root, true, false, 1024, dateFormat.parse(
				"01/02/2005 10:00:00").getTime())));
	}

	@Test
	public void testOpIsGreaterThan() throws FilterRuleNotAppliableException, ParseException {
		SystemDate.getInstance().setTimeSpeed(0);
		SystemDate.getInstance().setCurrent(dateFormat.parse("01/01/2005 10:00:00").getTime());
		FileAgeFileFilterRule filterRule = new FileAgeFileFilterRule(new AgeValue(1, AgeValue.Unit.SECONDS),
				FileAgeFileFilterRule.OP_IS_GREATER_THAN);
		assertFalse(filterRule.match(new TestNode("foobar.txt", root, true, false, 1024, dateFormat.parse(
				"01/01/2005 10:00:00").getTime())));
		assertFalse(filterRule.match(new TestNode("foobar.txt", root, true, false, 1024, dateFormat.parse(
				"01/01/2005 10:00:01").getTime())));
		assertFalse(filterRule.match(new TestNode("foobar.txt", root, true, false, 1024, dateFormat.parse(
				"01/01/2005 10:00:02").getTime())));
		assertTrue(filterRule.match(new TestNode("foobar.txt", root, true, false, 1024, dateFormat.parse(
				"30/12/2004 09:00:00").getTime())));
		assertTrue(filterRule.match(new TestNode("foobar.txt", root, true, false, 1024, dateFormat.parse(
				"01/01/2005 01:00:00").getTime())));
		assertFalse(filterRule.match(new TestNode("foobar.txt", root, true, false, 1024, dateFormat.parse(
				"01/01/2005 11:00:00").getTime())));
		assertFalse(filterRule.match(new TestNode("foobar.txt", root, true, false, 1024, dateFormat.parse(
				"01/01/2006 09:00:00").getTime())));
		assertTrue(filterRule.match(new TestNode("foobar.txt", root, true, false, 1024, dateFormat.parse(
				"01/01/2004 09:00:00").getTime())));
		assertFalse(filterRule.match(new TestNode("foobar.txt", root, true, false, 1024, dateFormat.parse(
				"01/01/2005 09:59:59").getTime())));
		assertTrue(filterRule.match(new TestNode("foobar.txt", root, true, false, 1024, dateFormat.parse(
				"01/01/2005 09:59:58").getTime())));
	}

	@Test
	public void testOpIsLessThan() throws FilterRuleNotAppliableException, ParseException {
		SystemDate.getInstance().setTimeSpeed(0);
		SystemDate.getInstance().setCurrent(dateFormat.parse("01/01/2005 10:00:00").getTime());
		FileAgeFileFilterRule filterRule = new FileAgeFileFilterRule(new AgeValue(10, AgeValue.Unit.SECONDS),
				FileAgeFileFilterRule.OP_IS_LESS_THAN);
		assertTrue(filterRule.match(new TestNode("foobar.txt", root, true, false, 1024, dateFormat.parse(
				"01/01/2005 10:00:00").getTime())));
		assertTrue(filterRule.match(new TestNode("foobar.txt", root, true, false, 1024, dateFormat.parse(
				"01/01/2005 09:59:57").getTime())));
		assertFalse(filterRule.match(new TestNode("foobar.txt", root, true, false, 1024, dateFormat.parse(
				"01/01/2005 09:59:50").getTime())));
		assertFalse(filterRule.match(new TestNode("foobar.txt", root, true, false, 1024, dateFormat.parse(
				"30/12/2004 10:00:00").getTime())));
		assertFalse(filterRule.match(new TestNode("foobar.txt", root, true, false, 1024, dateFormat.parse(
				"01/01/2005 09:00:00").getTime())));
		assertTrue(filterRule.match(new TestNode("foobar.txt", root, true, false, 1024, dateFormat.parse(
				"01/01/2005 11:00:00").getTime())));
	}
}
