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
package net.sourceforge.fullsync.buffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;

public class BlockBuffer implements ExecutionBuffer {
	Logger logger;

	int maxSize;
	int maxEntries;

	int freeBytes;
	int numberBytes;
	int numberEntries;
	byte[] buffer;
	Entry[] entries;

	int flushes;

	ArrayList<EntryFinishedListener> finishedListeners;

	public BlockBuffer(Logger logger) {
		this.logger = logger;

		maxSize = 1024 * 1024 * 10;
		maxEntries = 5000;
		numberBytes = 0;
		numberEntries = 0;
		freeBytes = maxSize;

		buffer = null;
		entries = null;

		flushes = 0;

		finishedListeners = new ArrayList<EntryFinishedListener>();
	}

	@Override
	public void load() {
		if (buffer == null) {
			buffer = new byte[maxSize];
			entries = new Entry[maxEntries];
		}
	}

	@Override
	public void unload() {
		buffer = null;
		entries = null;
	}

	@Override
	public void flush() throws IOException {
		for (int i = 0; i < numberEntries; ++i) {
			Entry e = entries[i];
			EntryDescriptor desc = e.descriptor;
			IOException ioe = null;
			try {
				OutputStream out = desc.getOutputStream();
				if (out != null) {
					out.write(buffer, e.start, e.length);
				}
				if ((e.internalSegment & Segment.LAST) > 0) {
					desc.finishWrite();
					String opDesc = desc.getOperationDescription();
					if (opDesc != null) {
						logger.info(opDesc);
					}
				}
			}
			catch (IOException ex) {
				ioe = ex;
				logger.error("Exception", ex);
			}
			if ((e.internalSegment & Segment.LAST) > 0) {
				for (EntryFinishedListener listener : finishedListeners) {
					listener.entryFinished(desc, ioe);
				}
			}
		}
		Arrays.fill(entries, null);
		numberBytes = 0;
		numberEntries = 0;
		freeBytes = maxSize;

		flushes++;
	}

	// Length must be the correct length (there must be enough free bytes and so on)
	/*
	 * long Load( Stream inStream, FileInfo dst, int length, bool lastSegment )
	 * {
	 * long offset = inStream.Position;
	 * int start = numberBytes;
	 * int read = inStream.Read( buffer, start, length );
	 *
	 * Segment s;
	 * if( offset == 0 )
	 * s += s.First;
	 * if( inStream.E
	 * entries[numberEntries] = new BufferEntry( dst, start, read, offset, lastSegment );
	 *
	 * numberBytes += read;
	 * numberEntries++;
	 * freeBytes -= read;
	 *
	 * return read;
	 * }
	 */
	// may not read as much as length says
	protected Entry storeBytes(InputStream inStream, long length) throws IOException {
		if (length > freeBytes) {
			length = freeBytes;
		}

		int start = numberBytes;
		int read = inStream.read(buffer, start, (int) length);
		//FIXME: read might return -1 which subsequently (in flush) throws an exception
		numberBytes += read;
		freeBytes -= read;

		Entry entry = new Entry(start, read);
		entries[numberEntries] = entry;
		numberEntries++;

		return entry;
	}

	private int store(InputStream inStream, long alreadyRead, long lengthLeft, EntryDescriptor descriptor) throws IOException {
		Entry entry = storeBytes(inStream, lengthLeft);

		int s = Segment.MIDDLE;
		if (alreadyRead == 0) {
			s |= Segment.FIRST;
		}
		if (entry.length == lengthLeft) {
			s |= Segment.LAST;
		}

		entry.internalSegment = s;
		entry.descriptor = descriptor;

		return entry.length;
	}

	private void storeEntry(InputStream data, long size, EntryDescriptor descriptor) throws IOException {
		long alreadyRead = 0;
		long lengthLeft = size;

		do {
			if ((lengthLeft > freeBytes) || (numberEntries == maxEntries)) {
				flush();
			}
			int read = store(data, alreadyRead, lengthLeft, descriptor);
			alreadyRead += read;
			lengthLeft -= read;
		} while (lengthLeft > 0);
	}

	@Override
	public void storeEntry(final EntryDescriptor descriptor) throws IOException {
		Entry entry;
		if (descriptor.getSize() == 0) {
			if (numberEntries == maxEntries) {
				flush();
			}
			entry = new Entry(numberBytes, 0);
			entry.descriptor = descriptor;
			entries[numberEntries] = entry;
			numberEntries++;
		}
		else {
			storeEntry(descriptor.getInputStream(), descriptor.getSize(), descriptor);
		}
		descriptor.finishStore();
	}

	@Override
	public void addEntryFinishedListener(final EntryFinishedListener listener) {
		finishedListeners.add(listener);
	}

	@Override
	public void removeEntryFinishedListener(final EntryFinishedListener listener) {
		finishedListeners.remove(listener);
	}
}
