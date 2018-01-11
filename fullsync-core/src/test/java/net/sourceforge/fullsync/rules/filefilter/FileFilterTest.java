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
import net.sourceforge.fullsync.rules.filefilter.values.AgeValue;
import net.sourceforge.fullsync.rules.filefilter.values.OperandValue;
import net.sourceforge.fullsync.rules.filefilter.values.SizeValue;
import net.sourceforge.fullsync.rules.filefilter.values.TextValue;

public class FileFilterTest {
	private File root;
	private File testNode;

	@Before
	public void setUp() {
		root = new TestNode("root", null, true, true, 0, 0);
		testNode = new TestNode("foobar.txt", root, true, false, 0, 0);
	}

	@Test
	public void testEmptyFilter() {
		FileFilter filter = new FileFilter();
		filter.setMatchType(FileFilter.MATCH_ALL);
		assertTrue(filter.match(testNode));

		filter.setMatchType(FileFilter.MATCH_ANY);
		assertTrue(filter.match(testNode));
	}

	@Test
	public void testOneRuleFilter() {
		FileFilter filter = new FileFilter();
		filter.setMatchType(FileFilter.MATCH_ALL);
		filter.setFileFilterRules(new FileFilterRule[] { new AlwaysTrueFileFilterRule() });

		assertTrue(filter.match(testNode));

		filter.setMatchType(FileFilter.MATCH_ANY);
		assertTrue(filter.match(testNode));

		filter.setFileFilterRules(new FileFilterRule[] { new AlwaysFalseFileFilterRule() });

		assertFalse(filter.match(testNode));

		filter.setMatchType(FileFilter.MATCH_ANY);
		assertFalse(filter.match(testNode));
	}

	@Test
	public void testOneRuleFilterAppliesToDir() {
		FileFilter filter = new FileFilter();
		filter.setMatchType(FileFilter.MATCH_ALL);
		filter.setFileFilterRules(new FileFilterRule[] { new AlwaysTrueFileFilterRule() });
		filter.setAppliesToDirectories(false);

		assertTrue(filter.match(root));

		filter.setMatchType(FileFilter.MATCH_ANY);
		assertTrue(filter.match(root));
	}

	@Test
	public void throwFilterRuleNotAppliableExceptionAll() {
		FileFilter filter = new FileFilter();
		filter
			.setFileFilterRules(new FileFilterRule[]
			{ new FileAgeFileFilterRule(new AgeValue(1, AgeValue.Unit.SECONDS), FileAgeFileFilterRule.OP_IS) });
		filter.setMatchType(FileFilter.MATCH_ALL);

		testNode.setLastModified(-1);

		assertTrue(filter.match(testNode));
	}

	@Test
	public void throwFilterRuleNotAppliableExceptionAny() {
		FileFilter filter = new FileFilter();
		FileFilterRule[] rules = new FileFilterRule[] {
			new FileAgeFileFilterRule(new AgeValue(1, AgeValue.Unit.SECONDS), FileAgeFileFilterRule.OP_IS) };
		filter.setFileFilterRules(rules);
		filter.setMatchType(FileFilter.MATCH_ANY);

		testNode.setLastModified(-1);

		assertTrue(filter.match(testNode));
	}

	@Test
	public void testFilterAllBasic() {
		FileFilter filter = new FileFilter();
		filter.setMatchType(FileFilter.MATCH_ALL);

		filter.setFileFilterRules(new FileFilterRule[] { new AlwaysTrueFileFilterRule(), new AlwaysTrueFileFilterRule() });
		assertTrue(filter.match(testNode));

		filter.setFileFilterRules(new FileFilterRule[] { new AlwaysFalseFileFilterRule(), new AlwaysTrueFileFilterRule() });
		assertFalse(filter.match(testNode));

		filter.setFileFilterRules(new FileFilterRule[] { new AlwaysTrueFileFilterRule(), new AlwaysFalseFileFilterRule() });
		assertFalse(filter.match(testNode));

		filter.setFileFilterRules(new FileFilterRule[] { new AlwaysFalseFileFilterRule(), new AlwaysFalseFileFilterRule() });
		assertFalse(filter.match(testNode));
	}

	@Test
	public void testFilterAnyBasic() {
		FileFilter filter = new FileFilter();
		filter.setMatchType(FileFilter.MATCH_ANY);

		filter.setFileFilterRules(new FileFilterRule[] { new AlwaysTrueFileFilterRule(), new AlwaysTrueFileFilterRule() });
		assertTrue(filter.match(testNode));

		filter.setFileFilterRules(new FileFilterRule[] { new AlwaysFalseFileFilterRule(), new AlwaysTrueFileFilterRule() });
		assertTrue(filter.match(testNode));

		filter.setFileFilterRules(new FileFilterRule[] { new AlwaysTrueFileFilterRule(), new AlwaysFalseFileFilterRule() });
		assertTrue(filter.match(testNode));

		filter.setFileFilterRules(new FileFilterRule[] { new AlwaysFalseFileFilterRule(), new AlwaysFalseFileFilterRule() });
		assertFalse(filter.match(testNode));
	}

	@Test
	public void testFilterInclude() throws Exception {
		FileFilter filter = new FileFilter();
		filter.setMatchType(FileFilter.MATCH_ANY);
		filter.setFilterType(FileFilter.INCLUDE);
		FileFilterRule nameEndsWithDotTxt = new FileNameFileFilterRule(new TextValue(".txt"), FileNameFileFilterRule.OP_ENDS_WITH);
		FileFilterRule sizeIsLessThan1k = new FileSizeFileFilterRule(new SizeValue("1024 Bytes"), FileSizeFileFilterRule.OP_IS_LESS_THAN);
		filter.setFileFilterRules(new FileFilterRule[] { nameEndsWithDotTxt, sizeIsLessThan1k });

		assertTrue(filter.match(new TestNode("foobar.txt", root, true, false, 0, 0)));
		assertTrue(filter.match(new TestNode("foobar.txt.", root, true, false, 0, 0)));
		assertFalse(filter.match(new TestNode("foobar.txt.", root, true, false, 2048, 0)));
	}

	@Test
	public void testFilterExclude() throws Exception {
		FileFilter filter = new FileFilter();
		filter.setMatchType(FileFilter.MATCH_ANY);
		filter.setFilterType(FileFilter.EXCLUDE);
		FileFilterRule nameEndsWithDotTxt = new FileNameFileFilterRule(new TextValue(".txt"), FileNameFileFilterRule.OP_ENDS_WITH);
		FileFilterRule sizeIsLessThan1k = new FileSizeFileFilterRule(new SizeValue("1024 Bytes"), FileSizeFileFilterRule.OP_IS_LESS_THAN);
		filter.setFileFilterRules(new FileFilterRule[] { nameEndsWithDotTxt, sizeIsLessThan1k });

		assertFalse(filter.match(new TestNode("foobar.txt", root, true, false, 0, 0)));
		assertFalse(filter.match(new TestNode("foobar.txt.", root, true, false, 0, 0)));
		assertTrue(filter.match(new TestNode("foobar.txt.", root, true, false, 2048, 0)));
	}
}

class AlwaysTrueFileFilterRule extends FileFilterRule {
	@Override
	public boolean match(File file) {
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

class AlwaysFalseFileFilterRule extends FileFilterRule {
	@Override
	public boolean match(File file) {
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
