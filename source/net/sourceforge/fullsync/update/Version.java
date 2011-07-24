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
package net.sourceforge.fullsync.update;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class Version {
	private String orig;
	private int parts[];

	public Version(String versionString) {
		this.orig = versionString;
		String[] partsString = versionString.split(".");
		parts = new int[partsString.length];

		for (int i = 0; i < parts.length; i++) {
			try {
				parts[i] = Integer.parseInt(partsString[i]);
			}
			catch (NumberFormatException nfe) {
				parts[i] = 0;
			}
		}
	}

	public int[] getParts() {
		return parts;
	}

	public boolean isHigherThan(Version version) {
		int[] otherParts = version.getParts();

		for (int i = 0;; i++) {
			if (parts.length < i)
				return false;
			else if (otherParts.length < i)
				return true;

			if (parts[i] > otherParts[i])
				return true;
		}
	}

	public String toString() {
		return orig;
	}
}
