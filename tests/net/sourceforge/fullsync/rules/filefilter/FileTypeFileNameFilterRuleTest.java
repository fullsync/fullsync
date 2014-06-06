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
package net.sourceforge.fullsync.rules.filefilter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.rules.filefilter.values.TypeValue;

import org.junit.Test;

public class FileTypeFileNameFilterRuleTest {

	private File root = new TestNode("root", null, true, true, 0, 0);

	@Test
	public void testIsFile() {
		FileTypeFileFilterRule filterRule = new FileTypeFileFilterRule(new TypeValue(TypeValue.Type.FILE), FileTypeFileFilterRule.OP_IS);
		assertTrue(filterRule.match(new TestNode("foobar.txt", root, true, false, 1024, System.currentTimeMillis())));
		assertFalse(filterRule.match(new TestNode("foobar.txt", root, true, true, 1024, System.currentTimeMillis())));

		filterRule = new FileTypeFileFilterRule(new TypeValue(TypeValue.Type.FILE), FileTypeFileFilterRule.OP_IS);
		assertTrue(filterRule.match(new TestNode("foobar.txt", root, true, false, 1024, System.currentTimeMillis())));
		assertFalse(filterRule.match(new TestNode("foobar.txt", root, true, true, 1024, System.currentTimeMillis())));
	}

	@Test
	public void testIsntFile() {
		FileTypeFileFilterRule filterRule = new FileTypeFileFilterRule(new TypeValue(TypeValue.Type.FILE), FileTypeFileFilterRule.OP_ISNT);
		assertFalse(filterRule.match(new TestNode("foobar.txt", root, true, false, 1024, System.currentTimeMillis())));
		assertTrue(filterRule.match(new TestNode("foobar.txt", root, true, true, 1024, System.currentTimeMillis())));

		filterRule = new FileTypeFileFilterRule(new TypeValue(TypeValue.Type.FILE), FileTypeFileFilterRule.OP_ISNT);
		assertFalse(filterRule.match(new TestNode("foobar.txt", root, true, false, 1024, System.currentTimeMillis())));
		assertTrue(filterRule.match(new TestNode("foobar.txt", root, true, true, 1024, System.currentTimeMillis())));
	}

	@Test
	public void testIsDirectory() {
		FileTypeFileFilterRule filterRule = new FileTypeFileFilterRule(new TypeValue(TypeValue.Type.DIRECTORY),
				FileTypeFileFilterRule.OP_IS);
		assertFalse(filterRule.match(new TestNode("foobar.txt", root, true, false, 1024, System.currentTimeMillis())));
		assertTrue(filterRule.match(new TestNode("foobar.txt", root, true, true, 1024, System.currentTimeMillis())));

		filterRule = new FileTypeFileFilterRule(new TypeValue(TypeValue.Type.DIRECTORY), FileTypeFileFilterRule.OP_IS);
		assertFalse(filterRule.match(new TestNode("foobar.txt", root, true, false, 1024, System.currentTimeMillis())));
		assertTrue(filterRule.match(new TestNode("foobar.txt", root, true, true, 1024, System.currentTimeMillis())));
	}

	@Test
	public void testIsntDirectory() {
		FileTypeFileFilterRule filterRule = new FileTypeFileFilterRule(new TypeValue(TypeValue.Type.DIRECTORY),
				FileTypeFileFilterRule.OP_ISNT);
		assertTrue(filterRule.match(new TestNode("foobar.txt", root, true, false, 1024, System.currentTimeMillis())));
		assertFalse(filterRule.match(new TestNode("foobar.txt", root, true, true, 1024, System.currentTimeMillis())));

		filterRule = new FileTypeFileFilterRule(new TypeValue(TypeValue.Type.DIRECTORY), FileTypeFileFilterRule.OP_ISNT);
		assertTrue(filterRule.match(new TestNode("foobar.txt", root, true, false, 1024, System.currentTimeMillis())));
		assertFalse(filterRule.match(new TestNode("foobar.txt", root, true, true, 1024, System.currentTimeMillis())));
	}

}
