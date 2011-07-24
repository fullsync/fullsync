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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.FileAttributes;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class LocalConnection implements FileSystemConnection {
	private static final long serialVersionUID = 1;

	private java.io.File base;
	private AbstractFile root;

	public LocalConnection(java.io.File base) {
		this.base = base;
		this.root = new AbstractFile(this, ".", ".", null, true, base.exists());
	}

	public File getRoot() {
		return root;
	}

	public boolean isAvailable() {
		return base.exists() || base.mkdirs();
	}

	public File createChild(File parent, String name, boolean directory) {
		return new AbstractFile(this, name, null, parent, directory, false);
	}

	public File buildNode(File parent, java.io.File file) {
		String name = file.getName();
		// String path = parent.getPath()+"/"+name;

		File n = new AbstractFile(this, name, null, parent, file.isDirectory(), true);
		if (file.isFile()) {
			n.setFileAttributes(new FileAttributes(file.length(), file.lastModified()));
		}
		return n;
	}

	public Hashtable getChildren(File dir) {
		if (dir.exists()) {
			java.io.File f = new java.io.File(base + "/" + dir.getPath());
			java.io.File[] files = f.listFiles();

			if (files == null)
				return new Hashtable();

			Hashtable table = new Hashtable(files.length);
			if (files != null)
				for (int i = 0; i < files.length; i++) {
					java.io.File file = files[i];
					if (file.isFile() || file.isDirectory())
						table.put(file.getName(), buildNode(dir, file));
				}
			return table;
		}
		else {
			return new Hashtable();
		}
	}

	public boolean makeDirectory(File dir) {
		java.io.File f = new java.io.File(base + "/" + dir.getPath());
		return f.mkdirs();
	}

	public boolean writeFileAttributes(File file, FileAttributes att) {
		java.io.File f = new java.io.File(base + "/" + file.getPath());
		f.setLastModified(att.getLastModified());
		return true;
	}

	public InputStream readFile(File file) {
		try {
			java.io.File f = new java.io.File(base + "/" + file.getPath());
			return new FileInputStream(f);
		}
		catch (FileNotFoundException e) {
			ExceptionHandler.reportException(e);
			return null;
		}
	}

	public OutputStream writeFile(File file) {
		try {
			java.io.File f = new java.io.File(base + "/" + file.getPath());
			return new FileOutputStream(f);
		}
		catch (FileNotFoundException e) {
			ExceptionHandler.reportException(e);
			return null;
		}
	}

	public boolean delete(File node) {
		java.io.File f = new java.io.File(base + "/" + node.getPath());
		return f.delete();
	}

	public void flush() throws IOException {

	}

	public void close() throws IOException {

	}

	public String getUri() {
		return base.toURI().toString();
	}

	public boolean isCaseSensitive() {
		// TODO find out whether current fs is case sensitive
		return false;
	}
}
