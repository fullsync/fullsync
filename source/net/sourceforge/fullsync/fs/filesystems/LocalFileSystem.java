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
 * Created on 18.07.2004
 */
package net.sourceforge.fullsync.fs.filesystems;

import java.io.IOException;
import java.net.URI;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.FileSystemException;
import net.sourceforge.fullsync.fs.FileSystem;
import net.sourceforge.fullsync.fs.Site;
import net.sourceforge.fullsync.fs.connection.LocalConnection;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class LocalFileSystem implements FileSystem {
	public LocalFileSystem() {

	}

	@Override
	public Site createConnection(ConnectionDescription desc) throws FileSystemException, IOException {
		URI uri = desc.getUri();
		if ((null != uri) && "file".equals(uri.getScheme())) {
			return new LocalConnection(new java.io.File(uri.getPath()));
		}
		return null;// TODO throw exception here !
	}

}
