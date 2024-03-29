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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import net.sourceforge.fullsync.FSFile;
import net.sourceforge.fullsync.FileSystemConnection;

class FileImpl implements FSFile {
	protected final FileSystemConnection fs;
	protected final String name;
	protected final FSFile parent;
	protected boolean exists;
	protected boolean directory;
	protected Map<String, FSFile> children;
	protected long size;
	protected long lastModified;

	FileImpl(FileSystemConnection fs, String name, FSFile parent, boolean directory, boolean exists) {
		this.fs = fs;
		this.name = name;
		this.parent = parent;
		this.exists = exists;
		this.directory = directory;
		this.children = null;
		this.size = -1;
		this.lastModified = -1;
	}

	public static FileImpl root(FileSystemConnection fs, boolean directory, boolean exists) {
		return new FileImpl(fs, "", null, directory, exists);
	}

	public FileSystemConnection getConnection() {
		return fs;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getFsAbsolutePath() {
		StringBuilder sb = new StringBuilder();
		if (null == parent) {
			sb.append(fs.getConnectionDescription().getPath());
		}
		else {
			sb.append(parent.getFsAbsolutePath());
		}
		if (sb.length() > 0) {
			if ('/' != sb.charAt(sb.length() - 1)) {
				sb.append('/');
			}
		}
		if (!name.isEmpty()) {
			sb.append(name);
		}
		return sb.toString();
	}

	@Override
	public String getDisplayPath() {
		StringBuilder sb = new StringBuilder();
		if (null != parent) {
			sb.append(parent.getDisplayPath());
		}
		// root has parent = null, name = "" and path = ""
		// this check avoids all paths starting with //
		if (sb.length() > 0) {
			sb.append('/');
		}
		sb.append(name);
		return sb.toString();
	}

	@Override
	public FSFile getParent() {
		return parent;
	}

	@Override
	public boolean exists() {
		return exists;
	}

	@Override
	public boolean isDirectory() {
		return directory;
	}

	@Override
	public boolean isFile() {
		return !directory;
	}

	@Override
	public boolean isBuffered() {
		return false;
	}

	@Override
	public FSFile getUnbuffered() throws IOException {
		return this;
	}

	@Override
	public void setLastModified(final long lastModified) {
		this.lastModified = lastModified;
	}

	@Override
	public void writeFileAttributes() throws IOException {
		getConnection().writeFileAttributes(this);
	}

	@Override
	public long getLastModified() {
		return lastModified;
	}

	@Override
	public long getSize() {
		return size;
	}

	@Override
	public void setSize(long size) {
		this.size = size;
	}

	@Override
	public FSFile createChild(final String name, final boolean directory) throws IOException {
		var f = getConnection().createChild(this, name, directory);
		children.put(name, f);
		return f;
	}

	@Override
	public FSFile getChild(final String name) throws IOException {
		if (null == children) {
			refresh();
		}
		return children.get(name);
	}

	@Override
	public Collection<FSFile> getChildren() throws IOException {
		if (null == children) {
			refresh();
		}
		return children.values();
	}

	@Override
	public boolean makeDirectory() throws IOException {
		if (isDirectory() && getConnection().makeDirectory(this)) {
			exists = true;
			return true;
		}
		return false;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		if (isFile()) {
			return getConnection().readFile(this);
		}
		return null;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		OutputStream out = null;
		if (isFile()) {
			out = getConnection().writeFile(this);
			if (null != out) {
				this.exists = true;
			}
		}
		return out;
	}

	@Override
	public boolean delete() throws IOException {
		if (fs.delete(this)) {
			this.exists = false;
			return true;
		}
		return false;
	}

	@Override
	public void refresh() throws IOException {
		if (isDirectory()) {
			refreshDirectory();
		}
		else {
			// TODO update file attribute data / existing / is dir and stuff
			// HACK wtf !? this makes ftp reload the dir on every change
			parent.refresh();
		}
	}

	private void refreshDirectory() throws IOException {
		// FIXME be aware of deleting entries that may be referenced by overlaying buffer
		var newChildren = getConnection().getChildren(this);
		if (null != children) {
			for (FSFile n : children.values()) {
				if (!newChildren.containsKey(n.getName())) {
					if (n.exists()) {
						var f = new FileImpl(getConnection(), n.getName(), n.getParent(), n.isDirectory(), false);
						newChildren.put(n.getName(), f);
					}
					else {
						newChildren.put(n.getName(), n);
					}
				}
			}
		}
		children = newChildren;
	}

	@Override
	public String toString() {
		var sb = new StringBuilder();
		sb.append(name);
		sb.append("; "); //$NON-NLS-1$
		if ((size >= 0) || (lastModified > 0)) {
			if (size >= 0) {
				sb.append(size);
				sb.append(" Bytes"); //$NON-NLS-1$
			}
			if (lastModified > 0) {
				sb.append(' ');
				sb.append(new Date(lastModified));
			}
		}
		else {
			sb.append('-');
		}
		return sb.toString();
	}
}
