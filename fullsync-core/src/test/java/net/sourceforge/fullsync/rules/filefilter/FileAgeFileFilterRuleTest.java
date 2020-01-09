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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.sourceforge.fullsync.SystemDate;
import net.sourceforge.fullsync.fs.FSFile;
import net.sourceforge.fullsync.rules.filefilter.values.AgeValue;

public class FileAgeFileFilterRuleTest {
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	private FSFile root = new TestNode("root", null, true, true, 0, 0);

	private FSFile createTestNode(String date) throws ParseException {
		long lastModified = dateFormat.parse(date).getTime();
		return new TestNode("foobar.txt", root, true, false, 1024, lastModified);
	}

	@BeforeEach
	public void setUp() throws Exception {
		SystemDate.getInstance().setTimeSpeed(0);
		SystemDate.getInstance().setCurrent(dateFormat.parse("01/01/2005 10:00:00").getTime());
	}

	@AfterEach
	public void tearDown() throws Exception {
		SystemDate.getInstance().setUseSystemTime();
	}

	@Test
	public void testOpIs() throws FilterRuleNotAppliableException, ParseException {
		FileAgeFileFilterRule filterRule = new FileAgeFileFilterRule(new AgeValue(1, AgeValue.Unit.SECONDS), FileAgeFileFilterRule.OP_IS);
		assertTrue(filterRule.match(createTestNode("01/01/2005 09:59:59")));
		assertFalse(filterRule.match(createTestNode("01/01/2005 10:00:01")));
		assertFalse(filterRule.match(createTestNode("01/01/2005 09:00:00")));
		assertFalse(filterRule.match(createTestNode("01/02/2005 10:00:00")));
	}

	@Test
	public void testOpIsnt() throws FilterRuleNotAppliableException, ParseException {
		FileAgeFileFilterRule filterRule = new FileAgeFileFilterRule(new AgeValue(1, AgeValue.Unit.SECONDS), FileAgeFileFilterRule.OP_ISNT);
		assertFalse(filterRule.match(createTestNode("01/01/2005 09:59:59")));
		assertTrue(filterRule.match(createTestNode("01/01/2005 10:00:01")));
		assertTrue(filterRule.match(createTestNode("01/01/2005 09:00:00")));
		assertTrue(filterRule.match(createTestNode("01/02/2005 10:00:00")));
	}

	@Test
	public void testOpIsGreaterThan() throws FilterRuleNotAppliableException, ParseException {
		FileAgeFileFilterRule filterRule = new FileAgeFileFilterRule(new AgeValue(1, AgeValue.Unit.SECONDS),
			FileAgeFileFilterRule.OP_IS_GREATER_THAN);
		assertFalse(filterRule.match(createTestNode("01/01/2005 10:00:00")));
		assertFalse(filterRule.match(createTestNode("01/01/2005 10:00:01")));
		assertFalse(filterRule.match(createTestNode("01/01/2005 10:00:02")));
		assertTrue(filterRule.match(createTestNode("30/12/2004 09:00:00")));
		assertTrue(filterRule.match(createTestNode("01/01/2005 01:00:00")));
		assertFalse(filterRule.match(createTestNode("01/01/2005 11:00:00")));
		assertFalse(filterRule.match(createTestNode("01/01/2006 09:00:00")));
		assertTrue(filterRule.match(createTestNode("01/01/2004 09:00:00")));
		assertFalse(filterRule.match(createTestNode("01/01/2005 09:59:59")));
		assertTrue(filterRule.match(createTestNode("01/01/2005 09:59:58")));
	}

	@Test
	public void testOpIsLessThan() throws FilterRuleNotAppliableException, ParseException {
		FileAgeFileFilterRule filterRule = new FileAgeFileFilterRule(new AgeValue(10, AgeValue.Unit.SECONDS),
			FileAgeFileFilterRule.OP_IS_LESS_THAN);
		assertTrue(filterRule.match(createTestNode("01/01/2005 10:00:00")));
		assertTrue(filterRule.match(createTestNode("01/01/2005 09:59:57")));
		assertFalse(filterRule.match(createTestNode("01/01/2005 09:59:50")));
		assertFalse(filterRule.match(createTestNode("30/12/2004 10:00:00")));
		assertFalse(filterRule.match(createTestNode("01/01/2005 09:00:00")));
		assertTrue(filterRule.match(createTestNode("01/01/2005 11:00:00")));
	}

	@Test
	public void testOpDefault() throws FilterRuleNotAppliableException, ParseException {
		FileAgeFileFilterRule filterRule = new FileAgeFileFilterRule(new AgeValue(1, AgeValue.Unit.SECONDS), -1);
		assertFalse(filterRule.match(createTestNode("01/01/2005 10:00:00")));
	}
}
