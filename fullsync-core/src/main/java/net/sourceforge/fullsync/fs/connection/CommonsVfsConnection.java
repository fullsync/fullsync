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
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.vfs2.Capability;
import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.VFS;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.FileSystemException;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.FileSystemAuthProvider;

public class CommonsVfsConnection implements FileSystemConnection {
	private final boolean canSetLastModifiedFile;
	private final boolean canSetLastModifiedFolder;
	private final ConnectionDescription connectionDescription;
	private final FileObject base;
	private final File root;

	public CommonsVfsConnection(final ConnectionDescription connectionDescription, final FileSystemAuthProvider fsAuthProvider)
		throws FileSystemException {
		try {
			this.connectionDescription = connectionDescription;
			FileSystemOptions options = new FileSystemOptions();
			if (null != fsAuthProvider) {
				fsAuthProvider.authSetup(connectionDescription, options);
			}
			int port = connectionDescription.getPort().orElse(-1);
			String host = connectionDescription.getHost().orElse(null);
			URI url = new URI(connectionDescription.getScheme(), null, host, port, connectionDescription.getPath(), null, null);
			base = VFS.getManager().resolveFile(url.toString(), options);
			root = new AbstractFile(this, ".", null, true, base.exists()); //$NON-NLS-1$
			canSetLastModifiedFile = base.getFileSystem().hasCapability(Capability.SET_LAST_MODIFIED_FILE);
			canSetLastModifiedFolder = base.getFileSystem().hasCapability(Capability.SET_LAST_MODIFIED_FOLDER);
		}
		catch (org.apache.commons.vfs2.FileSystemException | URISyntaxException e) {
			throw new FileSystemException(e);
		}
	}

	@Override
	public final File createChild(final File parent, final String name, final boolean directory) throws IOException {
		return new AbstractFile(this, name, parent, directory, false);
	}

	private File buildNode(final File parent, final FileObject file) throws org.apache.commons.vfs2.FileSystemException {
		String name = file.getName().getBaseName();

		File n = new AbstractFile(this, name, parent, file.getType() == FileType.FOLDER, true);
		if (file.getType() == FileType.FILE) {
			FileContent content = file.getContent();
			n.setLastModified(content.getLastModifiedTime());
			n.setSize(content.getSize());
		}
		return n;
	}

	@Override
	public final Map<String, File> getChildren(final File dir) throws IOException {
		try {
			Map<String, File> children = new HashMap<>();

			FileObject obj = base.resolveFile(dir.getPath());
			if (obj.exists() && (obj.getType() == FileType.FOLDER)) {
				FileObject[] list = obj.getChildren();
				for (FileObject element : list) {
					children.put(element.getName().getBaseName(), buildNode(dir, element));
				}
			}
			return children;
		}
		catch (org.apache.commons.vfs2.FileSystemException fse) {
			throw new IOException(fse.getMessage(), fse);
		}
	}

	@Override
	public final boolean makeDirectory(final File dir) throws IOException {
		FileObject obj = base.resolveFile(dir.getPath());
		obj.createFolder();
		return true;
	}

	@Override
	public final boolean writeFileAttributes(final File file) throws IOException {
		FileObject obj = base.resolveFile(file.getPath());
		FileContent content = obj.getContent();
		boolean setLastModified;
		if (FileType.FOLDER == obj.getType()) {
			setLastModified = canSetLastModifiedFolder;
		}
		else {
			setLastModified = canSetLastModifiedFile;
		}
		if (setLastModified) {
			content.setLastModifiedTime(file.getLastModified());
		}
		return true;
	}

	@Override
	public final InputStream readFile(final File file) throws IOException {
		FileObject obj = base.resolveFile(file.getPath());
		return obj.getContent().getInputStream();
	}

	@Override
	public final OutputStream writeFile(final File file) throws IOException {
		FileObject obj = base.resolveFile(file.getPath());
		return obj.getContent().getOutputStream();
	}

	@Override
	public final boolean delete(final File node) throws IOException {
		FileObject obj = base.resolveFile(node.getPath());
		return obj.delete();
	}

	@Override
	public final File getRoot() {
		return root;
	}

	@Override
	public final FileObject getBase() {
		return base;
	}

	@Override
	public void flush() throws IOException {
		// FIXME: implement?
	}

	@Override
	public final void close() throws Exception {
		VFS.getManager().closeFileSystem(base.getFileSystem());
	}

	@Override
	public final boolean isCaseSensitive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public final boolean isAvailable() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public ConnectionDescription getConnectionDescription() {
		return connectionDescription;
	}
}
