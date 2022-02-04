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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;

public class BlockBuffer implements ExecutionBuffer {
	private final Optional<Logger> logger;
	private final int maxSize;
	private final int maxEntries;
	private int freeBytes;
	private int numberBytes;
	private int numberEntries;
	private final byte[] buffer;
	private final Entry[] entries;
	private final List<EntryFinishedListener> finishedListeners = new ArrayList<>();

	public BlockBuffer(Logger logger) {
		this.logger = Optional.ofNullable(logger);

		maxSize = 1024 * 1024 * 10;
		maxEntries = 5000;
		numberBytes = 0;
		numberEntries = 0;
		freeBytes = maxSize;

		buffer = new byte[maxSize];
		entries = new Entry[maxEntries];
	}

	@Override
	public void flush() throws IOException {
		for (var i = 0; i < numberEntries; ++i) {
			var e = entries[i];
			var desc = e.descriptor();
			IOException ioe = null;
			try {
				var out = desc.outputStream();
				if (null != out) {
					out.write(buffer, e.start(), e.length());
				}
				if (e.isLastSegment()) {
					desc.finishWrite();
					var opDesc = desc.operationDescription();
					if ((null != opDesc) && logger.isPresent()) {
						logger.get().info(opDesc);
					}
				}
			}
			catch (IOException ex) {
				ioe = ex;
				logger.ifPresent(value -> value.error("Exception", ex)); //$NON-NLS-1$
			}
			if (e.isLastSegment()) {
				for (EntryFinishedListener listener : finishedListeners) {
					listener.entryFinished(desc, ioe);
				}
			}
		}
		Arrays.fill(entries, null);
		numberBytes = 0;
		numberEntries = 0;
		freeBytes = maxSize;
	}

	private int store(InputStream inStream, long alreadyRead, long lengthLeft, EntryDescriptor descriptor) throws IOException {
		var length = lengthLeft;
		if (length > freeBytes) {
			length = freeBytes;
		}

		var start = numberBytes;
		var read = inStream.read(buffer, start, (int) length);
		// FIXME: read might return -1 which subsequently (in flush) throws an exception
		numberBytes += read;
		freeBytes -= read;

		var s = Segment.MIDDLE;
		if (alreadyRead == 0) {
			s |= Segment.FIRST;
		}
		if (read == lengthLeft) {
			s |= Segment.LAST;
		}
		entries[numberEntries] = new Entry(start, read, s, descriptor);
		numberEntries++;
		return read;
	}

	private void storeEntry(InputStream data, long size, EntryDescriptor descriptor) throws IOException {
		var alreadyRead = 0L;
		var lengthLeft = size;

		do {
			if ((lengthLeft > freeBytes) || (numberEntries == maxEntries)) {
				flush();
			}
			var read = store(data, alreadyRead, lengthLeft, descriptor);
			alreadyRead += read;
			lengthLeft -= read;
		} while (lengthLeft > 0);
	}

	@Override
	public void storeEntry(final EntryDescriptor descriptor) throws IOException {
		if (descriptor.size() == 0) {
			if (numberEntries == maxEntries) {
				flush();
			}
			entries[numberEntries] = new Entry(numberBytes, 0, Segment.ONLY, descriptor);
			numberEntries++;
		}
		else {
			storeEntry(descriptor.inputStream(), descriptor.size(), descriptor);
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
