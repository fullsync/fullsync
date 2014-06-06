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

import static org.junit.Assert.assertTrue;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.rules.filefilter.values.TextValue;

import org.junit.Test;

public class FileNameFileFilterRuleTest {
	private File root = new TestNode("root", null, true, true, 0, 0);

	@Test
	public void testOpIs() {
		FileNameFileFilterRule filterRule = new FileNameFileFilterRule(new TextValue("foobar.txt"), FileNameFileFilterRule.OP_IS);
		assertTrue(filterRule.match(new TestNode("foobar.txt", root, true, false, 0, 0)));
		assertTrue(!filterRule.match(new TestNode("afoobar.txt", root, true, false, 0, 0)));
		assertTrue(!filterRule.match(new TestNode("foobar.txta", root, true, false, 0, 0)));
		assertTrue(!filterRule.match(new TestNode("foobara.txt", root, true, false, 0, 0)));
		assertTrue(!filterRule.match(new TestNode("fooba.txt", root, true, false, 0, 0)));
	}

	@Test
	public void testOpIsnt() {
		FileNameFileFilterRule filterRule = new FileNameFileFilterRule(new TextValue("foobar.txt"), FileNameFileFilterRule.OP_ISNT);
		assertTrue(!filterRule.match(new TestNode("foobar.txt", root, true, false, 0, 0)));
		assertTrue(filterRule.match(new TestNode("afoobar.txt", root, true, false, 0, 0)));
		assertTrue(filterRule.match(new TestNode("foobar.txta", root, true, false, 0, 0)));
		assertTrue(filterRule.match(new TestNode("foobara.txt", root, true, false, 0, 0)));
		assertTrue(filterRule.match(new TestNode("fooba.txt", root, true, false, 0, 0)));
	}

	@Test
	public void testOpContains() {
		FileNameFileFilterRule filterRule = new FileNameFileFilterRule(new TextValue("bar"), FileNameFileFilterRule.OP_CONTAINS);
		assertTrue(filterRule.match(new TestNode("foobar.txt", root, true, false, 0, 0)));
		assertTrue(filterRule.match(new TestNode("afoobar.txt", root, true, false, 0, 0)));
		assertTrue(filterRule.match(new TestNode("foobar.txta", root, true, false, 0, 0)));
		assertTrue(filterRule.match(new TestNode("foobara.txt", root, true, false, 0, 0)));
		assertTrue(!filterRule.match(new TestNode("fooba.txt", root, true, false, 0, 0)));
		assertTrue(!filterRule.match(new TestNode("foobsasr.txt", root, true, false, 0, 0)));
		assertTrue(!filterRule.match(new TestNode("foobasr.txt", root, true, false, 0, 0)));
		assertTrue(!filterRule.match(new TestNode("foorab.txt", root, true, false, 0, 0)));
	}

	@Test
	public void testOpDoesntContains() {
		FileNameFileFilterRule filterRule = new FileNameFileFilterRule(new TextValue("bar"), FileNameFileFilterRule.OP_DOESNT_CONTAINS);
		assertTrue(!filterRule.match(new TestNode("foobar.txt", root, true, false, 0, 0)));
		assertTrue(!filterRule.match(new TestNode("afoobar.txt", root, true, false, 0, 0)));
		assertTrue(!filterRule.match(new TestNode("foobar.txta", root, true, false, 0, 0)));
		assertTrue(!filterRule.match(new TestNode("foobara.txt", root, true, false, 0, 0)));
		assertTrue(filterRule.match(new TestNode("fooba.txt", root, true, false, 0, 0)));
		assertTrue(filterRule.match(new TestNode("foobsasr.txt", root, true, false, 0, 0)));
		assertTrue(filterRule.match(new TestNode("foobasr.txt", root, true, false, 0, 0)));
		assertTrue(filterRule.match(new TestNode("foorab.txt", root, true, false, 0, 0)));
	}

