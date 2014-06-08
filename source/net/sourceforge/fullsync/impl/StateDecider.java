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

import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.FileComparer;
import net.sourceforge.fullsync.Location;
import net.sourceforge.fullsync.State;
import net.sourceforge.fullsync.fs.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StateDecider implements net.sourceforge.fullsync.StateDecider {
	private static final Logger logger = LoggerFactory.getLogger(StateDecider.class.getSimpleName());
	private static final State inSyncNone = new State(State.NodeInSync, Location.None);
	private static final State orphanSrc = new State(State.Orphan, Location.Source);
	private static final State orphanDst = new State(State.Orphan, Location.Destination);
	private static final State inSyncBoth = new State(State.NodeInSync, Location.Both);
	private static final State dirFileSrc = new State(State.DirHereFileThere, Location.Source);
	private static final State dirFileDst = new State(State.DirHereFileThere, Location.Destination);

	protected FileComparer comparer;

	public StateDecider(FileComparer comparer) {
		this.comparer = comparer;
	}

	@Override
	public State getState(final File source, final File destination) throws DataParseException {
		logger.debug(source + " vs. " + destination);
		if (!source.exists()) {
			if (!destination.exists()) {
				logger.debug("both missing"); // FIXME: impossible?!
				return inSyncNone;
			}
			else {
				logger.debug("source missing");
				return orphanDst;
			}
		}
		else if (!destination.exists()) {
			logger.debug("destination missing");
			return orphanSrc;
		}

		if (source.isDirectory()) {
			if (destination.isDirectory()) {
				logger.debug("both are dirs");
				return inSyncBoth;
			}
			else {
				logger.debug("source directory, destination file");
				return dirFileSrc;
			}
		}
		else if (destination.isDirectory()) {
			logger.debug("source file, destination directory");
			return dirFileDst;
		}

		return comparer.compareFiles(source, destination);
	}
}
