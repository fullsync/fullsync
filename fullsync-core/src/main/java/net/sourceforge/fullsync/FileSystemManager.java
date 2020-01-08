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

import javax.inject.Inject;

import net.sourceforge.fullsync.fs.FileSystem;
import net.sourceforge.fullsync.fs.buffering.BufferingProvider;
import net.sourceforge.fullsync.fs.buffering.syncfiles.SyncFilesBufferingProvider;
import net.sourceforge.fullsync.fs.connection.FileSystemConnection;
import net.sourceforge.fullsync.fs.filesystems.FTPFileSystem;
import net.sourceforge.fullsync.fs.filesystems.LocalFileSystem;
import net.sourceforge.fullsync.fs.filesystems.SFTPFileSystem;
import net.sourceforge.fullsync.fs.filesystems.SmbFileSystem;

public class FileSystemManager {
	public static final String BUFFER_STRATEGY_SYNCFILES = "syncfiles"; //$NON-NLS-1$
	private final FullSync fullSync;

	@Inject
	public FileSystemManager(FullSync fullSync) {
		this.fullSync = fullSync;
	}

	private FileSystem getFilesystem(final String scheme) throws FileSystemException {
		switch (scheme) {
			case "file": //$NON-NLS-1$
				return new LocalFileSystem();
			case "ftp": //$NON-NLS-1$
				return new FTPFileSystem();
			case "sftp": //$NON-NLS-1$
				return new SFTPFileSystem();
			case "smb": //$NON-NLS-1$
				return new SmbFileSystem();
			default:
				throw new FileSystemException("Unknown scheme: " + scheme); //$NON-NLS-1$
		}
	}

	public final FileSystemConnection createConnection(final ConnectionDescription desc, boolean isInteractive)
		throws FileSystemException, IOException {
		String scheme = desc.getScheme();

		FileSystem fs = getFilesystem(scheme);

		FileSystemConnection s = null;
		if (null != fs) {
			s = fs.createConnection(fullSync, desc, isInteractive);
			/* FIXME: [BUFFERING] uncomment to reenable buffering
			String bufferStrategy = desc.getParameter(ConnectionDescription.PARAMETER_BUFFER_STRATEGY);
			if ((null != bufferStrategy) && !"".equals(bufferStrategy)) {
				s = resolveBuffering(s, bufferStrategy);
			}
			 */
		}
		return s;
	}

	public FileSystemConnection resolveBuffering(final FileSystemConnection dir, final String bufferStrategy)
		throws FileSystemException, IOException {
		BufferingProvider p = null;
		if (BUFFER_STRATEGY_SYNCFILES.equals(bufferStrategy)) {
			p = new SyncFilesBufferingProvider();
		}

		if (null == p) {
			throw new FileSystemException("BufferStrategy '" + bufferStrategy + "' not found"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		return p.createBufferedSite(dir);
	}
}
