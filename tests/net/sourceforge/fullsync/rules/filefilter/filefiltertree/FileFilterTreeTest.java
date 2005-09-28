/*
 * Created on Jun 20, 2005
 */
package net.sourceforge.fullsync.rules.filefilter.filefiltertree;

import junit.framework.TestCase;
import net.sourceforge.fullsync.rules.filefilter.FileFilter;
import net.sourceforge.fullsync.rules.filefilter.TestNode;

/**
 * @author Michele Aiello
 */
public class FileFilterTreeTest extends TestCase {

	public void testBasic() {
		FileFilterTree tree = new FileFilterTree("/");
		
		TestNode node1 = new TestNode("a", "./a", true, true, 0, 0);
		TestNode node2 = new TestNode("b", "./b", true, true, 0, 0);
		TestNode node3 = new TestNode("c", "./a/c", true, true, 0, 0);
		TestNode node4 = new TestNode("d", "./a/c/d", true, true, 0, 0);
		TestNode node5 = new TestNode("e", "./b/e", true, true, 0, 0);
		TestNode node6 = new TestNode("f", "./a/f", true, true, 0, 0);
		TestNode node7 = new TestNode("g", "./a/c/d/g", true, true, 0, 0);

		FileFilter filter1 = new FileFilter();
		FileFilter filter2 = new FileFilter();
		FileFilter filter3 = new FileFilter();
		FileFilter filter4 = new FileFilter();
		
		tree.addFileFilter("./a/c", filter1);
		tree.addFileFilter("./b/e", filter2);
		tree.addFileFilter("./a/c/d", filter3);
		tree.addFileFilter("./a/c/d/g", filter4);
				
		assertNull(tree.getFilter("./a"));
		assertNull(tree.getFilter("./a/f"));
		assertNull(tree.getFilter("./a/c"));
		assertNull(tree.getFilter("./b/e"));
		assertNull(tree.getFilter("./b/e/"));
		assertEquals(filter1, tree.getFilter("./a/c/d"));
		assertEquals(filter3, tree.getFilter("./a/c/d/file.txt"));
		assertEquals(filter3, tree.getFilter("./a/c/d/subdir"));
		assertEquals(filter3, tree.getFilter("./a/c/d/g"));
		assertEquals(filter3, tree.getFilter("./a/c/d/g/"));
		assertEquals(filter4, tree.getFilter("./a/c/d/g/file.txt"));
		assertEquals(filter2, tree.getFilter("./b/e/subdir/file.txt"));
	}
	
}
