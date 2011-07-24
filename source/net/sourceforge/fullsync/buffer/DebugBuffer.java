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
/*
 * Created on 20.07.2004
 */
package net.sourceforge.fullsync.buffer;

import java.io.IOException;
import java.util.Vector;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class DebugBuffer implements ExecutionBuffer {
	private Vector<EntryDescriptor> entries = null;

	public DebugBuffer() {
	}

	@Override
	public void flush() throws IOException {
		entries.clear();
	}

	@Override
	public void load() {
		entries = new Vector<EntryDescriptor>();
	}

	@Override
	public void unload() {
		entries = null;
	}

	@Override
	public void storeEntry(EntryDescriptor descriptor) throws IOException {
		entries.add(descriptor);
	}

	@Override
	public void addEntryFinishedListener(EntryFinishedListener listener) {
	}

	@Override
	public void removeEntryFinishedListener(EntryFinishedListener listener) {
	}
}
