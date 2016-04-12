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
import static net.sourceforge.fullsync.Location.None;
import static net.sourceforge.fullsync.Location.Source;

import java.io.IOException;
import java.util.ArrayList;

import net.sourceforge.fullsync.Action;
import net.sourceforge.fullsync.ActionDecider;
import net.sourceforge.fullsync.ActionType;
import net.sourceforge.fullsync.BufferStateDecider;
import net.sourceforge.fullsync.BufferUpdate;
import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.State;
import net.sourceforge.fullsync.StateDecider;
import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.fs.File;

/**
 * An ActionDecider for source to destination backup.
 */
public class BackupActionDecider implements ActionDecider {
	// TODO param keep orphans/exact copy

	private static final Action addDestination = new Action(ActionType.Add, Destination, BufferUpdate.Destination, "Add");
	private static final Action overwriteDestination = new Action(ActionType.Update, Destination, BufferUpdate.Destination, "overwrite destination");
	private static final Action updateDestination = new Action(ActionType.Update, Destination, BufferUpdate.Destination, "Source changed");
	private static final Action deleteDestinationOrphan = new Action(ActionType.Delete, Destination, BufferUpdate.Destination, "Delete orphan in destination", false);
	private static final Action ignoreDestinationOrphan = new Action(ActionType.Nothing, None, BufferUpdate.None, "Ignoring orphan in destination");
	private static final Action inSync = new Action(ActionType.Nothing, None, BufferUpdate.None, "In Sync");
	private static final Action ignore = new Action(ActionType.Nothing, None, BufferUpdate.None, "Ignore");

	@Override
	public Task getTask(final File src, final File dst, final StateDecider sd, final BufferStateDecider bsd)
			throws DataParseException, IOException {
		ArrayList<Action> actions = new ArrayList<>(3);
		State state = sd.getState(src, dst);
		switch (state) {
			case OrphanSource:
				if (!bsd.getState(dst).equals(State.OrphanSource)) {
					actions.add(addDestination);
				}
				else {
					actions.add(overwriteDestination);
				}
				break;
			case OrphanDestination:
				actions.add(ignoreDestinationOrphan);
				actions.add(deleteDestinationOrphan);
				break;
			case DirSourceFileDestination:
			case FileSourceDirDestination:
				State buff = bsd.getState(dst);
				if (buff.equals(State.OrphanDestination)) {
					actions.add(new Action(ActionType.Add, Destination, BufferUpdate.Destination,
							"There was a node in buff, but its orphan, so add"));
				}
				else if (buff.equals(state)) {
					if (state.equals(State.DirSourceFileDestination)) {
						actions.add(new Action(ActionType.Nothing, None, BufferUpdate.Destination,
								"dirherefilethere, but there is a dir instead of file, so its in sync"));
					}
					else {
						actions.add(new Action(ActionType.DirHereFileThereError, Destination, BufferUpdate.None,
								"file changed from/to dir, can't overwrite"));
						// TODO ^ recompare here
					}
				}
				else {
					if (state.equals(State.DirSourceFileDestination)) {
						actions.add(new Action(ActionType.DirHereFileThereError, Source, BufferUpdate.None, "cant update, dir here file there error occured"));
					}
					else {
						actions.add(new Action(ActionType.DirHereFileThereError, Destination, BufferUpdate.None, "cant update, dir here file there error occured"));
					}
				}
				break;
			case FileChangeSource:
				if (bsd.getState(dst).equals(State.InSync)) {
					actions.add(updateDestination);
				}
				break;
			case FileChangeDestination:
				if (bsd.getState(dst).equals(State.InSync)) {
					actions.add(overwriteDestination);
				}
				break;
			case InSync:
				// TODO this check is not neccessary, check rules whether to do or not
				// if( bsd.getState( dst ).equals( State.NodeInSync, Both ) || bsd.getState( dst ).equals( State.NodeInSync,
				// None ) )
				actions.add(inSync);
				actions.add(overwriteDestination);
				break;
		}

		actions.add(ignore);

		Action[] as = new Action[actions.size()];
		actions.toArray(as);
		return new Task(src, dst, state, as);
	}

}
