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
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Util {
	private static final Logger logger = LoggerFactory.getLogger(Util.class);

	private Util() {
	}

	/**
	 * used for all I/O buffers.
	 */
	private static final int IOBUFFERSIZE = 0x1000;

	public static String getInputStreamAsString(final InputStream is) throws IOException {
		var out = new StringBuilder();
		final var buffer = new char[IOBUFFERSIZE];
		try (Reader in = new InputStreamReader(is, StandardCharsets.UTF_8)) {
			int read;
			do {
				read = in.read(buffer, 0, buffer.length);
				if (read > 0) {
					out.append(buffer, 0, read);
				}
			} while (read >= 0);
		}
		return out.toString();
	}

	public static String getResourceAsString(final String name) {
		var s = "";
		try (var is = getContextClassLoader().getResourceAsStream(name)) {
			if (null != is) {
				s = getInputStreamAsString(is);
			}
		}
		catch (IOException ex) {
			logger.warn("Failed to load " + name, ex); //$NON-NLS-1$
		}
		return s;
	}

	public static String getFullSyncVersion() {
		return Util.getResourceAsString("net/sourceforge/fullsync/version.txt").trim(); //$NON-NLS-1$
	}

	public static File getInstalllocation() {
		var codeSource = Util.class.getProtectionDomain().getCodeSource().getLocation();
		try {
			return new File(codeSource.toURI().resolve("../")); //$NON-NLS-1$
		}
		catch (URISyntaxException ex) {
			logger.warn("Failed to get installlocation ", ex); //$NON-NLS-1$
		}
		return new File("."); //$NON-NLS-1$
	}

	public static Set<String> loadDirectoryFromClasspath(String path) throws URISyntaxException, IOException {
		var cl = getContextClassLoader();
		var urls = cl.getResources(path);
		Set<String> children = new HashSet<>();
		while (urls.hasMoreElements()) {
			var url = urls.nextElement();
			URI uri;
			if ("jar".equals(url.getProtocol())) { //$NON-NLS-1$
				uri = new URI(url.toString().replaceAll("^jar:(.+)!/.*$", "$1")); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else {
				uri = url.toURI();
			}
			var src = new File(uri);
			if (src.isDirectory() && src.exists()) {
				enumerateDirectoryChildren(children, src);
			}
			else if (src.isFile() && src.exists()) {
				enumerateJarEntriesWith(path, children, src);
			}
		}
		return children;
	}

	private static void enumerateJarEntriesWith(String path, Set<String> children, File src) throws IOException {
		try (var jar = new JarFile(src)) {
			var jarEntries = jar.entries();
			var prefix = path;
			if ('/' == prefix.charAt(0)) {
				prefix = prefix.substring(1);
			}
			while (jarEntries.hasMoreElements()) {
				var entry = jarEntries.nextElement();
				var name = entry.getName();
				if (!entry.isDirectory() && name.startsWith(prefix)) { // filter according to the path
					name = name.substring(prefix.length());
					children.add(name);
				}
			}
		}
	}

	private static void enumerateDirectoryChildren(Set<String> children, File src) {
		var files = src.list();
		if (null != files) {
			children.addAll(Arrays.asList(files));
		}
	}

	private static ClassLoader getContextClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}
}
