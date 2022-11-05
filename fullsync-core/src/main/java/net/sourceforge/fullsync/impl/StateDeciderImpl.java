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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.FileComparer;
import net.sourceforge.fullsync.State;
import net.sourceforge.fullsync.StateDecider;
import net.sourceforge.fullsync.fs.FSFile;

public class StateDeciderImpl implements StateDecider {
	private static final Logger logger = LoggerFactory.getLogger(StateDeciderImpl.class.getSimpleName());
	protected final FileComparer comparer;

	public StateDeciderImpl(FileComparer comparer) {
		this.comparer = comparer;
	}

	@Override
	public State getState(final FSFile source, final FSFile destination) throws DataParseException {
		logger.debug("{} vs. {}", source, destination); //$NON-NLS-1$
		if (!source.exists()) {
			if (!destination.exists()) {
				logger.debug("both missing"); // FIXME: impossible?! //$NON-NLS-1$
				return State.IN_SYNC;
			}
			else {
				logger.debug("source missing"); //$NON-NLS-1$
				return State.ORPHAN_DESTINATION;
			}
		}
		else if (!destination.exists()) {
			logger.debug("destination missing"); //$NON-NLS-1$
			return State.ORPHAN_SOURCE;
		}

		if (source.isDirectory()) {
			if (destination.isDirectory()) {
				logger.debug("both are dirs"); //$NON-NLS-1$
				return State.IN_SYNC;
			}
			else {
				logger.debug("source directory, destination file"); //$NON-NLS-1$
				return State.DIR_SOURCE_FILE_DESTINATION;
			}
		}
		else if (destination.isDirectory()) {
			logger.debug("source file, destination directory"); //$NON-NLS-1$
			return State.FILE_SOURCE_DIR_DESTINATION;
		}

		return comparer.compareFiles(source, destination);
	}
}
