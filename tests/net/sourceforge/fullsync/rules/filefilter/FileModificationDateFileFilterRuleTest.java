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

import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import net.sourceforge.fullsync.rules.filefilter.values.DateValue;

import org.junit.Test;

public class FileModificationDateFileFilterRuleTest {

	@Test
	public void testOpIs() throws ParseException, FilterRuleNotAppliableException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		FileModificationDateFileFilterRule filterRule = new FileModificationDateFileFilterRule(new DateValue(
				dateFormat.parse("01/06/2005 06:00:00").getTime()), FileModificationDateFileFilterRule.OP_IS);

		TestNode file = new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1000, dateFormat.parse("01/06/2005 10:00:00").getTime());

		assertTrue(filterRule.match(file));
		file.setLastModified(dateFormat.parse("02/06/2005 10:00:00").getTime());

		assertTrue(!filterRule.match(file));
	}

}
