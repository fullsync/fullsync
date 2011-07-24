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
package net.full.fs.ui;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.UserAuthenticator;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.eclipse.swt.widgets.Composite;

public class FileSystemUiManager {
	private static FileSystemUiManager instance;

	public static FileSystemUiManager getInstance() {
		if (instance == null)
			instance = new FileSystemUiManager();
		return instance;
	}

	public ProtocolSpecificComposite createProtocolSpecificComposite(Composite parent, int style, String protocol) {
		ProtocolSpecificComposite composite = null;

		if (protocol.equals("file"))
			composite = new FileSpecificComposite(parent, style);
		else if (protocol.equals("ftp"))
			composite = new UserPasswordSpecificComposite(parent, style);
		else if (protocol.equals("sftp"))
			composite = new UserPasswordSpecificComposite(parent, style);
		else if (protocol.equals("smb"))
			composite = new UserPasswordSpecificComposite(parent, style);

		if (composite != null) {
			composite.reset(protocol);
		}
		return composite;
	}

	public String[] getSchemes() {
		return new String[] { "file", "ftp", "sftp", "smb" };
	}

	public FileObject resolveFile(LocationDescription location) throws FileSystemException {
		String uri = location.getUri().getScheme();

		FileSystemOptions fileSystemOptions = new FileSystemOptions();

		if (uri.startsWith("ftp") || uri.startsWith("sftp") || uri.startsWith("smb")) {
			String username = location.getProperty("username");
			String password = location.getProperty("password");
			UserAuthenticator auth = new StaticUserAuthenticator(null, username, password);
			DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(fileSystemOptions, auth);
		}
		return VFS.getManager().resolveFile(uri, fileSystemOptions);
	}
}
