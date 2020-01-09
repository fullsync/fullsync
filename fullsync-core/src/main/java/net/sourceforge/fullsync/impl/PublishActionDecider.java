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
 * An ActionDecider for destination buffered Publish/Update.
 */
public class PublishActionDecider implements ActionDecider {
	private static final Action addDestination = new Action(ActionType.ADD, DESTINATION, BufferUpdate.DESTINATION, "Add");
	private static final Action ignoreDestinationExists = new Action(ActionType.UNEXPECTED_CHANGE_ERROR, DESTINATION, BufferUpdate.NONE,
		"will not add, destination already exists");
	private static final Action overwriteSource = new Action(ActionType.UPDATE, SOURCE, BufferUpdate.DESTINATION, "overwrite source");
	private static final Action overwriteDestination = new Action(ActionType.UPDATE, DESTINATION, BufferUpdate.DESTINATION,
		"overwrite destination");
	private static final Action updateDestination = new Action(ActionType.UPDATE, DESTINATION, BufferUpdate.DESTINATION, "Source changed");
	private static final Action unexpectedDestinationChanged = new Action(ActionType.UNEXPECTED_CHANGE_ERROR, DESTINATION,
		BufferUpdate.NONE, "Destination changed");
	private static final Action unexpectedBothChanged = new Action(ActionType.UNEXPECTED_CHANGE_ERROR, DESTINATION, BufferUpdate.NONE,
		"Source changed, but changed remotely too");
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
					actions.add(ignoreDestinationExists);
					actions.add(overwriteDestination);
				}
				break;
			case DIR_SOURCE_FILE_DESTINATION:
				State buff = bsd.getState(dst);
				if (buff.equals(State.ORPHAN_SOURCE)) {
					actions.add(new Action(ActionType.ADD, DESTINATION, BufferUpdate.DESTINATION,
						"There was a node in buff, but its orphan, so add"));
				}
				else if (buff.equals(State.DIR_SOURCE_FILE_DESTINATION)) {
					actions.add(new Action(ActionType.NOTHING, NONE, BufferUpdate.NONE,
						"dirherefilethere, but there is a dir instead of file, so its in sync"));
				}
				else {
					actions.add(new Action(ActionType.DIR_HERE_FILE_THERE_ERROR, SOURCE, BufferUpdate.NONE,
						"cant update, dir here file there error occured"));
				}
				break;
			case FILE_SOURCE_DIR_DESTINATION:
				State buff1 = bsd.getState(dst);
				if (buff1.equals(State.ORPHAN_SOURCE)) {
					actions.add(
						new Action(ActionType.ADD, SOURCE, BufferUpdate.DESTINATION, "There was a node in buff, but its orphan, so add"));
				}
				else if (buff1.equals(State.FILE_SOURCE_DIR_DESTINATION)) {
					actions.add(new Action(ActionType.UNEXPECTED_CHANGE_ERROR, DESTINATION, BufferUpdate.NONE,
						"dirherefilethere, but there is a file instead of dir, so unexpected change"));
					// TODO ^ recompare here
				}
				else {
					actions.add(new Action(ActionType.DIR_HERE_FILE_THERE_ERROR, DESTINATION, BufferUpdate.NONE,
						"cant update, dir here file there error occured"));
				}
				break;
			case FILE_CHANGE_SOURCE:
				if (bsd.getState(dst).equals(State.IN_SYNC)) {
					actions.add(updateDestination);
				}
				else {
					actions.add(unexpectedBothChanged);
					actions.add(overwriteDestination);
				}
				break;
			case FILE_CHANGE_DESTINATION:
				if (bsd.getState(dst).equals(State.IN_SYNC)) {
					actions.add(unexpectedDestinationChanged);
					actions.add(overwriteDestination);
				}
				else {
					actions.add(unexpectedBothChanged);
					actions.add(overwriteDestination);
				}
				break;

			case IN_SYNC:
				// TODO this check is not neccessary, check rules whether to do or not
				// if( bsd.getState( dst ).equals( State.NodeInSync, Both ) || bsd.getState( dst ).equals( State.NodeInSync,
				// None ) )
				actions.add(inSync);
				actions.add(overwriteDestination);
				actions.add(overwriteSource);
				break;

			case ORPHAN_DESTINATION:
				break;
		}

		actions.add(ignore);

		Action[] as = new Action[actions.size()];
		actions.toArray(as);
		return new Task(src, dst, state, as);
	}
}
