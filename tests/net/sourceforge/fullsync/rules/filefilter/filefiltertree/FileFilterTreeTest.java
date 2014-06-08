/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA 02110-1301, USA.
 *
 * For information about the authors of this project Have a look
 * at the AUTHORS file in the root of this project.
 */
/*
 * Created on Jun 20, 2005
 */
package net.sourceforge.fullsync.rules.filefilter.filefiltertree;

import junit.framework.TestCase;
import net.sourceforge.fullsync.rules.filefilter.FileFilter;
import net.sourceforge.fullsync.rules.filefilter.TestNode;

public class FileFilterTreeTest extends TestCase {

	public void testBasic() {
		FileFilterTree tree = new FileFilterTree();

		new TestNode("a", "./a", true, true, 0, 0);
		new TestNode("b", "./b", true, true, 0, 0);
		new TestNode("c", "./a/c", true, true, 0, 0);
		new TestNode("d", "./a/c/d", true, true, 0, 0);
		new TestNode("e", "./b/e", true, true, 0, 0);
		new TestNode("f", "./a/f", true, true, 0, 0);
		new TestNode("g", "./a/c/d/g", true, true, 0, 0);

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
