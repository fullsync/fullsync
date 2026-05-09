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
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class TextValueTest {
	@Test
	public void valueReturnsConstructorArgument() {
		assertEquals("hello", new TextValue("hello").value());
	}

	@Test
	public void toStringReturnsValue() {
		assertEquals("hello", new TextValue("hello").toString());
	}

	@Test
	public void recordEqualityMatchesOnValue() {
		assertEquals(new TextValue("hello"), new TextValue("hello"));
	}

	@Test
	public void recordEqualityDiffersOnDifferentValue() {
		assertNotEquals(new TextValue("hello"), new TextValue("world"));
	}

	@Test
	public void emptyValueIsSupported() {
		var tv = new TextValue("");
		assertEquals("", tv.value());
		assertEquals("", tv.toString());
	}
}
