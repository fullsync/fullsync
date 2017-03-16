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

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.util.ArrayList;

import net.sourceforge.fullsync.FullSync;
import net.sourceforge.fullsync.Launcher;

public class Main implements Launcher {
	public static void main(final String[] args) throws Exception {
		// TODO: redirect stdout && stderr here!
		net.sourceforge.fullsync.cli.Main.startup(args, new Main());
	}

	@Override
	public void launchGui(FullSync fullsync) throws Exception {
		// FIXME: implement SWT startup using reflection
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
		CodeSource cs = Main.class.getProtectionDomain().getCodeSource();
		String installlocation = cs.getLocation().toURI().toString().replaceAll("launcher\\.jar$", "");
		System.out.println("launching FullSync... OS=" + os + "; ARCH=" + arch + "; INSTALLLOCATION=" + installlocation);

		ArrayList<URL> jars = new ArrayList<>();
		jars.add(new URL(installlocation + "lib/assets.jar"));
		jars.add(new URL(installlocation + "lib/fullsync-ui.jar"));
		// add correct SWT implementation to the class-loader
		jars.add(new URL(installlocation + "lib/swt-" + os + "-" + arch + ".jar"));

		// instantiate an URL class-loader with the constructed class-path and load the UI
		URLClassLoader cl = new URLClassLoader(jars.toArray(new URL[jars.size()]), Main.class.getClassLoader());
		Class<?> cls = cl.loadClass("net.sourceforge.fullsync.ui.GuiController");
		Method launchUI = cls.getDeclaredMethod("launchUI", new Class<?>[] { FullSync.class });
		Thread.currentThread().setContextClassLoader(cl);
		launchUI.invoke(null, new Object[] { fullsync });
	}
}
