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
package net.sourceforge.fullsync.fs.buffering.debug;

import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.FileAttributes;
import net.sourceforge.fullsync.fs.buffering.BufferedFile;
import net.sourceforge.fullsync.fs.debug.DebugNode;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class BufferedDebugNode extends DebugNode implements BufferedFile {
	private static final long serialVersionUID = 2L;

	private DebugNode unbuff;

	public BufferedDebugNode(boolean exists, boolean directory, long length, long lm) {
		super(exists, directory, length, lm);
		this.unbuff = null;
	}

	public void flushDirty() {
	}

	@Override
	public void addChild(File node) {
	}

	@Override
	public void removeChild(String name) {
	}

	public File createChild(String name) {
		return null;
	}

	@Override
	public boolean isFile() {
		return false;
	}

	@Override
	public boolean isFiltered() {
		return false;
	}

	public boolean isDirty() {
		return false;
	}

	public void markDirty() {
	}

	@Override
	public FileAttributes getFsFileAttributes() {
		return null;
	}

	@Override
	public File createChild(String name, boolean directory) {
		return null;
	}

	@Override
	public FileAttributes getFileAttributes() {
		return null;
	}

	@Override
	public void setFileAttributes(FileAttributes att) {
	}

	@Override
	public boolean isBuffered() {
		return true;
	}

	public long getFileSystemLength() {
		return getLength();
	}

	public long getFileSystemLastModified() {
		return getLastModified();
	}

	@Override
	public File getUnbuffered() {
		return unbuff;
	}

	public void setUnbuffered(DebugNode unbuff) {
		this.unbuff = unbuff;
	}

	@Override
	public String toString() {
		return super.toString() + " [FS: " + unbuff.toString() + "]";
	}

	@Override
	public void refreshReference() {
	}
}
