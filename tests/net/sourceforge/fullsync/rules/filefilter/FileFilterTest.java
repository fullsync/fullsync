/*
 * Created on May 28, 2005
 */
package net.sourceforge.fullsync.rules.filefilter;

import java.io.File;

import junit.framework.TestCase;

/**
 * @author Michele Aiello
 */
public class FileFilterTest extends TestCase {
	
	private static class AlwaysTrueFileFilterRule implements FileFilterRule {
		public boolean match(File file) {
			return true;
		}
	}
	
	private static class AlwaysFalseFileFilterRule implements FileFilterRule {
		public boolean match(File file) {
			return false;
		}
	}
	
	public void testFilterAllBasic() {
		FileFilter filter = new FileFilter();
		filter.setMatchType(FileFilter.MATCH_ALL);
		
		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysTrueFileFilterRule(), new AlwaysTrueFileFilterRule()});
		assertTrue(filter.match(new File("foobar.txt")));
		
		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysFalseFileFilterRule(), new AlwaysTrueFileFilterRule()});
		assertTrue(!filter.match(new File("foobar.txt")));		
		
		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysTrueFileFilterRule(), new AlwaysFalseFileFilterRule()});
		assertTrue(!filter.match(new File("foobar.txt")));
		
		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysFalseFileFilterRule(), new AlwaysFalseFileFilterRule()});
		assertTrue(!filter.match(new File("foobar.txt")));
		
		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysTrueFileFilterRule(), 
				new AlwaysTrueFileFilterRule(),
				new AlwaysTrueFileFilterRule(),
				new AlwaysTrueFileFilterRule(),
				new AlwaysTrueFileFilterRule(),
				new AlwaysTrueFileFilterRule(),
				new AlwaysTrueFileFilterRule()});
		assertTrue(filter.match(new File("foobar.txt")));			
		
		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysTrueFileFilterRule(), 
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule()});
		assertTrue(!filter.match(new File("foobar.txt")));			

		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysTrueFileFilterRule(), 
				new AlwaysTrueFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule()});
		assertTrue(!filter.match(new File("foobar.txt")));			

		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysTrueFileFilterRule(), 
				new AlwaysTrueFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysTrueFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule()});
		assertTrue(!filter.match(new File("foobar.txt")));			

		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysTrueFileFilterRule(), 
				new AlwaysTrueFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysTrueFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysTrueFileFilterRule()});
		assertTrue(!filter.match(new File("foobar.txt")));			

		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysTrueFileFilterRule(), 
				new AlwaysTrueFileFilterRule(),
				new AlwaysTrueFileFilterRule(),
				new AlwaysTrueFileFilterRule(),
				new AlwaysTrueFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysTrueFileFilterRule()});
		assertTrue(!filter.match(new File("foobar.txt")));			

		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysTrueFileFilterRule(), 
				new AlwaysFalseFileFilterRule(),
				new AlwaysTrueFileFilterRule(),
				new AlwaysTrueFileFilterRule(),
				new AlwaysTrueFileFilterRule(),
				new AlwaysTrueFileFilterRule(),
				new AlwaysTrueFileFilterRule()});
		assertTrue(!filter.match(new File("foobar.txt")));			

	}
	
	
	public void testFilterAnyBasic() {
		FileFilter filter = new FileFilter();
		filter.setMatchType(FileFilter.MATCH_ANY);
		
		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysTrueFileFilterRule(), new AlwaysTrueFileFilterRule()});
		assertTrue(filter.match(new File("foobar.txt")));			
		
		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysFalseFileFilterRule(), new AlwaysTrueFileFilterRule()});
		assertTrue(filter.match(new File("foobar.txt")));			
		
		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysTrueFileFilterRule(), new AlwaysFalseFileFilterRule()});
		assertTrue(filter.match(new File("foobar.txt")));			
		
		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysFalseFileFilterRule(), new AlwaysFalseFileFilterRule()});
		assertTrue(!filter.match(new File("foobar.txt")));
		
		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysTrueFileFilterRule(), 
				new AlwaysTrueFileFilterRule(),
				new AlwaysTrueFileFilterRule(),
				new AlwaysTrueFileFilterRule(),
				new AlwaysTrueFileFilterRule(),
				new AlwaysTrueFileFilterRule(),
				new AlwaysTrueFileFilterRule()});
		assertTrue(filter.match(new File("foobar.txt")));			
		
		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysFalseFileFilterRule(), 
				new AlwaysFalseFileFilterRule(),
				new AlwaysTrueFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule()});
		assertTrue(filter.match(new File("foobar.txt")));			
		
		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysFalseFileFilterRule(), 
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysTrueFileFilterRule(),
				new AlwaysFalseFileFilterRule()});
		assertTrue(filter.match(new File("foobar.txt")));			
		
		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysFalseFileFilterRule(), 
				new AlwaysFalseFileFilterRule(),
				new AlwaysTrueFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysTrueFileFilterRule(),
				new AlwaysFalseFileFilterRule()});
		assertTrue(filter.match(new File("foobar.txt")));			
		
		filter.setFileFilterRules(new FileFilterRule[] {new AlwaysFalseFileFilterRule(), 
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule(),
				new AlwaysFalseFileFilterRule()});
		assertTrue(!filter.match(new File("foobar.txt")));			
	}
	
	public void testFilterAll() {
		FileFilter filter = new FileFilter();
		filter.setMatchType(FileFilter.MATCH_ANY);
		filter.setFileFilterRules(new FileFilterRule[] {new FileNameFileFilterRule(".txt", FileNameFileFilterRule.OP_ENDS_WITH)});
		
		assertTrue(filter.match(new File("foobar.txt")));
		assertTrue(filter.match(new File("somedir/foobar.txt")));
	}
}
