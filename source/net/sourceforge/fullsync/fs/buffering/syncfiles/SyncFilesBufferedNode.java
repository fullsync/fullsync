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
/*
 * Created on 18.07.2004
 */
package net.sourceforge.fullsync.fs.buffering.syncfiles;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.FileAttributes;
import net.sourceforge.fullsync.fs.buffering.BufferedFile;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class SyncFilesBufferedNode implements BufferedFile {
	private static final long serialVersionUID = 2L;

	protected BufferedFile parent;
	protected File unbuff;
	protected boolean dirty;
	protected String name;
	protected Hashtable<String, File> children;
	protected String syncBufferFilename;
	protected boolean directory;
	protected boolean exists;
	protected boolean filtered;
	protected FileAttributes fsAttributes;
	protected FileAttributes attributes;

	public SyncFilesBufferedNode(String name, File unbuff, String syncBufferFilename, BufferedFile parent, boolean directory, boolean exists) {
		this.parent = parent;
		this.unbuff = unbuff;
		this.name = name;
		this.syncBufferFilename = syncBufferFilename;
		this.directory = directory;
		this.exists = exists;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getPath() {
		if (unbuff == null) {
			return parent.getPath() + "/" + name;
		}
		else {
			return unbuff.getPath();
		}
	}

	@Override
	public File getParent() {
		return parent;
	}

	@Override
	public boolean isBuffered() {
		return true;
	}

	public boolean isDirty() {
		return dirty;
	}

	@Override
	public File getUnbuffered() {
		return unbuff;
	}

	public void markDirty() {
		dirty = true;
	}

	@Override
	public String toString() {
		return "Buffered: " + unbuff.toString();
	}

	@Override
	public boolean exists() {
		return exists;
	}

	public String toBufferLine() {
		if (isDirectory()) {
			return "D\t" + getName();
		}
		else {
			return "F\t" + getName() + "\t" + getFileAttributes().getLength() + "\t" + getFileAttributes().getLastModified() + "\t"
					+ getFsFileAttributes().getLength() + "\t" + getFsFileAttributes().getLastModified();
		}
	}

	protected void loadFromBuffer() throws IOException {
		try {
			File node = unbuff.getChild(syncBufferFilename);
			if ((node == null) || !node.exists() || node.isDirectory())
			 {
				return; // TODO clear children list ?
			}

			String line;
			File f = node;
			ByteArrayOutputStream out = new ByteArrayOutputStream((int) f.getFileAttributes().getLength());

			InputStream in = f.getInputStream();
			byte[] block = new byte[1024];
			while ((in.read(block)) > 0) {
				out.write(block);
			}
			in.close();
			out.close();

			BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(out.toByteArray())));
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split("\t");
				if (parts.length < 2) {
					continue;
				}
				File n = unbuff.getChild(parts[1]);
				if (n == null) {
					n = unbuff.createChild(parts[1], parts[0].equals("D"));
				}
				SyncFilesBufferedNode syncnode = new SyncFilesBufferedNode(parts[1], n, syncBufferFilename, this, parts[0].equals("D"),
						true);
				if (parts[0].equals("F")) {
					syncnode.setFileAttributes(new FileAttributes(Long.parseLong(parts[2]), Long.parseLong(parts[3])));
					syncnode.setFsFileAttributes(new FileAttributes(Long.parseLong(parts[4]), Long.parseLong(parts[5])));
				}
				children.put(parts[1], syncnode);
			}
			reader.close();
		}
		catch (IOException ioe) {
			ExceptionHandler.reportException(ioe);
		}
	}

	public void saveToBuffer() {
		try {
			File node = unbuff.getChild(syncBufferFilename);

			if (node == null) {
				node = unbuff.createChild(syncBufferFilename, false);
			}
			else if (node.isDirectory()) {
				return; // FIXME throw exception, log error, whatever
			}
			// TODO avoid writing empty files

			File f = node;
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(f.getOutputStream()));
			Collection<File> items = getChildren();
			for (File element : items) {
				SyncFilesBufferedNode n = (SyncFilesBufferedNode) element;
				if (n.exists()) {
					writer.write(n.toBufferLine());
					writer.write('\n');
				}
			}
			writer.close();
		}
		catch (IOException ioe) {
			ExceptionHandler.reportException(ioe);
		}
	}

	public void flushDirty() {
		if (isDirty()) {
			saveToBuffer();
		}
		for (Enumeration<File> e = children.elements(); e.hasMoreElements();) {
			BufferedFile n = (BufferedFile) e.nextElement();
			if (n.exists())
			 {
				;
			// n.flushDirty(); //FIXME!
			}
		}
	}

	@Override
	public File createChild(String name, boolean directory) throws IOException {
		markDirty();
		File n = unbuff.getChild(name);
		if (n == null) {
			n = unbuff.createChild(name, directory);
		}
		SyncFilesBufferedNode bn = new SyncFilesBufferedNode(name, n, syncBufferFilename, this, directory, false);
		children.put(name, bn);
		return bn;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return unbuff.getInputStream();
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		markDirty();
		return unbuff.getOutputStream();
	}

	@Override
	public FileAttributes getFileAttributes() {
		return attributes;
	}

	@Override
	public void writeFileAttributes() throws IOException {

	}

	@Override
	public void setFileAttributes(FileAttributes attributes) {
		this.attributes = attributes;
	}

	@Override
	public FileAttributes getFsFileAttributes() {
		return fsAttributes;
	}

	public void setFsFileAttributes(FileAttributes fsAttributes) {
		this.fsAttributes = fsAttributes;
	}

	@Override
	public boolean isDirectory() {
		return directory;
	}

	@Override
	public boolean makeDirectory() throws IOException {
		markDirty();
		// parent.markDirty();
		return unbuff.makeDirectory();
	}

	@Override
	public File getChild(String name) {
		Object obj = children.get(name);
		if (obj == null) {
			return null;
		}
		else {
			return (File) obj;
		}
	}

	@Override
	public Collection<File> getChildren() {
		return children.values();
	}

	@Override
	public void addChild(File node) {
		children.put(node.getName(), node);
	}

	@Override
	public void refreshReference() throws IOException {
		unbuff = getParent().getUnbuffered().getChild(getName());
	}

	@Override
	public void removeChild(String name) {
		children.remove(name);
	}

	@Override
	public boolean delete() throws IOException {
		markDirty();
		return unbuff.delete();
	}

	@Override
	public boolean isFile() {
		return !directory;
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
	public void refresh() {
		// TODO refresh()
	}

	@Override
	public void refreshBuffer() {
		// TODO refreshBuffer()
	}
}
