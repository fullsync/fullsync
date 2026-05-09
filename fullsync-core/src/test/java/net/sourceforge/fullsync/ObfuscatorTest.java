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
package net.sourceforge.fullsync;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class ObfuscatorTest {
	@Test
	public void obfuscateNullReturnsNull() {
		assertNull(Obfuscator.obfuscate(null));
	}

	@Test
	public void deobfuscateNullReturnsNull() {
		assertNull(Obfuscator.deobfuscate(null));
	}

	@Test
	public void roundtripEmptyString() {
		assertEquals("", Obfuscator.deobfuscate(Obfuscator.obfuscate("")));
	}

	@Test
	public void roundtripSingleChar() {
		assertEquals("a", Obfuscator.deobfuscate(Obfuscator.obfuscate("a")));
	}

	@Test
	public void roundtripAlphanumericString() {
		var input = "HelloWorld123";
		assertEquals(input, Obfuscator.deobfuscate(Obfuscator.obfuscate(input)));
	}

	@Test
	public void roundtripStringWithSpecialChars() {
		var input = "p@$$w0rd!#%";
		assertEquals(input, Obfuscator.deobfuscate(Obfuscator.obfuscate(input)));
	}

	@Test
	public void roundtripLongString() {
		var input = "FULLSYNC1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZfullsync";
		assertEquals(input, Obfuscator.deobfuscate(Obfuscator.obfuscate(input)));
	}

	@Test
	public void deobfuscateEmptyStringReturnsEmpty() {
		assertEquals("", Obfuscator.deobfuscate(""));
	}

	@Test
	public void deobfuscateInvalidNonNumericCharsReturnsEmpty() {
		assertEquals("", Obfuscator.deobfuscate("abc"));
	}
}
