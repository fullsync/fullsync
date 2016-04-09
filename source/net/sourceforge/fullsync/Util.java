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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * various utilities.
 */
public abstract class Util {
	/**
	 * used for all I/O buffers.
	 */
	private static final int IOBUFFERSIZE = 0x1000;

	// keep in sync with net.sourceforge.fullsync.launcher.Launcher.getResourceAsString(String)
	public static String getResourceAsString(final String name) {
		try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(name)) {
			final char[] buffer = new char[IOBUFFERSIZE];
			StringBuilder out = new StringBuilder();
			Reader in = new InputStreamReader(is, "UTF-8");
			int read;
			do {
				read = in.read(buffer, 0, buffer.length);
				if (read > 0) {
					out.append(buffer, 0, read);
				}
			} while (read >= 0);
			return out.toString();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		return "";
	}

	public static String getFullSyncVersion() {
		return Util.getResourceAsString("net/sourceforge/fullsync/version.txt").trim();
	}

	public static String getTwitterURL() {
		return Util.getResourceAsString("net/sourceforge/fullsync/twitter-url.txt").trim();
	}

	public static String getWebsiteURL() {
		return Util.getResourceAsString("net/sourceforge/fullsync/website-url.txt").trim();
	}

	public static File getInstalllocation() {
		URL codeSource = Util.class.getProtectionDomain().getCodeSource().getLocation();
		try {
			URI path = codeSource.toURI();
			path = path.resolve("../");
			return new File(path);
		}
		catch (URISyntaxException e) {
			e.printStackTrace();
			return new File(".");
		}
	}

	public static String[] loadDirectoryFromClasspath(Class<?> clazz, String path)
			throws URISyntaxException, UnsupportedEncodingException, IOException {
		URL dirURL = clazz.getProtectionDomain().getCodeSource().getLocation();
		File src = new File(dirURL.toURI());
		if (src.isDirectory() && src.exists()) {
			return new File(new File(dirURL.toURI()), path.replace('/', File.separatorChar)).list();
		}

		if (src.isFile() && src.exists()) {
			try (JarFile jar = new JarFile(src)) {
				Enumeration<JarEntry> jarEntries = jar.entries();
				Set<String> result = new HashSet<String>(); //avoid duplicates in case it is a subdirectory
				String prefix = path;
				if ('/' == prefix.charAt(0)) {
					prefix = prefix.substring(1);
				}
				while (jarEntries.hasMoreElements()) {
					JarEntry entry = jarEntries.nextElement();
					String name = entry.getName();
					if (!entry.isDirectory() && name.startsWith(prefix)) { //filter according to the path
						name = name.substring(prefix.length());
						result.add(name);
					}
				}
				return result.toArray(new String[result.size()]);
			}
		}
		return new String[] {};
	}

	public static void fileRenameToPortableLegacy(String from, String to) throws Exception {
		File srcFile = new File(from);
		File dstFile = new File(to);
		if (!srcFile.renameTo(dstFile)) {
			File tmpFile = File.createTempFile("fullsync", "tmp", dstFile.getParentFile());
			tmpFile.delete();
			if (dstFile.renameTo(tmpFile)) {
				if (srcFile.renameTo(dstFile)) {
					tmpFile.delete();
				}
				else {
					tmpFile.renameTo(dstFile);
					throw new Exception("File.renameTo failed (cannot rename file)");
				}
			}
			else {
				throw new Exception("File.renameTo failed (cannot move old file away)");
			}
		}
	}
}
