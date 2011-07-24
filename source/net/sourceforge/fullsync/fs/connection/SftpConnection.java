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
import java.util.ArrayList;
import java.util.Hashtable;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.ui.GuiController;

import com.sshtools.j2ssh.SshClient;
import com.sshtools.j2ssh.authentication.AuthenticationProtocolState;
import com.sshtools.j2ssh.authentication.HostbasedAuthenticationClient;
import com.sshtools.j2ssh.authentication.PasswordAuthenticationClient;
import com.sshtools.j2ssh.authentication.PublicKeyAuthenticationClient;
import com.sshtools.j2ssh.authentication.SshAuthenticationClient;
import com.sshtools.j2ssh.configuration.SshConnectionProperties;
import com.sshtools.j2ssh.sftp.FileAttributes;
import com.sshtools.j2ssh.sftp.SftpFile;
import com.sshtools.j2ssh.sftp.SftpFileInputStream;
import com.sshtools.j2ssh.sftp.SftpFileOutputStream;
import com.sshtools.j2ssh.sftp.SftpSubsystemClient;
import com.sshtools.j2ssh.transport.publickey.SshPrivateKey;
import com.sshtools.j2ssh.transport.publickey.SshPrivateKeyFile;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class SftpConnection extends InstableConnection {

	private static final long serialVersionUID = 2L;

	private ConnectionDescription desc;
	private URI connectionUri;
	private SshAuthenticationClient sshAuth;
	private SshClient sshClient;
	private SftpSubsystemClient sftpClient;
	private String basePath;
	private AbstractFile root;

	public SftpConnection(ConnectionDescription desc) throws IOException, URISyntaxException {
		this.desc = desc;
		this.root = new AbstractFile(this, ".", ".", null, true, true);
		this.connectionUri = new URI(desc.getUri());

		connect();
	}

	@Override
	public void connect() throws IOException {
		SshConnectionProperties prop = new SshConnectionProperties();
		prop.setHost(connectionUri.getHost());
		if (connectionUri.getPort() != -1) {
			prop.setPort(connectionUri.getPort());
		}

		sshClient = new SshClient();
		// REVISIT not really fine (the static method call)
		sshClient.connect(prop, new DialogKnownHostsKeyVerification(GuiController.getInstance().getMainShell()));

		if (sshAuth == null) {
			// REVISIT not really fine (the static method call)
			/*
			 * KBIAuthenticationClient client = new KBIAuthenticationClient();
			 * client.setKBIRequestHandler( new SshAuthenticationDialog( GuiController.getInstance().getMainShell() ) );
			 * sshAuth = client;
			 */

			String user = desc.getUsername();
			String password = desc.getPassword();
			String keyfile = null;
			int idx = user.indexOf('@');
			if (idx >= 0) {
				keyfile = user.substring(idx + 1);
				user = user.substring(0, idx);

				SshPrivateKeyFile file = SshPrivateKeyFile.parse(new java.io.File(keyfile));

				if (password == "") {
					HostbasedAuthenticationClient hb = new HostbasedAuthenticationClient();
					hb.setUsername(user);
					hb.setKey(file.toPrivateKey(null));
					sshAuth = hb;
				}
				else {
					PublicKeyAuthenticationClient pk = new PublicKeyAuthenticationClient();
					pk.setUsername(user);
					SshPrivateKey key = file.toPrivateKey(password);
					pk.setKey(key);
					sshAuth = pk;
				}
			}
			else {
				PasswordAuthenticationClient pw = new PasswordAuthenticationClient();
				pw.setUsername(user);
				pw.setPassword(password);
				sshAuth = pw;
			}
		}

		int result = sshClient.authenticate(sshAuth);
		if (result == AuthenticationProtocolState.COMPLETE) {
			sftpClient = sshClient.openSftpChannel();
			// sftpClient.cd( path );
			basePath = sftpClient.getDefaultDirectory() + connectionUri.getPath();
			if (basePath.endsWith("/")) {
				basePath = basePath.substring(0, basePath.length() - 1);
			}

		}
		else {

			throw new IOException("Could not connect (AuthProtocolState is " + result + ")");
		}
		this.root = new AbstractFile(this, ".", ".", null, true, true);
	}

	@Override
	public void reconnect() throws IOException {
		if (sshClient != null) {
			sshClient.disconnect();
		}
		connect();
	}

	@Override
	public void close() throws IOException {
		sftpClient.close();
		sshClient.disconnect();
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public void flush() throws IOException {

	}

	@Override
	public String getUri() {
		return desc.getUri();
	}

	@Override
	public boolean isCaseSensitive() {
		// TODO find out whether current fs is case sensitive
		return false;
	}

	@Override
	public File getRoot() {
		return root;
	}

	@Override
	public File _createChild(File parent, String name, boolean directory) {
		return new AbstractFile(this, name, null, parent, directory, false);
	}

	public File buildNode(File parent, SftpFile file) {
		String name = file.getFilename();
		// String path = parent.getPath()+"/"+name;

		File n = new AbstractFile(this, name, null, parent, file.isDirectory(), true);

		FileAttributes att = file.getAttributes();
		if (file.isFile()) {
			n.setFileAttributes(new net.sourceforge.fullsync.fs.FileAttributes(att.getSize().longValue(), att.getModifiedTime().longValue()));
		}

		return n;
	}

	@Override
	public Hashtable<String, File> _getChildren(File dir) throws IOException {
		SftpFile f = null;
		try {
			f = sftpClient.openDirectory(basePath + "/" + dir.getPath());
		}
		catch (IOException ioe) {
			if (ioe.getMessage().equals("No such file")) {
				return new Hashtable<String, File>(); // TODO: was return new Hashtable(0);
			}
			else {
				throw ioe;
			}
		}
		ArrayList<File> files = new ArrayList<File>();
		sftpClient.listChildren(f, files);

		Hashtable<String, File> table = new Hashtable<String, File>();
		for (Object element : files) {
			SftpFile file = (SftpFile) element;
			if (!file.getFilename().equals(".") && !file.getFilename().equals("..")) {
				table.put(file.getFilename(), buildNode(dir, file));
			}
		}

		return table;
	}

	@Override
	public boolean _makeDirectory(File dir) throws IOException {
		sftpClient.makeDirectory(basePath + "/" + dir.getPath());
		return true;
	}

	@Override
	public boolean _writeFileAttributes(File file, net.sourceforge.fullsync.fs.FileAttributes att) {
		return false;
	}

	@Override
	public InputStream _readFile(File file) throws IOException {
		return new SftpFileInputStream(sftpClient.openFile(basePath + "/" + file.getPath(), SftpSubsystemClient.OPEN_READ));
	}

	@Override
	public OutputStream _writeFile(File file) throws IOException {
		FileAttributes attrs = new FileAttributes();
		attrs.setPermissions("rw-rw----");
		SftpFile f = sftpClient.openFile(basePath + "/" + file.getPath(), SftpSubsystemClient.OPEN_CREATE | SftpSubsystemClient.OPEN_WRITE
				| SftpSubsystemClient.OPEN_TRUNCATE, attrs);
		return new SftpFileOutputStream(f);
	}

	@Override
	public boolean _delete(File node) throws IOException {
		if (node.isDirectory()) {
			sftpClient.removeDirectory(basePath + "/" + node.getPath());
		}
		else {
			sftpClient.removeFile(basePath + "/" + node.getPath());
		}
		return true;
	}
}
