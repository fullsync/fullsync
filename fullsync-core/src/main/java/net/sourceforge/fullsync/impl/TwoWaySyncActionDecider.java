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
import net.sourceforge.fullsync.FSFile;
import net.sourceforge.fullsync.Location;
import net.sourceforge.fullsync.State;
import net.sourceforge.fullsync.StateDecider;
import net.sourceforge.fullsync.Task;

/**
 * An ActionDecider for two way sync.
 * This one is not aware of buffers !
 */
public class TwoWaySyncActionDecider implements ActionDecider {
	// TODO param keep orphans/exact copy
	private static final Action addToDestination = new Action(ActionType.ADD, Location.DESTINATION, BufferUpdate.DESTINATION, "Add");
	private static final Action addToSource = new Action(ActionType.ADD, Location.SOURCE, BufferUpdate.SOURCE, "Add");
	private static final Action updateDestination = new Action(ActionType.UPDATE, Location.DESTINATION, BufferUpdate.DESTINATION,
		"source changed, update destination");
	private static final Action updateSource = new Action(ActionType.UPDATE, Location.SOURCE, BufferUpdate.SOURCE,
		"destination changed, update source");
	private static final Action overwriteDestination = new Action(ActionType.UPDATE, Location.DESTINATION, BufferUpdate.DESTINATION,
		"overwrite destination changes");
	private static final Action overwriteSource = new Action(ActionType.UPDATE, Location.SOURCE, BufferUpdate.SOURCE,
		"overwrite source changes");
	private static final Action deleteDestinationOrphan = new Action(ActionType.DELETE, Location.DESTINATION, BufferUpdate.DESTINATION,
		"Delete orphan in destination", false);
	private static final Action deleteSourceOrphan = new Action(ActionType.DELETE, Location.SOURCE, BufferUpdate.SOURCE,
		"Delete orphan in source", false);
	private static final Action inSync = new Action(ActionType.NOTHING, Location.NONE, BufferUpdate.NONE, "In Sync");
	private static final Action ignore = new Action(ActionType.NOTHING, Location.NONE, BufferUpdate.NONE, "Ignore");

	@Override
	public Task getTask(final FSFile src, final FSFile dst, StateDecider sd, BufferStateDecider bsd)
		throws DataParseException, IOException {
		List<Action> actions = new ArrayList<>(3);
		State state = sd.getState(src, dst);
		switch (state) {
			case ORPHAN_SOURCE:
				actions.add(addToDestination);
				actions.add(deleteSourceOrphan);
				break;
			case ORPHAN_DESTINATION:
				actions.add(addToSource);
				actions.add(deleteDestinationOrphan);
				break;
			case DIR_SOURCE_FILE_DESTINATION:
				actions.add(new Action(ActionType.DIR_HERE_FILE_THERE_ERROR, Location.DESTINATION, BufferUpdate.NONE,
					"file changed from/to dir, can't overwrite"));
				break;
			case FILE_SOURCE_DIR_DESTINATION:
				actions.add(new Action(ActionType.DIR_HERE_FILE_THERE_ERROR, Location.SOURCE, BufferUpdate.NONE,
					"file changed from/to dir, can't overwrite"));
				break;
			case FILE_CHANGE_SOURCE:
				actions.add(updateDestination);
				actions.add(overwriteSource);
				break;
			case FILE_CHANGE_DESTINATION:
				actions.add(updateSource);
				actions.add(overwriteDestination);
				break;

			case FILE_CHANGE_UNKNOWN:
				// TODO a change but we can't tell which file is 'better' (just size changed)
				break;
			case IN_SYNC:
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
