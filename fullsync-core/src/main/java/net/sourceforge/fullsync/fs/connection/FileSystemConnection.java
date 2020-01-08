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
import java.util.Map;

import org.apache.commons.vfs2.FileObject;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.fs.File;

public interface FileSystemConnection extends AutoCloseable {
	File getRoot();

	// open ?
	void flush() throws IOException;

	boolean isCaseSensitive();

	boolean isAvailable(); // reachable, correct auth,...

	FileObject getBase();

	ConnectionDescription getConnectionDescription();

	File createChild(File parent, String name, boolean directory) throws IOException;

	Map<String, File> getChildren(File dir) throws IOException;

	// refresh file, refresh directory ?

	boolean makeDirectory(File dir) throws IOException;

	boolean writeFileAttributes(File file) throws IOException;

	InputStream readFile(File file) throws IOException;

	OutputStream writeFile(File file) throws IOException;

	boolean delete(File node) throws IOException;
}
