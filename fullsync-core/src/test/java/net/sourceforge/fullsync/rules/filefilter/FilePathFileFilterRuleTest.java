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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.fs.FSFile;
import net.sourceforge.fullsync.rules.filefilter.values.TextValue;

public class FilePathFileFilterRuleTest {
	private FSFile root = new TestNode("root", null, true, true, 0, 0);
	private FSFile testNode;

	@BeforeEach
	public void setUp() {
		testNode = new TestNode("foobar.txt", root, true, false, 1000, 0);
	}

	@Test
	public void opMatchesRegexp() throws Exception {
		FilePathFileFilterRule filterRule = new FilePathFileFilterRule(new TextValue(".*"), FilePathFileFilterRule.OP_MATCHES_REGEXP);

		assertTrue(filterRule.match(testNode));
	}

	@Test
	public void throwPatternSyntaxException() throws Exception {
		TextValue t = new TextValue("{");
		assertThrows(DataParseException.class, () -> new FilePathFileFilterRule(t, FilePathFileFilterRule.OP_MATCHES_REGEXP));
	}
}
