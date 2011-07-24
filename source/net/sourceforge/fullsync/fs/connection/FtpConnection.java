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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.FileAttributes;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class FtpConnection extends InstableConnection {
	class FtpFileInputStream extends InputStream {
		private InputStream in;
		private FTPClient client;

		public FtpFileInputStream(InputStream in, FTPClient client) {
			this.in = in;
			this.client = client;
		}

		public int available() throws IOException {
			return in.available();
		}

		public void close() throws IOException {
			in.close();
			client.completePendingCommand();
		}

		public int read(byte[] b, int off, int len) throws IOException {
			return in.read(b, off, len);
		}

		public int read(byte[] b) throws IOException {
			return in.read(b);
		}

		public synchronized void reset() throws IOException {
			in.reset();
		}

		public int read() throws IOException {
			return in.read();
		}

		public synchronized void mark(int readlimit) {
			in.mark(readlimit);
		}

		public boolean markSupported() {
			return in.markSupported();
		}

		public long skip(long n) throws IOException {
			return in.skip(n);
		}
	}

	class FtpFileOutputStream extends OutputStream {
		private OutputStream out;
		private FTPClient client;

		public FtpFileOutputStream(OutputStream out, FTPClient client) {
			this.out = out;
			this.client = client;
		}

		public void close() throws IOException {
			out.close();
			client.completePendingCommand();
		}

		public void flush() throws IOException {
			out.flush();
		}

		public void write(byte[] b, int off, int len) throws IOException {
			out.write(b, off, len);
		}

		public void write(byte[] b) throws IOException {
			out.write(b);
		}

		public void write(int b) throws IOException {
			out.write(b);
		}
	}

	private static Log log = LogFactory.getLog(FtpConnection.class);

	private boolean changeDirBeforeList;
	private boolean usePassiveMode;
	private boolean caseSensitive;

	private ConnectionDescription desc;
	private URI connectionUri;
	private FTPClient client;
	private String basePath;
	private File root;

	public FtpConnection(ConnectionDescription desc) throws IOException, URISyntaxException {
		this.desc = desc;
		this.root = new AbstractFile(this, ".", ".", null, true, true);
		this.client = new FTPClient();
		this.connectionUri = new URI(desc.getUri());

		usePassiveMode = true;
		changeDirBeforeList = false;

		String query = connectionUri.getQuery();
		if (query != null) {
			Pattern p = Pattern.compile("(\\w+)=(\\w+)");
			Matcher m = p.matcher(query);
			while (m.find()) {
				if (m.group(1).equals("passive"))
					usePassiveMode = m.group(2).equals("true");
				else if (m.group(1).equals("compatible"))
					changeDirBeforeList = m.group(2).equals("true");
			}
		}

		if (log.isDebugEnabled()) {
			StringBuffer sb = new StringBuffer();
			sb.append("Creating FtpConnection to ");
			sb.append(connectionUri);
			sb.append(" [");
			if (usePassiveMode)
				sb.append("passive");
			else
				sb.append("active");
			if (changeDirBeforeList)
				sb.append(",changeDirBeforeList");
			sb.append("]");
			log.debug(sb.toString());
		}

		connect();
	}

	public void connect() throws IOException {
		client.setDefaultTimeout(30000);
		client.connect(connectionUri.getHost(), connectionUri.getPort() == -1 ? 21 : connectionUri.getPort());
		client.login(desc.getUsername(), desc.getPassword());
		if (usePassiveMode)
			client.enterLocalPassiveMode(); // FIXME i need to be specified in the ConnectionDescription
		client.setFileType(FTP.BINARY_FILE_TYPE);
		client.setSoTimeout(0);
		basePath = client.printWorkingDirectory() + connectionUri.getPath();

		// TODO find out whether remote server is case sensitive or not (win or unix)
		// maybe check whether . has x permission
		caseSensitive = false;

		if (!client.changeWorkingDirectory(basePath)) {
			client.quit();
			throw new IOException("Could not set working dir");
		}

		if (basePath.endsWith("/"))
			basePath = basePath.substring(0, basePath.length() - 1);
	}

	public void reconnect() throws IOException {
		try {
			client.quit();
		}
		finally {
			try {
				client.disconnect();
			}
			finally {
				connect();
			}
		}
	}

	public void close() throws IOException {
		client.quit();
		client.disconnect();
	}

	public void flush() throws IOException {

	}

	public boolean isAvailable() {
		return true;
	}

	public String getUri() {
		return desc.getUri();
	}

	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	public File getRoot() {
		return root;
	}

	public File _createChild(File parent, String name, boolean directory) {
		return new AbstractFile(this, name, null, parent, directory, false);
	}

	public File _buildNode(File parent, FTPFile file) {
		String name = file.getName();
		// String path = parent.getPath()+"/"+name;

		File n = new AbstractFile(this, name, null, parent, file.isDirectory(), true);
		if (!file.isDirectory())
			n.setFileAttributes(new FileAttributes(file.getSize(), file.getTimestamp().getTimeInMillis()));
		return n;
	}

	public Hashtable _getChildren(File dir) throws IOException {
		FTPFile[] files;
		if (changeDirBeforeList) {
			client.changeWorkingDirectory(basePath + "/" + dir.getPath());
			files = client.listFiles("-a");
		}
		else {
			files = client.listFiles("-a " + basePath + "/" + dir.getPath());
		}

		Hashtable table = new Hashtable();
		for (int i = 0; i < files.length; i++) {
			String name = files[i].getName();
			if (!name.equals(".") && !name.equals(".."))
				table.put(name, _buildNode(dir, files[i]));
		}

		return table;
	}

	public boolean _makeDirectory(File dir) throws IOException {
		return client.makeDirectory(basePath + "/" + dir.getPath());
	}

	public boolean _writeFileAttributes(File file, FileAttributes attr) {
		return false;
	}

	public InputStream _readFile(File file) throws IOException {
		InputStream in = client.retrieveFileStream(basePath + "/" + file.getPath());
		if (in == null)
			throw new IOException("Ftp error while trying to read " + file.getPath() + " [" + client.getReplyCode() + "] "
					+ client.getReplyString());

		return new FtpFileInputStream(in, client);
	}

	public OutputStream _writeFile(File file) throws IOException {
		OutputStream out = client.storeFileStream(basePath + "/" + file.getPath());
		if (out == null)
			throw new IOException("Ftp error while trying to write " + file.getPath() + " [" + client.getReplyCode() + "] "
					+ client.getReplyString());
		return new FtpFileOutputStream(out, client);
	}

	public boolean _delete(File file) throws IOException {
		if (file.isDirectory())
			return client.removeDirectory(basePath + "/" + file.getPath());
		else
			return client.deleteFile(basePath + "/" + file.getPath());
	}
}
