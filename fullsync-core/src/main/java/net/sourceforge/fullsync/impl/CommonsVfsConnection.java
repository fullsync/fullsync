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
package net.sourceforge.fullsync.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.vfs2.Capability;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.VFS;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.FSFile;
import net.sourceforge.fullsync.FileSystemConnection;
import net.sourceforge.fullsync.FileSystemException;
import net.sourceforge.fullsync.filesystems.FileSystemAuthProvider;

public class CommonsVfsConnection implements FileSystemConnection {
	private final boolean canSetLastModifiedFile;
	private final boolean canSetLastModifiedFolder;
	private final ConnectionDescription connectionDescription;
	private final FileObject base;
	private final FSFile root;

	public CommonsVfsConnection(final ConnectionDescription connectionDescription, final FileSystemAuthProvider fsAuthProvider)
		throws FileSystemException {
		try {
			this.connectionDescription = connectionDescription;
			var fileSystemOptions = new FileSystemOptions();
			if (null != fsAuthProvider) {
				fsAuthProvider.authSetup(connectionDescription, fileSystemOptions);
			}
			base = VFS.getManager().resolveFile(connectionDescription.getURI().toString(), fileSystemOptions);
			root = FileImpl.root(this, base.isFolder(), base.exists());
			canSetLastModifiedFile = base.getFileSystem().hasCapability(Capability.SET_LAST_MODIFIED_FILE);
			canSetLastModifiedFolder = base.getFileSystem().hasCapability(Capability.SET_LAST_MODIFIED_FOLDER);
		}
		catch (org.apache.commons.vfs2.FileSystemException | URISyntaxException e) {
			throw new FileSystemException(e);
		}
	}

	@Override
	public final FSFile createChild(final FSFile parent, final String name, final boolean directory) throws IOException {
		return new FileImpl(this, name, parent, directory, false);
	}

	private FSFile buildNode(final FSFile parent, final FileObject file) throws org.apache.commons.vfs2.FileSystemException {
		var name = file.getName().getBaseName();
		FSFile n = new FileImpl(this, name, parent, file.isFolder(), true);
		if (!file.isFolder()) {
			var content = file.getContent();
			n.setLastModified(content.getLastModifiedTime());
			n.setSize(content.getSize());
		}
		return n;
	}

	@Override
	public final Map<String, FSFile> getChildren(final FSFile dir) throws IOException {
		try {
			Map<String, FSFile> children = new HashMap<>();
			var obj = base.resolveFile(dir.getFsAbsolutePath());
			if (obj.exists() && obj.isFolder()) {
				var list = obj.getChildren();
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
	public final boolean makeDirectory(final FSFile dir) throws IOException {
		var obj = base.resolveFile(dir.getFsAbsolutePath());
		obj.createFolder();
		return true;
	}

	@Override
	public final boolean writeFileAttributes(final FSFile file) throws IOException {
		var obj = base.resolveFile(file.getFsAbsolutePath());
		var content = obj.getContent();
		boolean setLastModified;
		if (obj.isFolder()) {
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
	public final InputStream readFile(final FSFile file) throws IOException {
		var obj = base.resolveFile(file.getFsAbsolutePath());
		return obj.getContent().getInputStream();
	}

	@Override
	public final OutputStream writeFile(final FSFile file) throws IOException {
		var obj = base.resolveFile(file.getFsAbsolutePath());
		return obj.getContent().getOutputStream();
	}

	@Override
	public final boolean delete(final FSFile node) throws IOException {
		var obj = base.resolveFile(node.getFsAbsolutePath());
		return obj.delete();
	}

	@Override
	public final FSFile getRoot() {
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
