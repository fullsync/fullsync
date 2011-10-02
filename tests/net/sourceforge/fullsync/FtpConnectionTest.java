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

import java.net.URI;
import java.util.Date;
import java.util.Hashtable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class FtpConnectionTest extends BaseConnectionTest {
	private static final int TEST_FTP_PORT = 16131;
	private FakeFtpServer m_fakeServer;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();

		ConnectionDescription dst = new ConnectionDescription(new URI("ftp://127.0.0.1:" + TEST_FTP_PORT + "/"));
		dst.setParameter("bufferStrategy", "syncfiles");
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

	@Override
	@Test
	public void testSingleInSync() throws Exception {
		createRuleFile();
		Date d = new Date();
		long lm = d.getTime();

		createNewFileWithContents(testingSrc, "sourceFile1.txt", lm, "this is a test\ncontent1");
		createNewFileWithContents(testingSrc, "sourceFile2.txt", lm, "this is a test\ncontent2");
		FileEntry file = new FileEntry("/sourceFile1.txt", "this is a test\ncontent1");
		file.setLastModified(d);
		m_fakeServer.getFileSystem().add(file);

		Hashtable<String, Action> expectation = new Hashtable<String, Action>();
		expectation.put("sourceFile1.txt", new Action(Action.UnexpectedChangeError, Location.Destination, BufferUpdate.None, ""));
		expectation.put("sourceFile2.txt", new Action(Action.Add, Location.Destination, BufferUpdate.Destination, ""));
		// Phase One:
		TaskTree tree = assertPhaseOneActions(expectation);
		// Phase Three:
		synchronizer.performActions(tree); // TODO assert task finished events ?
	}

	@Override
	@Test
	public void testSingleSpaceMinus() throws Exception {
		super.testSingleSpaceMinus();
	}
}