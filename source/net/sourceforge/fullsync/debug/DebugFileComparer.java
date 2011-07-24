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
package net.sourceforge.fullsync.debug;

import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.FileComparer;
import net.sourceforge.fullsync.Location;
import net.sourceforge.fullsync.State;
import net.sourceforge.fullsync.fs.FileAttributes;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class DebugFileComparer implements FileComparer {
	public State compareFiles(FileAttributes src, FileAttributes dst) throws DataParseException {
		if (src.getLastModified() > dst.getLastModified())
			return new State(State.FileChange, Location.Source);
		else if (src.getLastModified() < dst.getLastModified())
			return new State(State.FileChange, Location.Destination);
		else if (src.getLength() != dst.getLength())
			return new State(State.FileChange, Location.None);
		else
			return new State(State.NodeInSync, Location.Both);
	}
}
