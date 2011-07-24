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
import java.util.Hashtable;

import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.FileAttributes;
import net.sourceforge.fullsync.fs.buffering.BufferedFile;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class AbstractBufferedFile extends AbstractFile implements BufferedFile {
	private static final long serialVersionUID = 1;

	protected File unbuffered;

	private boolean dirty;
	private FileAttributes fsAttributes;

	public AbstractBufferedFile(BufferedConnection bc, String name, String path, File parent, boolean directory, boolean exists) {
		super(bc, name, path, parent, directory, exists);
		this.dirty = false;
		this.unbuffered = null;
		children = new Hashtable<String, File>();
	}

	public AbstractBufferedFile(BufferedConnection bc, File unbuffered, File parent, boolean directory, boolean exists) {
		super(bc, unbuffered.getName(), unbuffered.getPath(), parent, directory, exists);
		this.dirty = false;
		this.unbuffered = unbuffered;
		children = new Hashtable<String, File>();
	}

	public boolean isDirty() {
		return dirty;
	}

	public void markDirty() {
		dirty = true;
	}

	@Override
	public boolean isBuffered() {
		return true;
	}

	@Override
	public File getUnbuffered() throws IOException {
		if (unbuffered == null)
			refreshReference();
		return unbuffered;
	}

	@Override
	public boolean makeDirectory() throws IOException {
		return getUnbuffered().makeDirectory();
	}

	public void setFsFileAttributes(FileAttributes fs) {
		this.fsAttributes = fs;
	}

	public FileAttributes getFsFileAttributes() {
		return fsAttributes;
	}

	@Override
	public FileAttributes getFileAttributes() {
		// in case we are requesting file attributes that
		// were not explicitly set, just take the fs attributes
		FileAttributes attrib = super.getFileAttributes();
		if (attrib == null)
			return fsAttributes;
		else
			return attrib;
	}

	public void clearCachedFileAttributes() throws IOException {
		setFileAttributes(getFsFileAttributes());
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return getUnbuffered().getInputStream();
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return getUnbuffered().getOutputStream();
	}

	public void addChild(File node) {
		children.put(node.getName(), node);
	}

	public void removeChild(String name) {
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
		directory = getUnbuffered().isDirectory();
		exists = getUnbuffered().exists();

		if (exists && !directory)
			setFsFileAttributes(getUnbuffered().getFileAttributes());
	}

	public void refreshReference() throws IOException {
		unbuffered = getParent().getUnbuffered().getChild(getName());
		if (unbuffered == null)
			unbuffered = getParent().getUnbuffered().createChild(getName(), directory);
	}

}
