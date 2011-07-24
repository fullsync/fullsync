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
/*
 * Created on 20.07.2004
 */
package net.sourceforge.fullsync.fs.filesystems;

import java.io.IOException;
import java.net.URISyntaxException;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.FileSystemException;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.FileSystem;
import net.sourceforge.fullsync.fs.Site;
import net.sourceforge.fullsync.fs.connection.FileSystemConnection;
import net.sourceforge.fullsync.fs.connection.FtpConnection;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class FtpFileSystem implements FileSystem {
	public FtpFileSystem() {
	}

	@Override
	public Site createConnection(ConnectionDescription desc) throws FileSystemException {
		if (!desc.getUri().startsWith("ftp:")) {
			return null;
		/*
		 * try {
		 * FTPClient client = new FTPClient();
		 * client.connect( uri.getHost(), uri.getPort()==-1?21:uri.getPort() );
		 * client.login( uri.getUserInfo(), "test" );
		 *
		 * if( !client.changeWorkingDirectory( uri.getPath() ) )
		 * {
		 * // we cannot change to given dir, maybe its a file
		 * /*
		 * int i = url.getPath().lastIndexOf('/');
		 * String path = url.getPath().substring( 0, i-1 );
		 * if( client.changeWorkingDirectory( path ) )
		 * {
		 * Directory d = new FtpDirectory( client, path, null );
		 * d.getChild( url.getPath() )
		 * }
		 */
		/*
		 * // nah, just print an error
		 * client.quit();
		 * throw new FileSystemException( "Could not set working dir" );
		 * }
		 *
		 * return new FtpDirectory(client, client.printWorkingDirectory() );
		 * } catch( IOException ioe ) {
		 * throw new FileSystemException(ioe);
		 * }
		 */
		}

		FileSystemConnection conn;
		try {
			conn = new FtpConnection(desc);
		}
		catch (URISyntaxException e) {
			throw new FileSystemException(e);
		}
		catch (IOException e) {
			throw new FileSystemException(e);
		}
		return conn;
	}

	public File getDirectory(String path) {
		// TODO look for .. s
		/*
		 * FtpDirectory dir = new FtpDirectory( client, path );
		 * cache.put( path, dir );
		 */
		return null;

	}

	public File getFile(String path) {
		return null;
	}

	public File getNode(String path) {
		return null;
	}

}
