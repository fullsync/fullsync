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

import static net.sourceforge.fullsync.Action.Add;
import static net.sourceforge.fullsync.Action.DirHereFileThereError;
import static net.sourceforge.fullsync.Action.NotDecidableError;
import static net.sourceforge.fullsync.Action.Nothing;
import static net.sourceforge.fullsync.Action.UnexpectedChangeError;
import static net.sourceforge.fullsync.Action.Update;
import static net.sourceforge.fullsync.Location.Both;
import static net.sourceforge.fullsync.Location.Buffer;
import static net.sourceforge.fullsync.Location.Destination;
import static net.sourceforge.fullsync.Location.FileSystem;
import static net.sourceforge.fullsync.Location.None;
import static net.sourceforge.fullsync.Location.Source;
import static net.sourceforge.fullsync.Location.getOpposite;

import java.io.IOException;
import java.util.Vector;

import net.sourceforge.fullsync.Action;
import net.sourceforge.fullsync.ActionDecider;
import net.sourceforge.fullsync.BufferStateDecider;
import net.sourceforge.fullsync.BufferUpdate;
import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.State;
import net.sourceforge.fullsync.StateDecider;
import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.TraversalType;
import net.sourceforge.fullsync.fs.File;

/**
 * An ActionDecider for destination buffered Publish/Update.
 *
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class PublishActionDecider implements ActionDecider {
	private static final Action addDestination = new Action(Add, Destination, BufferUpdate.Destination, "Add");
	private static final Action ignoreDestinationExists = new Action(UnexpectedChangeError, Destination, BufferUpdate.None, "will not add, destination already exists");
	private static final Action overwriteSource = new Action(Update, Source, BufferUpdate.Destination, "overwrite source");
	private static final Action overwriteDestination = new Action(Update, Destination, BufferUpdate.Destination, "overwrite destination");
	private static final Action updateDestination = new Action(Update, Destination, BufferUpdate.Destination, "Source changed");
	private static final Action unexpectedDestinationChanged = new Action(UnexpectedChangeError, Destination, BufferUpdate.None, "Destination changed");
	private static final Action unexpectedBothChanged = new Action(UnexpectedChangeError, Destination, BufferUpdate.None, "Source changed, but changed remotely too");
	private static final Action inSync = new Action(Nothing, None, BufferUpdate.None, "In Sync");
	private static final Action ignore = new Action(Nothing, None, BufferUpdate.None, "Ignore");

	@Override
	public TraversalType getTraversalType() {
		return new TraversalType();
	}

	@Override
	public Task getTask(final File src, final File dst, final StateDecider sd, final BufferStateDecider bsd) throws DataParseException, IOException {
		Vector<Action> actions = new Vector<Action>(3);
		State state = sd.getState(src, dst);
		switch (state.getType()) {
			case State.Orphan:
				if (state.getLocation() == Source) {
					if (!bsd.getState(dst).equals(State.Orphan, FileSystem)) {
						actions.add(addDestination);
					}
					else {
						actions.add(ignoreDestinationExists);
						actions.add(overwriteDestination);
					}
				}
				break;
			case State.DirHereFileThere:
				State buff = bsd.getState(dst);
				if (buff.equals(State.Orphan, Buffer)) {
					actions.add(new Action(Add, getOpposite(state.getLocation()), BufferUpdate.Destination,
							"There was a node in buff, but its orphan, so add"));
				}
				else if (buff.equals(State.DirHereFileThere, state.getLocation())) {
					if (state.getLocation() == Source) {
						actions.add(new Action(Nothing, None, BufferUpdate.None,
								"dirherefilethere, but there is a dir instead of file, so its in sync"));
					}
					else {
						actions.add(new Action(UnexpectedChangeError, Destination, BufferUpdate.None,
								"dirherefilethere, but there is a file instead of dir, so unexpected change"));
					// TODO ^ recompare here
					}
				}
				else {
					actions.add(new Action(DirHereFileThereError, state.getLocation(), BufferUpdate.None,
							"cant update, dir here file there error occured"));
				}
				break;
			case State.FileChange:
				if (bsd.getState(dst).equals(State.NodeInSync, Both)) {
					if (state.getLocation() == Source) {
						actions.add(updateDestination);
					}
					else if (state.getLocation() == Destination) {
						actions.add(unexpectedDestinationChanged);
						actions.add(overwriteDestination);
					}
				}
				else {
					actions.add(unexpectedBothChanged);
					actions.add(overwriteDestination);
				}
				break;
			case State.NodeInSync:
				// TODO this check is not neccessary, check rules whether to do or not
				// if( bsd.getState( dst ).equals( State.NodeInSync, Both ) || bsd.getState( dst ).equals( State.NodeInSync,
				// None ) )
			{
				actions.add(inSync);
				actions.add(overwriteDestination);
				actions.add(overwriteSource);
			}
				break;
			default:
				actions.add(new Action(NotDecidableError, None, BufferUpdate.None, "no rule found"));
				break;
		}

		actions.add(ignore);

		Action[] as = new Action[actions.size()];
		actions.toArray(as);
		return new Task(src, dst, state, as);
	}

}
