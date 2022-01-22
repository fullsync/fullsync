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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import net.sourceforge.fullsync.ActionType;
import net.sourceforge.fullsync.BufferUpdate;
import net.sourceforge.fullsync.Location;

public class BackupActionDeciderTest extends ActionDeciderTestUtil {
	private BackupActionDecider decider;

	@Override
	@BeforeEach
	protected void setUp() {
		super.setUp();
		decider = new BackupActionDecider();
	}

	@Test
	public void testInSync() throws Exception {
		var task = decider.getTask(existingTestNode, existingTestNode, stateDecider, bufferedStateDecider);
		checkAction(task.getCurrentAction(), ActionType.NOTHING, Location.NONE, BufferUpdate.NONE);
	}

	@Test
	public void testDirectoryInSync() throws Exception {
		var task = decider.getTask(directoryTestNode, directoryTestNode, stateDecider, bufferedStateDecider);
		checkAction(task.getCurrentAction(), ActionType.NOTHING, Location.NONE, BufferUpdate.NONE);
	}

	@Test
	public void testDestinationMissing() throws Exception {
		var task = decider.getTask(existingTestNode, missingTestNode, stateDecider, bufferedStateDecider);
		checkAction(task.getCurrentAction(), ActionType.ADD, Location.DESTINATION, BufferUpdate.DESTINATION);
	}

	@Test
	public void testSourceMissing() throws Exception {
		var task = decider.getTask(missingTestNode, existingTestNode, stateDecider, bufferedStateDecider);
		checkAction(task.getCurrentAction(), ActionType.NOTHING, Location.NONE, BufferUpdate.NONE);
	}

	@Test
	public void testSourceModifiedUpdated() throws Exception {
		var task = decider.getTask(largeTestNode, existingTestNode, stateDecider, bufferedStateDecider);
		checkAction(task.getCurrentAction(), ActionType.UPDATE, Location.DESTINATION, BufferUpdate.DESTINATION);
	}

	@Test
	public void testDestinationUpdated() throws Exception {
		var task = decider.getTask(existingTestNode, largeTestNode, stateDecider, bufferedStateDecider);
		checkAction(task.getCurrentAction(), ActionType.UPDATE, Location.DESTINATION, BufferUpdate.DESTINATION);
	}

	@Disabled
	@Test
	public void testSourceBigger() throws Exception {
		var task = decider.getTask(existingBigTestNode, existingTestNode, stateDecider, bufferedStateDecider);
		checkAction(task.getCurrentAction(), ActionType.NOT_DECIDABLE_ERROR, Location.NONE, BufferUpdate.NONE);
	}

	@Disabled
	@Test
	public void testDestinationBigger() throws Exception {
		var task = decider.getTask(existingTestNode, existingBigTestNode, stateDecider, bufferedStateDecider);
		checkAction(task.getCurrentAction(), ActionType.NOT_DECIDABLE_ERROR, Location.NONE, BufferUpdate.NONE);
	}

	@Test
	public void testFileToDirectory() throws Exception {
		var task = decider.getTask(existingTestNode, directoryTestNode, stateDecider, bufferedStateDecider);
		checkAction(task.getCurrentAction(), ActionType.DIR_HERE_FILE_THERE_ERROR, Location.DESTINATION, BufferUpdate.NONE);
	}

	@Test
	public void testDirectoryToFile() throws Exception {
		var task = decider.getTask(directoryTestNode, existingTestNode, stateDecider, bufferedStateDecider);
		checkAction(task.getCurrentAction(), ActionType.DIR_HERE_FILE_THERE_ERROR, Location.SOURCE, BufferUpdate.NONE);
	}
}
