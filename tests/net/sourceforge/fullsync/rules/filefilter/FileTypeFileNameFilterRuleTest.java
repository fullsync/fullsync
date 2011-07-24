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
 * Created on Jun 1, 2005
 */
package net.sourceforge.fullsync.rules.filefilter;

import junit.framework.TestCase;
import net.sourceforge.fullsync.rules.filefilter.values.TypeValue;

/**
 * @author Michele Aiello
 */
public class FileTypeFileNameFilterRuleTest extends TestCase {

	public void testWrongType() {
		FileTypeFileFilterRule filterRule = new FileTypeFileFilterRule(new TypeValue(5), FileTypeFileFilterRule.OP_IS);
		assertFalse(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, System.currentTimeMillis())));
		assertFalse(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, true, 1024, System.currentTimeMillis())));
	}

	public void testIsFile() {
		FileTypeFileFilterRule filterRule = new FileTypeFileFilterRule(new TypeValue(TypeValue.FILE_TYPE), FileTypeFileFilterRule.OP_IS);
		assertTrue(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, System.currentTimeMillis())));
		assertFalse(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, true, 1024, System.currentTimeMillis())));

		filterRule = new FileTypeFileFilterRule(new TypeValue(TypeValue.FILE_TYPE), FileTypeFileFilterRule.OP_IS);
		assertTrue(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, System.currentTimeMillis())));
		assertFalse(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, true, 1024, System.currentTimeMillis())));
	}

	public void testIsntFile() {
		FileTypeFileFilterRule filterRule = new FileTypeFileFilterRule(new TypeValue(TypeValue.FILE_TYPE), FileTypeFileFilterRule.OP_ISNT);
		assertFalse(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, System.currentTimeMillis())));
		assertTrue(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, true, 1024, System.currentTimeMillis())));

		filterRule = new FileTypeFileFilterRule(new TypeValue(TypeValue.FILE_TYPE), FileTypeFileFilterRule.OP_ISNT);
		assertFalse(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, System.currentTimeMillis())));
		assertTrue(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, true, 1024, System.currentTimeMillis())));
	}

	public void testIsDirectory() {
		FileTypeFileFilterRule filterRule = new FileTypeFileFilterRule(new TypeValue(TypeValue.DIRECTORY_TYPE),
				FileTypeFileFilterRule.OP_IS);
		assertFalse(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, System.currentTimeMillis())));
		assertTrue(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, true, 1024, System.currentTimeMillis())));

		filterRule = new FileTypeFileFilterRule(new TypeValue(TypeValue.DIRECTORY_TYPE), FileTypeFileFilterRule.OP_IS);
		assertFalse(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, System.currentTimeMillis())));
		assertTrue(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, true, 1024, System.currentTimeMillis())));
	}

	public void testIsntDirectory() {
		FileTypeFileFilterRule filterRule = new FileTypeFileFilterRule(new TypeValue(TypeValue.DIRECTORY_TYPE),
				FileTypeFileFilterRule.OP_ISNT);
		assertTrue(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, System.currentTimeMillis())));
		assertFalse(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, true, 1024, System.currentTimeMillis())));

		filterRule = new FileTypeFileFilterRule(new TypeValue(TypeValue.DIRECTORY_TYPE), FileTypeFileFilterRule.OP_ISNT);
		assertTrue(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1024, System.currentTimeMillis())));
		assertFalse(filterRule.match(new TestNode("foobar.txt", "/root/foobar.txt", true, true, 1024, System.currentTimeMillis())));
	}

}
