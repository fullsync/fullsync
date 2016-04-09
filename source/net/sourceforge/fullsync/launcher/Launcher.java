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

package net.sourceforge.fullsync.launcher;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.util.ArrayList;

public class Launcher {
	/**
	 *  setup the platform specific *sigh* classpath for SWT and load the application.
	 * @param args command line arguments
	 */
	public static void main(final String[] args) {
		// TODO: redirect stdout && stderr here!
		try {
			String arch = "x86";
			String osName = System.getProperty("os.name").toLowerCase();
			String os = "unknown";
			if (-1 != System.getProperty("os.arch").indexOf("64")) {
				arch = "x86_64";
			}
			if (-1 != osName.indexOf("linux")) {
				os = "gtk-linux";
			}
			else if (-1 != osName.indexOf("windows")) {
				os = "win32-win32";
			}
			else if (-1 != osName.indexOf("mac")) {
				os = "cocoa-macosx";
			}
			CodeSource cs = Launcher.class.getProtectionDomain().getCodeSource();
			String installlocation = cs.getLocation().toURI().toString().replaceAll("launcher\\.jar$", "");
			System.out.println("launching FullSync... OS=" + os + "; ARCH=" + arch + "; INSTALLLOCATION=" + installlocation);

			ArrayList<URL> jars = new ArrayList<URL>();
			jars.add(new URL(installlocation + "lib/fullsync.jar"));
			// add correct SWT implementation to the class-loader
			jars.add(new URL(installlocation + "lib/swt-" + os + "-" + arch + ".jar"));

			String dependencies = getResourceAsString("net/sourceforge/fullsync/launcher/dependencies.txt").trim();
			for (String s : dependencies.split("\r?\n")) {
				jars.add(new URL(installlocation + "lib/" + s.trim()));
			}

			// instantiate an URL class-loader with the constructed class-path and load the real main class
			URLClassLoader cl = new URLClassLoader(jars.toArray(new URL[jars.size()]), Launcher.class.getClassLoader());
			Class<?> cls = cl.loadClass("net.sourceforge.fullsync.cli.Main");
			Method main = cls.getDeclaredMethod("main", new Class<?>[] { String[].class });

			Thread.currentThread().setContextClassLoader(cl);
			// call the main method using reflection so that there is no static reference to it
			main.invoke(null, new Object[] { args });
		}
		catch (Exception e) {
			// TODO: tell the user
			e.printStackTrace();
		}
		System.exit(1);
	}

	// keep in sync with net.sourceforge.fullsync.Util.getResourceAsString(String)
	private static final int IOBUFFERSIZE = 0x1000;

	public static String getResourceAsString(final String name) {
		StringBuilder out = new StringBuilder();
		try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(name)) {
			if (null != is) {
				final char[] buffer = new char[IOBUFFERSIZE];
				Reader in = new InputStreamReader(is, "UTF-8");
				int read;
				do {
					read = in.read(buffer, 0, buffer.length);
					if (read > 0) {
						out.append(buffer, 0, read);
					}
				} while (read >= 0);
			}
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		return out.toString();
	}
}
