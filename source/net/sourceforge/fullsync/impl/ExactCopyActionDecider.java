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
package net.sourceforge.fullsync.impl;

import static net.sourceforge.fullsync.Location.Destination;
import static net.sourceforge.fullsync.Location.Source;

import java.io.IOException;
import java.util.Vector;

import net.sourceforge.fullsync.Action;
import net.sourceforge.fullsync.ActionDecider;
import net.sourceforge.fullsync.BufferStateDecider;
import net.sourceforge.fullsync.BufferUpdate;
import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.Location;
import net.sourceforge.fullsync.State;
import net.sourceforge.fullsync.StateDecider;
import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.fs.File;

/**
 * An ActionDecider for source to destination exact copy.
 * FIXME actually this one is the backup one with one line less :-/
 *
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class ExactCopyActionDecider implements ActionDecider {
	// TODO param keep orphans/exact copy

	private static final Action addDestination = new Action(Action.Add, Location.Destination, BufferUpdate.Destination, "Add");
	private static final Action overwriteDestination = new Action(Action.Update, Location.Destination, BufferUpdate.Destination,
			"overwrite destination");
	private static final Action updateDestination = new Action(Action.Update, Location.Destination, BufferUpdate.Destination,
			"Source changed");
	private static final Action deleteDestinationOrphan = new Action(Action.Delete, Location.Destination, BufferUpdate.Destination,
			"Delete orphan in destination", false);
	private static final Action inSync = new Action(Action.Nothing, Location.None, BufferUpdate.None, "In Sync");
	private static final Action ignore = new Action(Action.Nothing, Location.None, BufferUpdate.None, "Ignore");

	@Override
	public Task getTask(final File src, final File dst, final StateDecider sd, final BufferStateDecider bsd) throws DataParseException, IOException {
		Vector<Action> actions = new Vector<Action>(3);
		State state = sd.getState(src, dst);
		switch (state.getType()) {
			case State.Orphan:
				if (state.getLocation() == Location.Source) {
					if (!bsd.getState(dst).equals(State.Orphan, Location.FileSystem)) {
						actions.add(addDestination);
					}
					else {
						actions.add(overwriteDestination);
					}
				}
				else if (state.getLocation() == Location.Destination) {
					actions.add(deleteDestinationOrphan);
				}
				break;
			case State.DirHereFileThere:
				State buff = bsd.getState(dst);
				if (buff.equals(State.Orphan, Location.Buffer)) {
					actions.add(new Action(Action.Add, Location.Destination, BufferUpdate.Destination,
							"There was a node in buff, but its orphan, so add"));
				}
				else if (buff.equals(State.DirHereFileThere, state.getLocation())) {
					if (state.getLocation() == Location.Source) {
						actions.add(new Action(Action.Nothing, Location.None, BufferUpdate.Destination,
								"dirherefilethere, but there is a dir instead of file, so its in sync"));
					}
					else {
						actions.add(new Action(Action.DirHereFileThereError, Location.Destination, BufferUpdate.None,
								"file changed from/to dir, can't overwrite"));
						// TODO ^ recompare here
					}
				}
				else {
					actions.add(new Action(Action.DirHereFileThereError, state.getLocation(), BufferUpdate.None,
							"cant update, dir here file there error occured"));
				}
				break;
			case State.FileChange:
				if (bsd.getState(dst).equals(State.NodeInSync, Location.Both)) {
					if (state.getLocation() == Source) {
						actions.add(updateDestination);
					}
					else if (state.getLocation() == Destination) {
						actions.add(overwriteDestination);
					}
				}
				else {
					actions.add(overwriteDestination);
				}
				break;
			case State.NodeInSync:
				// TODO this check is not neccessary, check rules whether to do or not
				// if( bsd.getState( dst ).equals( State.NodeInSync, Location.Both ) || bsd.getState( dst ).equals( State.NodeInSync,
				// Location.None ) )
			{
				actions.add(inSync);
				actions.add(overwriteDestination);
			}
			break;
			default:
				actions.add(new Action(Action.NotDecidableError, Location.None, BufferUpdate.None, "no rule found"));
				break;
		}

		actions.add(ignore);

		Action[] as = new Action[actions.size()];
		actions.toArray(as);
		return new Task(src, dst, state, as);
	}

}
