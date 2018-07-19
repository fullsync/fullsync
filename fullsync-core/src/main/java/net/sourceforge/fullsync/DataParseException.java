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

/**
 * Thrown when an error occurred while parsing a file.
 */
public class DataParseException extends Exception {
	private static final long serialVersionUID = -6313024567929944698L;
	private static final String UNKNOWN_SOURCE = "unknown"; //$NON-NLS-1$
	private final String sourceName;
	private final long lineNumber;

	public DataParseException(String text) {
		this(text, -1, UNKNOWN_SOURCE);
	}

	public DataParseException(String text, long line) {
		this(text, line, UNKNOWN_SOURCE);
	}

	public DataParseException(String text, long line, String source) {
		super(text);
		sourceName = source;
		lineNumber = line;
	}

	public DataParseException(Throwable cause) {
		this(cause, -1);
	}

	public DataParseException(Throwable cause, long line) {
		super(cause);
		sourceName = UNKNOWN_SOURCE;
		lineNumber = line;
	}

	public DataParseException(String text, Throwable cause) {
		this(text, cause, -1, UNKNOWN_SOURCE);
	}

	public DataParseException(String text, Throwable cause, int line, String source) {
		super(text, cause);
		sourceName = source;
		lineNumber = line;
	}

	@Override
	public String getMessage() {
		return String.format("%s[%d]: %s", sourceName, lineNumber, super.getMessage()); //$NON-NLS-1$
	}

	public String getSourceName() {
		return sourceName;
	}

	public long getLineNumber() {
		return lineNumber;
	}
}
