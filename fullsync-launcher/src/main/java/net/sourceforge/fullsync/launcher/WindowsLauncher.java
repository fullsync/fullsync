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
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.zip.ZipFile;

class WindowsLauncher {
	public static void main(String[] args) throws Exception {
		URL codeSource = WindowsLauncher.class.getProtectionDomain().getCodeSource().getLocation();
		File basePath = new File(codeSource.toURI().resolve("../lib"));
		File coreJar = new File(basePath, "net.sourceforge.fullsync-fullsync-core.jar");
		Method mainMethod;
		try (JarFile fullsyncCore = new JarFile(coreJar, true, ZipFile.OPEN_READ)) {
			Attributes manifestAttributes = fullsyncCore.getManifest().getMainAttributes();
			String classpath = manifestAttributes.getValue(Attributes.Name.CLASS_PATH);
			String[] entries = classpath.split(" ");
			List<URL> urls = new ArrayList<>();
			urls.add(coreJar.toURI().toURL());
			for (String entry : entries) {
				urls.add(new File(basePath, entry).toURI().toURL());
			}
			URLClassLoader classloader = new URLClassLoader(urls.toArray(new URL[] {}), Thread.currentThread().getContextClassLoader());
			Thread.currentThread().setContextClassLoader(classloader);
			Class<?> mainClass = Class.forName(manifestAttributes.getValue(Attributes.Name.MAIN_CLASS), true, classloader);
			mainMethod = mainClass.getDeclaredMethod("main", String[].class);
		}
		mainMethod.invoke(null, new Object[] { args });
	}
}
