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

import net.sourceforge.fullsync.DataParseException;

public class TypeValue implements OperandValue {
	public enum Type {
		FILE,
		DIRECTORY,
	}

	private final Type type;

	public TypeValue(Type type) {
		this.type = type;
	}

	public TypeValue(String type) throws DataParseException {
		for (Type t : Type.values()) {
			if (t.name().equalsIgnoreCase(type)) {
				this.type = t;
				return;
			}
		}
		throw new DataParseException(String.format("'%s' is not a valid type", type));
	}

	public Type getType() {
		return this.type;
	}

	@Override
	public String toString() {
		return type.toString();
	}

	public boolean isFile() {
		return type == Type.FILE;
	}

	public boolean isDirectory() {
		return type == Type.DIRECTORY;
	}
}
