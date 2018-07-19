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

	/**
	 * Constructor for ParseException.
	 *
	 * @param text
	 */
	public DataParseException(String text) {
		super(text);
		sourceName = UNKNOWN_SOURCE;
		lineNumber = -1;
	}

	/**
	 * Constructor for ParseException.
	 *
	 * @param text
	 * @param line
	 */
	public DataParseException(String text, long line) {
		super(text);
		sourceName = UNKNOWN_SOURCE;
		lineNumber = line;
	}

	/**
	 * Constructor for ParseException.
	 *
	 * @param text
	 * @param line
	 * @param source
	 */
	public DataParseException(String text, long line, String source) {
		super(text);
		sourceName = source;
		lineNumber = line;
	}

	/**
	 * Constructor for ParseException.
	 *
	 * @param cause
	 */
	public DataParseException(Throwable cause) {
		super(cause);
		sourceName = UNKNOWN_SOURCE;
		lineNumber = -1;
	}

	/**
	 * Constructor for ParseException.
	 *
	 * @param cause
	 * @param line
	 */
	public DataParseException(Throwable cause, long line) {
		super(cause);
		sourceName = UNKNOWN_SOURCE;
		lineNumber = line;
	}

	/**
	 * Constructor for ParseException.
	 *
	 * @param text
	 * @param cause
	 */
	public DataParseException(String text, Throwable cause) {
		super(text, cause);
		sourceName = UNKNOWN_SOURCE;
		lineNumber = -1;
	}

	/**
	 * Constructor DataParseException.
	 *
	 * @param text
	 * @param cause
	 * @param line
	 * @param source
	 */
	public DataParseException(String text, Throwable cause, int line, String source) {
		super(text, cause);
		sourceName = source;
		lineNumber = line;
	}

	@Override
	public String getMessage() {
		return String.format("%s[%d]: %s", sourceName, lineNumber, super.getMessage()); //$NON-NLS-1$
	}

	/**
	 * Returns the file name.
	 *
	 * @return String
	 */
	public String getSourceName() {
		return sourceName;
	}

	/**
	 * Returns the line number.
	 *
	 * @return long
	 */
	public long getLineNumber() {
		return lineNumber;
	}
}
