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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.Hashtable;

import net.sourceforge.fullsync.impl.AdvancedRuleSetDescriptor;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

public class BaseConnectionTest {
	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();

	protected File testingDst;
	protected File testingSrc;
	protected Synchronizer synchronizer;
	protected Profile profile;
	protected Logger logger = Logger.getLogger(BaseConnectionTest.class);

	@Before
	public void setUp() throws Exception {
		testingDst = tmpFolder.newFolder("destination");
		testingDst.mkdirs();
		testingSrc = tmpFolder.newFolder("source");
		testingSrc.mkdirs();

		synchronizer = new Synchronizer();
		profile = new Profile();
		profile.setName("TestProfile");
		ConnectionDescription src = new ConnectionDescription(testingSrc.toURI());
		src.setParameter("bufferStrategy", "");
		profile.setSource(src);

		profile.setRuleSet(new AdvancedRuleSetDescriptor("UPLOAD"));
		profile.setSynchronizationType("Publish/Update");
	}

	@After
	public void tearDown() throws Exception {
		try {
			//FIXME: disconnect source and destination!
		}
		catch (Exception e) {
		}
		tmpFolder.delete();
	}

	/**
	 * recursively delete directory and all contained files.
	 *
	 * @param dir directory to clear
	 */
	protected void clearDirectory(final File dir) {
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				clearDirectory(file);
			}
			assertTrue("File.delete failed for: " + file.getAbsolutePath(), file.delete());
		}
	}


	protected void createRuleFile() throws IOException {
		createNewFileWithContents(testingSrc, ".syncrules", new Date().getTime(), "START RULESET UPLOAD\n" + "	USE RULEFILES SOURCE\n"
				+ "	USE DIRECTION DESTINATION\n" + "	USE RECURSION YES\n" + "	USE RECURSIONONIGNORE YES\n" + "\n"
				+ "	APPLY IGNORERULES YES\n" + "	APPLY TAKERULES YES\n" + "	APPLY DELETION DESTINATION\n" + "\n"
				+ "	DEFINE IGNORE \"^[.].+\"\n" + "	DEFINE SYNC \"length != length\"\n" + "	DEFINE SYNC \"date != date\"\n"
				+ "END RULESET UPLOAD");
	}

	protected PrintStream createNewFile(File dir, String filename) throws IOException {
		File file = new File(dir, filename);
		File d = file.getParentFile();
		assertTrue("File.mkdirs failed for: " + file.getParentFile().getAbsolutePath(), d.mkdirs() || d.exists());
		assertTrue("File.createNewFile failed for: " + file.getAbsolutePath(), file.createNewFile());
		PrintStream out = new PrintStream(new FileOutputStream(file));
		return out;
	}

	protected void createNewFileWithContents(File dir, String filename, long lm, String content) throws IOException {
		PrintStream out = createNewFile(dir, filename);
		out.print(content);
		out.close();
		File f = new File(dir, filename);

		assertTrue("File.setLastModified failed for: " + f.getAbsolutePath(), f.setLastModified(lm));
	}

	protected TaskTree assertPhaseOneActions(final Hashtable<String, Action> expectation) throws Exception {
		TaskGenerationListener list = new TaskGenerationListener() {
			@Override
			public void taskGenerationFinished(Task task) {
				Object ex = expectation.get(task.getSource().getName());

				if (null == ex) {
					logger.error("Unexpected generated Task for file: " + task.getSource().getName() + " (null)");
				}
				assertNotNull("Unexpected generated Task for file: " + task.getSource().getName(), ex);
				if (!task.getCurrentAction().equalsExceptExplanation((Action) ex)) {
					logger.error("Action was " + task.getCurrentAction() + ", expected: " + ex + " for File " + task.getSource().getName());
				}
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
		clearDirectory(testingSrc);
		clearDirectory(testingDst);
		createRuleFile();
		long lm = new Date().getTime();

		createNewFileWithContents(testingSrc, "sourceFile1.txt", lm, "this is a test\ncontent1");
		createNewFileWithContents(testingSrc, "sourceFile2.txt", lm, "this is a test\ncontent2");
		createNewFileWithContents(testingDst, "sourceFile1.txt", lm, "this is a test\ncontent1");

		Hashtable<String, Action> expectation = new Hashtable<String, Action>();
		expectation.put("sourceFile1.txt", new Action(Action.Nothing, Location.None, BufferUpdate.None, ""));
		expectation.put("sourceFile2.txt", new Action(Action.Add, Location.Destination, BufferUpdate.Destination, ""));
		// Phase One:
		TaskTree tree = assertPhaseOneActions(expectation);
		// Phase Three:
		synchronizer.performActions(tree); // TODO assert task finished events ?
	}

	public void testSingleSpaceMinus() throws Exception {
		clearDirectory(testingSrc);
		clearDirectory(testingDst);
		createRuleFile();
		long lm = new Date().getTime();

		createNewFileWithContents(testingSrc, "sub - folder/sub2 - folder/sourceFile1.txt", lm, "this is a test\ncontent1");
		createNewFileWithContents(testingSrc, "sub - folder/sourceFile2.txt", lm, "this is a test\ncontent2");

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

	public void testSingleFileChange() throws Exception {
		clearDirectory(testingSrc);
		clearDirectory(testingDst);
		createRuleFile();
		long lm = new Date().getTime();

		createNewFileWithContents(testingSrc, "sourceFile1.txt", lm, "this is a test\ncontent2");
		createNewFileWithContents(testingDst, "sourceFile1.txt", lm, "this is a test\ncontent2 bla");
		createNewFileWithContents(testingSrc, "sourceFile2.txt", lm + 3600, "this is a test\ncontent2");
		createNewFileWithContents(testingDst, "sourceFile2.txt", lm, "this is a test\ncontent2");

		Hashtable<String, Action> expectation = new Hashtable<String, Action>();
		expectation.put("sourceFile1.txt", new Action(Action.Update, Location.Destination, BufferUpdate.Destination, ""));
		expectation.put("sourceFile2.txt", new Action(Action.Update, Location.Destination, BufferUpdate.Destination, ""));
		assertPhaseOneActions(expectation);
	}

}
