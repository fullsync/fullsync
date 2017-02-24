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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import net.sourceforge.fullsync.fs.FileSystem;
import net.sourceforge.fullsync.fs.Site;
import net.sourceforge.fullsync.fs.buffering.BufferingProvider;
import net.sourceforge.fullsync.fs.buffering.syncfiles.SyncFilesBufferingProvider;
import net.sourceforge.fullsync.fs.filesystems.FTPFileSystem;
import net.sourceforge.fullsync.fs.filesystems.LocalFileSystem;
import net.sourceforge.fullsync.fs.filesystems.SFTPFileSystem;
import net.sourceforge.fullsync.fs.filesystems.SmbFileSystem;

public class FileSystemManager {

	private FileSystem getFilesystem(final String scheme) {
		if ("file".equals(scheme)) {
			return new LocalFileSystem();
		}
		if ("ftp".equals(scheme)) {
			return new FTPFileSystem();
		}
		if ("sftp".equals(scheme)) {
			return new SFTPFileSystem();
		}
		if ("smb".equals(scheme)) {
			return new SmbFileSystem();
		}
		return null;
	}

	public final Site createConnection(final FullSync fullsync, final ConnectionDescription desc) throws FileSystemException, IOException, URISyntaxException {
		URI url = desc.getUri();
		String scheme = url.getScheme();

		FileSystem fs = getFilesystem(scheme);

		if (fs == null) {
			// TODO maybe we should test and correct this in profile dialog !?
			// no fs found, test for native path
			File f = new File(url.toString()); // ignore query as local won't need query
			if (f.exists()) {
				fs = getFilesystem("file");
				url = f.toURI();
				desc.setUri(url);
			}
			else {
				throw new URISyntaxException(url.toString(), "Not a valid uri or unknown scheme");
			}
		}

		Site s = fs.createConnection(fullsync, desc);

		/* FIXME: [BUFFERING] uncomment to reenable buffering
		String bufferStrategy = desc.getParameter("bufferStrategy");

		if ((null != bufferStrategy) && !"".equals(bufferStrategy)) {
			s = resolveBuffering(s, bufferStrategy);
		}
		*/
		return s;
	}

	public Site resolveBuffering(final Site dir, final String bufferStrategy) throws FileSystemException, IOException {
		BufferingProvider p = null;
		if ("syncfiles".equals(bufferStrategy)) {
			p = new SyncFilesBufferingProvider();
		}

		if (p == null) {
			throw new FileSystemException("BufferStrategy '" + bufferStrategy + "' not found");
		}

		return p.createBufferedSite(dir);
	}
}
