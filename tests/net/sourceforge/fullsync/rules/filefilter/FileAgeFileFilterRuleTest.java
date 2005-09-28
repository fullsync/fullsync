/*
 * Created on Jun 2, 2005
 */
package net.sourceforge.fullsync.rules.filefilter;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import junit.framework.TestCase;
import net.sourceforge.fullsync.SystemDate;
import net.sourceforge.fullsync.rules.filefilter.values.AgeValue;

/**
 * @author Michele Aiello
 */
public class FileAgeFileFilterRuleTest extends TestCase {

	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	protected void tearDown() throws Exception {
		super.tearDown();
		SystemDate.getInstance().setUseSystemTime();
	}
	
	public void testOpIs() throws FilterRuleNotAppliableException, ParseException {
		SystemDate.getInstance().setTimeSpeed(0);
		SystemDate.getInstance().setCurrent(dateFormat.parse("01/01/2005 10:00:01").getTime());
		FileAgeFileFilterRule filterRule = new FileAgeFileFilterRule(new AgeValue(1, AgeValue.SECONDS), FileAgeFileFilterRule.OP_IS);
		assertTrue(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, dateFormat.parse("01/01/2005 10:00:00").getTime())));
		assertFalse(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, dateFormat.parse("01/01/2005 10:00:01").getTime())));
		assertFalse(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, dateFormat.parse("01/01/2005 09:00:00").getTime())));
		assertFalse(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, dateFormat.parse("01/02/2005 10:00:00").getTime())));
	}

	public void testOpIsnt() throws FilterRuleNotAppliableException, ParseException {
		SystemDate.getInstance().setTimeSpeed(0);
		SystemDate.getInstance().setCurrent(dateFormat.parse("01/01/2005 10:00:01").getTime());
		FileAgeFileFilterRule filterRule = new FileAgeFileFilterRule(new AgeValue(1, AgeValue.SECONDS), FileAgeFileFilterRule.OP_ISNT);
		assertFalse(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, dateFormat.parse("01/01/2005 10:00:00").getTime())));
		assertTrue(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, dateFormat.parse("01/01/2005 10:00:01").getTime())));
		assertTrue(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, dateFormat.parse("01/01/2005 09:00:00").getTime())));
		assertTrue(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, dateFormat.parse("01/02/2005 10:00:00").getTime())));
	}

	public void testOpIsGreaterThan() throws FilterRuleNotAppliableException, ParseException {
		SystemDate.getInstance().setTimeSpeed(0);
		SystemDate.getInstance().setCurrent(dateFormat.parse("01/01/2005 10:00:00").getTime());
		FileAgeFileFilterRule filterRule = new FileAgeFileFilterRule(new AgeValue(1, AgeValue.SECONDS), FileAgeFileFilterRule.OP_IS_GREATER_THAN);
		assertFalse(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, dateFormat.parse("01/01/2005 10:00:00").getTime())));
		assertFalse(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, dateFormat.parse("01/01/2005 10:00:01").getTime())));
		assertFalse(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, dateFormat.parse("01/01/2005 10:00:02").getTime())));
		assertTrue(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, dateFormat.parse("30/12/2004 09:00:00").getTime())));
		assertTrue(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, dateFormat.parse("01/01/2005 01:00:00").getTime())));
		assertFalse(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, dateFormat.parse("01/01/2005 11:00:00").getTime())));
		assertFalse(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, dateFormat.parse("01/01/2006 09:00:00").getTime())));
		assertTrue(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, dateFormat.parse("01/01/2004 09:00:00").getTime())));
		assertFalse(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, dateFormat.parse("01/01/2005 09:59:59").getTime())));
		assertTrue(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, dateFormat.parse("01/01/2005 09:59:58").getTime())));
	}

	public void testOpIsLessThan() throws FilterRuleNotAppliableException, ParseException {
		SystemDate.getInstance().setTimeSpeed(0);
		SystemDate.getInstance().setCurrent(dateFormat.parse("01/01/2005 10:00:00").getTime());
		FileAgeFileFilterRule filterRule = new FileAgeFileFilterRule(new AgeValue(10, AgeValue.SECONDS), FileAgeFileFilterRule.OP_IS_LESS_THAN);
		assertTrue(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, dateFormat.parse("01/01/2005 10:00:00").getTime())));
		assertTrue(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, dateFormat.parse("01/01/2005 09:59:57").getTime())));
		assertFalse(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, dateFormat.parse("01/01/2005 09:59:50").getTime())));
		assertFalse(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, dateFormat.parse("30/12/2004 10:00:00").getTime())));
		assertFalse(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, dateFormat.parse("01/01/2005 09:00:00").getTime())));
		assertTrue(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, dateFormat.parse("01/01/2005 11:00:00").getTime())));
	}
}
