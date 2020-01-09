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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.junit.After;
import org.junit.jupiter.api.BeforeAll;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;

import com.mindtree.techworks.infix.pluginscommon.test.ssh.SSHServerResource;

import net.sourceforge.fullsync.impl.SimplyfiedRuleSetDescriptor;

@RunWith(Parameterized.class)
public class FilesystemTest {
	protected static final int MILLI_SECONDS_PER_DAY = 86400000;
	private static final int TEST_FTP_PORT = 16131;

	@Parameterized.Parameters
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] { { "file" }, { "ftp" }, { "sftp" },
				//{"smb"}, // no server for smb/cifs
		});
	}

	@Parameter
	public String filesystem;
	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();

	protected File testingDst;
	protected File testingSrc;
	protected Synchronizer synchronizer;
	protected Profile profile;

	private FakeFtpServer m_fakeServer;
	@Rule
	public SSHServerResource m_sshServer = new SSHServerResource("SampleUser", 2222, "127.0.0.1");

	@BeforeEach
	public void setUp() throws Exception {
		testingDst = null;
		testingSrc = null;
		synchronizer = null;
		profile = null;
		m_fakeServer = null;
	}

	@After
	public void tearDown() throws Exception {
		if (null != m_fakeServer) {
			m_fakeServer.stop();
		}
		//FIXME: disconnect source and destination!
		tmpFolder.delete();
	}

	void prepareProfile(String syncType) throws Exception {
		testingDst = tmpFolder.newFolder("destination");
		testingDst.mkdirs();
		testingSrc = tmpFolder.newFolder("source");
		testingSrc.mkdirs();

		synchronizer = new Synchronizer();
		ConnectionDescription src = new ConnectionDescription(testingSrc.toURI());
		src.setParameter("bufferStrategy", "");

		String dstUrl = null;
		if ("file".equals(filesystem)) {
			dstUrl = testingDst.toURI().toString();
		}
		if ("ftp".equals(filesystem)) {
			m_fakeServer = new FakeFtpServer();
			m_fakeServer.setServerControlPort(TEST_FTP_PORT);
			clearDirectory(testingDst);
			m_fakeServer.addUserAccount(new UserAccount("SampleUser", "Sample", "/sampleuser"));
			m_fakeServer.start();
			dstUrl = "ftp://127.0.0.1:" + TEST_FTP_PORT + "/sampleuser";
		}
		if ("sftp".equals(filesystem)) {
			System.setProperty("vfs.sftp.sshdir", new File("./tests/sshd-config/").getAbsolutePath());
			testingDst.delete();
			testingDst = m_sshServer.getUserHome();
			dstUrl = "sftp://127.0.0.1:2222/";
		}

		ConnectionDescription dst = new ConnectionDescription(new URI(dstUrl));
		dst.setParameter("bufferStrategy", "");
		dst.setParameter("username", "SampleUser");
		dst.setSecretParameter("password", "Sample");

		profile = new Profile("TestProfile", src, dst, new SimplyfiedRuleSetDescriptor(true, null, false, null));
		profile.setSynchronizationType(syncType);
		profile.setDestination(dst);
	}

	/**
	 * recursively delete directory and all contained files.
	 *
	 * @param dir directory to clear
	 */
	protected void clearDirectory(final File dir) {
		if ((testingDst == dir) && "ftp".equals(filesystem)) {
			FileSystem fs = new UnixFakeFileSystem();
			fs.add(new DirectoryEntry("/sampleuser"));
			m_fakeServer.setFileSystem(fs);
		}
		else {
			File[] children = dir.listFiles();
			if (null != children) {
				for (File file : children) {
					if (file.isDirectory()) {
						clearDirectory(file);
					}
					assertTrue("File.delete failed for: " + file.getAbsolutePath(), file.delete());
				}
			}
		}
	}

	protected void createNewDir(File dir, String dirname, long lastModified) {
		File d = new File(dir, dirname);
		d.mkdir();
		setLastModified(dir, dirname, lastModified);
	}

	protected long getLastModified() {
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date d = cal.getTime();
		return d.getTime();
	}

	protected void setLastModified(File dir, String filename, long lm) {
		File file = new File(dir, filename);
		if (!file.setLastModified(lm)) {
			throw new RuntimeException("file.setLastModified(" + dir.getAbsolutePath() + "/" + filename + ") FAILED");
		}
	}

	protected void delete(File dir, String filename) {
		if (!(new File(dir, filename).delete())) {
			throw new RuntimeException("file.delete(" + dir.getAbsolutePath() + "/" + filename + ") FAILED");
		}
	}

	protected void fileToDir(File dir, String filename, long lm) {
		File file = new File(dir, filename);
		if (!file.delete()) {
			throw new RuntimeException("file.delete(" + dir.getAbsolutePath() + "/" + filename + ") FAILED");
		}
		if (!file.mkdir()) {
			throw new RuntimeException("file.mkdir(" + dir.getAbsolutePath() + "/" + filename + ") FAILED");
		}
		setLastModified(dir, filename, lm);
	}

	protected void dirToFile(File dir, String filename, long lm) throws IOException {
		File file = new File(dir, filename);
		if (!file.delete()) {
			throw new RuntimeException("file.delete(" + dir.getAbsolutePath() + "/" + filename + ") FAILED");
		}
		if (!file.createNewFile()) {
			throw new RuntimeException("file.createNewFile(" + dir.getAbsolutePath() + "/" + filename + ") FAILED");
		}
		setLastModified(dir, filename, lm);
	}

	protected PrintStream createNewFile(final File dir, final String filename) throws IOException {
		File file = new File(dir, filename);
		File d = file.getParentFile();
		assertTrue("File.mkdirs failed for: " + file.getParentFile().getAbsolutePath(), d.mkdirs() || d.exists());
		assertTrue("File.createNewFile failed for: " + file.getAbsolutePath(), file.createNewFile());
		PrintStream out = new PrintStream(new FileOutputStream(file));
		return out;
	}

	protected void createNewFileWithContents(File dir, String filename, long lm, String content) throws IOException {
		if ((dir == testingDst) && "ftp".equals(filesystem)) {
			FileEntry file = new FileEntry("/sampleuser/" + filename, content);
			file.setLastModified(new Date(lm));
			m_fakeServer.getFileSystem().add(file);
		}
		else {
			try (PrintStream out = createNewFile(dir, filename)) {
				out.print(content);
			}
			File f = new File(dir, filename);
			assertTrue("File.setLastModified failed for: " + f.getAbsolutePath(), f.setLastModified(lm));
		}
	}

	protected TaskTree assertPhaseOneActions(final Map<String, Action> expectation) throws Exception {
		TaskGenerationListener list = new TaskGenerationListener() {
			@Override
			public void taskGenerationFinished(final Task task) {
				Object ex = expectation.get(task.getSource().getName());

				assertNotNull("Unexpected generated Task for file: " + task.getSource().getName(), ex);
				assertTrue("Action was " + task.getCurrentAction() + ", expected: " + ex + " for File " + task.getSource().getName(),
						task.getCurrentAction().equalsExceptExplanation((Action) ex));
			}

			@Override
			public void taskGenerationStarted(final FSFile source, final FSFile destination) {
			}

			@Override
			public void taskTreeFinished(final TaskTree tree) {
			}

			@Override
			public void taskTreeStarted(final TaskTree tree) {
			}
		};

		TaskGenerator processor = synchronizer.getTaskGenerator();
		processor.addTaskGenerationListener(list);
		TaskTree tree = processor.execute(profile, false);
		processor.removeTaskGenerationListener(list);
		return tree;
	}

	private long prepareForTest() {
		clearDirectory(testingSrc);
		clearDirectory(testingDst);
		return getLastModified();
	}

	private void verifyExpectations(Map<String, Action> expectation) throws Exception {
		TaskTree tree = assertPhaseOneActions(expectation);
		synchronizer.performActions(tree);
	}

	@Test
	public void testPublishUpdate() throws Exception {
		prepareProfile("Publish/Update");
		Map<String, Action> expectation = new HashMap<>();
		long lm;

		lm = prepareForTest();
		createNewFileWithContents(testingSrc, "sourceFile1.txt", lm, "this is a test\ncontent1");
		createNewFileWithContents(testingSrc, "sourceFile2.txt", lm, "this is a test\ncontent2");
		createNewFileWithContents(testingDst, "sourceFile1.txt", lm, "this is a test\ncontent1");

		createNewFileWithContents(testingSrc, "-strangeFolder/sub folder/sourceFile3.txt", lm, "this is a test\ncontent3");
		createNewFileWithContents(testingDst, "-strangeFolder/sub folder/sourceFile3.txt", lm, "this is a test\ncontent3");

		expectation.clear();
		expectation.put("sourceFile1.txt", new Action(ActionType.Nothing, Location.None, BufferUpdate.None, ""));
		expectation.put("sourceFile2.txt", new Action(ActionType.Add, Location.Destination, BufferUpdate.Destination, ""));
		expectation.put("sourceFile3.txt", new Action(ActionType.Nothing, Location.None, BufferUpdate.None, ""));
		expectation.put("-strangeFolder", new Action(ActionType.Nothing, Location.None, BufferUpdate.None, ""));
		expectation.put("sub folder", new Action(ActionType.Nothing, Location.None, BufferUpdate.None, ""));
		verifyExpectations(expectation);

		lm = prepareForTest();
		createNewFileWithContents(testingSrc, "sub - folder/sub2 - folder/sourceFile1.txt", lm, "this is a test\ncontent1");
		createNewFileWithContents(testingSrc, "sub - folder/sourceFile2.txt", lm, "this is a test\ncontent2");
		createNewFileWithContents(testingSrc, "-strangeFolder/sub folder/sourceFile3.txt", lm, "this is a test\ncontent3");
		createNewFileWithContents(testingDst, "-strangeFolder2/sub2 folder/sourceFile4.txt", lm, "this is a test\ncontent4");

		expectation.clear();
		expectation.put("sub - folder", new Action(ActionType.Add, Location.Destination, BufferUpdate.Destination, ""));
		expectation.put("sub2 - folder", new Action(ActionType.Add, Location.Destination, BufferUpdate.Destination, ""));
		expectation.put("sourceFile1.txt", new Action(ActionType.Add, Location.Destination, BufferUpdate.Destination, ""));
		expectation.put("sourceFile2.txt", new Action(ActionType.Add, Location.Destination, BufferUpdate.Destination, ""));
		expectation.put("-strangeFolder", new Action(ActionType.Add, Location.Destination, BufferUpdate.Destination, ""));
		expectation.put("sub folder", new Action(ActionType.Add, Location.Destination, BufferUpdate.Destination, ""));
		expectation.put("sourceFile3.txt", new Action(ActionType.Add, Location.Destination, BufferUpdate.Destination, ""));
		expectation.put("-strangeFolder2", new Action(ActionType.Nothing, Location.None, BufferUpdate.None, ""));
		expectation.put("sub2 folder", new Action(ActionType.Nothing, Location.None, BufferUpdate.None, ""));
		expectation.put("sourceFile4.txt", new Action(ActionType.Nothing, Location.None, BufferUpdate.None, ""));
		verifyExpectations(expectation);

		lm = prepareForTest();
		createNewFileWithContents(testingSrc, "sourceFile1.txt", lm, "this is a test\ncontent2");
		createNewFileWithContents(testingDst, "sourceFile1.txt", lm, "this is a test\ncontent2 bla");
		createNewFileWithContents(testingSrc, "sourceFile2.txt", lm + MILLI_SECONDS_PER_DAY, "this is a test\ncontent2");
		createNewFileWithContents(testingDst, "sourceFile2.txt", lm, "this is a test\ncontent2");

		expectation.clear();
		expectation.put("sourceFile1.txt", new Action(ActionType.Nothing, Location.None, BufferUpdate.None, ""));
		expectation.put("sourceFile2.txt", new Action(ActionType.Update, Location.Destination, BufferUpdate.Destination, ""));
		verifyExpectations(expectation);
	}
}
