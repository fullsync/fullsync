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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.fullsync.fs.File;

public class Task implements Serializable {
	private static final long serialVersionUID = 2L;

	private File source;
	private File destination;
	private State state;
	private Action[] actions;
	private int currentAction;

	private List<Task> children;

	public Task(File source, File destination, State state, Action[] actions) {
		this.source = source;
		this.destination = destination;
		this.state = state;
		this.actions = actions;
	}

	public File getDestination() {
		return destination;
	}

	public void setDestination(File destination) {
		this.destination = destination;
	}

	public File getSource() {
		return source;
	}

	public void setSource(File source) {
		this.source = source;
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

	public void setState(State state) {
		this.state = state;
	}

	public Action[] getActions() {
		return actions;
	}

	public void setActions(Action[] actions) {
		this.actions = actions;
	}

	public void addChild(Task child) {
		if (null == children) {
			children = new ArrayList<>(5);
		}
		this.children.add(child);
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
		return 1 + getChildren().parallelStream().mapToInt(t -> t.getTaskCount()).sum();
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

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeObject(source);
		out.writeObject(destination);
		out.writeObject(state);
		out.writeObject(actions);
		out.writeInt(currentAction);
		out.writeObject(children);
	}

	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.source = (File) in.readObject();
		this.destination = (File) in.readObject();
		this.state = (State) in.readObject();
		this.actions = (Action[]) in.readObject();
		this.currentAction = in.readInt();
		this.children = (ArrayList<Task>) in.readObject();
	}
}
