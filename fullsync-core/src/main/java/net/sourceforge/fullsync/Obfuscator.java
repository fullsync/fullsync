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

import java.text.NumberFormat;

public class Obfuscator {
	private static final String OBFUSCATION_KEY = "FULLSYNC1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ"; //$NON-NLS-1$

	private Obfuscator() {
	}

	public static String obfuscate(String str) {
		if (null == str) {
			return null;
		}

		var pos = str.length() % OBFUSCATION_KEY.length();

		var ret = new StringBuilder();
		var format = NumberFormat.getIntegerInstance();
		format.setMinimumIntegerDigits(3);
		format.setMaximumFractionDigits(0);
		for (var i = 0; i < str.length(); i++) {
			ret.append(format.format(str.charAt(i) ^ OBFUSCATION_KEY.charAt((i + pos) % OBFUSCATION_KEY.length())));
		}
		return ret.toString();
	}

	public static String deobfuscate(String str) {
		if (null == str) {
			return null;
		}

		var pos = (str.length() / 3) % OBFUSCATION_KEY.length();

		var ret = new StringBuilder();
		for (var i = 0; i < (str.length() / 3); i++) {
			int digit;
			var number = 0;
			digit = str.charAt(i * 3);
			if ((digit < '0') || (digit > '9')) {
				return ""; //$NON-NLS-1$
			}
			number += (digit - '0') * 100;
			digit = str.charAt((i * 3) + 1);
			if ((digit < '0') || (digit > '9')) {
				return ""; //$NON-NLS-1$
			}
			number += (digit - '0') * 10;
			digit = str.charAt((i * 3) + 2);
			if ((digit < '0') || (digit > '9')) {
				return ""; //$NON-NLS-1$
			}
			number += digit - '0';
			ret.append((char) (number ^ OBFUSCATION_KEY.charAt((i + pos) % OBFUSCATION_KEY.length())));
		}
		return ret.toString();
	}
}
