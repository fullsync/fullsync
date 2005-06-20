/*
 * Created on May 29, 2005
 */
package net.sourceforge.fullsync.rules.filefilter;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import net.sourceforge.fullsync.fs.FileAttributes;
import net.sourceforge.fullsync.rules.filefilter.values.DateValue;

import junit.framework.TestCase;

/**
 * @author Michele Aiello
 */
public class FileModificationDateFileFilterRuleTest extends TestCase {

	public void testOpIs() throws ParseException, FilterRuleNotAppliableException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		FileModificationDateFileFilterRule filterRule = new FileModificationDateFileFilterRule(
				new DateValue(dateFormat.parse("01/06/2005 06:00:00")), 
				FileModificationDateFileFilterRule.OP_IS);
		
		TestNode file = new TestNode("foobar.txt", "/root/foobar.txt", 
				true, false, 
				1000, dateFormat.parse("01/06/2005 10:00:00").getTime());
		
		assertTrue(filterRule.match(file));
		file.setFileAttributes(new FileAttributes(1000, dateFormat.parse("02/06/2005 10:00:00").getTime()));

		assertTrue(!filterRule.match(file));
	}
	
}
