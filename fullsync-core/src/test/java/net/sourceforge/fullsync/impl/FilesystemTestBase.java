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
package net.sourceforge.fullsync.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.junit.jupiter.api.io.TempDir;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.Futures;

import net.sourceforge.fullsync.Action;
import net.sourceforge.fullsync.ActionType;
import net.sourceforge.fullsync.BufferUpdate;
import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.FileSystemException;
import net.sourceforge.fullsync.FileSystemManager;
import net.sourceforge.fullsync.FullSync;
import net.sourceforge.fullsync.Location;
import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.RuleSetDescriptor;
import net.sourceforge.fullsync.Synchronizer;
import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.TaskGenerator;
import net.sourceforge.fullsync.event.TaskGenerationFinished;
import net.sourceforge.fullsync.fs.connection.FileSystemConnection;
import net.sourceforge.fullsync.fs.filesystems.FTPFileSystem;
import net.sourceforge.fullsync.fs.filesystems.LocalFileSystem;
import net.sourceforge.fullsync.fs.filesystems.SFTPFileSystem;
import net.sourceforge.fullsync.fs.filesystems.SmbFileSystem;
import net.sourceforge.fullsync.schedule.Schedule;

public abstract class FilesystemTestBase implements FileSystemManager {
	protected static final int MILLI_SECONDS_PER_DAY = 86400000;
	protected Map<String, Action> expectation;

	@Override
	public FileSystemConnection createConnection(ConnectionDescription connectionDescription, boolean interactive)
		throws FileSystemException, IOException {
		switch (connectionDescription.getScheme()) {
			case "file": //$NON-NLS-1$
				return new LocalFileSystem().createConnection(connectionDescription, interactive);
			case "sftp": //$NON-NLS-1$
				return new SFTPFileSystem(fullSync).createConnection(connectionDescription, interactive);
			case "ftp": //$NON-NLS-1$
				return new FTPFileSystem().createConnection(connectionDescription, interactive);
			case "smb": //$NON-NLS-1$
				return new SmbFileSystem().createConnection(connectionDescription, interactive);
		}
		throw new RuntimeException("Unknown scheme: " + connectionDescription.getScheme());
	}

	private EventBus eventBus;
	private FullSync fullSync;
	private TaskGenerator taskGenerator;
	protected Synchronizer synchronizer;
	protected Profile profile;
	@TempDir
	protected File testingRoot;
	protected File testingSrc;
	protected File testingDst;

	public void setUpEach() throws Exception {
		expectation = new HashMap<>();
		eventBus = new EventBus();
		eventBus.register(this);
		fullSync = new FullSync();
		fullSync.pushQuestionHandler(question -> Futures.immediateFuture(false));
		taskGenerator = new TaskGeneratorImpl(this, eventBus);
		testingSrc = new File(testingRoot, "src");
		testingDst = new File(testingRoot, "dst");
		assertTrue(testingSrc.mkdirs(), "create testingSrc");
		assertTrue(testingDst.mkdirs(), "create testingDst");
	}

	public void tearDownEach() {
		eventBus.unregister(this);
		synchronizer = null;
		profile = null;
	}

	protected abstract ConnectionDescription getDestinationConnectionDescription();

	void prepareProfile(String syncType) throws Exception {
		synchronizer = new Synchronizer(taskGenerator);
		ConnectionDescription.Builder srcBuilder = new ConnectionDescription.Builder();
		srcBuilder.setScheme("file");
		srcBuilder.setPath(testingSrc.getAbsolutePath());
		srcBuilder.setBufferStrategy("");

		String id = "0";
		String name = "TestProfile";
		String description = "Description";
		String synchronizationType = syncType;
		ConnectionDescription src = srcBuilder.build();
		ConnectionDescription dst = getDestinationConnectionDescription();
		RuleSetDescriptor ruleSet = new SimplifiedRuleSetDescriptor(true, null, false, null);
		boolean schedulingEnabled = false;
		Schedule schedule = null;
		Date lastUpdate = null;
		int lastErrorLevel = 0;
		String lastErrorString = null;
		long lastScheduleTime = 0;

		profile = new ProfileImpl(eventBus, id, name, description, synchronizationType, src, dst, ruleSet, schedulingEnabled, schedule,
			lastUpdate, lastErrorLevel, lastErrorString, lastScheduleTime);
	}

