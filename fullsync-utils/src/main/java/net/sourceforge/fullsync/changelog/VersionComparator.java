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
package net.sourceforge.fullsync.changelog;

import java.util.Comparator;

public class VersionComparator implements Comparator<String> {
	private int[] parseVersion(String v) {
		String[] components = v.split("\\."); //$NON-NLS-1$
		int[] numbers = new int[components.length];
		for (int i = 0; i < components.length; ++i) {
			if (!components[i].isEmpty()) {
				numbers[i] = Integer.parseInt(components[i]);
			}
		}
		return numbers;
	}

	@Override
	public int compare(String o1, String o2) {
		int[] v1components = parseVersion(o1);
		int[] v2components = parseVersion(o2);
		int num = Math.max(v1components.length, v2components.length);
		for (int i = 0; i < num; ++i) {
			int v1 = i < v1components.length ? v1components[i] : 0;
			int v2 = i < v2components.length ? v2components[i] : 0;
			if (v1 > v2) {
				return 1;
			}
			if (v1 < v2) {
				return -1;
			}
		}
		return 0;
	}
}
