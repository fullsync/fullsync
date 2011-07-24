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
import net.sourceforge.fullsync.fs.Site;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface FileSystemConnection extends Site {
	public File createChild(File parent, String name, boolean directory) throws IOException;

	// public Directory getParent( File node );
	public Hashtable<String, File> getChildren(File dir) throws IOException;

	// refresh file, refresh directory ?

	public boolean makeDirectory(File dir) throws IOException;

	public boolean writeFileAttributes(File file, FileAttributes att) throws IOException;

	public InputStream readFile(File file) throws IOException;

	public OutputStream writeFile(File file) throws IOException;

	public boolean delete(File node) throws IOException;
}
