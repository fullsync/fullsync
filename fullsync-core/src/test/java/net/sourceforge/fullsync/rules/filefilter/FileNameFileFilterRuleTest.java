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

import org.junit.jupiter.api.Test;

import net.sourceforge.fullsync.FSFile;
import net.sourceforge.fullsync.rules.filefilter.values.TextValue;

public class FileNameFileFilterRuleTest {
	private FSFile root = new TestNode("root", null, true, true, 0, 0);

	private FSFile createTestNode(String name) {
		return new TestNode(name, root, true, false, 0, 0);
	}

	@Test
	public void testOpIs() throws Exception {
		FileNameFileFilterRule filterRule = new FileNameFileFilterRule(new TextValue("foobar.txt"), FileNameFileFilterRule.OP_IS);
		assertTrue(filterRule.match(createTestNode("foobar.txt")));
		assertFalse(filterRule.match(createTestNode("afoobar.txt")));
		assertFalse(filterRule.match(createTestNode("foobar.txta")));
		assertFalse(filterRule.match(createTestNode("foobara.txt")));
		assertFalse(filterRule.match(createTestNode("fooba.txt")));
	}

	@Test
	public void testOpIsnt() throws Exception {
		FileNameFileFilterRule filterRule = new FileNameFileFilterRule(new TextValue("foobar.txt"), FileNameFileFilterRule.OP_ISNT);
		assertFalse(filterRule.match(createTestNode("foobar.txt")));
		assertTrue(filterRule.match(createTestNode("afoobar.txt")));
		assertTrue(filterRule.match(createTestNode("foobar.txta")));
		assertTrue(filterRule.match(createTestNode("foobara.txt")));
		assertTrue(filterRule.match(createTestNode("fooba.txt")));
	}

	@Test
	public void testOpContains() throws Exception {
		FileNameFileFilterRule filterRule = new FileNameFileFilterRule(new TextValue("bar"), FileNameFileFilterRule.OP_CONTAINS);
		assertTrue(filterRule.match(createTestNode("foobar.txt")));
		assertTrue(filterRule.match(createTestNode("afoobar.txt")));
		assertTrue(filterRule.match(createTestNode("foobar.txta")));
		assertTrue(filterRule.match(createTestNode("foobara.txt")));
		assertFalse(filterRule.match(createTestNode("fooba.txt")));
		assertFalse(filterRule.match(createTestNode("foobsasr.txt")));
		assertFalse(filterRule.match(createTestNode("foobasr.txt")));
		assertFalse(filterRule.match(createTestNode("foorab.txt")));
	}

	@Test
	public void testOpDoesntContains() throws Exception {
		FileNameFileFilterRule filterRule = new FileNameFileFilterRule(new TextValue("bar"), FileNameFileFilterRule.OP_DOESNT_CONTAINS);
		assertFalse(filterRule.match(createTestNode("foobar.txt")));
		assertFalse(filterRule.match(createTestNode("afoobar.txt")));
		assertFalse(filterRule.match(createTestNode("foobar.txta")));
		assertFalse(filterRule.match(createTestNode("foobara.txt")));
		assertTrue(filterRule.match(createTestNode("fooba.txt")));
		assertTrue(filterRule.match(createTestNode("foobsasr.txt")));
		assertTrue(filterRule.match(createTestNode("foobasr.txt")));
		assertTrue(filterRule.match(createTestNode("foorab.txt")));
	}

	@Test
	public void testOpBeginsWith() throws Exception {
		FileNameFileFilterRule filterRule = new FileNameFileFilterRule(new TextValue("foo"), FileNameFileFilterRule.OP_BEGINS_WITH);
		assertTrue(filterRule.match(createTestNode("foobar.txt")));
		assertFalse(filterRule.match(createTestNode("afoobar.txt")));
		assertTrue(filterRule.match(createTestNode("foobar.txta")));
		assertTrue(filterRule.match(createTestNode("foobara.txt")));
		assertTrue(filterRule.match(createTestNode("fooba.txt")));
		assertFalse(filterRule.match(createTestNode("foboar.txt")));
		assertFalse(filterRule.match(createTestNode("oofbar.txt")));
		assertTrue(filterRule.match(createTestNode("foo")));
		assertTrue(filterRule.match(createTestNode("foo.")));
		assertTrue(filterRule.match(createTestNode("foo.txt")));
	}

	@Test
	public void testOpBeginsWith2() throws Exception {
		FileNameFileFilterRule filterRule = new FileNameFileFilterRule(new TextValue(".foo"), FileNameFileFilterRule.OP_BEGINS_WITH);
		assertTrue(filterRule.match(createTestNode(".foobar.txt")));
		assertFalse(filterRule.match(createTestNode("a.foobar.txt")));
		assertTrue(filterRule.match(createTestNode(".foobar.txta")));
		assertTrue(filterRule.match(createTestNode(".foobara.txt")));
		assertTrue(filterRule.match(createTestNode(".fooba.txt")));
		assertFalse(filterRule.match(createTestNode(".foboar.txt")));
		assertFalse(filterRule.match(createTestNode(".oofbar.txt")));
		assertTrue(filterRule.match(createTestNode(".foo")));
		assertTrue(filterRule.match(createTestNode(".foo.txt")));
	}

