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

import java.io.Serializable;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class State implements Serializable {
	private static final long serialVersionUID = 2L;

	public static final int NodeInSync = 1;
	public static final int Orphan = 2;
	public static final int FileChange = 3;
	public static final int DirHereFileThere = 4;

	public static final String[] names = new String[] { "", "NodeInSync", "Orphan", "FileChange", "DirHereFileThere" };

	private int type;
	private int location;

	public State(int type, int location) {
		this.type = type;
		this.location = location;
	}

	public int getLocation() {
		return location;
	}

	public int getType() {
		return type;
	}

	public boolean equals(int type, int location) {
		return ((this.type == type) && (this.location == location));
	}

	public static String toString(int type) {
		return names[type];
	}

	@Override
	public String toString() {
		return State.toString(type) + " - " + Location.toString(location);
	}

}