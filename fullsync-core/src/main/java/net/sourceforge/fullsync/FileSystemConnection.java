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
package net.sourceforge.fullsync;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.vfs2.FileObject;

public interface FileSystemConnection extends AutoCloseable {
	FSFile getRoot();

	// open ?
	void flush() throws IOException;

	boolean isCaseSensitive();

	boolean isAvailable(); // reachable, correct auth,...

	FileObject getBase();

	ConnectionDescription getConnectionDescription();

	FSFile createChild(FSFile parent, String name, boolean directory) throws IOException;

	Map<String, FSFile> getChildren(FSFile dir) throws IOException;

	// refresh file, refresh directory ?

	boolean makeDirectory(FSFile dir) throws IOException;

	boolean writeFileAttributes(FSFile file) throws IOException;

	InputStream readFile(FSFile file) throws IOException;

	OutputStream writeFile(FSFile file) throws IOException;

	boolean delete(FSFile node) throws IOException;
}
