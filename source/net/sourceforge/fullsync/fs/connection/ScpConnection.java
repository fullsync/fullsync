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
import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.FileSystemException;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.ui.GuiController;

import com.sshtools.j2ssh.ScpClient;
import com.sshtools.j2ssh.SshClient;
import com.sshtools.j2ssh.authentication.AuthenticationProtocolState;
import com.sshtools.j2ssh.authentication.PasswordAuthenticationClient;
import com.sshtools.j2ssh.configuration.SshConnectionProperties;
import com.sshtools.j2ssh.session.SessionChannelClient;
import com.sshtools.j2ssh.sftp.FileAttributes;
import com.sshtools.j2ssh.sftp.SftpFile;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class ScpConnection implements FileSystemConnection {
	private static final long serialVersionUID = 1;

	private ConnectionDescription desc;
	private SshClient sshClient;
	private ScpClient scpClient;
	private SessionChannelClient schClient;
	private String basePath;
	private AbstractFile root;

	public ScpConnection(ConnectionDescription desc) throws FileSystemException, IOException, URISyntaxException {
		this.desc = desc;
		URI uri = new URI(desc.getUri());
		sshClient = new SshClient();
		SshConnectionProperties prop = new SshConnectionProperties();
		prop.setHost(uri.getHost());

		// REVISIT not really fine
		sshClient.connect(prop, new DialogKnownHostsKeyVerification(GuiController.getInstance().getMainWindow().getShell()));

		PasswordAuthenticationClient pwd = new PasswordAuthenticationClient();
		pwd.setUsername(desc.getUsername());
		pwd.setPassword(desc.getPassword());

		int result = sshClient.authenticate(pwd);
		if (result == AuthenticationProtocolState.COMPLETE) {
			scpClient = sshClient.openScpClient();
			schClient = sshClient.openSessionChannel();

			// basePath = scpClient.getDefaultDirectory()+uri.getPath();
			schClient.executeCommand("pwd");
			// basePath = ; ??????????/

			if (basePath.endsWith("/"))
				basePath = basePath.substring(0, basePath.length() - 1);
		}
		else {
			throw new FileSystemException("Could not connect");
		}
		this.root = new AbstractFile(this, ".", ".", null, true, true);
	}

	public boolean isAvailable() {
		return true;
	}

	public File getRoot() {
		return root;
	}

	public File createChild(File parent, String name, boolean directory) {
		return new AbstractFile(this, name, parent.getPath() + "/" + name, parent, directory, false);
	}

	public File buildNode(File parent, SftpFile file) {
		String name = file.getFilename();
		String path = parent.getPath() + "/" + name;

		File n = new AbstractFile(this, name, path, parent, file.isDirectory(), true);

		FileAttributes att = file.getAttributes();
		if (file.isFile())
			n.setFileAttributes(new net.sourceforge.fullsync.fs.FileAttributes(att.getSize().longValue(), att.getModifiedTime().longValue()));

		return n;
	}

	public Hashtable getChildren(File dir) {
		/*
		 * try {
		 * SftpFile f = sftpClient.openDirectory( basePath+"/"+dir.getPath() );
		 * ArrayList files = new ArrayList();
		 * sftpClient.listChildren( f, files );
		 * 
		 * Hashtable table = new Hashtable();
		 * for( Iterator i = files.iterator(); i.hasNext(); )
		 * {
		 * SftpFile file = (SftpFile)i.next();
		 * table.put( file.getFilename(), buildNode( dir, file ) );
		 * }
		 * 
		 * return table;
		 * } catch( IOException ioe ) {
		 * //ExceptionHandler.reportException( ioe );
		 * return new Hashtable();
		 * }
		 */
		return null;
	}

	public boolean makeDirectory(File dir) {
		try {
			schClient.executeCommand("mkdir" + basePath + "/" + dir.getPath());
			return true;
		}
		catch (IOException e) {
			ExceptionHandler.reportException(e);
			return false;
		}
	}

	public boolean writeFileAttributes(File file, net.sourceforge.fullsync.fs.FileAttributes att) {
		return false;
	}

	public InputStream readFile(File file) {
		/*
		 * try {
		 * return new SftpFileInputStream( sftpClient.openFile( basePath+"/"+file.getPath(), SftpSubsystemClient.OPEN_READ ) );
		 * } catch( IOException e ) {
		 * ExceptionHandler.reportException( e );
		 * return null;
		 * }
		 */
		return null;
	}

	public OutputStream writeFile(File file) {
		/*
		 * try {
		 * FileAttributes attrs = new FileAttributes();
		 * attrs.setPermissions("rw-rw----");
		 * SftpFile f = scpClient.put( basePath+"/"+file.getPath(), SftpSubsystemClient.OPEN_CREATE | SftpSubsystemClient.OPEN_WRITE, attrs
		 * );
		 * return new SftpFileOutputStream( f );
		 * } catch( IOException e ) {
		 * ExceptionHandler.reportException( e );
		 * return null;
		 * }
		 */
		return null;
	}

	public boolean delete(File node) {
		try {
			if (node.isDirectory())
				schClient.executeCommand("rmdir " + basePath + "/" + node.getPath());
			else
				schClient.executeCommand("rm " + basePath + "/" + node.getPath());
			return true;
		}
		catch (IOException e) {
			ExceptionHandler.reportException(e);
			return false;
		}
	}

	public void flush() throws IOException {

	}

	public void close() throws IOException {
		schClient.close();
		sshClient.disconnect();
	}

	public String getUri() {
		return desc.getUri();
	}

	public boolean isCaseSensitive() {
		// TODO find out whether current fs is case sensitive
		return false;
	}

}
