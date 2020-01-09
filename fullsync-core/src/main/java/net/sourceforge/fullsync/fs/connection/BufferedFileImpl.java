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
package net.sourceforge.fullsync.fs.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import net.sourceforge.fullsync.fs.FSFile;
import net.sourceforge.fullsync.fs.buffering.BufferedFile;

class BufferedFileImpl extends FileImpl implements BufferedFile {
	protected FSFile unbuffered;
	private long fsSize;
	private long fsLastModified;

	BufferedFileImpl(BufferedFileSystemConnection bc, String name, FSFile parent, boolean directory, boolean exists) {
		super(bc, name, parent, directory, exists);
		this.unbuffered = null;
		children = new HashMap<>();
		fsSize = -1;
		fsLastModified = -1;
	}

	BufferedFileImpl(BufferedFileSystemConnection bc, FSFile unbuffered, FSFile parent, boolean directory, boolean exists) {
		this(bc, unbuffered.getName(), parent, directory, exists);
		this.unbuffered = unbuffered;
	}

	@Override
	public boolean isBuffered() {
		return true;
	}

	@Override
	public FSFile getUnbuffered() throws IOException {
		if (null == unbuffered) {
			refreshReference();
		}
		return unbuffered;
	}

	@Override
	public boolean makeDirectory() throws IOException {
		return getUnbuffered().makeDirectory();
	}

	public void setFsLastModified(long lastModified) {
		this.fsLastModified = lastModified;
	}

	public void setFsSize(long size) {
		this.fsSize = size;
	}

	@Override
	public long getFsLastModified() {
		// if the last modified timestamp has not been directly set, use the cached value
		long lm = super.getLastModified();
		if (-1 == lm) {
			lm = this.fsLastModified;
		}
		return lm;
	}

	@Override
	public long getFsSize() {
		// if the size has not been directly set, use the cached value
		long size = super.getSize();
		if (-1 == size) {
			size = this.fsSize;
		}
		return size;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return getUnbuffered().getInputStream();
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return getUnbuffered().getOutputStream();
	}

	@Override
	public void addChild(final FSFile node) {
		children.put(node.getName(), node);
	}

	@Override
	public void removeChild(final String name) {
		children.remove(name);
	}

	@Override
	public void refresh() throws IOException {
		// FIXME a dir refresh must be performed on the underlaying layer pretty carefully
		getUnbuffered().refresh();
		refreshReference();
	}

	@Override
	public void refreshBuffer() throws IOException {
		FSFile unb = getUnbuffered();
		directory = unb.isDirectory();
		exists = unb.exists();

		if (exists && !directory) {
			setFsLastModified(unb.getLastModified());
			setFsSize(unb.getSize());
		}
	}

	@Override
	public void refreshReference() throws IOException {
		unbuffered = getParent().getUnbuffered().getChild(getName());
		if (null == unbuffered) {
			unbuffered = getParent().getUnbuffered().createChild(getName(), directory);
		}
	}
}
