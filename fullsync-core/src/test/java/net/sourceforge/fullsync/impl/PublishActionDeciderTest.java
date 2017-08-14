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
import org.junit.Test;

import net.sourceforge.fullsync.ActionType;
import net.sourceforge.fullsync.BufferUpdate;
import net.sourceforge.fullsync.Location;
import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.rules.filefilter.TestNode;

public class PublishActionDeciderTest extends ActionDeciderTestUtil {
	private PublishActionDecider decider;
	private File root;
	private File existingTestNode;
	private File missingTestNode;
	private File largeTestNode;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		decider = new PublishActionDecider();
		root = new TestNode("root", null, true, true, 0, 0);
		existingTestNode = new TestNode("test", root, true, false, 1, 1000);
		missingTestNode = new TestNode("test", root, false, false, 0, 1000);
		largeTestNode = new TestNode("test", root, true, false, 6000, 2000);
	}

	@Test
	public void testInSync() throws Exception {
		Task task = decider.getTask(existingTestNode, existingTestNode, stateDecider, bufferedStateDecider);
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
		checkAction(task.getCurrentAction(), ActionType.Nothing, Location.None, BufferUpdate.None);
	}

	@Test
	public void testSourceModifiedUpdated() throws Exception {
		Task task = decider.getTask(largeTestNode, existingTestNode, stateDecider, bufferedStateDecider);
		checkAction(task.getCurrentAction(), ActionType.Update, Location.Destination, BufferUpdate.Destination);
	}

	@Test
	public void testDestinationUpdated() throws Exception {
		Task task = decider.getTask(existingTestNode, largeTestNode, stateDecider, bufferedStateDecider);
		checkAction(task.getCurrentAction(), ActionType.UnexpectedChangeError, Location.Destination, BufferUpdate.None);
	}
}
