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
 * Created on May 28, 2005
 */
package net.sourceforge.fullsync.rules;

import java.util.regex.Pattern;

import net.sourceforge.fullsync.fs.File;

/**
 * @author Michele Aiello
 */
public class WildcardRule implements Rule {

	private Pattern pattern;

	public WildcardRule(String pattern) {
		this.pattern = Pattern.compile(toRegExp(pattern));
	}

	@Override
	public boolean accepts(File node) {
		return pattern.matcher(node.getName()).matches();
	}

	private String toRegExp(String pattern) {
		StringBuffer buff = new StringBuffer(pattern.length() * 2);

		for (int i = 0; i < pattern.length(); i++) {
			char c = pattern.charAt(i);
			switch (c) {
				case '*':
				case '?':
					buff.append('.');
					buff.append(c);
					break;
				case '.':
				case '/':
				case '\\':
					buff.append('\\');
					buff.append(c);
					break;
				default:
					buff.append(c);
			}
		}
		return buff.toString();
	}

}
