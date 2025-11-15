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

import net.sourceforge.fullsync.Preferences;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class ShellStateHandler {
	private final ShellStateHandlerSWTListeners shellStateHandlerSWTListeners = new ShellStateHandlerSWTListeners();
	private final Preferences preferences;
	private final String name;
	private final Shell shell;

	private class ShellStateHandlerSWTListeners implements ShellListener, ControlListener {
		@Override
		public void shellActivated(ShellEvent e) {
			// not relevant
		}

		@Override
		public void shellClosed(ShellEvent e) {
			// TODO: save
		}

		@Override
		public void shellDeactivated(ShellEvent e) {
			// not relevant
		}

		@Override
		public void shellDeiconified(ShellEvent e) {

		}

		@Override
		public void shellIconified(ShellEvent e) {

		}

		@Override
		public void controlMoved(ControlEvent e) {

		}

		@Override
		public void controlResized(ControlEvent e) {

		}
	}

	// NO_UCD (use default)
	public ShellStateHandler(Preferences preferences, String name, Shell shell) {
		this.preferences = preferences;
		this.name = name;
		this.shell = shell;

		shell.addListener(SWT.Show, this::shellShow);
		shell.addListener(SWT.Close, this::shellClosed);
		shell.addShellListener(shellStateHandlerSWTListeners);
		shell.addControlListener(shellStateHandlerSWTListeners);
		shell.setVisible(false);
	}

	public static ShellStateHandler apply(Preferences preferences, Shell shell, Class<?> clazz) { // NO_UCD (use default)
		return new ShellStateHandler(preferences, clazz.getSimpleName(), shell);
	}

	private void shellShow(Event event) {
		LoggerFactory.getLogger(ShellStateHandler.class).atDebug().log("shellShow {}", name);
		shell.removeListener(SWT.Show, this::shellShow);
		applyPreferences();
	}

	private void applyPreferences() {
		if (shell.isDisposed()) {
			// The shell is already disposed, likely because it crashed as it should have been invisible until now...
			return;
		}

		var ws = preferences.getWindowState(name);
		shell.setVisible(true);
		var r = shell.getDisplay().getBounds();
		if (ws.isValid() && ws.isInsideOf(r.x, r.y, r.width, r.height)) {
			shell.setBounds(ws.getX(), ws.getY(), ws.getWidth(), ws.getHeight());
		}
		if (ws.isMinimized()) {
			shell.setMinimized(true);
		}
		if (ws.isMaximized()) {
			shell.setMaximized(true);
		}
	}

	private void shellClosed(Event event) {
		saveWindowState();
	}

	public void saveWindowState() { // NO_UCD (use default)
		var ws = preferences.getWindowState(name);
		ws.setMaximized(shell.getMaximized());
		ws.setMinimized(shell.getMinimized());
		if (!ws.isMaximized()) {
			var r = shell.getBounds();
			ws.setX(r.x);
			ws.setY(r.y);
			ws.setWidth(r.width);
			ws.setHeight(r.height);
		}
		preferences.setWindowState(name, ws);
	}

	public Preferences preferences() {
		return preferences;
	}

	public String name() {
		return name;
	}

	public Shell shell() {
		return shell;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (ShellStateHandler) obj;
		return Objects.equals(this.preferences, that.preferences) &&
			Objects.equals(this.name, that.name) &&
			Objects.equals(this.shell, that.shell);
	}

	@Override
	public int hashCode() {
		return Objects.hash(preferences, name, shell);
	}

	@Override
	public String toString() {
		return "ShellStateHandler[" +
			"preferences=" + preferences + ", " +
			"name=" + name + ", " +
			"shell=" + shell + ']';
	}

}
