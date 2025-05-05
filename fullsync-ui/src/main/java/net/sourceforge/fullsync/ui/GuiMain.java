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
package net.sourceforge.fullsync.ui;

import com.google.inject.Injector;

import net.sourceforge.fullsync.Launcher;
import net.sourceforge.fullsync.cli.Main;

public class GuiMain implements Launcher { // NO_UCD (unused code)
	public static void main(String[] args) throws Exception {
		try {
			Main.startup(args, new GuiMain());
		}
		// removing this seemingly unnecessary catch block would cause a hang on macOS if an exception occurs
		catch (Throwable t) {
			t.printStackTrace(); // I mean, we are kind of out of options here on macOS...
			System.exit(1);
		}
		System.exit(0);
	}

	@Override
	public void launchGui(Injector injector) {
		GuiController.launchUI(injector);
	}
}
