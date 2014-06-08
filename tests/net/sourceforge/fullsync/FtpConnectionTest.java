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
package net.sourceforge.fullsync;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;

public class FtpConnectionTest extends BaseConnectionTest {
	private static final int TEST_FTP_PORT = 16131;
	private FakeFtpServer m_fakeServer;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();

		ConnectionDescription dst = new ConnectionDescription(new URI("ftp://127.0.0.1:" + TEST_FTP_PORT + "/sampleuser"));
		dst.setParameter("bufferStrategy", "");
		dst.setParameter("username", "SampleUser");
		dst.setSecretParameter("password", "Sample");
		profile.setDestination(dst);

		m_fakeServer = new FakeFtpServer();
		m_fakeServer.setServerControlPort(TEST_FTP_PORT);

		FileSystem fs = new UnixFakeFileSystem();
		fs.add(new DirectoryEntry("/sampleuser"));

		m_fakeServer.addUserAccount(new UserAccount("SampleUser", "Sample", "/sampleuser"));
		m_fakeServer.setFileSystem(fs);
		m_fakeServer.start();
	}

	@Override
	@After
	public void tearDown() throws Exception {
		m_fakeServer.stop();
		super.tearDown();
	}

	/**
	 * recursively delete directory and all contained files.
	 *
	 * @param dir directory to clear
	 */
	@Override
	protected void clearDirectory(final File dir) {
		if (dir == testingDst) {
			FileSystem fs = new UnixFakeFileSystem();
			fs.add(new DirectoryEntry("/sampleuser"));
			m_fakeServer.setFileSystem(fs);
		}
		else {
			super.clearDirectory(dir);
		}
	}

	@Override
	protected void createNewFileWithContents(File dir, String filename, long lm, String content) throws IOException {
		if (dir == testingDst) {
			FileEntry file = new FileEntry("/sampleuser/" + filename, content);
			file.setLastModified(new Date(lm));
			m_fakeServer.getFileSystem().add(file);
		}
		else {
			super.createNewFileWithContents(dir, filename, lm, content);
		}
	}

	@Override
	@Test
	public void testSingleInSync() throws Exception {
		super.testSingleInSync();
	}

	@Override
	@Test
	public void testSingleSpaceMinus() throws Exception {
		super.testSingleSpaceMinus();
	}

	@Override
	@Test
	public void testSingleFileChange() throws Exception {
		super.testSingleFileChange();
	}
}