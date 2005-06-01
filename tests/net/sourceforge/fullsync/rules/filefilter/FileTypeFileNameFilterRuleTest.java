/*
 * Created on Jun 1, 2005
 */
package net.sourceforge.fullsync.rules.filefilter;

import junit.framework.TestCase;

/**
 * @author Michele Aiello
 */
public class FileTypeFileNameFilterRuleTest extends TestCase {
	
	public void testWrongType() {
		FileTypeFileFilterRule filterRule = new FileTypeFileFilterRule("textfile", FileTypeFileFilterRule.OP_IS);
		assertFalse(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, System.currentTimeMillis())));
		assertFalse(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, true, 1024, System.currentTimeMillis())));
	}
	
	public void testIsFile() {
		FileTypeFileFilterRule filterRule = new FileTypeFileFilterRule("file", FileTypeFileFilterRule.OP_IS);
		assertTrue(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, System.currentTimeMillis())));
		assertFalse(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, true, 1024, System.currentTimeMillis())));
		
		filterRule = new FileTypeFileFilterRule(FileTypeFileFilterRule.FILE_TYPE, FileTypeFileFilterRule.OP_IS);
		assertTrue(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, System.currentTimeMillis())));
		assertFalse(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, true, 1024, System.currentTimeMillis())));
	}

	public void testIsntFile() {
		FileTypeFileFilterRule filterRule = new FileTypeFileFilterRule("file", FileTypeFileFilterRule.OP_ISNT);
		assertFalse(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, System.currentTimeMillis())));
		assertTrue(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, true, 1024, System.currentTimeMillis())));
		
		filterRule = new FileTypeFileFilterRule(FileTypeFileFilterRule.FILE_TYPE, FileTypeFileFilterRule.OP_ISNT);
		assertFalse(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, System.currentTimeMillis())));
		assertTrue(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, true, 1024, System.currentTimeMillis())));
	}

	public void testIsDirectory() {
		FileTypeFileFilterRule filterRule = new FileTypeFileFilterRule("directory", FileTypeFileFilterRule.OP_IS);
		assertFalse(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, System.currentTimeMillis())));
		assertTrue(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, true, 1024, System.currentTimeMillis())));
		
		filterRule = new FileTypeFileFilterRule(FileTypeFileFilterRule.DIRECTORY_TYPE, FileTypeFileFilterRule.OP_IS);
		assertFalse(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, System.currentTimeMillis())));
		assertTrue(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, true, 1024, System.currentTimeMillis())));
	}

	public void testIsntDirectory() {
		FileTypeFileFilterRule filterRule = new FileTypeFileFilterRule("directory", FileTypeFileFilterRule.OP_ISNT);
		assertTrue(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, System.currentTimeMillis())));
		assertFalse(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, true, 1024, System.currentTimeMillis())));
		
		filterRule = new FileTypeFileFilterRule(FileTypeFileFilterRule.DIRECTORY_TYPE, FileTypeFileFilterRule.OP_ISNT);
		assertTrue(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, System.currentTimeMillis())));
		assertFalse(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, true, 1024, System.currentTimeMillis())));
	}

}
