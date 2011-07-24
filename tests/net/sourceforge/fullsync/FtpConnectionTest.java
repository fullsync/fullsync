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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.Hashtable;

import junit.framework.TestCase;
import net.sourceforge.fullsync.impl.AdvancedRuleSetDescriptor;

import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class FtpConnectionTest extends TestCase {
	private static final int TEST_FTP_PORT = 16131;
	private File testingDir;
	private File testingSource;
	private Synchronizer synchronizer;
	private Profile profile;
	private FakeFtpServer m_fakeServer;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		testingDir = new File("testing");
		testingSource = new File(testingDir, "source");
		testingDir.mkdirs();
		testingSource.mkdir();

		synchronizer = new Synchronizer();
		profile = new Profile();
		profile.setName("TestProfile");
		profile.setSource(new ConnectionDescription(testingSource.toURI().toString(), ""));
		profile.setDestination(new ConnectionDescription("ftp://127.0.0.1:" + TEST_FTP_PORT + "/", "syncfiles"));
		profile.getDestination().setUsername("SampleUser");
		profile.getDestination().setPassword("Sample");
		profile.setRuleSet(new AdvancedRuleSetDescriptor("UPLOAD"));
		profile.setSynchronizationType("Publish/Update");

		m_fakeServer = new FakeFtpServer();
		m_fakeServer.setServerControlPort(TEST_FTP_PORT);

		FileSystem fs = new UnixFakeFileSystem();
		fs.add(new DirectoryEntry("/sampleuser"));

		m_fakeServer.addUserAccount(new UserAccount("SampleUser", "Sample", "/sampleuser"));
		m_fakeServer.setFileSystem(fs);
		m_fakeServer.start();

		clearDirectory(testingSource);
	}

	@Override
	protected void tearDown() throws Exception {
		m_fakeServer.stop();
		clearDirectory(testingSource);
		testingSource.delete();
		testingDir.delete();
		super.tearDown();
	}

	/**
	 * recursively delete directory and all contained files.
	 *
	 * @param dir
	 *            directory to clear
	 */
	protected void clearDirectory(final File dir) {
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				clearDirectory(file);
			}
			file.delete();
		}
	}

	protected void createRuleFile() throws IOException {
		createNewFileWithContents(testingSource, ".syncrules", new Date().getTime(), "START RULESET UPLOAD\n" + "	USE RULEFILES SOURCE\n"
				+ "	USE DIRECTION DESTINATION\n" + "	USE RECURSION YES\n" + "	USE RECURSIONONIGNORE YES\n" + "\n"
				+ "	APPLY IGNORERULES YES\n" + "	APPLY TAKERULES YES\n" + "	APPLY DELETION DESTINATION\n" + "\n"
				+ "	DEFINE IGNORE \"^[.].+\"\n" + "	DEFINE SYNC \"length != length\"\n" + "	DEFINE SYNC \"date != date\"\n"
				+ "END RULESET UPLOAD");
	}

	protected PrintStream createNewFile(File dir, String filename) throws IOException {
		File file = new File(dir, filename);
		file.createNewFile();
		PrintStream out = new PrintStream(new FileOutputStream(file));
		return out;
	}

	protected void createNewFileWithContents(File dir, String filename, long lm, String content) throws IOException {
		PrintStream out = createNewFile(dir, filename);
		out.print(content);
		out.close();

		new File(dir, filename).setLastModified(lm);
	}

	protected TaskTree assertPhaseOneActions(final Hashtable<String, Action> expectation) throws Exception {
		TaskGenerationListener list = new TaskGenerationListener() {
			@Override
			public void taskGenerationFinished(Task task) {
				Object ex = expectation.get(task.getSource().getName());
				assertNotNull("Unexpected generated Task for file: " + task.getSource().getName(), ex);
				assertTrue("Action was " + task.getCurrentAction() + ", expected: " + ex + " for File " + task.getSource().getName(), task
						.getCurrentAction().equalsExceptExplanation((Action) ex));
			}

			@Override
			public void taskGenerationStarted(net.sourceforge.fullsync.fs.File source, net.sourceforge.fullsync.fs.File destination) {
			}

			@Override
			public void taskTreeFinished(TaskTree tree) {
			}

			@Override
			public void taskTreeStarted(TaskTree tree) {
			}
		};

		TaskGenerator processor = synchronizer.getTaskGenerator();
		processor.addTaskGenerationListener(list);
		TaskTree tree = processor.execute(profile);
		processor.removeTaskGenerationListener(list);
		return tree;
	}

	public void testSingleInSync() throws Exception {
		createRuleFile();
		Date d = new Date();
		long lm = d.getTime();

		createNewFileWithContents(testingSource, "sourceFile1.txt", lm, "this is a test\ncontent1");
		createNewFileWithContents(testingSource, "sourceFile2.txt", lm, "this is a test\ncontent2");
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

	public void testSingleSpaceMinus() throws Exception {
		createRuleFile();
		long lm = new Date().getTime();

		new File(testingSource, "sub - folder").mkdir();
		new File(testingSource, "sub - folder/sub2 - folder").mkdir();
		createNewFileWithContents(testingSource, "sub - folder/sub2 - folder/sourceFile1.txt", lm, "this is a test\ncontent1");
		createNewFileWithContents(testingSource, "sub - folder/sourceFile2.txt", lm, "this is a test\ncontent2");

		Hashtable<String, Action> expectation = new Hashtable<String, Action>();
		expectation.put("sub - folder", new Action(Action.Add, Location.Destination, BufferUpdate.Destination, ""));
		expectation.put("sub2 - folder", new Action(Action.Add, Location.Destination, BufferUpdate.Destination, ""));
		expectation.put("sourceFile1.txt", new Action(Action.Add, Location.Destination, BufferUpdate.Destination, ""));
		expectation.put("sourceFile2.txt", new Action(Action.Add, Location.Destination, BufferUpdate.Destination, ""));
		// Phase One:
		TaskTree tree = assertPhaseOneActions(expectation);
		// Phase Three:
		synchronizer.performActions(tree); // TODO assert task finished events ?
	}
}