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

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import net.sourceforge.fullsync.ActionType;
import net.sourceforge.fullsync.BufferUpdate;
import net.sourceforge.fullsync.Location;
import net.sourceforge.fullsync.Task;

public class ExactCopyActionDeciderTest extends ActionDeciderTestUtil {
	private ExactCopyActionDecider decider;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		decider = new ExactCopyActionDecider();
	}

	@Test
	public void testInSync() throws Exception {
		Task task = decider.getTask(existingTestNode, existingTestNode, stateDecider, bufferedStateDecider);
		checkAction(task.getCurrentAction(), ActionType.Nothing, Location.None, BufferUpdate.None);
	}

	@Test
	public void testDirectoryInSync() throws Exception {
		Task task = decider.getTask(directoryTestNode, directoryTestNode, stateDecider, bufferedStateDecider);
		checkAction(task.getCurrentAction(), ActionType.Nothing, Location.None, BufferUpdate.None);
	}

	@Test
	public void testDestinationMissing() throws Exception {
		Task task = decider.getTask(existingTestNode, missingTestNode, stateDecider, bufferedStateDecider);
		checkAction(task.getCurrentAction(), ActionType.Add, Location.Destination, BufferUpdate.Destination);
	}

	@Test
	public void testSourceMissing() throws Exception {
		Task task = decider.getTask(missingTestNode, existingTestNode, stateDecider, bufferedStateDecider);
		checkAction(task.getCurrentAction(), ActionType.Delete, Location.Destination, BufferUpdate.Destination);
	}

	@Test
	public void testSourceModifiedUpdated() throws Exception {
		Task task = decider.getTask(largeTestNode, existingTestNode, stateDecider, bufferedStateDecider);
		checkAction(task.getCurrentAction(), ActionType.Update, Location.Destination, BufferUpdate.Destination);
	}

	@Test
	public void testDestinationUpdated() throws Exception {
		Task task = decider.getTask(existingTestNode, largeTestNode, stateDecider, bufferedStateDecider);
		checkAction(task.getCurrentAction(), ActionType.Update, Location.Destination, BufferUpdate.Destination);
	}

	@Ignore
	@Test
	public void testSourceBigger() throws Exception {
		Task task = decider.getTask(existingBigTestNode, existingTestNode, stateDecider, bufferedStateDecider);
		checkAction(task.getCurrentAction(), ActionType.Update, Location.Destination, BufferUpdate.Destination);
	}

	@Ignore
	@Test
	public void testDestinationBigger() throws Exception {
		Task task = decider.getTask(existingTestNode, existingBigTestNode, stateDecider, bufferedStateDecider);
		checkAction(task.getCurrentAction(), ActionType.Update, Location.Destination, BufferUpdate.Destination);
	}

	@Test
	public void testFileToDirectory() throws Exception {
		Task task = decider.getTask(existingTestNode, directoryTestNode, stateDecider, bufferedStateDecider);
		checkAction(task.getCurrentAction(), ActionType.DirHereFileThereError, Location.Destination, BufferUpdate.None);
	}

	@Test
	public void testDirectoryToFile() throws Exception {
		Task task = decider.getTask(directoryTestNode, existingTestNode, stateDecider, bufferedStateDecider);
		checkAction(task.getCurrentAction(), ActionType.DirHereFileThereError, Location.Source, BufferUpdate.None);
	}
}
