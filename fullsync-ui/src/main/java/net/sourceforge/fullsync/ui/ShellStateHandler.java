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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.fullsync.Preferences;
import net.sourceforge.fullsync.WindowState;

public class ShellStateHandler {
	private final Preferences preferences;
	private final String name;

	public ShellStateHandler(Shell _shell, String _name, Preferences _preferences) {
		preferences = _preferences;
		name = _name;
		_shell.addListener(SWT.Close, this::shellClosed);
		_shell.setVisible(false);
		_shell.getDisplay().asyncExec(() -> applyPreferences(_shell));
	}

	public static void apply(Shell _shell, String _name) {
		new ShellStateHandler(_shell, _name, GuiController.getInstance().getPreferences());
	}

	public static void apply(Shell _shell, Class<?> _class) {
		new ShellStateHandler(_shell, _class.getSimpleName(), GuiController.getInstance().getPreferences());
	}

	private void applyPreferences(Shell _shell) {
		WindowState ws = preferences.getWindowState(name);
		_shell.setVisible(true);
		Rectangle r = _shell.getDisplay().getBounds();
		if (ws.isValid() && ws.isInsideOf(r.x, r.y, r.width, r.height)) {
			_shell.setBounds(ws.getX(), ws.getY(), ws.getWidth(), ws.getHeight());
		}
		if (ws.isMinimized()) {
			_shell.setMinimized(true);
		}
		if (ws.isMaximized()) {
			_shell.setMaximized(true);
		}
	}

	private void shellClosed(Event _event) {
		Shell shell = (Shell) _event.widget;
		WindowState ws = new WindowState();
		ws.setMaximized(shell.getMaximized());
		ws.setMinimized(shell.getMinimized());
		if (!ws.isMaximized()) {
			Rectangle r = shell.getBounds();
			ws.setX(r.x);
			ws.setY(r.y);
			ws.setWidth(r.width);
			ws.setHeight(r.height);
		}
		preferences.setWindowState(name, ws);
	}
}
