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

import java.io.IOException;
import java.util.ArrayList;

import net.sourceforge.fullsync.Action;
import net.sourceforge.fullsync.ActionDecider;
import net.sourceforge.fullsync.ActionType;
import net.sourceforge.fullsync.BufferStateDecider;
import net.sourceforge.fullsync.BufferUpdate;
import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.Location;
import net.sourceforge.fullsync.State;
import net.sourceforge.fullsync.StateDecider;
import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.fs.File;

/**
 * An ActionDecider for destination buffered Publish/Update.
 */
public class PublishOverwriteActionDecider implements ActionDecider {
	private static final Action addDestination = new Action(ActionType.Add, Location.Destination, BufferUpdate.Destination, "Add");
	private static final Action overwriteSource = new Action(ActionType.Update, Location.Source, BufferUpdate.Destination,
			"overwrite source");
	private static final Action overwriteDestination = new Action(ActionType.Update, Location.Destination, BufferUpdate.Destination,
			"overwrite destination");
	private static final Action updateDestination = new Action(ActionType.Update, Location.Destination, BufferUpdate.Destination,
			"Source changed");
	private static final Action deleteDestination = new Action(ActionType.Delete, Location.Destination, BufferUpdate.Destination,
			"Delete destination file", false);
	private static final Action inSync = new Action(ActionType.Nothing, Location.None, BufferUpdate.None, "In Sync");
	private static final Action ignore = new Action(ActionType.Nothing, Location.None, BufferUpdate.None, "Ignore");

	@Override
	public Task getTask(File src, File dst, StateDecider sd, BufferStateDecider bsd) throws DataParseException, IOException {
		ArrayList<Action> actions = new ArrayList<Action>(3);
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
				actions.add(deleteDestination);
				break;
			case DirSourceFileDestination:
				State buff = bsd.getState(dst);
				if (buff.equals(State.OrphanSource)) {
					actions.add(new Action(ActionType.Add, Location.Destination, BufferUpdate.Destination,
							"There was a node in buff, but its orphan, so add"));
				}
				else if (buff.equals(State.DirSourceFileDestination)) {
					actions.add(new Action(ActionType.Nothing, Location.None, BufferUpdate.None,
							"dirherefilethere, but there is a dir instead of file, so its in sync"));
				}
				else {
					actions.add(new Action(ActionType.DirHereFileThereError, Location.Source, BufferUpdate.None,
							"cant update, dir here file there error occured"));
				}
				break;
			case FileSourceDirDestination:
				State buff1 = bsd.getState(dst);
				if (buff1.equals(State.OrphanSource)) {
					actions.add(new Action(ActionType.Add, Location.Source, BufferUpdate.Destination,
							"There was a node in buff, but its orphan, so add"));
				}
				else if (buff1.equals(State.FileSourceDirDestination)) {
					actions.add(new Action(ActionType.UnexpectedChangeError, Location.Destination, BufferUpdate.None,
							"dirherefilethere, but there is a file instead of dir, so unexpected change"));
					// TODO ^ recompare here
				}
				else {
					actions.add(new Action(ActionType.DirHereFileThereError, Location.Destination, BufferUpdate.None,
							"cant update, dir here file there error occured"));
				}
				break;
			case FileChangeDestination:
			case FileChangeSource:
				if (bsd.getState(dst).equals(State.InSync)) {
					actions.add(updateDestination);
				}
				else {
					actions.add(overwriteDestination);
				}
				break;
			case InSync:
				// TODO this check is not neccessary, check rules whether to do or not
				// if( bsd.getState( dst ).equals( State.NodeInSync, Location.Both ) || bsd.getState( dst ).equals( State.NodeInSync,
				// Location.None ) )
				actions.add(inSync);
				actions.add(overwriteDestination);
				actions.add(overwriteSource);
				break;
		}

		actions.add(ignore);

		Action[] as = new Action[actions.size()];
		actions.toArray(as);
		return new Task(src, dst, state, as);
	}

}
