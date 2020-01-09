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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.sourceforge.fullsync.fs.FSFile;
import net.sourceforge.fullsync.rules.filefilter.values.DateValue;

public class FileModificationDateFileFilterRuleTest {
	private static final String OLD_DATE_TIME = "01/06/2005 06:00:00";
	private static final String NEW_DATE_TIME = "02/06/2005 06:00:00";
	private FSFile root = new TestNode("root", null, true, true, 0, 0);
	private FSFile testNode;
	private long oldtime;
	private long newtime;
	private SimpleDateFormat dateFormat;

	@BeforeEach
	public void setUp() throws ParseException {
		dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		oldtime = dateFormat.parse(OLD_DATE_TIME).getTime();
		newtime = dateFormat.parse(NEW_DATE_TIME).getTime();

		testNode = new TestNode("foobar.txt", root, true, false, 1000, oldtime);
	}

	@Test
	public void testOpIs() throws Exception {
		FileModificationDateFileFilterRule filterRule = new FileModificationDateFileFilterRule(new DateValue(OLD_DATE_TIME),
			FileModificationDateFileFilterRule.OP_IS);

		assertTrue(filterRule.match(testNode));
		testNode.setLastModified(newtime);

		assertFalse(filterRule.match(testNode));
	}

	@Test
	public void testOpIsnt() throws Exception {
		FileModificationDateFileFilterRule filterRule = new FileModificationDateFileFilterRule(new DateValue(OLD_DATE_TIME),
			FileModificationDateFileFilterRule.OP_ISNT);

		testNode.setLastModified(newtime);
		assertTrue(filterRule.match(testNode));
	}

	@Test
	public void testOpIsAfter() throws Exception {
		FileModificationDateFileFilterRule filterRule = new FileModificationDateFileFilterRule(new DateValue(NEW_DATE_TIME),
			FileModificationDateFileFilterRule.OP_IS_AFTER);

		testNode.setLastModified(oldtime);

		assertNotEquals(oldtime, newtime);
		assertTrue(filterRule.match(testNode));
	}

	@Test
	public void testOpIsBefore() throws Exception {
		FileModificationDateFileFilterRule filterRule = new FileModificationDateFileFilterRule(new DateValue(OLD_DATE_TIME),
			FileModificationDateFileFilterRule.OP_IS_BEFORE);

		testNode.setLastModified(newtime);
		assertNotEquals(oldtime, newtime);
		assertTrue(filterRule.match(testNode));
	}

	@Test
	public void testOpDefault() throws Exception {
		FileModificationDateFileFilterRule filterRule = new FileModificationDateFileFilterRule(new DateValue(NEW_DATE_TIME), -1);

		assertFalse(filterRule.match(testNode));
	}

	@Test
	public void throwFilterRuleNotAppliableExceptionAll() throws Exception {
		FileModificationDateFileFilterRule filterRule = new FileModificationDateFileFilterRule(new DateValue(NEW_DATE_TIME),
			FileModificationDateFileFilterRule.OP_IS);

		testNode.setLastModified(-1);

		assertThrows(FilterRuleNotAppliableException.class, () -> filterRule.match(testNode));
	}
}
