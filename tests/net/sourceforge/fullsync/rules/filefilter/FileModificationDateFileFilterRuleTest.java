/*
 * Created on May 29, 2005
 */
package net.sourceforge.fullsync.rules.filefilter;

import net.sourceforge.fullsync.SystemDate;
import net.sourceforge.fullsync.fs.FileAttributes;

import junit.framework.TestCase;

/**
 * @author Michele Aiello
 */
public class FileModificationDateFileFilterRuleTest extends TestCase {

	public void testOpIs() {
		long now = SystemDate.getInstance().currentTimeMillis();
		FileModificationDateFileFilterRule filterRule = new FileModificationDateFileFilterRule(now, FileModificationDateFileFilterRule.OP_IS);
		TestNode file = new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1000, now);
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		assertTrue(filterRule.match(file));
		file.setFileAttributes(new FileAttributes(1000, SystemDate.getInstance().currentTimeMillis()));

		assertTrue(!filterRule.match(file));
	}
	
}
