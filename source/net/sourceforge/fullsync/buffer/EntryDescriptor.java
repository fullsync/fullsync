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
 * Created on 18.07.2004
 */
package net.sourceforge.fullsync.buffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface EntryDescriptor {
	public Object getReferenceObject();

	public long getLength();

	// REVISIT if those streams don't get closed, the entry descriptor should
	// return the same one as before (say the opened one)
	public InputStream getInputStream() throws IOException;

	public OutputStream getOutputStream() throws IOException;

	public void finishStore(); // into buffer

	public void finishWrite(); // to target
	// public void flush( Buffer buffer, Entry entry ) throws IOException;

	public String getOperationDescription();
}
