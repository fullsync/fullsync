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
 * Thrown when an error occured while parsing a file.
 *
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class DataParseException extends NestedException {
	private static final long serialVersionUID = 2L;
	String sourceName = "unknown";
	long linenumber = -1;

	/**
	 * Constructor for ParseException.
	 *
	 * @param text
	 */
	public DataParseException(String text) {
		super(text);
	}

	/**
	 * Constructor for ParseException.
	 *
	 * @param text
	 * @param linenumber
	 */
	public DataParseException(String text, long linenumber) {
		this(text);
		setLinenumber(linenumber);
	}

	/**
	 * Constructor for ParseException.
	 *
	 * @param text
	 * @param linenumber
	 * @param sourcename
	 */
	public DataParseException(String text, long linenumber, String sourcename) {
		this(text, linenumber);
		setSourceName(sourcename);
	}

	/**
	 * Constructor for ParseException.
	 *
	 * @param cause
	 */
	public DataParseException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor for ParseException.
	 *
	 * @param cause
	 * @param linenumber
	 */
	public DataParseException(Throwable cause, long linenumber) {
		this(cause);
		setLinenumber(linenumber);
	}

	/**
	 * Constructor for ParseException.
	 *
	 * @param text
	 * @param cause
	 */
	public DataParseException(String text, Throwable cause) {
		super(text, cause);
	}

	/**
	 * Constructor DataParseException.
	 *
	 * @param text
	 * @param cause
	 * @param lineno
	 * @param source
	 */
	public DataParseException(String text, Throwable cause, int lineno, String source) {
		this(text, cause);
		setLinenumber(lineno);
		setSourceName(source);
	}

	@Override
	public String getMessage() {
		return sourceName + "[" + linenumber + "]: " + super.getMessage();
	}

	/**
	 * Returns the filename.
	 *
	 * @return String
	 */
	public String getSourceName() {
		return sourceName;
	}

	/**
	 * Returns the linenumber.
	 *
	 * @return long
	 */
	public long getLinenumber() {
		return linenumber;
	}

	/**
	 * Sets the sourceName.
	 *
	 * @param sourceName
	 *            The sourceName to set
	 */
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	/**
	 * Sets the linenumber.
	 *
	 * @param linenumber
	 *            The linenumber to set
	 */
	public void setLinenumber(long linenumber) {
		this.linenumber = linenumber;
	}

}
