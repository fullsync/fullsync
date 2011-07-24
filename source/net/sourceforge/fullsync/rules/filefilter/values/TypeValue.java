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
 * Created on Jun 6, 2005
 */
package net.sourceforge.fullsync.rules.filefilter.values;

/**
 * @author Michele Aiello
 */
public class TypeValue implements OperandValue {

	private static final long serialVersionUID = 2L;

	public static final int FILE_TYPE = 0;
	public static final int DIRECTORY_TYPE = 1;

	private static final String[] valueNames = new String[] { "file", "directory" };

	private int type;

	public TypeValue() {
		this.type = FILE_TYPE;
	}

	public TypeValue(int type) {
		this.type = type;
	}

	public TypeValue(String type) {
		fromString(type);
	}

	public void setType(int fileType) {
		if ((fileType < FILE_TYPE) || (fileType > DIRECTORY_TYPE)) {
			// TODO exception?
		}
		this.type = fileType;
	}

	public int getType() {
		return this.type;
	}

	@Override
	public void fromString(String str) {
		for (int i = 0; i < valueNames.length; i++) {
			if (valueNames[i].equalsIgnoreCase(str)) {
				this.type = i;
				return;
			}
		}
	}

	@Override
	public String toString() {
		return valueNames[type];
	}

	public boolean isFile() {
		return (type == FILE_TYPE);
	}

	public boolean isDirectory() {
		return (type == DIRECTORY_TYPE);
	}

	public static String[] getAllTypes() {
		return valueNames;
	}

}
