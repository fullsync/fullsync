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
package net.sourceforge.fullsync.rules.filefilter.values;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.sourceforge.fullsync.DataParseException;

import org.junit.jupiter.api.Test;

public class TypeValueTest {
	@Test
	public void parseFileType() throws Exception {
		var tv = new TypeValue("FILE");
		assertTrue(tv.isFile());
		assertFalse(tv.isDirectory());
		assertEquals(TypeValue.Type.FILE, tv.getType());
	}

	@Test
	public void parseDirectoryType() throws Exception {
		var tv = new TypeValue("DIRECTORY");
		assertTrue(tv.isDirectory());
		assertFalse(tv.isFile());
		assertEquals(TypeValue.Type.DIRECTORY, tv.getType());
	}

	@Test
	public void parseIsCaseInsensitive() throws Exception {
		var tv = new TypeValue("file");
		assertTrue(tv.isFile());
	}

	@Test
	public void parseInvalidTypeThrowsDataParseException() {
		assertThrows(DataParseException.class, () -> new TypeValue("SYMLINK"));
	}

	@Test
	public void constructorWithEnumSetsType() {
		var tv = new TypeValue(TypeValue.Type.DIRECTORY);
		assertTrue(tv.isDirectory());
	}

	@Test
	public void toStringReturnsTypeName() {
		assertEquals("FILE", new TypeValue(TypeValue.Type.FILE).toString());
		assertEquals("DIRECTORY", new TypeValue(TypeValue.Type.DIRECTORY).toString());
	}
}
