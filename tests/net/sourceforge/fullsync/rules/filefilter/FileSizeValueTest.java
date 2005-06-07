/*
 * Created on Jun 6, 2005
 */
package net.sourceforge.fullsync.rules.filefilter;

import net.sourceforge.fullsync.rules.filefilter.values.SizeValue;
import junit.framework.TestCase;

/**
 * @author Michele Aiello
 */
public class FileSizeValueTest extends TestCase {
	
	public void testFromString() {
		SizeValue value = new SizeValue();
		
		value.fromString("100 bytes");
		System.out.println(value);
		
		value.fromString("1.1 KBytes");
		System.out.println(value);
		
		value.fromString("1.2 KBytes");
		System.out.println(value);
		
		value.fromString("1 MBytes");
		System.out.println(value);
		System.out.println(value.getBytes());		
	}	
}
