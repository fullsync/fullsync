/*
 * Created on May 28, 2005
 */
package net.sourceforge.fullsync.rules.filefilter;

import java.io.File;

import junit.framework.TestCase;

/**
 * @author Michele Aiello
 */
public class FileNameFileFilterRuleTest extends TestCase {

	public void testOpIs() {
		FileNameFileFilterRule filterRule = new FileNameFileFilterRule("foobar.txt", FileNameFileFilterRule.OP_IS);
		assertTrue(filterRule.match(new File("foobar.txt")));
		assertTrue(!filterRule.match(new File("afoobar.txt")));
		assertTrue(!filterRule.match(new File("foobar.txta")));
		assertTrue(!filterRule.match(new File("foobara.txt")));
		assertTrue(!filterRule.match(new File("fooba.txt")));
	}

	public void testOpIsnt() {
		FileNameFileFilterRule filterRule = new FileNameFileFilterRule("foobar.txt", FileNameFileFilterRule.OP_ISNT);
		assertTrue(!filterRule.match(new File("foobar.txt")));
		assertTrue(filterRule.match(new File("afoobar.txt")));
		assertTrue(filterRule.match(new File("foobar.txta")));
		assertTrue(filterRule.match(new File("foobara.txt")));
		assertTrue(filterRule.match(new File("fooba.txt")));
	}

	public void testOpContains() {
		FileNameFileFilterRule filterRule = new FileNameFileFilterRule("bar", FileNameFileFilterRule.OP_CONTAINS);
		assertTrue(filterRule.match(new File("foobar.txt")));
		assertTrue(filterRule.match(new File("afoobar.txt")));
		assertTrue(filterRule.match(new File("foobar.txta")));
		assertTrue(filterRule.match(new File("foobara.txt")));
		assertTrue(!filterRule.match(new File("fooba.txt")));
		assertTrue(!filterRule.match(new File("foobsasr.txt")));
		assertTrue(!filterRule.match(new File("foobasr.txt")));
		assertTrue(!filterRule.match(new File("foorab.txt")));
	}

	public void testOpDoesntContains() {
		FileNameFileFilterRule filterRule = new FileNameFileFilterRule("bar", FileNameFileFilterRule.OP_DOESNT_CONTAINS);
		assertTrue(!filterRule.match(new File("foobar.txt")));
		assertTrue(!filterRule.match(new File("afoobar.txt")));
		assertTrue(!filterRule.match(new File("foobar.txta")));
		assertTrue(!filterRule.match(new File("foobara.txt")));
		assertTrue(filterRule.match(new File("fooba.txt")));
		assertTrue(filterRule.match(new File("foobsasr.txt")));
		assertTrue(filterRule.match(new File("foobasr.txt")));
		assertTrue(filterRule.match(new File("foorab.txt")));
	}

	public void testOpBeginsWith() {
		FileNameFileFilterRule filterRule = new FileNameFileFilterRule("foo", FileNameFileFilterRule.OP_BEGINS_WITH);
		assertTrue(filterRule.match(new File("foobar.txt")));
		assertTrue(!filterRule.match(new File("afoobar.txt")));
		assertTrue(filterRule.match(new File("foobar.txta")));
		assertTrue(filterRule.match(new File("foobara.txt")));
		assertTrue(filterRule.match(new File("fooba.txt")));
		assertTrue(!filterRule.match(new File("foboar.txt")));
		assertTrue(!filterRule.match(new File("oofbar.txt")));
		assertTrue(filterRule.match(new File("foo")));
		assertTrue(filterRule.match(new File("foo.")));
		assertTrue(filterRule.match(new File("foo.txt")));
	}

	public void testOpBeginsWith2() {
		FileNameFileFilterRule filterRule = new FileNameFileFilterRule(".foo", FileNameFileFilterRule.OP_BEGINS_WITH);
		assertTrue(filterRule.match(new File(".foobar.txt")));
		assertTrue(!filterRule.match(new File("a.foobar.txt")));
		assertTrue(filterRule.match(new File(".foobar.txta")));
		assertTrue(filterRule.match(new File(".foobara.txt")));
		assertTrue(filterRule.match(new File(".fooba.txt")));
		assertTrue(!filterRule.match(new File(".foboar.txt")));
		assertTrue(!filterRule.match(new File(".oofbar.txt")));
		assertTrue(filterRule.match(new File(".foo")));
		assertTrue(filterRule.match(new File(".foo.txt")));
	}

	public void testOpEndsWith() {
		FileNameFileFilterRule filterRule = new FileNameFileFilterRule("txt", FileNameFileFilterRule.OP_ENDS_WITH);
		assertTrue(filterRule.match(new File("foobar.txt")));
		assertTrue(filterRule.match(new File("afoobar.atxt")));
		assertTrue(!filterRule.match(new File("foobar.txta")));
		assertTrue(filterRule.match(new File("foobara.txt")));
		assertTrue(filterRule.match(new File("fooba.txt")));
		assertTrue(filterRule.match(new File("foboar.ttxt")));
		assertTrue(!filterRule.match(new File("oofbar.xt")));
		assertTrue(!filterRule.match(new File("foo")));
		assertTrue(filterRule.match(new File("txt")));
		assertTrue(filterRule.match(new File(".txt")));
		assertTrue(!filterRule.match(new File("txt.")));
	}

	public void testOpEndsWith2() {
		FileNameFileFilterRule filterRule = new FileNameFileFilterRule(".txt", FileNameFileFilterRule.OP_ENDS_WITH);
		assertTrue(filterRule.match(new File("foobar.txt")));
		assertTrue(!filterRule.match(new File("afoobar.atxt")));
		assertTrue(!filterRule.match(new File("foobar.txta")));
		assertTrue(filterRule.match(new File("foobara.txt")));
		assertTrue(filterRule.match(new File("fooba.txt")));
		assertTrue(!filterRule.match(new File("foboar.ttxt")));
		assertTrue(!filterRule.match(new File("oofbar.xt")));
		assertTrue(!filterRule.match(new File("foo")));
		assertTrue(!filterRule.match(new File("txt")));
		assertTrue(filterRule.match(new File(".txt")));
		assertTrue(!filterRule.match(new File("txt.")));
	}

	public void testOpRegExp() {
		FileNameFileFilterRule filterRule = new FileNameFileFilterRule(".+\\.gif", FileNameFileFilterRule.OP_REGEXP);
		assertTrue(filterRule.match(new File("foobar.gif")));
		assertTrue(filterRule.match(new File("foobara.gif")));
		assertTrue(!filterRule.match(new File("gif")));
		assertTrue(!filterRule.match(new File("foobar.jpg")));
	}
}
