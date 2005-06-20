/*
 * Created on May 29, 2005
 */
package net.sourceforge.fullsync.rules.filefilter;

import net.sourceforge.fullsync.fs.FileAttributes;
import net.sourceforge.fullsync.rules.filefilter.values.SizeValue;

import junit.framework.TestCase;

/**
 * @author Michele Aiello
 */
public class FileSizeFileFilterRuleTest extends TestCase {

	public void testOpIs() throws FilterRuleNotAppliableException {
		FileSizeFileFilterRule filterRule = new FileSizeFileFilterRule(new SizeValue(1000, SizeValue.BYTES), 
				FileSizeFileFilterRule.OP_IS);
		TestNode file = new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1000, 0);
		
		assertTrue(filterRule.match(file));

		file.setFileAttributes(new FileAttributes(2000, 0));
		assertTrue(!filterRule.match(file));
	}

	public void testOpIsnt() throws FilterRuleNotAppliableException {
		FileSizeFileFilterRule filterRule = new FileSizeFileFilterRule(new SizeValue(1000, SizeValue.BYTES),
				FileSizeFileFilterRule.OP_ISNT);
		TestNode file = new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1000, 0);
		assertTrue(!filterRule.match(file));
		
		file.setFileAttributes(new FileAttributes(2000, 0));
		assertTrue(filterRule.match(file));
	}

	public void testOpIsGreaterThan() throws FilterRuleNotAppliableException {
		FileSizeFileFilterRule filterRule = new FileSizeFileFilterRule(new SizeValue(1000, SizeValue.BYTES),
				FileSizeFileFilterRule.OP_IS_GREATER_THAN);
		TestNode file = new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1000, 0);

		assertTrue(!filterRule.match(file));
		
		file.setFileAttributes(new FileAttributes(2000, 0));
		assertTrue(filterRule.match(file));

		file.setFileAttributes(new FileAttributes(999, 0));
		assertTrue(!filterRule.match(file));
	}

	public void testOpIsLessThan() throws FilterRuleNotAppliableException {
		FileSizeFileFilterRule filterRule = new FileSizeFileFilterRule(new SizeValue(1000, SizeValue.BYTES),
				FileSizeFileFilterRule.OP_IS_LESS_THAN);
		TestNode file = new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1000, 0);

		assertTrue(!filterRule.match(file));
		
		file.setFileAttributes(new FileAttributes(2000, 0));
		assertTrue(!filterRule.match(file));

		file.setFileAttributes(new FileAttributes(999, 0));
		assertTrue(filterRule.match(file));
	}

}