	/**
	 * recursively delete directory and all contained files.
	 *
	 * @param dir directory to clear
	 */
	protected void clearDirectory(final File dir) {
		File[] children = dir.listFiles();
		if (null != children) {
			for (File file : children) {
				if (file.isDirectory()) {
					clearDirectory(file);
				}
				assertTrue(file.delete(), "File.delete failed for: " + file.getAbsolutePath());
			}
		}
	}

	protected void createNewDir(File dir, String dirname, long lastModified) {
		assertTrue(new File(dir, dirname).mkdir(), "Failed to create directory: " + dir.getAbsolutePath() + "/" + dirname);
		setLastModified(dir, dirname, lastModified);
	}

	protected long getLastModified() {
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime().getTime();
	}

	protected void setLastModified(File dir, String filename, long lm) {
		assertTrue(new File(dir, filename).setLastModified(lm),
			"file.setLastModified(" + dir.getAbsolutePath() + "/" + filename + ") FAILED");
	}

	protected void delete(File dir, String filename) {
		assertTrue(new File(dir, filename).delete(), "file.delete(" + dir.getAbsolutePath() + "/" + filename + ") FAILED");
	}

	protected void fileToDir(File dir, String filename, long lm) {
		File file = new File(dir, filename);
		assertTrue(file.delete(), "file.delete(" + dir.getAbsolutePath() + "/" + filename + ") FAILED");
		assertTrue(file.mkdir(), "file.mkdir(" + dir.getAbsolutePath() + "/" + filename + ") FAILED");
		setLastModified(dir, filename, lm);
	}

	protected void dirToFile(File dir, String filename, long lm) throws IOException {
		File file = new File(dir, filename);
		assertTrue(file.delete(), "file.delete(" + dir.getAbsolutePath() + "/" + filename + ") FAILED");
		assertTrue(file.createNewFile(), "file.createNewFile(" + dir.getAbsolutePath() + "/" + filename + ") FAILED");
		setLastModified(dir, filename, lm);
	}

	protected PrintStream createNewFile(final File dir, final String filename) throws IOException {
		File file = new File(dir, filename);
		File d = file.getParentFile();
		assertTrue(d.mkdirs() || d.exists(), "File.mkdirs failed for: " + d.getAbsolutePath());
		assertTrue(file.createNewFile(), "File.createNewFile failed for: " + file.getAbsolutePath());
		return new PrintStream(new FileOutputStream(file));
	}

	protected void createNewFileWithContents(File dir, String filename, long lm, String content) throws IOException {
		try (PrintStream out = createNewFile(dir, filename)) {
			out.print(content);
		}
		File f = new File(dir, filename);
		if (lm > 0) {
			assertTrue(f.setLastModified(lm), "File.setLastModified failed for: " + f.getAbsolutePath());
		}
	}

	@Subscribe
	public void taskGenerationFinished(final TaskGenerationFinished taskGenerationFinished) {
		Task task = taskGenerationFinished.getTask();
		Action ex = expectation.get(task.getSource().getName());

		assertNotNull(ex, "Unexpected generated Task for file: " + task.getSource().getName());
		assertTrue(task.getCurrentAction().equalsExceptExplanation(ex),
			"Action was " + task.getCurrentAction() + ", expected: " + ex + " for File " + task.getSource().getName());
	}

	private long prepareForTest() {
		clearDirectory(testingSrc);
		clearDirectory(testingDst);
		return getLastModified();
	}

