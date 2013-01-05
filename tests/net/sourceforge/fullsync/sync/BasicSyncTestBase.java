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
package net.sourceforge.fullsync.sync;

import java.util.Hashtable;

import net.sourceforge.fullsync.Action;
import net.sourceforge.fullsync.BaseConnectionTest;
import net.sourceforge.fullsync.TaskTree;
import net.sourceforge.fullsync.impl.SimplyfiedRuleSetDescriptor;

import org.junit.After;
import org.junit.Before;

public class BasicSyncTestBase extends BaseConnectionTest {
	protected Hashtable<String, Action> expectation;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		profile.setRuleSet(new SimplyfiedRuleSetDescriptor(true, null, false, null));
		expectation = new Hashtable<String, Action>();
	}

	@After
	public void after() throws Exception {
		TaskTree tree;
		/* Phase One: */
		tree = assertPhaseOneActions(expectation);
		/* Phase Two: */
		synchronizer.performActions(tree); // TODO assert task finished events ?
	}

	public void fileInSync() throws Exception {
		createNewFileWithContents(testingSrc, "inSync.txt", lm, "This file is in sync");
		createNewFileWithContents(testingDst, "inSync.txt", lm, "This file is in sync");
	}

	public void fileChangeInSource() throws Exception {
		createNewFileWithContents(testingSrc, "changeSource.txt", lm, "This file will be changed in source.");
		createNewFileWithContents(testingDst, "changeSource.txt", lm, "This file will be changed in source.");
		setLastModified(testingSrc, "changeSource.txt", lm + MILLI_SECONDS_PER_DAY);
	}

	public void fileChangeInDestination() throws Exception {
		createNewFileWithContents(testingSrc, "changeDestination.txt", lm, "This file will be changed in destination.");
		createNewFileWithContents(testingDst, "changeDestination.txt", lm, "This file will be changed in destination.");
		setLastModified(testingDst, "changeDestination.txt", lm + MILLI_SECONDS_PER_DAY);
	}
	public void fileNewInDestination() throws Exception {
		createNewFileWithContents(testingDst, "newInDestination.txt", lm, "This file is new in Destination.");
	}
	public void fileToDirSource() throws Exception {
		createNewDir(testingSrc, "fileToDirSource.txt", lm);
		createNewFileWithContents(testingDst, "fileToDirSource.txt", lm, "This file will get a dir in source.");
	}
	public void fileToDirDestination() throws Exception {
		createNewFileWithContents(testingSrc, "fileToDirDestination.txt", lm, "This file will get a dir in destination.");
		createNewDir(testingDst, "fileToDirDestination.txt", lm);
	}
	public void dirInSync() throws Exception {
		createNewDir(testingSrc, "inSync", lm);
		createNewDir(testingDst, "inSync", lm);
	}
}
