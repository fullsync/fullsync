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
package net.sourceforge.fullsync.fs.filesystems;

import java.io.IOException;

import javax.inject.Inject;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.FileSystemException;
import net.sourceforge.fullsync.fs.FileSystem;
import net.sourceforge.fullsync.fs.connection.CommonsVfsConnection;
import net.sourceforge.fullsync.fs.connection.FileSystemConnection;

public class FTPFileSystem implements FileSystem {
	@Inject
	public FTPFileSystem() {
	}

	@Override
	public final FileSystemConnection createConnection(final ConnectionDescription connectionDescription, boolean interactive)
		throws FileSystemException, IOException {
		return new CommonsVfsConnection(connectionDescription, new FTPAuthenticationProvider());
	}
}
