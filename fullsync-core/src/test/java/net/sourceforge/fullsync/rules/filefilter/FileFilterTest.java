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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.sourceforge.fullsync.FSFile;
import net.sourceforge.fullsync.rules.filefilter.values.OperandValue;
import net.sourceforge.fullsync.rules.filefilter.values.SizeValue;
import net.sourceforge.fullsync.rules.filefilter.values.TextValue;

public class FileFilterTest {
	private FSFile root;
	private FSFile testNode;
	private FileFilterRule alwaysTrue = new AlwaysTrueFileFilterRule();
	private FileFilterRule alwaysFalse = new AlwaysFalseFileFilterRule();

	@BeforeEach
	public void setUp() {
		root = new TestNode("root", null, true, true, 0, 0);
		testNode = new TestNode("foobar.txt", root, true, false, 0, 0);
	}

	@Test
	public void testEmptyFilterMatchAll() {
		FileFilter filter = new FileFilter(FileFilter.MATCH_ALL, FileFilter.INCLUDE, true);
		assertTrue(filter.match(testNode));
	}

	@Test
	public void testEmptyFilterMatchAny() {
		FileFilter filter = new FileFilter(FileFilter.MATCH_ANY, FileFilter.INCLUDE, true);
		assertTrue(filter.match(testNode));
	}

	@Test
	public void testOneRuleFilterMatchAll() {
		FileFilter filter = new FileFilter(FileFilter.MATCH_ALL, FileFilter.INCLUDE, true, alwaysTrue);
		assertTrue(filter.match(testNode));
	}

	@Test
	public void testOneRuleFilterMatchAny() {
		FileFilter filter = new FileFilter(FileFilter.MATCH_ANY, FileFilter.INCLUDE, true, alwaysTrue);
		assertTrue(filter.match(testNode));
	}

	@Test
	public void testOneRuleFilterMatchAnyNegative() {
		FileFilter filter = new FileFilter(FileFilter.MATCH_ANY, FileFilter.INCLUDE, true, alwaysFalse);
		assertFalse(filter.match(testNode));
	}

	@Test
	public void testOneRuleFilterAppliesToDirMatchAll() {
		FileFilter filter = new FileFilter(FileFilter.MATCH_ALL, FileFilter.INCLUDE, false, alwaysTrue);
		assertTrue(filter.match(root));
	}

	@Test
	public void testOneRuleFilterAppliesToDirMatchAny() {
		FileFilter filter = new FileFilter(FileFilter.MATCH_ANY, FileFilter.INCLUDE, false, alwaysTrue);
		assertTrue(filter.match(root));
	}

	@Test
	public void testFilterAllBasicTrueTrue() {
		FileFilter filter = new FileFilter(FileFilter.MATCH_ALL, FileFilter.INCLUDE, true, alwaysTrue, alwaysTrue);
		assertTrue(filter.match(testNode));
	}

	@Test
	public void testFilterAllBasicFalseTrue() {
		FileFilter filter = new FileFilter(FileFilter.MATCH_ALL, FileFilter.INCLUDE, true, alwaysFalse, alwaysTrue);
		assertFalse(filter.match(testNode));
	}

	@Test
	public void testFilterAllBasicTrueFalse() {
		FileFilter filter = new FileFilter(FileFilter.MATCH_ALL, FileFilter.INCLUDE, true, alwaysTrue, alwaysFalse);
		assertFalse(filter.match(testNode));
	}

	@Test
	public void testFilterAllBasicFalseFalse() {
		FileFilter filter = new FileFilter(FileFilter.MATCH_ALL, FileFilter.INCLUDE, true, alwaysFalse, alwaysFalse);
		assertFalse(filter.match(testNode));
	}

	@Test
	public void testFilterAnyBasicTrueTrue() {
		FileFilter filter = new FileFilter(FileFilter.MATCH_ANY, FileFilter.INCLUDE, true, alwaysTrue, alwaysTrue);
		assertTrue(filter.match(testNode));
	}

	@Test
	public void testFilterAnyBasicFalseTrue() {
		FileFilter filter = new FileFilter(FileFilter.MATCH_ANY, FileFilter.INCLUDE, true, alwaysFalse, alwaysTrue);
		assertTrue(filter.match(testNode));
	}

	@Test
	public void testFilterAnyBasicTrueFalse() {
		FileFilter filter = new FileFilter(FileFilter.MATCH_ANY, FileFilter.INCLUDE, true, alwaysTrue, alwaysFalse);
		assertTrue(filter.match(testNode));
	}

	@Test
	public void testFilterAnyBasicFalseFalse() {
		FileFilter filter = new FileFilter(FileFilter.MATCH_ANY, FileFilter.INCLUDE, true, alwaysFalse, alwaysFalse);
		assertFalse(filter.match(testNode));
	}

	@Test
	public void testFilterInclude() throws Exception {
		FileFilterRule nameEndsWithDotTxt = new FileNameFileFilterRule(new TextValue(".txt"), FileNameFileFilterRule.OP_ENDS_WITH);
		FileFilterRule sizeIsLessThan1k = new FileSizeFileFilterRule(new SizeValue("1024 Bytes"), FileSizeFileFilterRule.OP_IS_LESS_THAN);
		FileFilter filter = new FileFilter(FileFilter.MATCH_ANY, FileFilter.INCLUDE, true, nameEndsWithDotTxt, sizeIsLessThan1k);

		assertTrue(filter.match(new TestNode("foobar.txt", root, true, false, 0, 0)));
		assertTrue(filter.match(new TestNode("foobar.txt.", root, true, false, 0, 0)));
		assertFalse(filter.match(new TestNode("foobar.txt.", root, true, false, 2048, 0)));
	}

	@Test
	public void testFilterExclude() throws Exception {
		FileFilterRule nameEndsWithDotTxt = new FileNameFileFilterRule(new TextValue(".txt"), FileNameFileFilterRule.OP_ENDS_WITH);
		FileFilterRule sizeIsLessThan1k = new FileSizeFileFilterRule(new SizeValue("1024 Bytes"), FileSizeFileFilterRule.OP_IS_LESS_THAN);
		FileFilter filter = new FileFilter(FileFilter.MATCH_ANY, FileFilter.EXCLUDE, true, nameEndsWithDotTxt, sizeIsLessThan1k);

		assertFalse(filter.match(new TestNode("foobar.txt", root, true, false, 0, 0)));
		assertFalse(filter.match(new TestNode("foobar.txt.", root, true, false, 0, 0)));
		assertTrue(filter.match(new TestNode("foobar.txt.", root, true, false, 2048, 0)));
	}
}

class AlwaysTrueFileFilterRule implements FileFilterRule {
	@Override
	public boolean match(FSFile file) {
		return true;
	}

	@Override
	public String toString() {
		return "TRUE";
	}

	@Override
	public int getOperator() {
		return 0;
	}

	@Override
	public String getOperatorName() {
		return null;
	}

	@Override
	public String getRuleType() {
		return "True";
	}

	@Override
	public OperandValue getValue() {
		return null;
	}
}

class AlwaysFalseFileFilterRule implements FileFilterRule {
	@Override
	public boolean match(FSFile file) {
		return false;
	}

	@Override
	public String toString() {
		return "FALSE";
	}

	@Override
	public int getOperator() {
		return 0;
	}

	@Override
	public String getOperatorName() {
		return null;
	}

	@Override
	public String getRuleType() {
		return "False";
	}

	@Override
	public OperandValue getValue() {
		return null;
	}
}
