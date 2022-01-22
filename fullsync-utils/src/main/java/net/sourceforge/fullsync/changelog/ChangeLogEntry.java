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
package net.sourceforge.fullsync.changelog;

import java.io.PrintWriter;
import java.io.Writer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import net.sourceforge.fullsync.utils.XmlUtils;

public class ChangeLogEntry {
	private final Node ul;
	private final String manual;
	private final LocalDate date;
	private final String version;

	public ChangeLogEntry(Document doc) {
		ul = doc.getFirstChild();
		var attrs = ul.getAttributes();
		version = attr(attrs, "data-version"); //$NON-NLS-1$
		manual = attr(attrs, "data-manual"); //$NON-NLS-1$
		var d = attr(attrs, "data-date"); //$NON-NLS-1$
		if ("00000000".equals(d)) { //$NON-NLS-1$
			date = LocalDate.now();
		}
		else {
			date = LocalDate.parse(d, DateTimeFormatter.BASIC_ISO_DATE);
		}
	}

	private String attr(NamedNodeMap attrs, String name) {
		var n = attrs.getNamedItem(name);
		if (null != n) {
			return n.getNodeValue();
		}
		return null;
	}

	public void write(String headerTemplate, String entryTemplate, Writer wr, DateTimeFormatter dateFormat) {
		var pw = new PrintWriter(wr);
		pw.println(String.format(headerTemplate, version, dateFormat.format(date)));
		XmlUtils.forEachChildElement(ul, li -> pw.println(String.format(entryTemplate, li.getTextContent())));
		pw.println();
		pw.flush();
	}

	public String getVersion() {
		return version;
	}

	public LocalDate getDate() {
		return date;
	}

	public String getManual() {
		return manual;
	}
}
