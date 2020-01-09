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
import java.util.List;

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
import net.sourceforge.fullsync.fs.FSFile;

/**
 * An ActionDecider for destination buffered Publish/Update.
 */
public class PublishOverwriteActionDecider implements ActionDecider {
	private static final Action addDestination = new Action(ActionType.ADD, Location.DESTINATION, BufferUpdate.DESTINATION, "Add");
	private static final Action overwriteSource = new Action(ActionType.UPDATE, Location.SOURCE, BufferUpdate.DESTINATION,
		"overwrite source");
	private static final Action overwriteDestination = new Action(ActionType.UPDATE, Location.DESTINATION, BufferUpdate.DESTINATION,
		"overwrite destination");
	private static final Action updateDestination = new Action(ActionType.UPDATE, Location.DESTINATION, BufferUpdate.DESTINATION,
		"Source changed");
	private static final Action deleteDestination = new Action(ActionType.DELETE, Location.DESTINATION, BufferUpdate.DESTINATION,
		"Delete destination file", false);
	private static final Action inSync = new Action(ActionType.NOTHING, Location.NONE, BufferUpdate.NONE, "In Sync");
	private static final Action ignore = new Action(ActionType.NOTHING, Location.NONE, BufferUpdate.NONE, "Ignore");

	@Override
	public Task getTask(FSFile src, FSFile dst, StateDecider sd, BufferStateDecider bsd) throws DataParseException, IOException {
		List<Action> actions = new ArrayList<>(3);
		State state = sd.getState(src, dst);
		switch (state) {
			case ORPHAN_SOURCE:
				if (!bsd.getState(dst).equals(State.ORPHAN_SOURCE)) {
					actions.add(addDestination);
				}
				else {
					actions.add(overwriteDestination);
				}
				break;
			case ORPHAN_DESTINATION:
				actions.add(deleteDestination);
				break;
			case DIR_SOURCE_FILE_DESTINATION:
				State buff = bsd.getState(dst);
				if (buff.equals(State.ORPHAN_SOURCE)) {
					actions.add(new Action(ActionType.ADD, Location.DESTINATION, BufferUpdate.DESTINATION,
						"There was a node in buff, but its orphan, so add"));
				}
				else if (buff.equals(State.DIR_SOURCE_FILE_DESTINATION)) {
					actions.add(new Action(ActionType.NOTHING, Location.NONE, BufferUpdate.NONE,
						"dirherefilethere, but there is a dir instead of file, so its in sync"));
				}
				else {
					actions.add(new Action(ActionType.DIR_HERE_FILE_THERE_ERROR, Location.SOURCE, BufferUpdate.NONE,
						"cant update, dir here file there error occured"));
				}
				break;
			case FILE_SOURCE_DIR_DESTINATION:
				State buff1 = bsd.getState(dst);
				if (buff1.equals(State.ORPHAN_SOURCE)) {
					actions.add(new Action(ActionType.ADD, Location.SOURCE, BufferUpdate.DESTINATION,
						"There was a node in buff, but its orphan, so add"));
				}
				else if (buff1.equals(State.FILE_SOURCE_DIR_DESTINATION)) {
					actions.add(new Action(ActionType.UNEXPECTED_CHANGE_ERROR, Location.DESTINATION, BufferUpdate.NONE,
						"dirherefilethere, but there is a file instead of dir, so unexpected change"));
					// TODO ^ recompare here
				}
				else {
					actions.add(new Action(ActionType.DIR_HERE_FILE_THERE_ERROR, Location.DESTINATION, BufferUpdate.NONE,
						"cant update, dir here file there error occured"));
				}
				break;
			case FILE_CHANGE_DESTINATION:
			case FILE_CHANGE_SOURCE:
				if (bsd.getState(dst).equals(State.IN_SYNC)) {
					actions.add(updateDestination);
				}
				else {
					actions.add(overwriteDestination);
				}
				break;
			case IN_SYNC:
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