	@Test
	public void testOpDoesntBeginsWith() throws Exception {
		FileNameFileFilterRule filterRule = new FileNameFileFilterRule(new TextValue("foo"), FileNameFileFilterRule.OP_DOESNT_BEGINS_WITH);
		assertFalse(filterRule.match(createTestNode("foobar.txt")));
		assertTrue(filterRule.match(createTestNode("afoobar.txt")));
		assertFalse(filterRule.match(createTestNode("foobar.txta")));
		assertFalse(filterRule.match(createTestNode("foobara.txt")));
		assertFalse(filterRule.match(createTestNode("fooba.txt")));
		assertTrue(filterRule.match(createTestNode("foboar.txt")));
		assertTrue(filterRule.match(createTestNode("oofbar.txt")));
		assertFalse(filterRule.match(createTestNode("foo")));
		assertFalse(filterRule.match(createTestNode("foo.")));
		assertFalse(filterRule.match(createTestNode("foo.txt")));
	}

	@Test
	public void testOpEndsWith() throws Exception {
		FileNameFileFilterRule filterRule = new FileNameFileFilterRule(new TextValue("txt"), FileNameFileFilterRule.OP_ENDS_WITH);
		assertTrue(filterRule.match(createTestNode("foobar.txt")));
		assertTrue(filterRule.match(createTestNode("afoobar.atxt")));
		assertFalse(filterRule.match(createTestNode("foobar.txta")));
		assertTrue(filterRule.match(createTestNode("foobara.txt")));
		assertTrue(filterRule.match(createTestNode("fooba.txt")));
		assertTrue(filterRule.match(createTestNode("foboar.ttxt")));
		assertFalse(filterRule.match(createTestNode("oofbar.xt")));
		assertFalse(filterRule.match(createTestNode("foo")));
		assertTrue(filterRule.match(createTestNode("txt")));
		assertTrue(filterRule.match(createTestNode(".txt")));
		assertFalse(filterRule.match(createTestNode("txt.")));
	}

	@Test
	public void testOpEndsWith2() throws Exception {
		FileNameFileFilterRule filterRule = new FileNameFileFilterRule(new TextValue(".txt"), FileNameFileFilterRule.OP_ENDS_WITH);
		assertTrue(filterRule.match(createTestNode("foobar.txt")));
		assertFalse(filterRule.match(createTestNode("afoobar.atxt")));
		assertFalse(filterRule.match(createTestNode("foobar.txta")));
		assertTrue(filterRule.match(createTestNode("foobara.txt")));
		assertTrue(filterRule.match(createTestNode("fooba.txt")));
		assertFalse(filterRule.match(createTestNode("foboar.ttxt")));
		assertFalse(filterRule.match(createTestNode("oofbar.xt")));
		assertFalse(filterRule.match(createTestNode("foo")));
		assertFalse(filterRule.match(createTestNode("txt")));
		assertTrue(filterRule.match(createTestNode(".txt")));
		assertFalse(filterRule.match(createTestNode("txt.")));
	}

	@Test
	public void testOpDoesntEndsWith() throws Exception {
		FileNameFileFilterRule filterRule = new FileNameFileFilterRule(new TextValue("txt"), FileNameFileFilterRule.OP_DOESNT_ENDS_WITH);
		assertFalse(filterRule.match(createTestNode("foobar.txt")));
		assertFalse(filterRule.match(createTestNode("afoobar.atxt")));
		assertTrue(filterRule.match(createTestNode("foobar.txta")));
		assertFalse(filterRule.match(createTestNode("foobara.txt")));
		assertFalse(filterRule.match(createTestNode("fooba.txt")));
		assertFalse(filterRule.match(createTestNode("foboar.ttxt")));
		assertTrue(filterRule.match(createTestNode("oofbar.xt")));
		assertTrue(filterRule.match(createTestNode("foo")));
		assertFalse(filterRule.match(createTestNode("txt")));
		assertFalse(filterRule.match(createTestNode(".txt")));
		assertTrue(filterRule.match(createTestNode("txt.")));
	}

	@Test
	public void testOpRegExp() throws Exception {
		FileNameFileFilterRule filterRule = new FileNameFileFilterRule(new TextValue(".+\\.gif"), FileNameFileFilterRule.OP_MATCHES_REGEXP);
		assertTrue(filterRule.match(createTestNode("foobar.gif")));
		assertTrue(filterRule.match(createTestNode("foobara.gif")));
		assertFalse(filterRule.match(createTestNode("gif")));
		assertFalse(filterRule.match(createTestNode("foobar.jpg")));
	}
}
