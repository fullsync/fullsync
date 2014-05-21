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

public class Action implements Serializable {
	private static final long serialVersionUID = 2L;

	private ActionType type;
	private Location location;
	private BufferUpdate bufferUpdate;
	private boolean beforeRecursion;
	private String explanation;

	public Action(ActionType type, Location location, BufferUpdate bufferUpdate, String explanation) {
		this(type, location, bufferUpdate, explanation, true);
	}

	public Action(ActionType type, Location location, BufferUpdate bufferUpdate, String explanation, boolean beforeRecursion) {
		this.type = type;
		this.location = location;
		this.bufferUpdate = bufferUpdate;
		this.beforeRecursion = beforeRecursion;
		this.explanation = explanation;
	}

	public ActionType getType() {
		return type;
	}

	public Location getLocation() {
		return location;
	}

	public BufferUpdate getBufferUpdate() {
		return bufferUpdate;
	}

	public boolean isBeforeRecursion() {
		return beforeRecursion;
	}

	public String getExplanation() {
		return explanation;
	}

	public boolean isError() {
		return type.isError();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[" + type.toString() + "(" + getLocation().toString() + ") BU: ");
		sb.append(bufferUpdate.toString() + "; Rec: ");
		sb.append(isBeforeRecursion());
		sb.append(" - " + explanation + "]");
		return sb.toString();
	}

	public boolean equalsExceptExplanation(Action action) {
		return ((getType() == action.getType()) && (getLocation() == action.getLocation())
				&& (getBufferUpdate() == action.getBufferUpdate()) && (isBeforeRecursion() == action.isBeforeRecursion()));
	}
}
