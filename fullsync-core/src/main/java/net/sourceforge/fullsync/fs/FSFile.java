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
package net.sourceforge.fullsync.fs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

public interface FSFile {
	String getName();

	String getPath();

	FSFile getParent();

	boolean isDirectory();

	boolean isFile();

	boolean exists();

	boolean isBuffered();

	FSFile getUnbuffered() throws IOException;

	default void refreshBuffer() throws IOException {
	}

	void writeFileAttributes() throws IOException;

	long getLastModified();

	void setLastModified(long lastModified);

	long getSize();

	void setSize(long size);

	Collection<FSFile> getChildren() throws IOException;

	FSFile getChild(String name) throws IOException;

	// TODO currently, 'create' isn't the right word
	// they do not exist before and may not exists after sync
	FSFile createChild(String name, boolean directory) throws IOException;

	void refresh() throws IOException;

	boolean makeDirectory() throws IOException;

	InputStream getInputStream() throws IOException;

	OutputStream getOutputStream() throws IOException;

	boolean delete() throws IOException;
}
