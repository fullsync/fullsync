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

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class Launcher {
	// setup the platform specific *sigh* classpath for SWT and load the application
	public static void main(String[] args) {
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
				os = "win32";
			}
			else if (-1 != osName.indexOf("mac")) {
				os = "cocoa-macosx";
			}
			String dot = new File(".").getAbsoluteFile().getCanonicalPath();
			ArrayList<URL> jars = new ArrayList<URL>();

			// add application and correct SWT implementation the class-loader
			jars.add(new URL("file://" + dot + "/lib/fullsync.jar"));
			jars.add(new URL("file://" + dot + "/lib/swt-" + os + "-" + arch + ".jar"));

			// read out the "fake" class-path set on the launcher jar
			File launcher = new File(dot + File.separator + "launcher.jar");
			JarFile jf = new JarFile(launcher);
			Manifest manifest = jf.getManifest();
			Attributes attributes = manifest.getMainAttributes();
			String fsClassPath = attributes.getValue("FullSync-Class-Path");
			for (String s : fsClassPath.split("\\.jar\\s")) {
				jars.add(new URL("file://" + dot + "/" + s.trim() + ".jar"));
			}
			URL[] urls = new URL[jars.size()];
			System.arraycopy(jars.toArray(), 0, urls, 0, urls.length);

			// instantiate an URL class-loader with the constructed class-path and load the real main class
			URLClassLoader cl = new URLClassLoader(urls, Launcher.class.getClassLoader());
			Class<?> cls = cl.loadClass("net.sourceforge.fullsync.cli.Main");
			Method main = cls.getDeclaredMethod("main", new Class<?>[] { String[].class });

			// call the main method using reflection so that there is no static reference to it
			main.invoke(null, new Object[] { args });
		}
		catch (Exception e) {
			// TODO: tell the user
			e.printStackTrace();
		}
	}
}