	public void testPublishUpdate() throws Exception {
		prepareProfile("Publish/Update");
		long lm;

		lm = prepareForTest();
		createNewFileWithContents(testingSrc, "sourceFile1.txt", lm, "this is a test\ncontent1");
		createNewFileWithContents(testingSrc, "sourceFile2.txt", lm, "this is a test\ncontent2");
		createNewFileWithContents(testingDst, "sourceFile1.txt", lm, "this is a test\ncontent1");

		createNewFileWithContents(testingSrc, "-strangeFolder/sub folder/sourceFile3.txt", lm, "this is a test\ncontent3");
		createNewFileWithContents(testingDst, "-strangeFolder/sub folder/sourceFile3.txt", lm, "this is a test\ncontent3");

		expectation.clear();
		expectation.put("sourceFile1.txt", new Action(ActionType.NOTHING, Location.NONE, BufferUpdate.NONE, ""));
		expectation.put("sourceFile2.txt", new Action(ActionType.ADD, Location.DESTINATION, BufferUpdate.DESTINATION, ""));
		expectation.put("sourceFile3.txt", new Action(ActionType.NOTHING, Location.NONE, BufferUpdate.NONE, ""));
		expectation.put("-strangeFolder", new Action(ActionType.NOTHING, Location.NONE, BufferUpdate.NONE, ""));
		expectation.put("sub folder", new Action(ActionType.NOTHING, Location.NONE, BufferUpdate.NONE, ""));
		synchronizer.performActions(taskGenerator.execute(profile, false));

		lm = prepareForTest();
		createNewFileWithContents(testingSrc, "sub - folder/sub2 - folder/sourceFile1.txt", lm, "this is a test\ncontent1");
		createNewFileWithContents(testingSrc, "sub - folder/sourceFile2.txt", lm, "this is a test\ncontent2");
		createNewFileWithContents(testingSrc, "-strangeFolder/sub folder/sourceFile3.txt", lm, "this is a test\ncontent3");
		createNewFileWithContents(testingDst, "-strangeFolder2/sub2 folder/sourceFile4.txt", lm, "this is a test\ncontent4");

		expectation.clear();
		expectation.put("sub - folder", new Action(ActionType.ADD, Location.DESTINATION, BufferUpdate.DESTINATION, ""));
		expectation.put("sub2 - folder", new Action(ActionType.ADD, Location.DESTINATION, BufferUpdate.DESTINATION, ""));
		expectation.put("sourceFile1.txt", new Action(ActionType.ADD, Location.DESTINATION, BufferUpdate.DESTINATION, ""));
		expectation.put("sourceFile2.txt", new Action(ActionType.ADD, Location.DESTINATION, BufferUpdate.DESTINATION, ""));
		expectation.put("-strangeFolder", new Action(ActionType.ADD, Location.DESTINATION, BufferUpdate.DESTINATION, ""));
		expectation.put("sub folder", new Action(ActionType.ADD, Location.DESTINATION, BufferUpdate.DESTINATION, ""));
		expectation.put("sourceFile3.txt", new Action(ActionType.ADD, Location.DESTINATION, BufferUpdate.DESTINATION, ""));
		expectation.put("-strangeFolder2", new Action(ActionType.NOTHING, Location.NONE, BufferUpdate.NONE, ""));
		expectation.put("sub2 folder", new Action(ActionType.NOTHING, Location.NONE, BufferUpdate.NONE, ""));
		expectation.put("sourceFile4.txt", new Action(ActionType.NOTHING, Location.NONE, BufferUpdate.NONE, ""));
		synchronizer.performActions(taskGenerator.execute(profile, false));

		lm = prepareForTest();
		createNewFileWithContents(testingSrc, "sourceFile1.txt", lm, "this is a test\ncontent2");
		createNewFileWithContents(testingDst, "sourceFile1.txt", lm, "this is a test\ncontent2 bla");
		createNewFileWithContents(testingSrc, "sourceFile2.txt", lm + MILLI_SECONDS_PER_DAY, "this is a test\ncontent2");
		createNewFileWithContents(testingDst, "sourceFile2.txt", lm, "this is a test\ncontent2");

		expectation.clear();
		expectation.put("sourceFile1.txt", new Action(ActionType.NOTHING, Location.NONE, BufferUpdate.NONE, ""));
		expectation.put("sourceFile2.txt", new Action(ActionType.UPDATE, Location.DESTINATION, BufferUpdate.DESTINATION, ""));
		synchronizer.performActions(taskGenerator.execute(profile, false));
	}
}
