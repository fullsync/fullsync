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

import net.sourceforge.fullsync.Action;
import net.sourceforge.fullsync.BufferUpdate;
import net.sourceforge.fullsync.Location;

import org.junit.Before;
import org.junit.Test;

public class PublishUpdate extends BasicSyncTestBase {
	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		profile.setSynchronizationType("Publish/Update");
	}
	@Override
	@Test
	public void fileInSync() throws Exception {
		expectation.put("inSync.txt", new Action(Action.Nothing, Location.None, BufferUpdate.None, "in sync"));
		super.fileInSync();
	}
	@Override
	@Test
	public void fileChangeInSource() throws Exception {
		expectation.put("changeSource.txt", new Action(Action.Update, Location.Destination, BufferUpdate.Destination, "source changed, update destination"));
		super.fileChangeInSource();
	}
	@Override
	@Test
	public void fileChangeInDestination() throws Exception {
		expectation.put("changeDestination.txt", new Action(Action.UnexpectedChangeError, Location.Destination, BufferUpdate.None, ""));
		super.fileChangeInDestination();
	}
	@Override
	@Test
	public void fileNewInDestination() throws Exception {
		expectation.put("newInDestination.txt", new Action(Action.Nothing, Location.None, BufferUpdate.None, ""));
		super.fileNewInDestination();
	}
	@Override
	@Test
	public void fileToDirSource() throws Exception {
		expectation.put("fileToDirSource.txt", new Action(Action.DirHereFileThereError, Location.Source, BufferUpdate.None, ""));
		super.fileToDirSource();
	}
	@Override
	@Test
	public void fileToDirDestination() throws Exception {
		expectation.put("fileToDirDestination.txt", new Action(Action.DirHereFileThereError, Location.Destination, BufferUpdate.None, ""));
		super.fileToDirDestination();
	}
	@Override
	@Test
	public void dirInSync() throws Exception {
		expectation.put("inSync", new Action(Action.Nothing, Location.None, BufferUpdate.None, ""));
		super.dirInSync();
	}
}
