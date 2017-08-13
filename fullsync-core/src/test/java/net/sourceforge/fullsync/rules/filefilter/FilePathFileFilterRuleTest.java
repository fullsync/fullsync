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

import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.rules.filefilter.values.TextValue;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class FilePathFileFilterRuleTest {
	private File root = new TestNode("root", null, true, true, 0, 0);
	private File testNode;

	@Before
	public void setUp() {
		testNode = new TestNode("foobar.txt", root, true, false, 1000, 0);
	}

	@Test
	public void opMatchesRegexp() throws Exception {
		FilePathFileFilterRule filterRule = new FilePathFileFilterRule(new TextValue(".*"), FilePathFileFilterRule.OP_MATCHES_REGEXP);

		assertTrue(filterRule.match(testNode));
	}

	@Test(expected = DataParseException.class)
	public void throwPatternSyntaxException() throws DataParseException {
		FilePathFileFilterRule filterRule = new FilePathFileFilterRule(new TextValue("{"), FilePathFileFilterRule.OP_MATCHES_REGEXP);

		filterRule.match(testNode);
	}
}
