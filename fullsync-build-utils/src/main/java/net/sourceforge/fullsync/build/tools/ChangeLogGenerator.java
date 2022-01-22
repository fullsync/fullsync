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
package net.sourceforge.fullsync.build.tools;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import net.sourceforge.fullsync.changelog.ChangeLogEntry;
import net.sourceforge.fullsync.changelog.ChangeLogLoader;

public class ChangeLogGenerator {
	private static void usage() {
		System.out.println("Usage: [--src-dir source-directory] [--pattern file-pattern] [--changelog output-file]");
		System.out.println("  --src-dir ..... directory in which to look for source files");
		System.out.println("  --pattern ..... pattern of files to consider for the changelog generation");
		System.out.println("  --changelog ... name of the target file for the changelog");
	}

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		var srcDir = ".";
		var pattern = ".+\\.html";
		String changelog = null;

		for (var i = 0; (i + 1) < args.length; i += 2) {
			switch (args[i]) {
				case "--src-dir" -> srcDir = args[i + 1];
				case "--pattern" -> pattern = args[i + 1];
				case "--changelog" -> changelog = args[i + 1];
				default -> {
					System.err.printf("Error: unknown argument '%s'%n", args[i]);
					usage();
					System.exit(1);
				}
			}
		}
		if (((args.length % 2) > 0)) {
			System.err.printf("Error: missing parameter for argument '%s'%n", args[args.length - 1]);
			usage();
			System.exit(1);
		}

		var dir = new File(srcDir);
		if (!dir.exists()) {
			System.err.printf("Error: Directory '%s' does not exist.%n", dir.getAbsolutePath());
			System.exit(1);
		}
		var c = new ChangeLogLoader();
		var changelogEntries = c.load(dir, pattern);
		var pw = null == changelog ? new PrintWriter(System.out) : new PrintWriter(changelog);
		for (ChangeLogEntry entry : changelogEntries) {
			entry.write("FullSync %s %s", " - %s", pw, DateTimeFormatter.ofPattern("MMMM d, uuuu"));
		}
		pw.flush();
	}
}
