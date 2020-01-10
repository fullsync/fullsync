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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.sourceforge.fullsync.BufferUpdate;
import net.sourceforge.fullsync.FSFile;
import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.buffer.EntryDescriptor;

public class BufferUpdateEntryDescriptor implements EntryDescriptor {
	private final BufferUpdate bufferUpdate;
	private final FSFile src;
	private final FSFile dst;

	public BufferUpdateEntryDescriptor(FSFile src, FSFile dst, BufferUpdate bufferUpdate) {
		this.bufferUpdate = bufferUpdate;
		this.src = src;
		this.dst = dst;
	}

	@Override
	public Task getTask() {
		return null;
	}

	@Override
	public long getSize() {
		return 0;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return null;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return null;
	}

	@Override
	public void finishStore() throws IOException {
		// nothing to do
	}

	@Override
	public void finishWrite() throws IOException {
		if ((bufferUpdate == BufferUpdate.SOURCE) || (bufferUpdate == BufferUpdate.BOTH)) {
			src.refreshBuffer();
		}
		if ((bufferUpdate == BufferUpdate.DESTINATION) || (bufferUpdate == BufferUpdate.BOTH)) {
			dst.refreshBuffer();
		}
	}

	@Override
	public String getOperationDescription() {
		return null;
	}
}