	@Test
	public void testOpBeginsWith() {
		FileNameFileFilterRule filterRule = new FileNameFileFilterRule(new TextValue("foo"), FileNameFileFilterRule.OP_BEGINS_WITH);
		assertTrue(filterRule.match(new TestNode("foobar.txt", root, true, false, 0, 0)));
		assertTrue(!filterRule.match(new TestNode("afoobar.txt", root, true, false, 0, 0)));
		assertTrue(filterRule.match(new TestNode("foobar.txta", root, true, false, 0, 0)));
		assertTrue(filterRule.match(new TestNode("foobara.txt", root, true, false, 0, 0)));
		assertTrue(filterRule.match(new TestNode("fooba.txt", root, true, false, 0, 0)));
		assertTrue(!filterRule.match(new TestNode("foboar.txt", root, true, false, 0, 0)));
		assertTrue(!filterRule.match(new TestNode("oofbar.txt", root, true, false, 0, 0)));
		assertTrue(filterRule.match(new TestNode("foo", root, true, false, 0, 0)));
		assertTrue(filterRule.match(new TestNode("foo.", root, true, false, 0, 0)));
		assertTrue(filterRule.match(new TestNode("foo.txt", root, true, false, 0, 0)));
	}

	@Test
	public void testOpBeginsWith2() {
		FileNameFileFilterRule filterRule = new FileNameFileFilterRule(new TextValue(".foo"), FileNameFileFilterRule.OP_BEGINS_WITH);
		assertTrue(filterRule.match(new TestNode(".foobar.txt", root, true, false, 0, 0)));
		assertTrue(!filterRule.match(new TestNode("a.foobar.txt", root, true, false, 0, 0)));
		assertTrue(filterRule.match(new TestNode(".foobar.txta", root, true, false, 0, 0)));
		assertTrue(filterRule.match(new TestNode(".foobara.txt", root, true, false, 0, 0)));
		assertTrue(filterRule.match(new TestNode(".fooba.txt", root, true, false, 0, 0)));
		assertTrue(!filterRule.match(new TestNode(".foboar.txt", root, true, false, 0, 0)));
		assertTrue(!filterRule.match(new TestNode(".oofbar.txt", root, true, false, 0, 0)));
		assertTrue(filterRule.match(new TestNode(".foo", root, true, false, 0, 0)));
		assertTrue(filterRule.match(new TestNode(".foo.txt", root, true, false, 0, 0)));
	}

	@Test
	public void testOpDoesntBeginsWith() {
		FileNameFileFilterRule filterRule = new FileNameFileFilterRule(new TextValue("foo"), FileNameFileFilterRule.OP_DOESNT_BEGINS_WITH);
		assertTrue(!filterRule.match(new TestNode("foobar.txt", root, true, false, 0, 0)));
		assertTrue(filterRule.match(new TestNode("afoobar.txt", root, true, false, 0, 0)));
		assertTrue(!filterRule.match(new TestNode("foobar.txta", root, true, false, 0, 0)));
		assertTrue(!filterRule.match(new TestNode("foobara.txt", root, true, false, 0, 0)));
		assertTrue(!filterRule.match(new TestNode("fooba.txt", root, true, false, 0, 0)));
		assertTrue(filterRule.match(new TestNode("foboar.txt", root, true, false, 0, 0)));
		assertTrue(filterRule.match(new TestNode("oofbar.txt", root, true, false, 0, 0)));
		assertTrue(!filterRule.match(new TestNode("foo", root, true, false, 0, 0)));
		assertTrue(!filterRule.match(new TestNode("foo.", root, true, false, 0, 0)));
		assertTrue(!filterRule.match(new TestNode("foo.txt", root, true, false, 0, 0)));
	}

