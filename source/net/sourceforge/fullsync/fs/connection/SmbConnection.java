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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Hashtable;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.FileAttributes;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class SmbConnection extends InstableConnection {
	private static final long serialVersionUID = 2L;

	private ConnectionDescription desc;

	private SmbFile base;
	private AbstractFile root;

	public SmbConnection(ConnectionDescription desc) throws IOException {
		this.desc = desc;
		this.root = new AbstractFile(this, ".", ".", null, true, true);
		connect();
	}

	@Override
	public void connect() throws IOException {
		String domain = null, username, password;
		String[] user = desc.getParameter("username").split("@");
		username = user[0];
		if (user.length > 1) {
			domain = user[1];
		}
		password = desc.getSecretParameter("password");

		NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(domain, username, password);
		base = new SmbFile(desc.getUri().toString() + "/", auth);
	}

	@Override
	public void reconnect() throws IOException {
	}

	@Override
	public void flush() throws IOException {

	}

	@Override
	public void close() throws IOException {
	}

	@Override
	public File getRoot() {
		return root;
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public File _createChild(File parent, String name, boolean directory) {
		return new AbstractFile(this, name, null, parent, directory, false);
	}

	public File buildNode(File parent, SmbFile file) throws SmbException {
		String name = file.getName();
		if (name.endsWith("/"))
		 {
			name = name.substring(0, name.length() - 1);
		// String path = parent.getPath()+"/"+name;
		}

		File n = new AbstractFile(this, name, null, parent, file.isDirectory(), true);
		if (file.isFile()) {
			n.setFileAttributes(new FileAttributes(file.length(), file.lastModified()));
		}
		return n;
	}

	@Override
	public Hashtable<String, File> _getChildren(File dir) throws SmbException, MalformedURLException, UnknownHostException {
		if (dir.exists()) {
			SmbFile f = new SmbFile(base, dir.getPath() + "/");
			SmbFile[] files = f.listFiles();

			if (files == null) {
				return new Hashtable<String, File>();
			}

			Hashtable<String, File> table = new Hashtable<String, File>(files.length);
			if (files != null) {
				for (SmbFile file : files) {
					String name = file.getName();
					if (name.endsWith("/")) {
						name = name.substring(0, name.length() - 1);
					}

					if (file.isFile() || file.isDirectory()) {
						table.put(name, buildNode(dir, file));
					}
				}
			}
			return table;
		}
		else {
			return new Hashtable<String, File>();
		}
	}

	@Override
	public boolean _makeDirectory(File dir) throws MalformedURLException, SmbException, UnknownHostException {
		SmbFile f = new SmbFile(base, dir.getPath());
		f.mkdirs();
		return f.exists();
	}

	@Override
	public boolean _writeFileAttributes(File file, FileAttributes att) throws MalformedURLException, SmbException, UnknownHostException {
		SmbFile f = new SmbFile(base, file.getPath());
		f.setLastModified(att.getLastModified());
		return true;
	}

	@Override
	public InputStream _readFile(File file) throws MalformedURLException, UnknownHostException, IOException {
		SmbFile f = new SmbFile(base, file.getPath());
		return f.getInputStream();
	}

	@Override
	public OutputStream _writeFile(File file) throws MalformedURLException, UnknownHostException, IOException {
		SmbFile f = new SmbFile(base, file.getPath());
		return f.getOutputStream();
	}

	@Override
	public boolean _delete(File node) throws MalformedURLException, UnknownHostException, SmbException {
		SmbFile f = new SmbFile(base, node.getPath());
		f.delete();
		return !f.exists();
	}

	@Override
	public URI getUri() {
		return desc.getUri();
	}

	@Override
	public boolean isCaseSensitive() {
		// TODO find out whether current fs is case sensitive
		return false;
	}
}
