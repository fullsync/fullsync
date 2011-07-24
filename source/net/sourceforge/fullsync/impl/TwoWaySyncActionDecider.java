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
import net.sourceforge.fullsync.TraversalType;
import net.sourceforge.fullsync.fs.File;

/**
 * An ActionDecider for two way sync.
 * This one is not aware of buffers !
 *
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class TwoWaySyncActionDecider implements ActionDecider {
	// TODO param keep orphans/exact copy

	private static final Action addToDestination = new Action(Action.Add, Location.Destination, BufferUpdate.Destination, "Add");
	private static final Action addToSource = new Action(Action.Add, Location.Source, BufferUpdate.Source, "Add");
	private static final Action updateDestination = new Action(Action.Update, Location.Destination, BufferUpdate.Destination,
			"source changed, update destination");
	private static final Action updateSource = new Action(Action.Update, Location.Source, BufferUpdate.Source,
			"destination changed, update source");
	private static final Action overwriteDestination = new Action(Action.Update, Location.Destination, BufferUpdate.Destination,
			"overwrite destination changes");
	private static final Action overwriteSource = new Action(Action.Update, Location.Source, BufferUpdate.Source,
			"overwrite source changes");
	private static final Action deleteDestinationOrphan = new Action(Action.Delete, Location.Destination, BufferUpdate.Destination,
			"Delete orphan in destination", false);
	private static final Action deleteSourceOrphan = new Action(Action.Delete, Location.Source, BufferUpdate.Source,
			"Delete orphan in source", false);
	private static final Action inSync = new Action(Action.Nothing, Location.None, BufferUpdate.None, "In Sync");
	private static final Action ignore = new Action(Action.Nothing, Location.None, BufferUpdate.None, "Ignore");

	@Override
	public TraversalType getTraversalType() {
		return new TraversalType();
	}

	/*
	 * public Action getDefaultAction( File src, File dst, StateDecider sd, BufferStateDecider bsd ) throws DataParseException
	 * {
	 * return getPossibleActions( src, dst, sd, bsd )[0];
	 * }
	 */
	@Override
	public Task getTask(File src, File dst, StateDecider sd, BufferStateDecider bsd) throws DataParseException, IOException {
		Vector<Action> actions = new Vector<Action>(3);
		State state = sd.getState(src, dst);
		switch (state.getType()) {
			case State.Orphan:
				if (state.getLocation() == Location.Source) {
					actions.add(addToDestination);
					actions.add(deleteSourceOrphan);
				}
				else if (state.getLocation() == Location.Destination) {
					actions.add(addToSource);
					actions.add(deleteDestinationOrphan);
				}
				break;
			case State.DirHereFileThere:
				actions.add(new Action(Action.DirHereFileThereError, state.getLocation(), BufferUpdate.None,
						"file changed from/to dir, can't overwrite"));
				break;
			case State.FileChange:
				if (state.getLocation() == Location.Source) {
					actions.add(updateDestination);
					actions.add(overwriteSource);
				}
				else if (state.getLocation() == Location.Destination) {
					actions.add(updateSource);
					actions.add(overwriteDestination);
				}
				else {
					// TODO a change but we can't tell which file is 'better' (just size changed)
				}
				break;
			case State.NodeInSync:
				actions.add(inSync);
				actions.add(overwriteDestination);
				actions.add(overwriteSource);
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
