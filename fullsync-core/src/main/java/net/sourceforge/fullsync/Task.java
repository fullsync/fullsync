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

import java.util.ArrayList;
import java.util.List;

public class Task {
	private final FSFile source;
	private final FSFile destination;
	private final State state;
	private final Action[] actions;
	private int currentAction;
	private List<Task> children;

	public Task(FSFile source, FSFile destination, State state, Action[] actions) {
		this.source = source;
		this.destination = destination;
		this.state = state;
		this.actions = actions;
	}

	public FSFile getDestination() {
		return destination;
	}

	public FSFile getSource() {
		return source;
	}

	public Action getCurrentAction() {
		return actions[currentAction];
	}

	public int getCurrentActionIndex() {
		return currentAction;
	}

	public void setCurrentAction(int i) {
		this.currentAction = i;
	}

	public State getState() {
		return state;
	}

	public Action[] getActions() {
		return actions;
	}

	public void addChild(Task child) {
		if (null == children) {
			children = new ArrayList<>(5);
		}
		children.add(child);
	}

	public List<Task> getChildren() {
		if (null == children) {
			return new ArrayList<>();
		}
		return children;
	}

	@Override
	public String toString() {
		return getCurrentAction().toString();
	}

	public int getTaskCount() {
		return 1 + getChildren().parallelStream().mapToInt(Task::getTaskCount).sum();
	}

	// HACK equals and hashCode should use more fields!!! Moreover some of the fields can be null.
	@Override
	public boolean equals(Object o) {
		if (o instanceof Task) {
			Task t = (Task) o;
			if (source.getName().equals(t.source.getName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return source.getPath().hashCode();
	}
}
