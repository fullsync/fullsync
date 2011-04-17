/*
 * Created on May 28, 2005
 */
package net.sourceforge.fullsync.rules.filefilter;

import junit.framework.TestCase;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.rules.filefilter.values.OperandValue;
import net.sourceforge.fullsync.rules.filefilter.values.SizeValue;
import net.sourceforge.fullsync.rules.filefilter.values.TextValue;

/**
 * @author Michele Aiello
 */
public class FileFilterTest extends TestCase {
	
	private static class AlwaysTrueFileFilterRule extends FileFilterRule {
		public boolean match(File file) {
			return true;
		}
		public String toString() {
			return "TRUE";
		}
		public int getOperator() {
			return 0;
		}
		public String getOperatorName() {
			return null;
		}
		public String getRuleType() {
			return "True";
		}
		public OperandValue getValue() {
			return null;
		}
	}

	private static class AlwaysFalseFileFilterRule extends FileFilterRule {
		public boolean match(File file) {
			return false;
		}
		public String toString() {
			return "FALSE";
		}
		public int getOperator() {
			return 0;
		}
		public String getOperatorName() {
			return null;
		}
		public String getRuleType() {
			return "False";
		}
		public OperandValue getValue() {
			return null;
		}
	}

	
	public void testEmptyFilter() {
		FileFilter filter = new FileFilter();
		filter.setMatchType(FileFilter.MATCH_ALL);
		assertTrue(filter.match(new TestNode("foobar.txt", "foobar.txt", true, false, 0, 0)));

		filter.setMatchType(FileFilter.MATCH_ANY);
		assertTrue(filter.match(new TestNode("foobar.txt", "foobar.txt", true, false, 0, 0)));
	}
	
