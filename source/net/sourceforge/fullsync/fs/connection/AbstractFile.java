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
import java.util.Collection;
import java.util.Hashtable;

import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.FileAttributes;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class AbstractFile implements File {
	private static final long serialVersionUID = 1;

	protected FileSystemConnection fs;
	protected String name;
	protected String path;
	protected File parent;
	protected boolean exists;
	protected boolean filtered;
	protected boolean directory;
	protected FileAttributes attributes;
	protected Hashtable<String, File> children;

	public AbstractFile(FileSystemConnection fs, String name, String path, File parent, boolean directory, boolean exists) {
		this.fs = fs;
		this.name = name;
		this.path = path;
		this.parent = parent;
		this.exists = exists;
		this.filtered = false;
		this.directory = directory;
		this.children = null;
	}

	public FileSystemConnection getConnection() {
		return fs;
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		if (path == null)
			return parent.getPath() + "/" + name;
		else
			return path;
	}

	public File getParent() {
		return parent;
	}

	public boolean exists() {
		return exists;
	}

	public boolean isFiltered() {
		return filtered;
	}

	public void setFiltered(boolean filtered) {
		this.filtered = filtered;
	}

	public boolean isDirectory() {
		return directory;
	}

	public boolean isFile() {
		return !directory;
	}

	public boolean isBuffered() {
		return false;
	}

	public File getUnbuffered() throws IOException {
		return this;
	}

	public void setFileAttributes(FileAttributes att) {
		this.attributes = att;
	}

	public void writeFileAttributes() throws IOException {
		getConnection().writeFileAttributes(this, attributes);
	}

	public FileAttributes getFileAttributes() {
		return attributes;
	}

	public File createChild(String name, boolean directory) throws IOException {
		File f = getConnection().createChild(this, name, directory);
		children.put(name, f);
		return f;
	}

	public File getChild(String name) throws IOException {
		if (children == null)
			refresh();
		return (File) children.get(name);
	}

	@Override
	public Collection<File> getChildren() throws IOException {
		if (children == null)
			refresh();
		return children.values();
	}

	public boolean makeDirectory() throws IOException {
		if (isDirectory()) {
			if (getConnection().makeDirectory(this)) {
				exists = true;
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}

	public InputStream getInputStream() throws IOException {
		if (isFile())
			return getConnection().readFile(this);
		else
			return null;
	}

	public OutputStream getOutputStream() throws IOException {
		if (isFile()) {
			OutputStream out = getConnection().writeFile(this);
			if (out != null)
				this.exists = true;
			return out;
		}
		else {
			return null;
		}
	}

	public boolean delete() throws IOException {
		if (fs.delete(this)) {
			this.exists = false;
			return true;
		}
		else {
			return false;
		}
	}

	public void refresh() throws IOException {
		if (isDirectory()) {
			// FIXME be aware of deleting entries that may be referenced by overlaying buffer
			Hashtable<String, File> newChildren = getConnection().getChildren(this);
			if (children != null) {
				for (File n : children.values()) {
					if (!newChildren.containsKey(n.getName())) {
						if (n.exists()) {
							newChildren.put(n.getName(),
									new AbstractFile(getConnection(), n.getName(), null, n.getParent(), n.isDirectory(), false));
						}
						else {
							newChildren.put(n.getName(), n);
						}
					}
				}
			}
			children = newChildren;
		}
		else {
			// TODO update file attribute data / existing / is dir and stuff
			// HACK wtf !? this makes ftp reload the dir on every change
			parent.refresh();
		}
	}

	public void refreshBuffer() throws IOException {

	}
}
