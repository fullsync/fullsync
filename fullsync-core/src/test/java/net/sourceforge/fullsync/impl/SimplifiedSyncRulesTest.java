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
package net.sourceforge.fullsync.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.sourceforge.fullsync.FSFile;
import net.sourceforge.fullsync.State;
import net.sourceforge.fullsync.rules.filefilter.FileFilter;
import net.sourceforge.fullsync.rules.filefilter.FileFilterRule;
import net.sourceforge.fullsync.rules.filefilter.TestNode;
import net.sourceforge.fullsync.rules.filefilter.values.OperandValue;

public class SimplifiedSyncRulesTest {
	private static final FileFilterRule ALWAYS_TRUE = new FileFilterRule() {
		@Override
		public boolean match(FSFile file) {
			return true;
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
	};

	private TestNode root;
	private SimplifiedSyncRules rules;

	@BeforeEach
	public void setUp() {
		root = TestNode.root();
		rules = new SimplifiedSyncRules();
	}

	@Test
	public void compareFilesSourceNewer() {
		var src = root.createChildNode("a", true, false, 100, 2000);
		var dst = root.createChildNode("a", true, false, 100, 1000);
		assertEquals(State.FILE_CHANGE_SOURCE, rules.compareFiles(src, dst));
	}

	@Test
	public void compareFilesDestinationNewer() {
		var src = root.createChildNode("a", true, false, 100, 1000);
		var dst = root.createChildNode("a", true, false, 100, 2000);
		assertEquals(State.FILE_CHANGE_DESTINATION, rules.compareFiles(src, dst));
	}

	@Test
	public void compareFilesSameSecondDifferentSizeIsUnknown() {
		var src = root.createChildNode("a", true, false, 100, 1000);
		var dst = root.createChildNode("a", true, false, 200, 1000);
		assertEquals(State.FILE_CHANGE_UNKNOWN, rules.compareFiles(src, dst));
	}

	@Test
	public void compareFilesSameSecondSameSizeIsInSync() {
		var src = root.createChildNode("a", true, false, 100, 1000);
		var dst = root.createChildNode("a", true, false, 100, 1000);
		assertEquals(State.IN_SYNC, rules.compareFiles(src, dst));
	}

	@Test
	public void compareFilesWithinSameFloorSecondIsInSync() {
		// 1001ms and 1999ms both floor to 1 second
		var src = root.createChildNode("a", true, false, 100, 1001);
		var dst = root.createChildNode("a", true, false, 100, 1999);
		assertEquals(State.IN_SYNC, rules.compareFiles(src, dst));
	}

	@Test
	public void isNodeIgnoredWithUseFilterFalseAlwaysReturnsFalse() {
		rules.setUseFilter(false);
		var node = root.createChildNode("x", true, false, 0, 0);
		assertFalse(rules.isNodeIgnored(node));
	}

	@Test
	public void isNodeIgnoredWithNoFilterSetReturnsFalse() {
		rules.setUseFilter(true);
		rules.setFileFilter(null);
		var node = root.createChildNode("x", true, false, 0, 0);
		assertFalse(rules.isNodeIgnored(node));
	}

	@Test
	public void isNodeIgnoredWithIncludeFilterReturnsFalse() {
		rules.setUseFilter(true);
		rules.setFileFilter(new FileFilter(FileFilter.MATCH_ALL, FileFilter.INCLUDE, true, ALWAYS_TRUE));
		var node = root.createChildNode("x", true, false, 0, 0);
		assertFalse(rules.isNodeIgnored(node));
	}

	@Test
	public void isNodeIgnoredWithExcludeFilterReturnsTrue() {
		rules.setUseFilter(true);
		// EXCLUDE filter with matching rule: match() returns false → take=false → ignored
		rules.setFileFilter(new FileFilter(FileFilter.MATCH_ALL, FileFilter.EXCLUDE, true, ALWAYS_TRUE));
		var node = root.createChildNode("x", true, false, 0, 0);
		assertTrue(rules.isNodeIgnored(node));
	}

	@Test
	public void isUsingRecursionDefaultsToTrue() {
		assertTrue(rules.isUsingRecursion());
	}

	@Test
	public void setUsingRecursionUpdatesFlag() {
		rules.setUsingRecursion(false);
		assertFalse(rules.isUsingRecursion());
	}

	@Test
	public void createChildReturnsSelf() {
		var src = root.createChildNode("a", true, false, 0, 0);
		var dst = root.createChildNode("a", true, false, 0, 0);
		assertSame(rules, rules.createChild(src, dst));
	}
}
