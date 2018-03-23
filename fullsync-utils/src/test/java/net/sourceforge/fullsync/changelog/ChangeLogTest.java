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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringStartsWith;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ChangeLogTest {
	private static File versionsDirectory;

	@BeforeAll
	static void beforeClass() {
		versionsDirectory = new File("versions");
	}

	@Test
	public void testLoadAllVersions() throws Exception {
		ChangeLogLoader loader = new ChangeLogLoader();
		List<ChangeLogEntry> changelog = loader.load(versionsDirectory, ".*");
		assertEquals(3, changelog.size());
	}

	@Test
	public void testLoadPaternMatchesNothing() throws Exception {
		ChangeLogLoader loader = new ChangeLogLoader();
		List<ChangeLogEntry> changelog = loader.load(versionsDirectory, ".*\\.txt");
		assertEquals(0, changelog.size());
		List<ChangeLogEntry> filtered = ChangeLogLoader.filterAfter(changelog, "42");
		assertEquals(0, filtered.size());
	}

	@Test
	public void testLoadNonexistentDirectory() throws Exception {
		ChangeLogLoader loader = new ChangeLogLoader();
		List<ChangeLogEntry> changelog = loader.load(new File("build.gradle"), ".*\\.html");
		assertEquals(0, changelog.size());
		List<ChangeLogEntry> filtered = ChangeLogLoader.filterAfter(changelog, "42");
		assertEquals(0, filtered.size());
	}

	@Test
	public void testLoadMathingPattern() throws Exception {
		ChangeLogLoader loader = new ChangeLogLoader();
		List<ChangeLogEntry> changelog = loader.load(versionsDirectory, "^0\\.9.*\\.html$");
		assertEquals(2, changelog.size());
		assertEquals("0.9.1", changelog.get(0).getVersion());
		assertEquals("0.9", changelog.get(1).getVersion());
	}

	@Test
	public void testChangeLogEntryData() throws Exception {
		ChangeLogLoader loader = new ChangeLogLoader();
		List<ChangeLogEntry> changelog = loader.load(versionsDirectory, "^0\\.9\\.1\\.html$");
		assertEquals(1, changelog.size());
		assertEquals("0.9.1", changelog.get(0).getVersion());
		assertEquals(LocalDate.parse("2005-03-08"), changelog.get(0).getDate());
		assertEquals("index.html", changelog.get(0).getManual());
	}

	@Test
	public void testChangeLogFiltering() throws Exception {
		ChangeLogLoader loader = new ChangeLogLoader();
		List<ChangeLogEntry> changelog = loader.load(versionsDirectory, "^0\\.9.*\\.html$");
		assertEquals(2, changelog.size());
		List<ChangeLogEntry> filtered = ChangeLogLoader.filterAfter(changelog, "0.9");
		assertEquals(1, filtered.size(), "only 0.9.1 came after 0.9");
		assertEquals("0.9.1", filtered.get(0).getVersion(), "remaining version is 0.9.1");
	}

	@Test
	public void testChangeLogEntryFormatting() throws Exception {
		ChangeLogEntry changelog = new ChangeLogLoader().load(versionsDirectory, "^0\\.9\\.1\\.html$").get(0);
		StringWriter sw = new StringWriter();
		String entryPattern = "### ENTRY ### ";
		changelog.write("### HEADER ### %s %s", entryPattern + "%s", sw, DateTimeFormatter.BASIC_ISO_DATE);
		String entry = sw.toString();
		String[] lines = entry.split("\r?\n");
		assertEquals("### HEADER ### 0.9.1 20050308", lines[0].trim(), "first line is the header");
		assertEquals(3, lines.length, "entry has three lines");
		for (int i = 1; i < lines.length; ++i) {
			MatcherAssert.assertThat("Line " + i, lines[i], StringStartsWith.startsWith(entryPattern));
		}
	}

	@Test
	public void testDefaultEntryDateIsToday() throws Exception {
		ChangeLogLoader loader = new ChangeLogLoader();
		List<ChangeLogEntry> changelog = loader.load(versionsDirectory, "^0\\.10\\.0\\.html$");
		assertEquals(1, changelog.size());
		assertEquals(changelog.get(0).getDate(), LocalDate.now());
	}
}
