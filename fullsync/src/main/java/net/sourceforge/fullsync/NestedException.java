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

import java.io.PrintStream;
import java.io.PrintWriter;

public class NestedException extends Exception {

	private static final long serialVersionUID = 2L;

	private Throwable ex;

	public NestedException(String text) {
		super(text);
		ex = null;
	}

	public NestedException(Throwable ex) {
		super("Nested exception: " + ex.getMessage());
		this.ex = ex;
	}

	public NestedException(String text, Throwable ex) {
		super(text);
		this.ex = ex;
	}

	@Override
	public void printStackTrace(PrintStream stream) {
		printStackTrace(new PrintWriter(stream));
	}

	@Override
	public void printStackTrace(PrintWriter out) {
		if (null != ex) {
			out.println(this.toString());
			out.println("nested exception: ");
			ex.printStackTrace(out);
		}
		super.printStackTrace(out);
		out.flush();
	}
}
