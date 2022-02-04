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

import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.buffer.EntryDescriptor;
import net.sourceforge.fullsync.fs.File;

public class FileCopyEntryDescriptor implements EntryDescriptor {
	private final Task task;
	private final File src;
	private final File dst;
	private InputStream inputStream;
	private OutputStream outputStream;

	public FileCopyEntryDescriptor(Task task, File src, File dst) {
		this.task = task;
		this.src = src;
		this.dst = dst;
	}

	@Override
	public Task task() {
		return task;
	}

	@Override
	public long size() {
		return src.getSize();
	}

	@Override
	public InputStream inputStream() throws IOException {
		if (null == inputStream) {
			inputStream = src.getInputStream();
		}
		return inputStream;
	}

	@Override
	public OutputStream outputStream() throws IOException {
		if (null == outputStream) {
			outputStream = dst.getOutputStream();
		}
		return outputStream;
	}

	@Override
	public void finishWrite() throws IOException {
		if (null != outputStream) {
			outputStream.close();
		}
		dst.setLastModified(src.getLastModified());
		dst.writeFileAttributes();
		dst.refresh();
	}

	@Override
	public void finishStore() throws IOException {
		if (null != inputStream) {
			inputStream.close();
		}
	}

	@Override
	public String operationDescription() {
		return "Copied " + src.getPath() + " to " + dst.getPath();
	}
}
