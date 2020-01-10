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

import net.sourceforge.fullsync.BufferStateDecider;
import net.sourceforge.fullsync.BufferedFile;
import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.FSFile;
import net.sourceforge.fullsync.FileComparer;
import net.sourceforge.fullsync.State;

public class BufferStateDeciderImpl extends StateDeciderImpl implements BufferStateDecider {
	public BufferStateDeciderImpl(FileComparer comparer) {
		super(comparer);
	}

	@Override
	public State getState(FSFile buffered) throws DataParseException, IOException {
		if (!buffered.isBuffered()) {
			return State.IN_SYNC;
		}

		FSFile source = buffered.getUnbuffered();
		BufferedFile destination = (BufferedFile) buffered;

		if (!source.exists()) {
			if (!destination.exists()) {
				return State.IN_SYNC;
			}
			else {
				return State.ORPHAN_DESTINATION;
			}
		}
		else if (!destination.exists()) {
			return State.ORPHAN_SOURCE;
		}

		if (source.isDirectory()) {
			if (destination.isDirectory()) {
				return State.IN_SYNC;
			}
			else {
				return State.DIR_SOURCE_FILE_DESTINATION;
			}
		}
		else if (destination.isDirectory()) {
			return State.FILE_SOURCE_DIR_DESTINATION;
		}

		return comparer.compareFiles(source, destination);
	}
}
