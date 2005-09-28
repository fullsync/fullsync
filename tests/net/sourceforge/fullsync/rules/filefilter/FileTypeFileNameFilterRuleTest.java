/*
 * Created on Jun 1, 2005
 */
package net.sourceforge.fullsync.rules.filefilter;

import junit.framework.TestCase;
import net.sourceforge.fullsync.rules.filefilter.values.TypeValue;

/**
 * @author Michele Aiello
 */
public class FileTypeFileNameFilterRuleTest extends TestCase {
	
	public void testWrongType() {
		FileTypeFileFilterRule filterRule = new FileTypeFileFilterRule(new TypeValue(5), FileTypeFileFilterRule.OP_IS);
		assertFalse(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, System.currentTimeMillis())));
		assertFalse(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, true, 1024, System.currentTimeMillis())));
	}
	
	public void testIsFile() {
		FileTypeFileFilterRule filterRule = new FileTypeFileFilterRule(new TypeValue(TypeValue.FILE_TYPE), 
				FileTypeFileFilterRule.OP_IS);
		assertTrue(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, System.currentTimeMillis())));
		assertFalse(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, true, 1024, System.currentTimeMillis())));
		
		filterRule = new FileTypeFileFilterRule(new TypeValue(TypeValue.FILE_TYPE), FileTypeFileFilterRule.OP_IS);
		assertTrue(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, System.currentTimeMillis())));
		assertFalse(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, true, 1024, System.currentTimeMillis())));
	}

	public void testIsntFile() {
		FileTypeFileFilterRule filterRule = new FileTypeFileFilterRule(new TypeValue(TypeValue.FILE_TYPE), FileTypeFileFilterRule.OP_ISNT);
		assertFalse(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, System.currentTimeMillis())));
		assertTrue(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, true, 1024, System.currentTimeMillis())));
		
		filterRule = new FileTypeFileFilterRule(new TypeValue(TypeValue.FILE_TYPE), FileTypeFileFilterRule.OP_ISNT);
		assertFalse(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, System.currentTimeMillis())));
		assertTrue(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, true, 1024, System.currentTimeMillis())));
	}

	public void testIsDirectory() {
		FileTypeFileFilterRule filterRule = new FileTypeFileFilterRule(new TypeValue(TypeValue.DIRECTORY_TYPE), FileTypeFileFilterRule.OP_IS);
		assertFalse(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, System.currentTimeMillis())));
		assertTrue(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, true, 1024, System.currentTimeMillis())));
		
		filterRule = new FileTypeFileFilterRule(new TypeValue(TypeValue.DIRECTORY_TYPE), FileTypeFileFilterRule.OP_IS);
		assertFalse(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, System.currentTimeMillis())));
		assertTrue(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, true, 1024, System.currentTimeMillis())));
	}

	public void testIsntDirectory() {
		FileTypeFileFilterRule filterRule = new FileTypeFileFilterRule(new TypeValue(TypeValue.DIRECTORY_TYPE), FileTypeFileFilterRule.OP_ISNT);
		assertTrue(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, System.currentTimeMillis())));
		assertFalse(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, true, 1024, System.currentTimeMillis())));
		
		filterRule = new FileTypeFileFilterRule(new TypeValue(TypeValue.DIRECTORY_TYPE), FileTypeFileFilterRule.OP_ISNT);
		assertTrue(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, System.currentTimeMillis())));
		assertFalse(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, true, 1024, System.currentTimeMillis())));
	}

}
