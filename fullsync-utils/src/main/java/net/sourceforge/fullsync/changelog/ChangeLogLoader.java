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

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class ChangeLogLoader {
	private final DocumentBuilderFactory parserFactory = DocumentBuilderFactory.newInstance();

	public ChangeLogLoader() {
		parserFactory.setValidating(false);
		parserFactory.setNamespaceAware(false);
		parserFactory.setIgnoringComments(true);
		parserFactory.setIgnoringElementContentWhitespace(true);
		parserFactory.setExpandEntityReferences(false);
	}

	public List<ChangeLogEntry> load(File srcDir, String pattern) throws ParserConfigurationException, SAXException, IOException {
		var p = Pattern.compile(pattern);
		List<ChangeLogEntry> changelogEntries = new LinkedList<>();
		var children = srcDir.list();
		if (null != children) {
			for (String file : children) {
				var m = p.matcher(file);
				if (m.matches()) {
					changelogEntries.add(loadChangeLogFile(new File(srcDir, file)));
				}
			}
		}
		changelogEntries.sort(Comparator.comparing(ChangeLogEntry::getDate, Comparator.reverseOrder()));
		return changelogEntries;
	}

	public static List<ChangeLogEntry> filterAfter(List<ChangeLogEntry> changelogEntries, String version) {
		List<ChangeLogEntry> newerVersions = new LinkedList<>();
		var vc = new VersionComparator();
		for (ChangeLogEntry entry : changelogEntries) {
			if (vc.compare(entry.getVersion(), version) < 1) {
				break;
			}
			newerVersions.add(entry);
		}
		return newerVersions;
	}

	private ChangeLogEntry loadChangeLogFile(File f) throws ParserConfigurationException, SAXException, IOException {
		var parser = parserFactory.newDocumentBuilder();
		return new ChangeLogEntry(parser.parse(f));
	}
}
