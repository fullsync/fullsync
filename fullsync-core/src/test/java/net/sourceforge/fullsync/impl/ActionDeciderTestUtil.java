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

import org.junit.jupiter.api.Assertions;

import net.sourceforge.fullsync.Action;
import net.sourceforge.fullsync.ActionType;
import net.sourceforge.fullsync.BufferStateDecider;
import net.sourceforge.fullsync.BufferUpdate;
import net.sourceforge.fullsync.FileComparer;
import net.sourceforge.fullsync.Location;
import net.sourceforge.fullsync.StateDecider;
import net.sourceforge.fullsync.fs.FSFile;
import net.sourceforge.fullsync.rules.filefilter.TestNode;

public class ActionDeciderTestUtil {
	protected FileComparer fileComparer;
	protected StateDecider stateDecider;
	protected BufferStateDecider bufferedStateDecider;
	protected FSFile root;
	protected FSFile existingTestNode;
	protected FSFile existingBigTestNode;
	protected FSFile missingTestNode;
	protected FSFile largeTestNode;
	protected FSFile directoryTestNode;

	protected void setUp() {
		fileComparer = new SimplifiedSyncRules();
		bufferedStateDecider = new BufferStateDeciderImpl(fileComparer);
		stateDecider = new StateDeciderImpl(fileComparer);
		root = new TestNode("root", null, true, true, 0, 0);
		existingTestNode = new TestNode("test", root, true, false, 1, 1000);
		existingBigTestNode = new TestNode("test", root, true, false, 2, 1000);
		missingTestNode = new TestNode("test", root, false, false, 0, 1000);
		largeTestNode = new TestNode("test", root, true, false, 6000, 2000);
		directoryTestNode = new TestNode("test", root, true, true, 1, 1000);
	}

	protected void checkAction(Action action, ActionType type, Location location, BufferUpdate bufferUpdate) {
		Assertions.assertEquals(type, action.getType());
		Assertions.assertEquals(location, action.getLocation());
		Assertions.assertEquals(bufferUpdate, action.getBufferUpdate());
	}
}
