/*
 * Created on May 28, 2005
 */
package net.sourceforge.fullsync.rules.filefilter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;

import junit.framework.TestCase;

/**
 * @author Michele Aiello
 */
public class FileFilterTest extends TestCase {
//	
//	private static class AlwaysTrueFileFilterRule implements FileFilterRule {
//		public boolean match(File file) {
//			return true;
//		}
//		public String toString() {
//			return "TRUE";
//		}
//	}
//	
//	private static class AlwaysFalseFileFilterRule implements FileFilterRule {
//		public boolean match(File file) {
//			return false;
//		}
//		public String toString() {
//			return "FALSE";
//		}
//	}
//	
//	public void testEmptyFilter() {
//		FileFilter filter = new FileFilter();
//		filter.setMatchType(FileFilter.MATCH_ALL);
//		assertTrue(filter.match(new File("foobar.txt")));
//
//		filter.setMatchType(FileFilter.MATCH_ANY);
//		assertTrue(filter.match(new File("foobar.txt")));
//	}
//	
//	public void testOneRuleFilter() {
//		FileFilter filter = new FileFilter();
//		filter.setMatchType(FileFilter.MATCH_ALL);
//		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysTrueFileFilterRule()});
//		
//		assertTrue(filter.match(new File("foobar.txt")));
//
//		filter.setMatchType(FileFilter.MATCH_ANY);
//		assertTrue(filter.match(new File("foobar.txt")));
//		
//		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysFalseFileFilterRule()});
//
//		assertTrue(!filter.match(new File("foobar.txt")));
//
//		filter.setMatchType(FileFilter.MATCH_ANY);
//		assertTrue(!filter.match(new File("foobar.txt")));
//	}
//	
//	public void testFilterAllBasic() {
//		FileFilter filter = new FileFilter();
//		filter.setMatchType(FileFilter.MATCH_ALL);
//		
//		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysTrueFileFilterRule(), new AlwaysTrueFileFilterRule()});
//		assertTrue(filter.match(new File("foobar.txt")));
//		
//		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysFalseFileFilterRule(), new AlwaysTrueFileFilterRule()});
//		assertTrue(!filter.match(new File("foobar.txt")));		
//		
//		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysTrueFileFilterRule(), new AlwaysFalseFileFilterRule()});
//		assertTrue(!filter.match(new File("foobar.txt")));
//		
//		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysFalseFileFilterRule(), new AlwaysFalseFileFilterRule()});
//		assertTrue(!filter.match(new File("foobar.txt")));
//		
//		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysTrueFileFilterRule(), 
//				new AlwaysTrueFileFilterRule(),
//				new AlwaysTrueFileFilterRule(),
//				new AlwaysTrueFileFilterRule(),
//				new AlwaysTrueFileFilterRule(),
//				new AlwaysTrueFileFilterRule(),
//				new AlwaysTrueFileFilterRule()});
//		assertTrue(filter.match(new File("foobar.txt")));
//		
//		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysTrueFileFilterRule(), 
//				new AlwaysFalseFileFilterRule(),
//				new AlwaysFalseFileFilterRule(),
//				new AlwaysFalseFileFilterRule(),
//				new AlwaysFalseFileFilterRule(),
//				new AlwaysFalseFileFilterRule(),
//				new AlwaysFalseFileFilterRule()});
//		assertTrue(!filter.match(new File("foobar.txt")));			
//
//		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysTrueFileFilterRule(), 
//				new AlwaysTrueFileFilterRule(),
//				new AlwaysFalseFileFilterRule(),
//				new AlwaysFalseFileFilterRule(),
//				new AlwaysFalseFileFilterRule(),
//				new AlwaysFalseFileFilterRule(),
//				new AlwaysFalseFileFilterRule()});
//		assertTrue(!filter.match(new File("foobar.txt")));			
//
//		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysTrueFileFilterRule(), 
//				new AlwaysTrueFileFilterRule(),
//				new AlwaysFalseFileFilterRule(),
//				new AlwaysTrueFileFilterRule(),
//				new AlwaysFalseFileFilterRule(),
//				new AlwaysFalseFileFilterRule(),
//				new AlwaysFalseFileFilterRule()});
//		assertTrue(!filter.match(new File("foobar.txt")));			
//
//		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysTrueFileFilterRule(), 
//				new AlwaysTrueFileFilterRule(),
//				new AlwaysFalseFileFilterRule(),
//				new AlwaysTrueFileFilterRule(),
//				new AlwaysFalseFileFilterRule(),
//				new AlwaysFalseFileFilterRule(),
//				new AlwaysTrueFileFilterRule()});
//		assertTrue(!filter.match(new File("foobar.txt")));			
//
//		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysTrueFileFilterRule(), 
//				new AlwaysTrueFileFilterRule(),
//				new AlwaysTrueFileFilterRule(),
//				new AlwaysTrueFileFilterRule(),
//				new AlwaysTrueFileFilterRule(),
//				new AlwaysFalseFileFilterRule(),
//				new AlwaysTrueFileFilterRule()});
//		assertTrue(!filter.match(new File("foobar.txt")));			
//
//		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysTrueFileFilterRule(), 
//				new AlwaysFalseFileFilterRule(),
//				new AlwaysTrueFileFilterRule(),
//				new AlwaysTrueFileFilterRule(),
//				new AlwaysTrueFileFilterRule(),
//				new AlwaysTrueFileFilterRule(),
//				new AlwaysTrueFileFilterRule()});
//		assertTrue(!filter.match(new File("foobar.txt")));			
//
//	}
//	
//	
//	public void testFilterAnyBasic() {
//		FileFilter filter = new FileFilter();
//		filter.setMatchType(FileFilter.MATCH_ANY);
//		
//		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysTrueFileFilterRule(), new AlwaysTrueFileFilterRule()});
//		assertTrue(filter.match(new File("foobar.txt")));			
//		
//		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysFalseFileFilterRule(), new AlwaysTrueFileFilterRule()});
//		assertTrue(filter.match(new File("foobar.txt")));			
//		
//		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysTrueFileFilterRule(), new AlwaysFalseFileFilterRule()});
//		assertTrue(filter.match(new File("foobar.txt")));			
//		
//		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysFalseFileFilterRule(), new AlwaysFalseFileFilterRule()});
//		assertTrue(!filter.match(new File("foobar.txt")));
//		
//		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysTrueFileFilterRule(), 
//				new AlwaysTrueFileFilterRule(),
//				new AlwaysTrueFileFilterRule(),
//				new AlwaysTrueFileFilterRule(),
//				new AlwaysTrueFileFilterRule(),
//				new AlwaysTrueFileFilterRule(),
//				new AlwaysTrueFileFilterRule()});
//		assertTrue(filter.match(new File("foobar.txt")));			
//		
//		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysFalseFileFilterRule(), 
//				new AlwaysFalseFileFilterRule(),
//				new AlwaysTrueFileFilterRule(),
//				new AlwaysFalseFileFilterRule(),
//				new AlwaysFalseFileFilterRule(),
//				new AlwaysFalseFileFilterRule(),
//				new AlwaysFalseFileFilterRule()});
//		assertTrue(filter.match(new File("foobar.txt")));			
//		
//		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysFalseFileFilterRule(), 
//				new AlwaysFalseFileFilterRule(),
//				new AlwaysFalseFileFilterRule(),
//				new AlwaysFalseFileFilterRule(),
//				new AlwaysFalseFileFilterRule(),
//				new AlwaysTrueFileFilterRule(),
//				new AlwaysFalseFileFilterRule()});
//		assertTrue(filter.match(new File("foobar.txt")));			
//		
//		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysFalseFileFilterRule(), 
//				new AlwaysFalseFileFilterRule(),
//				new AlwaysTrueFileFilterRule(),
//				new AlwaysFalseFileFilterRule(),
//				new AlwaysFalseFileFilterRule(),
//				new AlwaysTrueFileFilterRule(),
//				new AlwaysFalseFileFilterRule()});
//		assertTrue(filter.match(new File("foobar.txt")));			
//		
//		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysFalseFileFilterRule(), 
//				new AlwaysFalseFileFilterRule(),
//				new AlwaysFalseFileFilterRule(),
//				new AlwaysFalseFileFilterRule(),
//				new AlwaysFalseFileFilterRule(),
//				new AlwaysFalseFileFilterRule(),
//				new AlwaysFalseFileFilterRule()});
//		assertTrue(!filter.match(new File("foobar.txt")));			
//	}
//	
//	public void testFilterAll() {
//		FileFilter filter = new FileFilter();
//		filter.setMatchType(FileFilter.MATCH_ANY);
//		filter.setFileFilterRules(new FileFilterRule[] {new FileNameFileFilterRule(".txt", FileNameFileFilterRule.OP_ENDS_WITH),
//				new FileSizeFileFilterRule(1024, FileSizeFileFilterRule.OP_IS_LESS_THAN)});
//		
//		assertTrue(filter.match(new File("foobar.txt")));
//		assertTrue(filter.match(new File("somedir/foobar.txt")));
//	}
//	
//	public void testSerialization() {
//		FileFilterManager fileFilterManager = new FileFilterManager();
//
//		File filterFile = new File("filter.xml");
//		filterFile.deleteOnExit();
//		try {
//			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//			Document doc = docBuilder.newDocument();
//
//			FileFilter filter = new FileFilter();
//			filter.setMatchType(FileFilter.MATCH_ANY);
//			filter.setFileFilterRules(new FileFilterRule[] {new FileNameFileFilterRule(".txt", FileNameFileFilterRule.OP_ENDS_WITH),
//					new FileSizeFileFilterRule(1024, FileSizeFileFilterRule.OP_IS_LESS_THAN)});
//			
//			assertTrue(filter.match(new File("foobar.txt")));
//			assertTrue(filter.match(new File("somedir/foobar.txt")));
//
//			System.out.println("Serializzato:");
//			System.out.println(filter.toString());
//			
//			fileFilterManager.serializeFileFilter(filter, doc);
//			
//			OutputStream out = new FileOutputStream(filterFile);
//
//			OutputFormat format = new OutputFormat(doc, "UTF-8", true);
//			XMLSerializer serializer = new XMLSerializer (out, format);
//			serializer.asDOMSerializer();
//			serializer.serialize(doc);
//		} catch( Exception e ) {
//			e.printStackTrace();
//		}
//		
//		try {
//			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//			Document doc = builder.parse(filterFile);
//			
//			FileFilter filter = fileFilterManager.unserializeFileFilter(doc.getDocumentElement());
//			
//			assertTrue(filter.match(new File("foobar.txt")));
//			assertTrue(filter.match(new File("somedir/foobar.txt")));
//
//			System.out.println("Deserializzato:");
//			System.out.println(filter.toString());
//		} catch( Exception e ) {
//			e.printStackTrace();
//		}
//		
//	}
	
}
