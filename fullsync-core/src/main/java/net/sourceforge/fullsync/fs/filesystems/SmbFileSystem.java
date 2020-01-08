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

import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;
import org.apache.commons.vfs2.provider.smb.SmbFileProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.FileSystemException;
import net.sourceforge.fullsync.FullSync;
import net.sourceforge.fullsync.fs.FileSystem;
import net.sourceforge.fullsync.fs.connection.CommonsVfsConnection;
import net.sourceforge.fullsync.fs.connection.FileSystemConnection;

public class SmbFileSystem implements FileSystem {
	private static final Logger logger = LoggerFactory.getLogger(SmbFileSystem.class);
	static {
		// even tough VFS-552 is fixed this si still needed
		// [VFS-552][sandbox] include vfs-providers.xml in JAR for dynamic registration of mime and smb providers.
		try {
			FileSystemManager fsm = VFS.getManager();
			if (!fsm.hasProvider("smb") && (fsm instanceof DefaultFileSystemManager)) { //$NON-NLS-1$
				DefaultFileSystemManager dfsm = (DefaultFileSystemManager) fsm;
				dfsm.addProvider("smb", new SmbFileProvider()); //$NON-NLS-1$
			}
		}
		catch (org.apache.commons.vfs2.FileSystemException ex) {
			logger.warn("Failed to add SMB file system provider", ex); //$NON-NLS-1$
		}
	}

	@Override
	public final FileSystemConnection createConnection(final FullSync fullsync, final ConnectionDescription description,
		boolean isInteractive) throws FileSystemException {
		return new CommonsVfsConnection(description, new SmbAuthProvider());
	}
}
