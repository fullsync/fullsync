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
package net.sourceforge.fullsync;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * various utilities.
 * 
 * @author cobexer
 */
public abstract class Util {
	/**
	 * used for all I/O buffers.
	 */
	private static final int IOBUFFERSIZE = 0x1000;

	/**
	 * readStreamAsString reads everything from the given stream to a string using UTF-8 as encoding.
	 * 
	 * @param is
	 *            the InputStream to convert to a string
	 * @return resulting String
	 */
	public static String readStreamAsString(final InputStream is) {
		if (null != is) {
			final char[] buffer = new char[IOBUFFERSIZE];
			StringBuilder out = new StringBuilder();
			Reader in;
			try {
				in = new InputStreamReader(is, "UTF-8");
				int read;
				do {
					read = in.read(buffer, 0, buffer.length);
					if (read > 0) {
						out.append(buffer, 0, read);
					}
				} while (read >= 0);
				return out.toString();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "";
	}
}
