/*
 * Created on May 29, 2005
 */
package net.sourceforge.fullsync.rules.filefilter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import junit.framework.TestCase;

/**
 * @author Michele Aiello
 */
public class FileModificationDateFileFilterRuleTest extends TestCase {

	public void testOpIs() throws IOException {
		long now = System.currentTimeMillis();
		FileModificationDateFileFilterRule filterRule = new FileModificationDateFileFilterRule(now, FileModificationDateFileFilterRule.OP_IS);
		File file = new File("foobar.txt");
		FileWriter writer = new FileWriter(file, false);
		char[] buff = new char[1000];
		writer.write(buff);
		writer.flush();
		file.setLastModified(now);
		
		assertTrue(filterRule.match(file));
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		
		writer.write(buff);
		writer.flush();
		file.setLastModified(System.currentTimeMillis());

		assertTrue(!filterRule.match(file));
		
		file.delete();
		System.out.println(filterRule.toString());
	}
	
}
