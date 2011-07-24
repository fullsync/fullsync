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
package net.sourceforge.fullsync.fs.debug;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.FileAttributes;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class DebugNode implements File {
	private static final long serialVersionUID = 1;

	private String name;
	private String path;
	private boolean directory;
	private boolean exists;
	private boolean filtered;

	private long length;
	private long lastModified;

	public DebugNode(boolean exists, boolean directory, long length, long lm) {
		this.name = "debug";
		this.path = "debug";
		this.exists = exists;
		this.directory = directory;
		this.length = length;
		this.lastModified = lm;
	}

	public File getDirectory() {
		return null;
	}

	public long getLength() {
		return length;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lm) {
		this.lastModified = lm;
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
		return null;
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
		return path;
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
	public FileAttributes getFileAttributes() {
		return null;
	}

	@Override
	public boolean isFile() {
		return false;
	}

	@Override
	public boolean isFiltered() {
		return filtered;
	}

	@Override
	public void setFiltered(boolean filtered) {
		this.filtered = filtered;
	}

	@Override
	public void setFileAttributes(FileAttributes att) {
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

	public void setLength(long length) {
		this.length = length;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String toString() {
		if (!exists)
			return "not exists";
		else if (directory)
			return "Directory";
		else
			return "File (" + length + "," + lastModified + ")";
	}
}
