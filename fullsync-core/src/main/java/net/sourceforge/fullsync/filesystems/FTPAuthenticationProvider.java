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
package net.sourceforge.fullsync.filesystems;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.ftp.FtpFileSystemConfigBuilder;

import net.sourceforge.fullsync.ConnectionDescription;

class FTPAuthenticationProvider implements FileSystemAuthProvider {
	@Override
	public final void authSetup(final ConnectionDescription description, final FileSystemOptions options) throws FileSystemException {
		String username = description.getUsername().orElse(""); //$NON-NLS-1$
		String password = description.getPassword().orElse(""); //$NON-NLS-1$
		StaticUserAuthenticator auth = new StaticUserAuthenticator(null, username, password);
		FtpFileSystemConfigBuilder cfg = FtpFileSystemConfigBuilder.getInstance();
		cfg.setPassiveMode(options, true);
		cfg.setUserDirIsRoot(options, description.isUserDirIsRoot());
		DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(options, auth);
	}
}
