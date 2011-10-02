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
import java.util.Date;
import java.util.Hashtable;

import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class PublishUpdateTest extends BaseConnectionTest {
	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		ConnectionDescription dst = new ConnectionDescription(testingDst.toURI());
		dst.setParameter("bufferStrategy", "syncfiles");
		profile.setDestination(dst);
	}

	protected void createNewDir(File dir, String dirname, long lastModified) {
		File d = new File(dir, dirname);
		d.mkdir();
		d.setLastModified(lastModified);
	}

	@Test
	public void testBasicSynchronization() throws Exception {
		createRuleFile();
		long lm = new Date().getTime();
		Hashtable<String, Action> expectation = new Hashtable<String, Action>();
		TaskTree tree;

		// Creating files and dirs
		createNewFileWithContents(testingSrc, "inSync.txt", lm, "This file will stay in sync");
		createNewFileWithContents(testingSrc, "changeSource.txt", lm, "This file will be changed in source.");
		createNewFileWithContents(testingSrc, "changeDestination.txt", lm, "This file will be changed in destination.");
		createNewFileWithContents(testingSrc, "changeBoth.txt", lm, "This file will be changed in source and destination.");
		createNewFileWithContents(testingSrc, "deleteSource.txt", lm, "This file will be deleted in source.");
		createNewFileWithContents(testingSrc, "deleteDestination.txt", lm, "This file will be deleted in source.");
		createNewFileWithContents(testingSrc, "deleteBoth.txt", lm, "This file will be deleted in source.");
		createNewFileWithContents(testingSrc, "fileToDirSource.txt", lm, "This file will get a dir in source.");
		createNewFileWithContents(testingSrc, "fileToDirDestination.txt", lm, "This file will get a dir in destination.");
		createNewFileWithContents(testingSrc, "fileToDirBoth.txt", lm, "This file will get a dir in source and destination.");
		createNewDir(testingSrc, "inSync", lm); // This dir will stay in sync.
		createNewDir(testingSrc, "changeSource", lm); // "This file will be changed in source." );
		createNewDir(testingSrc, "changeDestination", lm); // "This file will be changed in destination." );
		createNewDir(testingSrc, "changeBoth", lm); // "This file will be changed in source and destination." );
		createNewDir(testingSrc, "deleteSource", lm); // This dir will get a file." );
		createNewDir(testingSrc, "deleteDestination", lm); // This dir will get a file." );
		createNewDir(testingSrc, "deleteBoth", lm); // This dir will get a file." );
		createNewDir(testingSrc, "dirToFileSource", lm); // This dir will get a file." );
		createNewDir(testingSrc, "dirToFileDestination", lm); // This dir will get a file." );
		createNewDir(testingSrc, "dirToFileBoth", lm); // This dir will get a file." );

		// Setting expectations for initial synchronization
		expectation.clear();
		expectation.put("inSync.txt", new Action(Action.Add, Location.Destination, BufferUpdate.Destination, ""));
		expectation.put("changeSource.txt", new Action(Action.Add, Location.Destination, BufferUpdate.Destination, ""));
		expectation.put("changeDestination.txt", new Action(Action.Add, Location.Destination, BufferUpdate.Destination, ""));
		expectation.put("changeBoth.txt", new Action(Action.Add, Location.Destination, BufferUpdate.Destination, ""));
		expectation.put("deleteSource.txt", new Action(Action.Add, Location.Destination, BufferUpdate.Destination, ""));
		expectation.put("deleteDestination.txt", new Action(Action.Add, Location.Destination, BufferUpdate.Destination, ""));
		expectation.put("deleteBoth.txt", new Action(Action.Add, Location.Destination, BufferUpdate.Destination, ""));
		expectation.put("fileToDirSource.txt", new Action(Action.Add, Location.Destination, BufferUpdate.Destination, ""));
		expectation.put("fileToDirDestination.txt", new Action(Action.Add, Location.Destination, BufferUpdate.Destination, ""));
		expectation.put("fileToDirBoth.txt", new Action(Action.Add, Location.Destination, BufferUpdate.Destination, ""));

		expectation.put("inSync", new Action(Action.Add, Location.Destination, BufferUpdate.Destination, ""));
		expectation.put("changeSource", new Action(Action.Add, Location.Destination, BufferUpdate.Destination, ""));
		expectation.put("changeDestination", new Action(Action.Add, Location.Destination, BufferUpdate.Destination, ""));
		expectation.put("changeBoth", new Action(Action.Add, Location.Destination, BufferUpdate.Destination, ""));
		expectation.put("deleteSource", new Action(Action.Add, Location.Destination, BufferUpdate.Destination, ""));
		expectation.put("deleteDestination", new Action(Action.Add, Location.Destination, BufferUpdate.Destination, ""));
		expectation.put("deleteBoth", new Action(Action.Add, Location.Destination, BufferUpdate.Destination, ""));
		expectation.put("dirToFileSource", new Action(Action.Add, Location.Destination, BufferUpdate.Destination, ""));
		expectation.put("dirToFileDestination", new Action(Action.Add, Location.Destination, BufferUpdate.Destination, ""));
		expectation.put("dirToFileBoth", new Action(Action.Add, Location.Destination, BufferUpdate.Destination, ""));

		/* Phase One: */tree = assertPhaseOneActions(expectation);
		/* Phase Two: */synchronizer.performActions(tree); // TODO assert task finished events ?

		// Now changing files and dirs and setting expectations
		expectation.clear();
		expectation.put("inSync.txt", new Action(Action.Nothing, Location.None, BufferUpdate.None, ""));

		setLastModified(testingSrc, "changeSource.txt", lm + 10000);
		setLastModified(testingDst, "changeDestination.txt", lm + 10000);
		setLastModified(testingSrc, "changeBoth.txt", lm + 10000);
		setLastModified(testingDst, "changeBoth.txt", lm + 10000);
		expectation.put("changeSource.txt", new Action(Action.Update, Location.Destination, BufferUpdate.Destination, ""));
		expectation.put("changeDestination.txt", new Action(Action.Nothing, Location.None, BufferUpdate.None, ""));
		expectation.put("changeBoth.txt", new Action(Action.UnexpectedChangeError, Location.Destination, BufferUpdate.None, ""));

		delete(testingSrc, "deleteSource.txt");
		delete(testingDst, "deleteDestination.txt");
		delete(testingSrc, "deleteBoth.txt");
		delete(testingDst, "deleteBoth.txt");
		expectation.put("deleteSource.txt", new Action(Action.Delete, Location.Destination, BufferUpdate.Destination, "", false));
		expectation.put("deleteDestination.txt", new Action(Action.Nothing, Location.None, BufferUpdate.None, ""));
		expectation.put("deleteBoth.txt", new Action(Action.UnexpectedChangeError, Location.Destination, BufferUpdate.None, ""));

		fileToDir(testingSrc, "fileToDirSource.txt");
		fileToDir(testingDst, "fileToDirDestination.txt");
		fileToDir(testingSrc, "fileToDirBoth.txt");
		fileToDir(testingDst, "fileToDirBoth.txt");
		expectation.put("fileToDirSource.txt", new Action(Action.DirHereFileThereError, Location.Source, BufferUpdate.None, ""));
		expectation.put("fileToDirDestination.txt", new Action(Action.Nothing, Location.None, BufferUpdate.None, ""));
		expectation.put("fileToDirBoth.txt", new Action(Action.Nothing, Location.None, BufferUpdate.None, ""));

		expectation.put("inSync", new Action(Action.Nothing, Location.None, BufferUpdate.None, ""));

		setLastModified(testingSrc, "changeSource", lm + 10000);
		setLastModified(testingDst, "changeDestination", lm + 10000);
		setLastModified(testingSrc, "changeBoth", lm + 10000);
		setLastModified(testingDst, "changeBoth", lm + 10000);
		expectation.put("changeSource", new Action(Action.Nothing, Location.None, BufferUpdate.None, ""));
		expectation.put("changeDestination", new Action(Action.Nothing, Location.None, BufferUpdate.None, ""));
		expectation.put("changeBoth", new Action(Action.Nothing, Location.None, BufferUpdate.None, ""));

		delete(testingSrc, "deleteSource");
		delete(testingDst, "deleteDestination");
		delete(testingSrc, "deleteBoth");
		delete(testingDst, "deleteBoth");
		expectation.put("deleteSource", new Action(Action.Delete, Location.Destination, BufferUpdate.Destination, "", false));
		expectation.put("deleteDestination", new Action(Action.Nothing, Location.None, BufferUpdate.None, ""));
		expectation.put("deleteBoth", new Action(Action.UnexpectedChangeError, Location.Destination, BufferUpdate.None, ""));

		dirToFile(testingSrc, "dirToFileSource");
		dirToFile(testingDst, "dirToFileDestination");
		dirToFile(testingSrc, "dirToFileBoth");
		dirToFile(testingDst, "dirToFileBoth");
		expectation.put("dirToFileSource", new Action(Action.DirHereFileThereError, Location.Destination, BufferUpdate.None, ""));
		expectation.put("dirToFileDestination", new Action(Action.Nothing, Location.None, BufferUpdate.None, ""));
		expectation.put("dirToFileBoth", new Action(Action.UnexpectedChangeError, Location.Destination, BufferUpdate.None, ""));

		/* Phase One: */tree = assertPhaseOneActions(expectation);
		/* Phase Two: */synchronizer.performActions(tree); // TODO assert task finished events ?
	}

	protected void setLastModified(File dir, String filename, long lm) {
		File file = new File(dir, filename);
		file.setLastModified(lm);
	}

	protected void delete(File dir, String filename) {
		File file = new File(dir, filename);
		file.delete();
	}

	protected void fileToDir(File dir, String filename) {
		File file = new File(dir, filename);
		long lm = file.lastModified();
		file.delete();
		file.mkdir();
		file.setLastModified(lm);
	}

	protected void dirToFile(File dir, String filename) throws IOException {
		File file = new File(dir, filename);
		long lm = file.lastModified();
		file.delete();
		file.createNewFile();
		file.setLastModified(lm);
	}
}
