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
 * An ActionDecider for two way sync.
 * This one is not aware of buffers !
 */
public class TwoWaySyncActionDecider implements ActionDecider {
	// TODO param keep orphans/exact copy

	private static final Action addToDestination = new Action(ActionType.Add, Location.Destination, BufferUpdate.Destination, "Add");
	private static final Action addToSource = new Action(ActionType.Add, Location.Source, BufferUpdate.Source, "Add");
	private static final Action updateDestination = new Action(ActionType.Update, Location.Destination, BufferUpdate.Destination,
		"source changed, update destination");
	private static final Action updateSource = new Action(ActionType.Update, Location.Source, BufferUpdate.Source,
		"destination changed, update source");
	private static final Action overwriteDestination = new Action(ActionType.Update, Location.Destination, BufferUpdate.Destination,
		"overwrite destination changes");
	private static final Action overwriteSource = new Action(ActionType.Update, Location.Source, BufferUpdate.Source,
		"overwrite source changes");
	private static final Action deleteDestinationOrphan = new Action(ActionType.Delete, Location.Destination, BufferUpdate.Destination,
		"Delete orphan in destination", false);
	private static final Action deleteSourceOrphan = new Action(ActionType.Delete, Location.Source, BufferUpdate.Source,
		"Delete orphan in source", false);
	private static final Action inSync = new Action(ActionType.Nothing, Location.None, BufferUpdate.None, "In Sync");
	private static final Action ignore = new Action(ActionType.Nothing, Location.None, BufferUpdate.None, "Ignore");

	@Override
	public Task getTask(final File src, final File dst, StateDecider sd, BufferStateDecider bsd) throws DataParseException, IOException {
		ArrayList<Action> actions = new ArrayList<>(3);
		State state = sd.getState(src, dst);
		switch (state) {
			case OrphanSource:
				actions.add(addToDestination);
				actions.add(deleteSourceOrphan);
				break;
			case OrphanDestination:
				actions.add(addToSource);
				actions.add(deleteDestinationOrphan);
				break;
			case DirSourceFileDestination:
				actions.add(new Action(ActionType.DirHereFileThereError, Location.Destination, BufferUpdate.None,
					"file changed from/to dir, can't overwrite"));
				break;
			case FileSourceDirDestination:
				actions.add(new Action(ActionType.DirHereFileThereError, Location.Source, BufferUpdate.None,
					"file changed from/to dir, can't overwrite"));
				break;
			case FileChangeSource:
				actions.add(updateDestination);
				actions.add(overwriteSource);
				break;
			case FileChangeDestination:
				actions.add(updateSource);
				actions.add(overwriteDestination);
				break;

			case FileChangeUnknown:
				// TODO a change but we can't tell which file is 'better' (just size changed)
				break;
			case InSync:
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
