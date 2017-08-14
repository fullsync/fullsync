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

import org.junit.Assert;

import net.sourceforge.fullsync.Action;
import net.sourceforge.fullsync.ActionType;
import net.sourceforge.fullsync.BufferStateDecider;
import net.sourceforge.fullsync.BufferUpdate;
import net.sourceforge.fullsync.FileComparer;
import net.sourceforge.fullsync.Location;
import net.sourceforge.fullsync.StateDecider;

public class ActionDeciderTestUtil {
	protected FileComparer fileComparer;
	protected StateDecider stateDecider;
	protected BufferStateDecider bufferedStateDecider;

	public void setUp() {
		fileComparer = new SimplyfiedSyncRules();
		bufferedStateDecider = new BufferStateDeciderImpl(fileComparer);
		stateDecider = new StateDeciderImpl(fileComparer);
	}

	protected void checkAction(Action action, ActionType type, Location location, BufferUpdate bufferUpdate) {
		Assert.assertEquals(type, action.getType());
		Assert.assertEquals(location, action.getLocation());
		Assert.assertEquals(bufferUpdate, action.getBufferUpdate());
	}
}
