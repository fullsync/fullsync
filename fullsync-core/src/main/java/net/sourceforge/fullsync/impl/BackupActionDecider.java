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

import static net.sourceforge.fullsync.Location.DESTINATION;
import static net.sourceforge.fullsync.Location.NONE;
import static net.sourceforge.fullsync.Location.SOURCE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.fullsync.Action;
import net.sourceforge.fullsync.ActionDecider;
import net.sourceforge.fullsync.ActionType;
import net.sourceforge.fullsync.BufferStateDecider;
import net.sourceforge.fullsync.BufferUpdate;
import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.State;
import net.sourceforge.fullsync.StateDecider;
import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.fs.FSFile;

/**
 * An ActionDecider for source to destination backup.
 */
public class BackupActionDecider implements ActionDecider {
	// TODO param keep orphans/exact copy
	private static final Action addDestination = new Action(ActionType.ADD, DESTINATION, BufferUpdate.DESTINATION, "Add");
	private static final Action overwriteDestination = new Action(ActionType.UPDATE, DESTINATION, BufferUpdate.DESTINATION,
		"overwrite destination");
	private static final Action updateDestination = new Action(ActionType.UPDATE, DESTINATION, BufferUpdate.DESTINATION, "Source changed");
	private static final Action deleteDestinationOrphan = new Action(ActionType.DELETE, DESTINATION, BufferUpdate.DESTINATION,
		"Delete orphan in destination", false);
	private static final Action ignoreDestinationOrphan = new Action(ActionType.NOTHING, NONE, BufferUpdate.NONE,
		"Ignoring orphan in destination");
	private static final Action inSync = new Action(ActionType.NOTHING, NONE, BufferUpdate.NONE, "In Sync");
	private static final Action ignore = new Action(ActionType.NOTHING, NONE, BufferUpdate.NONE, "Ignore");

	@Override
	public Task getTask(final FSFile src, final FSFile dst, final StateDecider sd, final BufferStateDecider bsd)
		throws DataParseException, IOException {

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
				actions.add(ignoreDestinationOrphan);
				actions.add(deleteDestinationOrphan);
				break;
			case DIR_SOURCE_FILE_DESTINATION:
			case FILE_SOURCE_DIR_DESTINATION:
				State buff = bsd.getState(dst);
				if (buff.equals(State.ORPHAN_DESTINATION)) {
					actions.add(new Action(ActionType.ADD, DESTINATION, BufferUpdate.DESTINATION,
						"There was a node in buff, but its orphan, so add"));
				}
				else if (buff.equals(state)) {
					if (state.equals(State.DIR_SOURCE_FILE_DESTINATION)) {
						actions.add(new Action(ActionType.NOTHING, NONE, BufferUpdate.DESTINATION,
							"dirherefilethere, but there is a dir instead of file, so its in sync"));
					}
					else {
						actions.add(new Action(ActionType.DIR_HERE_FILE_THERE_ERROR, DESTINATION, BufferUpdate.NONE,
							"file changed from/to dir, can't overwrite"));
						// TODO ^ recompare here
					}
				}
				else {
					if (state.equals(State.DIR_SOURCE_FILE_DESTINATION)) {
						String explanation = "cant update, dir here file there error occured";
						actions.add(new Action(ActionType.DIR_HERE_FILE_THERE_ERROR, SOURCE, BufferUpdate.NONE, explanation));
					}
					else {
						String explanation = "cant update, dir here file there error occured";
						actions.add(new Action(ActionType.DIR_HERE_FILE_THERE_ERROR, DESTINATION, BufferUpdate.NONE, explanation));
					}
				}
				break;
			case FILE_CHANGE_SOURCE:
				if (bsd.getState(dst).equals(State.IN_SYNC)) {
					actions.add(updateDestination);
				}
				break;
			case FILE_CHANGE_DESTINATION:
				if (bsd.getState(dst).equals(State.IN_SYNC)) {
					actions.add(overwriteDestination);
				}
				break;
			case IN_SYNC:
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
