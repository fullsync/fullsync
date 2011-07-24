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

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.buffer.EntryDescriptor;
import net.sourceforge.fullsync.fs.File;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class DirCreationEntryDescriptor implements EntryDescriptor {
	private Object reference;
	private File dst;

	public DirCreationEntryDescriptor(Object reference, File dst) {
		this.reference = reference;
		this.dst = dst;
		if (dst == null) {
			throw new RuntimeException("can't give me null !!");
		}
	}

	@Override
	public Object getReferenceObject() {
		return reference;
	}

	@Override
	public long getLength() {
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
	public void finishWrite() {
		try {
			dst.makeDirectory();
			dst.refresh();

		}
		catch (IOException e) {
			ExceptionHandler.reportException(e);
		}
	}

	@Override
	public void finishStore() {

	}

	@Override
	public String getOperationDescription() {
		return "Making Directory " + dst.getPath();
	}
}
