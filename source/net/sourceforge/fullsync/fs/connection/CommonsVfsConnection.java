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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.FileAttributes;

import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.UserAuthenticator;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;

public class CommonsVfsConnection implements FileSystemConnection {
	private static final long serialVersionUID = 2L;
	private ConnectionDescription desc;
	private FileObject base;
	private FileSystem fileSystem;
	private File root;

	public CommonsVfsConnection(ConnectionDescription desc) throws net.sourceforge.fullsync.FileSystemException {
		try {
			this.desc = desc;

			FileSystemOptions options = new FileSystemOptions();
			// UserInfo userInfo = new TrustEveryoneUserInfo();
			// SftpFileSystemConfigBuilder.getInstance().setUserInfo( options, userInfo );

			String uriString = desc.getUri();
			// FIXME: fix authentication here!
			/*
			 * String userinfo = desc.getUsername();
			 * if( userinfo != null )
			 * {
			 * String[] parts = userinfo.split("@");
			 *
			 * if( parts.length == 2 )
			 * {
			 * userinfo = parts[0];
			 * String publickeyfile = parts[1];
			 * SftpFileSystemConfigBuilder.getInstance().setIdentities( options, new java.io.File[]{ new java.io.File(publickeyfile) } );
			 * }
			 *
			 * userinfo = URLEncoder.encode( userinfo );
			 * if( desc.getPassword() != null )
			 * userinfo += ":" + URLEncoder.encode( desc.getPassword() );
			 *
			 * uriString = desc.getUri().replaceFirst( "//", "//"+userinfo+"@" );
			 * } else {
			 * uriString = desc.getUri();
			 * }
			 */
			URI uri = new URI(uriString);
			String baseUri = uriString.substring(0, uriString.length() - (uri.getPath().length()));
			UserAuthenticator auth = new StaticUserAuthenticator(null, desc.getUsername(), desc.getPassword());
			DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(options, auth);
			base = VFS.getManager().resolveFile(baseUri, options);
			base = base.resolveFile(uri.getPath());
			fileSystem = base.getFileSystem();
			root = new AbstractFile(this, ".", ".", null, true, base.exists());
		}
		catch (FileSystemException e) {
			throw new net.sourceforge.fullsync.FileSystemException(e);
		}
		catch (URISyntaxException e) {
			throw new net.sourceforge.fullsync.FileSystemException(e);
		}
	}

	@Override
	public File createChild(File parent, String name, boolean directory) throws IOException {
		return new AbstractFile(this, name, null, parent, directory, false);

	}

	public File buildNode(File parent, FileObject file) throws FileSystemException {
		String name = file.getName().getBaseName();
		// String path = parent.getPath()+"/"+name;

		File n = new AbstractFile(this, name, null, parent, file.getType() == FileType.FOLDER, true);
		if (file.getType() == FileType.FILE) {
			FileContent content = file.getContent();
			n.setFileAttributes(new FileAttributes(content.getSize(), content.getLastModifiedTime()));
		}
		return n;
	}

	@Override
	public Hashtable<String, File> getChildren(File dir) throws IOException {
		try {
			Hashtable<String, File> children = new Hashtable<String, File>();

			FileObject obj = base.resolveFile(dir.getPath());
			if (obj.exists() && (obj.getType() == FileType.FOLDER)) {
				FileObject[] list = obj.getChildren();
				for (FileObject element : list) {
					children.put(element.getName().getBaseName(), buildNode(dir, element));
				}
			}
			return children;
		}
		catch (FileSystemException fse) {
			throw new IOException(fse.getMessage());
		}
	}

	@Override
	public boolean makeDirectory(File dir) throws IOException {
		FileObject obj = base.resolveFile(dir.getPath());
		obj.createFolder();
		return true;
	}

	@Override
	public boolean writeFileAttributes(File file, FileAttributes att) throws IOException {
		FileObject obj = base.resolveFile(file.getPath());
		FileContent content = obj.getContent();
		content.setLastModifiedTime(att.getLastModified());
		return true;
	}

	@Override
	public InputStream readFile(File file) throws IOException {
		FileObject obj = base.resolveFile(file.getPath());
		return obj.getContent().getInputStream();
	}

	@Override
	public OutputStream writeFile(File file) throws IOException {
		FileObject obj = base.resolveFile(file.getPath());
		return obj.getContent().getOutputStream();
	}

	@Override
	public boolean delete(File node) throws IOException {
		FileObject obj = base.resolveFile(node.getPath());
		return obj.delete();
	}

	@Override
	public File getRoot() {
		return root;
	}

	@Override
	public void flush() throws IOException {

	}

	@Override
	public void close() throws IOException {
	}

	@Override
	public String getUri() {
		return desc.getUri();
	}

	@Override
	public boolean isCaseSensitive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAvailable() {
		// TODO Auto-generated method stub
		return true;
	}

}
