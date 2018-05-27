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
import org.w3c.dom.NodeList;

public class ChangeLogEntry {
	private Node ul;
	private String manual;
	private LocalDate date;
	private String version;

	public ChangeLogEntry(Document doc) {
		ul = doc.getFirstChild();
		NamedNodeMap attrs = ul.getAttributes();
		version = attr(attrs, "data-version");
		manual = attr(attrs, "data-manual");
		String d = attr(attrs, "data-date");
		if ("00000000".equals(d)) {
			date = LocalDate.now();
		}
		else {
			date = LocalDate.parse(d, DateTimeFormatter.BASIC_ISO_DATE);
		}
	}

	private String attr(NamedNodeMap attrs, String name) {
		Node n = attrs.getNamedItem(name);
		if (null != n) {
			return n.getNodeValue();
		}
		return null;
	}

	public void write(String headerTemplate, String entryTemplate, Writer wr, DateTimeFormatter dateFormat) {
		PrintWriter pw = new PrintWriter(wr);
		pw.println(String.format(headerTemplate, version, dateFormat.format(date)));
		NodeList lis = ul.getChildNodes();
		for (int i = 0; i < lis.getLength(); ++i) {
			Node li = lis.item(i);
			if (Node.ELEMENT_NODE == li.getNodeType()) {
				pw.println(String.format(entryTemplate, li.getTextContent()));
			}
		}
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
