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
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.TimeZone;

import net.sourceforge.fullsync.impl.SimplyfiedRuleSetDescriptor;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseConnectionTest {
	protected static final int MILLI_SECONDS_PER_DAY = 86400000;
	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();

	protected File testingDst;
	protected File testingSrc;
	protected Synchronizer synchronizer;
	protected Profile profile;
	protected Logger logger = LoggerFactory.getLogger(BaseConnectionTest.class.getSimpleName());
	protected long lm;

	@Before
	public void setUp() throws Exception {
		testingDst = tmpFolder.newFolder("destination");
		testingDst.mkdirs();
		testingSrc = tmpFolder.newFolder("source");
		testingSrc.mkdirs();

		synchronizer = new Synchronizer();
		ConnectionDescription src = new ConnectionDescription(testingSrc.toURI());
		ConnectionDescription dst = new ConnectionDescription(testingDst.toURI());
		src.setParameter("bufferStrategy", "");

		profile = new Profile(
				"TestProfile",
				src,
				dst,
				new SimplyfiedRuleSetDescriptor(true, null, false, null)
				);
		profile.setSynchronizationType("Publish/Update");

		clearDirectory(testingSrc);
		clearDirectory(testingDst);
		lm = getLastModified();
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
		if (file.exists() && !file.setLastModified(lm)) {
			throw new RuntimeException("file.setLastModified(" + dir.getAbsolutePath() + "/" + filename + ") FAILED");
		}
	}

	protected void delete(File dir, String filename) {
		File file = new File(dir, filename);
		if (file.exists() && !file.delete()) {
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
		if (file.exists() && !file.delete()) {
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

	protected void createNewFileWithContents(final File dir, final String filename, final long lm, final String content) throws IOException {
		PrintStream out = createNewFile(dir, filename);
		out.print(content);
		out.close();
		File f = new File(dir, filename);

		assertTrue("File.setLastModified failed for: " + f.getAbsolutePath(), f.setLastModified(lm));
	}

	protected TaskTree assertPhaseOneActions(final Hashtable<String, Action> expectation) throws Exception {
		TaskGenerationListener list = new TaskGenerationListener() {
			@Override
			public void taskGenerationFinished(final Task task) {
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
			public void taskGenerationStarted(final net.sourceforge.fullsync.fs.File source, final net.sourceforge.fullsync.fs.File destination) {
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

	public void testSingleInSync() throws Exception {
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
		createNewFileWithContents(testingSrc, "sourceFile1.txt", lm, "this is a test\ncontent2");
		createNewFileWithContents(testingDst, "sourceFile1.txt", lm, "this is a test\ncontent2 bla");
		createNewFileWithContents(testingSrc, "sourceFile2.txt", lm + MILLI_SECONDS_PER_DAY, "this is a test\ncontent2");
		createNewFileWithContents(testingDst, "sourceFile2.txt", lm, "this is a test\ncontent2");

		Hashtable<String, Action> expectation = new Hashtable<String, Action>();
		expectation.put("sourceFile1.txt", new Action(Action.Nothing, Location.None, BufferUpdate.None, ""));
		expectation.put("sourceFile2.txt", new Action(Action.Update, Location.Destination, BufferUpdate.Destination, ""));
		assertPhaseOneActions(expectation);
	}

}
