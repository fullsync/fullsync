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

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class Crypt {
	private static String m_key = "FULLSYNC1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	public static String encrypt(String str) {
		if (str == null)
			return null;

		int pos = str.length() % m_key.length();

		StringBuffer ret = new StringBuffer();
		NumberFormat format = NumberFormat.getIntegerInstance();
		format.setMinimumIntegerDigits(3);
		format.setMaximumFractionDigits(0);
		for (int i = 0; i < str.length(); i++) {
			ret.append(format.format((int) (str.charAt(i) ^ m_key.charAt((i + pos) % m_key.length()))));
		}
		return ret.toString();
	}

	public static String decrypt(String str) {
		if (str == null)
			return null;

		int pos = (str.length() / 3) % m_key.length();

		StringBuffer ret = new StringBuffer();
		for (int i = 0; i < str.length() / 3; i++) {
			int digit;
			int number = 0;
			digit = str.charAt(i * 3);
			if (digit < '0' || digit > '9')
				return "";
			number += (digit - '0') * 100;
			digit = str.charAt(i * 3 + 1);
			if (digit < '0' || digit > '9')
				return "";
			number += (digit - '0') * 10;
			digit = str.charAt(i * 3 + 2);
			if (digit < '0' || digit > '9')
				return "";
			number += digit - '0';
			ret.append((char) (number ^ m_key.charAt((i + pos) % m_key.length())));
		}
		return ret.toString();
	}
}
