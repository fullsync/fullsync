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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

public class ChangeLogTest {
	@Test
	public void testVersionComparator() {
		VersionComparator vc = new VersionComparator();
		assertTrue("0.0.1 == 0.0.1", 0 == vc.compare("0.0.1", "0.0.1"));
		assertTrue("0.0.1 == 0.0.1.0", 0 == vc.compare("0.0.1", "0.0.1.0"));
		assertTrue("1 == 1.0.0.0.0", 0 == vc.compare("1", "1.0.0.0.0"));
		assertTrue("0.0.1 < 0.0.2", -1 == vc.compare("0.0.1", "0.0.2"));
		assertTrue("0.0.1 < 0.1.1", -1 == vc.compare("0.0.1", "0.1.1"));
		assertTrue("0.0.1 < 1.1.1", -1 == vc.compare("0.0.1", "1.1.1"));
		assertTrue("0.0.1 < 1.1.1", -1 == vc.compare("0.0.1", "1.1.1"));
		assertTrue("0.0.1 < 1.1.1", -1 == vc.compare("0.0.1", "1.1.1"));

		assertTrue("0.1.0 > 0.0.1", 1 == vc.compare("0.1.0", "0.0.1"));
		assertTrue("0.10.0 > 0.1.1", 1 == vc.compare("0.10.0", "0.1.1"));
		assertTrue("0.1.1 > 0.1.0", 1 == vc.compare("0.1.1", "0.1.0"));
		assertTrue("1.0.0 > 0.5.9", 1 == vc.compare("1.0.0", "0.5.9"));
		assertTrue("1.0.0 > 0.5.9", 1 == vc.compare("1.0.0", "0.5.9"));
		assertTrue("1.0.0 > ", 1 == vc.compare("1.0.0", ""));
	}

	@Test
	public void testChangeLogLoader() throws ParserConfigurationException, SAXException, IOException, ParseException {
		ChangeLogLoader loader = new ChangeLogLoader();
		List<ChangeLogEntry> changelog = loader.load(new File("versions"), "^0\\.9.*\\.html$");
		assertEquals("found two versions", changelog.size(), 2);
		List<ChangeLogEntry> filtered = ChangeLogLoader.filterAfter(changelog, "0.9");
		assertEquals("only 0.9.1 came after 0.9", filtered.size(), 1);
		assertEquals("remaining version is 0.9.1", filtered.get(0).getVersion(), "0.9.1");
		StringWriter sw = new StringWriter();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String entryPattern = "### ENTRY ### ";
		filtered.get(0).write("### HEADER ### %s %s", entryPattern + "%s", sw, df);
		String entry = sw.toString();
		String[] lines = entry.split("\r?\n");
		assertEquals("first line is the header", "### HEADER ### 0.9.1 20050308", lines[0].trim());
		assertNotEquals("entry has more than one line as result", lines.length, 1);
		for (int i = 1; i < lines.length; ++i) {
			assertEquals("line starts with marker", entryPattern, lines[i].substring(0, entryPattern.length()));
		}
	}
}