	public void testOneRuleFilter() {
		FileFilter filter = new FileFilter();
		filter.setMatchType(FileFilter.MATCH_ALL);
		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysTrueFileFilterRule()});
		
		assertTrue(filter.match(new TestNode("foobar.txt", "foobar.txt", true, false, 0, 0)));

		filter.setMatchType(FileFilter.MATCH_ANY);
		assertTrue(filter.match(new TestNode("foobar.txt", "foobar.txt", true, false, 0, 0)));
		
		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysFalseFileFilterRule()});

		assertTrue(!filter.match(new TestNode("foobar.txt", "foobar.txt", true, false, 0, 0)));

		filter.setMatchType(FileFilter.MATCH_ANY);
		assertTrue(!filter.match(new TestNode("foobar.txt", "foobar.txt", true, false, 0, 0)));
	}
	
	public void testFilterAllBasic() {
		FileFilter filter = new FileFilter();
		filter.setMatchType(FileFilter.MATCH_ALL);
		
		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysTrueFileFilterRule(), new AlwaysTrueFileFilterRule()});
		assertTrue(filter.match(new TestNode("foobar.txt", "foobar.txt", true, false, 0, 0)));
		
		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysFalseFileFilterRule(), new AlwaysTrueFileFilterRule()});
		assertTrue(!filter.match(new TestNode("foobar.txt", "foobar.txt", true, false, 0, 0)));		
		
		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysTrueFileFilterRule(), new AlwaysFalseFileFilterRule()});
		assertTrue(!filter.match(new TestNode("foobar.txt", "foobar.txt", true, false, 0, 0)));
		
		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysFalseFileFilterRule(), new AlwaysFalseFileFilterRule()});
		assertTrue(!filter.match(new TestNode("foobar.txt", "foobar.txt", true, false, 0, 0)));
		
		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysTrueFileFilterRule(), 
				new AlwaysTrueFileFilterRule(),
				new AlwaysTrueFileFilterRule(),
				new AlwaysTrueFileFilterRule(),
				new AlwaysTrueFileFilterRule(),
				new AlwaysTrueFileFilterRule(),
				new AlwaysTrueFileFilterRule()});
		assertTrue(filter.match(new TestNode("foobar.txt", "foobar.txt", true, false, 0, 0)));
		
		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysTrueFileFilterRule(), 
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule()});
		assertTrue(!filter.match(new TestNode("foobar.txt", "foobar.txt", true, false, 0, 0)));			

		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysTrueFileFilterRule(), 
				new AlwaysTrueFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule()});
		assertTrue(!filter.match(new TestNode("foobar.txt", "foobar.txt", true, false, 0, 0)));			

		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysTrueFileFilterRule(), 
				new AlwaysTrueFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysTrueFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule()});
		assertTrue(!filter.match(new TestNode("foobar.txt", "foobar.txt", true, false, 0, 0)));			

		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysTrueFileFilterRule(), 
				new AlwaysTrueFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysTrueFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysTrueFileFilterRule()});
		assertTrue(!filter.match(new TestNode("foobar.txt", "foobar.txt", true, false, 0, 0)));			

		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysTrueFileFilterRule(), 
				new AlwaysTrueFileFilterRule(),
				new AlwaysTrueFileFilterRule(),
				new AlwaysTrueFileFilterRule(),
				new AlwaysTrueFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysTrueFileFilterRule()});
		assertTrue(!filter.match(new TestNode("foobar.txt", "foobar.txt", true, false, 0, 0)));			

		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysTrueFileFilterRule(), 
				new AlwaysFalseFileFilterRule(),
				new AlwaysTrueFileFilterRule(),
				new AlwaysTrueFileFilterRule(),
				new AlwaysTrueFileFilterRule(),
				new AlwaysTrueFileFilterRule(),
				new AlwaysTrueFileFilterRule()});
		assertTrue(!filter.match(new TestNode("foobar.txt", "foobar.txt", true, false, 0, 0)));			

	}
	
	
	public void testFilterAnyBasic() {
		FileFilter filter = new FileFilter();
		filter.setMatchType(FileFilter.MATCH_ANY);
		
		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysTrueFileFilterRule(), new AlwaysTrueFileFilterRule()});
		assertTrue(filter.match(new TestNode("foobar.txt", "foobar.txt", true, false, 0, 0)));			
		
		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysFalseFileFilterRule(), new AlwaysTrueFileFilterRule()});
		assertTrue(filter.match(new TestNode("foobar.txt", "foobar.txt", true, false, 0, 0)));			
		
		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysTrueFileFilterRule(), new AlwaysFalseFileFilterRule()});
		assertTrue(filter.match(new TestNode("foobar.txt", "foobar.txt", true, false, 0, 0)));			
		
		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysFalseFileFilterRule(), new AlwaysFalseFileFilterRule()});
		assertTrue(!filter.match(new TestNode("foobar.txt", "foobar.txt", true, false, 0, 0)));
		
		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysTrueFileFilterRule(), 
				new AlwaysTrueFileFilterRule(),
				new AlwaysTrueFileFilterRule(),
				new AlwaysTrueFileFilterRule(),
				new AlwaysTrueFileFilterRule(),
				new AlwaysTrueFileFilterRule(),
				new AlwaysTrueFileFilterRule()});
		assertTrue(filter.match(new TestNode("foobar.txt", "foobar.txt", true, false, 0, 0)));			
		
		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysFalseFileFilterRule(), 
				new AlwaysFalseFileFilterRule(),
				new AlwaysTrueFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule()});
		assertTrue(filter.match(new TestNode("foobar.txt", "foobar.txt", true, false, 0, 0)));			
		
		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysFalseFileFilterRule(), 
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysTrueFileFilterRule(),
				new AlwaysFalseFileFilterRule()});
		assertTrue(filter.match(new TestNode("foobar.txt", "foobar.txt", true, false, 0, 0)));			
		
		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysFalseFileFilterRule(), 
				new AlwaysFalseFileFilterRule(),
				new AlwaysTrueFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysTrueFileFilterRule(),
				new AlwaysFalseFileFilterRule()});
		assertTrue(filter.match(new TestNode("foobar.txt", "foobar.txt", true, false, 0, 0)));			
		
		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysFalseFileFilterRule(), 
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule()});
		assertTrue(!filter.match(new TestNode("foobar.txt", "foobar.txt", true, false, 0, 0)));			
	}
	
	public void testFilterInclude() {
		FileFilter filter = new FileFilter();
		filter.setMatchType(FileFilter.MATCH_ANY);
		filter.setFilterType(FileFilter.INCLUDE);
		filter.setFileFilterRules(new FileFilterRule[] {
				new FileNameFileFilterRule(new TextValue(".txt"), FileNameFileFilterRule.OP_ENDS_WITH),
				new FileSizeFileFilterRule(new SizeValue(1024, SizeValue.BYTES), FileSizeFileFilterRule.OP_IS_LESS_THAN)});
		
		assertTrue(filter.match(new TestNode("foobar.txt", "foobar.txt", true, false, 0, 0)));
		assertTrue(filter.match(new TestNode("foobar.txt.", "somedir/foobar.txt", true, false, 0, 0)));			
		assertTrue(!filter.match(new TestNode("foobar.txt.", "somedir/foobar.txt", true, false, 2048, 0)));	
	}

	public void testFilterExclude() {
		FileFilter filter = new FileFilter();
		filter.setMatchType(FileFilter.MATCH_ANY);
		filter.setFilterType(FileFilter.EXCLUDE);
		filter.setFileFilterRules(new FileFilterRule[] {
				new FileNameFileFilterRule(new TextValue(".txt"), FileNameFileFilterRule.OP_ENDS_WITH),
				new FileSizeFileFilterRule(new SizeValue(1024, SizeValue.BYTES), FileSizeFileFilterRule.OP_IS_LESS_THAN)});
		
		assertTrue(!filter.match(new TestNode("foobar.txt", "foobar.txt", true, false, 0, 0)));
		assertTrue(!filter.match(new TestNode("foobar.txt.", "somedir/foobar.txt", true, false, 0, 0)));		
		assertTrue(filter.match(new TestNode("foobar.txt.", "somedir/foobar.txt", true, false, 2048, 0)));		
	}

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
//			assertTrue(filter.match(new TestNode("foobar.txt", "foobar.txt", true, false, 0, 0)));
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
//			assertTrue(filter.match(new TestNode("foobar.txt", "foobar.txt", true, false, 0, 0)));
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
