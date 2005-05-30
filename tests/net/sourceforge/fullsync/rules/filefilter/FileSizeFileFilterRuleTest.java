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
public class FileSizeFileFilterRuleTest extends TestCase {
//
//	public void testOpIs() throws IOException {
//		FileSizeFileFilterRule filterRule = new FileSizeFileFilterRule(1000, FileSizeFileFilterRule.OP_IS);
//		File file = new File("foobar.txt");
//		FileWriter writer = new FileWriter(file, false);
//		char[] buff = new char[1000];
//		writer.write(buff);
//		writer.flush();
//		writer.close();
//
//		assertTrue(filterRule.match(file));
//		
//		writer = new FileWriter(file, false);
//		buff = new char[2000];
//		writer.write(buff);
//		writer.flush();
//		writer.close();
//
//		assertTrue(!filterRule.match(file));
//		
//		file.delete();
//	}
//
//	public void testOpIsnt() throws IOException {
//		FileSizeFileFilterRule filterRule = new FileSizeFileFilterRule(1000, FileSizeFileFilterRule.OP_ISNT);
//		File file = new File("foobar.txt");
//		FileWriter writer = new FileWriter(file, false);
//		char[] buff = new char[1000];
//		writer.write(buff);
//		writer.flush();
//		writer.close();
//
//		assertTrue(!filterRule.match(file));
//		
//		writer = new FileWriter(file, false);
//		buff = new char[2000];
//		writer.write(buff);
//		writer.flush();
//		writer.close();
//
//		assertTrue(filterRule.match(file));
//		
//		file.delete();
//	}
//
//	public void testOpIsGreaterThan() throws IOException {
//		FileSizeFileFilterRule filterRule = new FileSizeFileFilterRule(1000, FileSizeFileFilterRule.OP_IS_GREATER_THAN);
//		File file = new File("foobar.txt");
//		FileWriter writer = new FileWriter(file, false);
//		char[] buff = new char[1000];
//		writer.write(buff);
//		writer.flush();
//		writer.close();
//
//		assertTrue(!filterRule.match(file));
//		
//		writer = new FileWriter(file, false);
//		buff = new char[2000];
//		writer.write(buff);
//		writer.flush();
//		writer.close();
//
//		assertTrue(filterRule.match(file));
//
//		writer = new FileWriter(file, false);
//		buff = new char[999];
//		writer.write(buff);
//		writer.flush();
//		writer.close();
//
//		assertTrue(!filterRule.match(file));
//
//		file.delete();
//	}
//
//	public void testOpIsLessThan() throws IOException {
//		FileSizeFileFilterRule filterRule = new FileSizeFileFilterRule(1000, FileSizeFileFilterRule.OP_IS_LESS_THAN);
//		File file = new File("foobar.txt");
//		FileWriter writer = new FileWriter(file, false);
//		char[] buff = new char[1000];
//		writer.write(buff);
//		writer.flush();
//		writer.close();
//
//		assertTrue(!filterRule.match(file));
//		
//		writer = new FileWriter(file, false);
//		buff = new char[2000];
//		writer.write(buff);
//		writer.flush();
//		writer.close();
//
//		assertTrue(!filterRule.match(file));
//
//		writer = new FileWriter(file, false);
//		buff = new char[999];
//		writer.write(buff);
//		writer.flush();
//		writer.close();
//
//		assertTrue(filterRule.match(file));
//
//		file.delete();
//	}

}