	@Test
	public void testOpEndsWith() {
		FileNameFileFilterRule filterRule = new FileNameFileFilterRule(new TextValue("txt"), FileNameFileFilterRule.OP_ENDS_WITH);
		assertTrue(filterRule.match(new TestNode("foobar.txt", root, true, false, 0, 0)));
		assertTrue(filterRule.match(new TestNode("afoobar.atxt", root, true, false, 0, 0)));
		assertTrue(!filterRule.match(new TestNode("foobar.txta", root, true, false, 0, 0)));
		assertTrue(filterRule.match(new TestNode("foobara.txt", root, true, false, 0, 0)));
		assertTrue(filterRule.match(new TestNode("fooba.txt", root, true, false, 0, 0)));
		assertTrue(filterRule.match(new TestNode("foboar.ttxt", root, true, false, 0, 0)));
		assertTrue(!filterRule.match(new TestNode("oofbar.xt", root, true, false, 0, 0)));
		assertTrue(!filterRule.match(new TestNode("foo", root, true, false, 0, 0)));
		assertTrue(filterRule.match(new TestNode("txt", root, true, false, 0, 0)));
		assertTrue(filterRule.match(new TestNode(".txt", root, true, false, 0, 0)));
		assertTrue(!filterRule.match(new TestNode("txt.", root, true, false, 0, 0)));
	}

	@Test
	public void testOpEndsWith2() {
		FileNameFileFilterRule filterRule = new FileNameFileFilterRule(new TextValue(".txt"), FileNameFileFilterRule.OP_ENDS_WITH);
		assertTrue(filterRule.match(new TestNode("foobar.txt", root, true, false, 0, 0)));
		assertTrue(!filterRule.match(new TestNode("afoobar.atxt", root, true, false, 0, 0)));
		assertTrue(!filterRule.match(new TestNode("foobar.txta", root, true, false, 0, 0)));
		assertTrue(filterRule.match(new TestNode("foobara.txt", root, true, false, 0, 0)));
		assertTrue(filterRule.match(new TestNode("fooba.txt", root, true, false, 0, 0)));
		assertTrue(!filterRule.match(new TestNode("foboar.ttxt", root, true, false, 0, 0)));
		assertTrue(!filterRule.match(new TestNode("oofbar.xt", root, true, false, 0, 0)));
		assertTrue(!filterRule.match(new TestNode("foo", root, true, false, 0, 0)));
		assertTrue(!filterRule.match(new TestNode("txt", root, true, false, 0, 0)));
		assertTrue(filterRule.match(new TestNode(".txt", root, true, false, 0, 0)));
		assertTrue(!filterRule.match(new TestNode("txt.", root, true, false, 0, 0)));
	}

	@Test
	public void testOpDoesntEndsWith() {
		FileNameFileFilterRule filterRule = new FileNameFileFilterRule(new TextValue("txt"), FileNameFileFilterRule.OP_DOESNT_ENDS_WITH);
		assertTrue(!filterRule.match(new TestNode("foobar.txt", root, true, false, 0, 0)));
		assertTrue(!filterRule.match(new TestNode("afoobar.atxt", root, true, false, 0, 0)));
		assertTrue(filterRule.match(new TestNode("foobar.txta", root, true, false, 0, 0)));
		assertTrue(!filterRule.match(new TestNode("foobara.txt", root, true, false, 0, 0)));
		assertTrue(!filterRule.match(new TestNode("fooba.txt", root, true, false, 0, 0)));
		assertTrue(!filterRule.match(new TestNode("foboar.ttxt", root, true, false, 0, 0)));
		assertTrue(filterRule.match(new TestNode("oofbar.xt", root, true, false, 0, 0)));
		assertTrue(filterRule.match(new TestNode("foo", root, true, false, 0, 0)));
		assertTrue(!filterRule.match(new TestNode("txt", root, true, false, 0, 0)));
		assertTrue(!filterRule.match(new TestNode(".txt", root, true, false, 0, 0)));
		assertTrue(filterRule.match(new TestNode("txt.", root, true, false, 0, 0)));
	}

	@Test
	public void testOpRegExp() {
		FileNameFileFilterRule filterRule = new FileNameFileFilterRule(new TextValue(".+\\.gif"), FileNameFileFilterRule.OP_MATCHES_REGEXP);
		assertTrue(filterRule.match(new TestNode("foobar.gif", root, true, false, 0, 0)));
		assertTrue(filterRule.match(new TestNode("foobara.gif", root, true, false, 0, 0)));
		assertTrue(!filterRule.match(new TestNode("gif", root, true, false, 0, 0)));
		assertTrue(!filterRule.match(new TestNode("foobar.jpg", root, true, false, 0, 0)));
	}
}
