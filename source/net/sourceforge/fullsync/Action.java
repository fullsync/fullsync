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
public class Action implements Serializable {
	private static final long serialVersionUID = 2L;

	public static final int Nothing = 0;
	public static final int Add = 1;
	public static final int Update = 2;
	public static final int Delete = 3;

	public static final int NotDecidableError = 10;
	public static final int UnexpectedChangeError = 11;
	public static final int DirHereFileThereError = 12;

	public static final String[] names = new String[] { "Nothing", "Add", "Update", "Delete" };
	public static final String[] errorNames = new String[] { "NotDecidableError", "UnexpectedChangeError", "DirHereFileThereError" };

	private int type;
	private int location;
	private int bufferUpdate;
	private boolean beforeRecursion;
	private String explanation;

	public Action(int type, int location, int bufferUpdate, String explanation) {
		this(type, location, bufferUpdate, explanation, true);
	}

	public Action(int type, int location, int bufferUpdate, String explanation, boolean beforeRecursion) {
		this.type = type;
		this.location = location;
		this.bufferUpdate = bufferUpdate;
		this.beforeRecursion = beforeRecursion;
		this.explanation = explanation;
	}

	public int getType() {
		return type;
	}

	public int getLocation() {
		return location;
	}

	public int getBufferUpdate() {
		return bufferUpdate;
	}

	public boolean isBeforeRecursion() {
		return beforeRecursion;
	}

	public String getExplanation() {
		return explanation;
	}

	public boolean isError() {
		return (type >= 10);
	}

	public static String toString(int type) {
		if (type >= 10) {
			return errorNames[type - 10];
		}
		else {
			return names[type];
		}
	}

	@Override
	public String toString() {
		return "[" + toString(type) + "(" + Location.toString(location) + ") - " + explanation + "]";
	}

	public boolean equalsExceptExplanation(Action action) {
		return ((getType() == action.getType()) && (getLocation() == action.getLocation()) && (getBufferUpdate() == action.getBufferUpdate()) && (isBeforeRecursion() == action
				.isBeforeRecursion()));
	}
}
