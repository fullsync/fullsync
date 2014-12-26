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
package net.sourceforge.fullsync.rules.filefilter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Date;

import net.sourceforge.fullsync.fs.File;

public class TestNode implements File {
	private static final long serialVersionUID = 2L;
	private File parent;
	private String name;
	private boolean directory;
	private boolean exists;
	private long lastModified;
	private long size;

	public TestNode(String name, File parent, boolean exists, boolean directory, long length, long lm) {
		this.name = name;
		this.parent = parent;
		this.exists = exists;
		this.directory = directory;
		this.lastModified = lm;
		this.size = length;
	}

	public static TestNode createRoot(boolean exists, long lm) {
		return new TestNode("", null, exists, true, 0, lm);
	}

	public File getDirectory() {
		return null;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return null;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return null;
	}

	@Override
	public File getParent() {
		return parent;
	}

	@Override
	public Collection<File> getChildren() {
		return null;
	}

	@Override
	public File getChild(String name) {
		return null;
	}

	public File createDirectory(String name) {
		return null;
	}

	public File createFile(String name) {
		return null;
	}

	@Override
	public boolean makeDirectory() {
		return true;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getPath() {
		StringBuilder sb = new StringBuilder();
		if (null != parent) {
			sb.append(parent.getPath());
		}
		// root has parent = null, name = "" and path = ""
		// this check avoids all paths starting with /
		if (sb.length() > 0) {
			sb.append('/');
		}
		sb.append(name);
		return sb.toString();
	}

	@Override
	public boolean isDirectory() {
		return directory;
	}

	@Override
	public boolean exists() {
		return exists;
	}

	@Override
	public boolean isBuffered() {
		return false;
	}

	@Override
	public File getUnbuffered() {
		return null;
	}

	@Override
	public boolean delete() {
		return false;
	}

	@Override
	public void refresh() {

	}

	@Override
	public void refreshBuffer() {
	}

	@Override
	public File createChild(String name, boolean directory) {
		return null;
	}

	@Override
	public File buildChildNode(String name, boolean directory, boolean exists) {
		return new TestNode(name, this, exists, directory, 0, -1);
	}

	@Override
	public boolean isFile() {
		return !directory;
	}

	@Override
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	@Override
	public void writeFileAttributes() throws IOException {

	}

	public void setDirectory(boolean directory) {
		this.directory = directory;
	}

	public void setExists(boolean exists) {
		this.exists = exists;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		sb.append("; ");
		if ((size >= 0) || (lastModified > 0)) {
			if (size >= 0) {
				sb.append(size);
				sb.append(" Bytes");
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
}
