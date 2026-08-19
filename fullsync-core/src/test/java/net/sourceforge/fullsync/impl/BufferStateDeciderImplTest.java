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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.sourceforge.fullsync.BufferedFile;
import net.sourceforge.fullsync.FSFile;
import net.sourceforge.fullsync.State;

public class BufferStateDeciderImplTest {
	private BufferStateDeciderImpl decider;
	private FSFile source;
	private BufferedFile buffered;

	@BeforeEach
	public void setUp() {
		decider = new BufferStateDeciderImpl(new SimplifiedSyncRules());
		source = mock(FSFile.class);
		buffered = mock(BufferedFile.class);
	}

	@Test
	public void notBufferedReturnsInSync() throws Exception {
		when(buffered.isBuffered()).thenReturn(false);
		assertEquals(State.IN_SYNC, decider.getState(buffered));
	}

	@Test
	public void bufferedSourceMissingDestMissingReturnsInSync() throws Exception {
		when(buffered.isBuffered()).thenReturn(true);
		when(buffered.getUnbuffered()).thenReturn(source);
		when(source.exists()).thenReturn(false);
		when(buffered.exists()).thenReturn(false);
		assertEquals(State.IN_SYNC, decider.getState(buffered));
	}

	@Test
	public void bufferedSourceMissingDestExistsReturnsOrphanDestination() throws Exception {
		when(buffered.isBuffered()).thenReturn(true);
		when(buffered.getUnbuffered()).thenReturn(source);
		when(source.exists()).thenReturn(false);
		when(buffered.exists()).thenReturn(true);
		assertEquals(State.ORPHAN_DESTINATION, decider.getState(buffered));
	}

	@Test
	public void bufferedSourceExistsDestMissingReturnsOrphanSource() throws Exception {
		when(buffered.isBuffered()).thenReturn(true);
		when(buffered.getUnbuffered()).thenReturn(source);
		when(source.exists()).thenReturn(true);
		when(buffered.exists()).thenReturn(false);
		assertEquals(State.ORPHAN_SOURCE, decider.getState(buffered));
	}

	@Test
	public void bufferedBothDirectoriesReturnsInSync() throws Exception {
		when(buffered.isBuffered()).thenReturn(true);
		when(buffered.getUnbuffered()).thenReturn(source);
		when(source.exists()).thenReturn(true);
		when(buffered.exists()).thenReturn(true);
		when(source.isDirectory()).thenReturn(true);
		when(buffered.isDirectory()).thenReturn(true);
		assertEquals(State.IN_SYNC, decider.getState(buffered));
	}

	@Test
	public void bufferedSourceDirDestFileReturnsDirSourceFileDestination() throws Exception {
		when(buffered.isBuffered()).thenReturn(true);
		when(buffered.getUnbuffered()).thenReturn(source);
		when(source.exists()).thenReturn(true);
		when(buffered.exists()).thenReturn(true);
		when(source.isDirectory()).thenReturn(true);
		when(buffered.isDirectory()).thenReturn(false);
		assertEquals(State.DIR_SOURCE_FILE_DESTINATION, decider.getState(buffered));
	}

	@Test
	public void bufferedSourceFileDest_dirReturnsFileSourceDirDestination() throws Exception {
		when(buffered.isBuffered()).thenReturn(true);
		when(buffered.getUnbuffered()).thenReturn(source);
		when(source.exists()).thenReturn(true);
		when(buffered.exists()).thenReturn(true);
		when(source.isDirectory()).thenReturn(false);
		when(buffered.isDirectory()).thenReturn(true);
		assertEquals(State.FILE_SOURCE_DIR_DESTINATION, decider.getState(buffered));
	}

	@Test
	public void bufferedBothFilesSourceNewerReturnsFileChangeSource() throws Exception {
		when(buffered.isBuffered()).thenReturn(true);
		when(buffered.getUnbuffered()).thenReturn(source);
		when(source.exists()).thenReturn(true);
		when(buffered.exists()).thenReturn(true);
		when(source.isDirectory()).thenReturn(false);
		when(buffered.isDirectory()).thenReturn(false);
		when(source.getLastModified()).thenReturn(2000L);
		when(buffered.getLastModified()).thenReturn(1000L);
		assertEquals(State.FILE_CHANGE_SOURCE, decider.getState(buffered));
	}

	@Test
	public void bufferedBothFilesInSyncReturnsInSync() throws Exception {
		when(buffered.isBuffered()).thenReturn(true);
		when(buffered.getUnbuffered()).thenReturn(source);
		when(source.exists()).thenReturn(true);
		when(buffered.exists()).thenReturn(true);
		when(source.isDirectory()).thenReturn(false);
		when(buffered.isDirectory()).thenReturn(false);
		when(source.getLastModified()).thenReturn(1000L);
		when(buffered.getLastModified()).thenReturn(1000L);
		when(source.getSize()).thenReturn(512L);
		when(buffered.getSize()).thenReturn(512L);
		assertEquals(State.IN_SYNC, decider.getState(buffered));
	}
}
