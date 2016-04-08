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
package net.sourceforge.fullsync.impl;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

import net.sourceforge.fullsync.Crypt;
import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.Preferences;
import net.sourceforge.fullsync.Util;

import org.eclipse.swt.graphics.Rectangle;

public class ConfigurationPreferences implements Preferences {
	private static final String PREFERENCE_DEFAULT_PROFILE_LIST_STYLE = "NiceListView";
	private static final String PREFERENCE_DEFAULT_LANGUAGE_CODE = "en";
	private static final String PREFERENCE_WINDOW_STATE_HEIGHT = "Interface.WindowState.height";
	private static final String PREFERENCE_WINDOW_STATE_WIDTH = "Interface.WindowState.width";
	private static final String PREFERENCE_WINDOW_STATE_Y = "Interface.WindowState.y";
	private static final String PREFERENCE_WINDOW_STATE_X = "Interface.WindowState.x";
	private static final String PREFERENCE_WINDOW_STATE_MINIMIZED = "Interface.WindowState.minimized";
	private static final String PREFERENCE_WINDOW_STATE_MAXIMIZED = "Interface.WindowState.maximized";
	private static final String PREFERENCE_SKIP_WELCOME_SCREEN = "Interface.SkipWelcomeScreen";
	private static final String PREFERENCE_HELP_SHOWN = "Interface.HelpShown";
	private static final String PREFERENCE_LANGUAGE_CODE = "Interface.LanguageCode";
	private static final String PREFERENCE_AUTOSTART_SCHEDULER = "Interface.AutostartScheduler";
	private static final String PREFERENCE_REMOTE_CONNECTION_PASSWORD = "RemoteConnection.password";
	private static final String PREFERENCE_REMOTE_CONNECTION_PORT = "RemoteConnection.port";
	private static final String PREFERENCE_REMOTE_CONNECTION_ACTIVE = "RemoteConnection.active";
	private static final String PREFERENCE_PROFILE_LIST_STYLE = "Interface.ProfileList.Style";
	private static final String PREFERENCE_SYSTEM_TRAY_ENABLED = "Interface.SystemTray.Enabled";
	private static final String PREFERENCE_MINIMIZE_MINIMIZES_TO_SYSTEM_TRAY = "Interface.MinimizeMinimizesToSystemTray";
	private static final String PREFERENCE_CLOSE_MINIMIZES_TO_SYSTEM_TRAY = "Interface.CloseMinimizesToSystemTray";
	private static final String PREFERENCE_CONFIRM_EXIT = "Interface.ConfirmExit";
	private static final String PREFERENCE_FULLSYNC_VERSION = "FullSync.Version";
	private final String configFileName;
	private final Properties props;
	private final String lastFullSyncVersion;

	public ConfigurationPreferences(final String configFile) {
		configFileName = configFile;
		props = new Properties();

		File file = new File(configFileName);
		if (file.exists()) {
			try (Reader reader = new FileReader(file)) {
				props.load(reader);
			}
			catch (IOException e) {
				ExceptionHandler.reportException(e);
			}
		}
		lastFullSyncVersion = props.getProperty(PREFERENCE_FULLSYNC_VERSION, "");
	}

	private boolean getProperty(String name, boolean defaultValue) {
		String sValue = props.getProperty(name);
		return null == sValue ? defaultValue : Boolean.parseBoolean(sValue);
	}

	private String getProperty(String name, String defaultValue) {
		return props.getProperty(name, defaultValue);
	}

	private int getProperty(String name, int defaultValue) {
		String sValue = props.getProperty(name);
		return null == sValue ? defaultValue : Integer.parseInt(sValue, 10);
	}

	private void setProperty(String name, boolean value) {
		props.setProperty(name, Boolean.toString(value));
	}

	private void setProperty(String name, int value) {
		props.setProperty(name, Integer.toString(value));
	}

	private void setProperty(String name, String value) {
		props.setProperty(name, value);
	}

	@Override
	public void save() {
		String currentFullSyncVersion = Util.getFullSyncVersion();
		props.setProperty(PREFERENCE_FULLSYNC_VERSION, currentFullSyncVersion);
		try (Writer writer = new FileWriter(configFileName)) {
			props.store(writer, null);
			writer.flush();
		}
		catch (IOException e) {
			ExceptionHandler.reportException(e);
		}
	}

	@Override
	public boolean confirmExit() {
		return getProperty(PREFERENCE_CONFIRM_EXIT, true);
	}

	@Override
	public void setConfirmExit(final boolean bool) {
		setProperty(PREFERENCE_CONFIRM_EXIT, bool);
	}

	@Override
	public boolean closeMinimizesToSystemTray() {
		return getProperty(PREFERENCE_CLOSE_MINIMIZES_TO_SYSTEM_TRAY, false);
	}

	@Override
	public void setCloseMinimizesToSystemTray(final boolean bool) {
		setProperty(PREFERENCE_CLOSE_MINIMIZES_TO_SYSTEM_TRAY, bool);
	}

	@Override
	public boolean minimizeMinimizesToSystemTray() {
		return getProperty(PREFERENCE_MINIMIZE_MINIMIZES_TO_SYSTEM_TRAY, false);
	}

	@Override
	public void setMinimizeMinimizesToSystemTray(final boolean bool) {
		setProperty(PREFERENCE_MINIMIZE_MINIMIZES_TO_SYSTEM_TRAY, bool);
	}

	@Override
	public boolean systemTrayEnabled() {
		return getProperty(PREFERENCE_SYSTEM_TRAY_ENABLED, true);
	}

	@Override
	public void setSystemTrayEnabled(final boolean bool) {
		setProperty(PREFERENCE_SYSTEM_TRAY_ENABLED, bool);
	}

	@Override
	public String getProfileListStyle() {
		return getProperty(PREFERENCE_PROFILE_LIST_STYLE, PREFERENCE_DEFAULT_PROFILE_LIST_STYLE);
	}

	@Override
	public void setProfileListStyle(final String profileListStyle) {
		setProperty(PREFERENCE_PROFILE_LIST_STYLE, profileListStyle);
	}

	@Override
	public boolean listeningForRemoteConnections() {
		return getProperty(PREFERENCE_REMOTE_CONNECTION_ACTIVE, false);
	}

	@Override
	public void setListeningForRemoteConnections(final boolean bool) {
		setProperty(PREFERENCE_REMOTE_CONNECTION_ACTIVE, bool);
	}

	@Override
	public int getRemoteConnectionsPort() {
		return getProperty(PREFERENCE_REMOTE_CONNECTION_PORT, 10000);
	}

	@Override
	public void setRemoteConnectionsPort(final int port) {
		setProperty(PREFERENCE_REMOTE_CONNECTION_PORT, port);
	}

	@Override
	public String getRemoteConnectionsPassword() {
		String passwd = getProperty(PREFERENCE_REMOTE_CONNECTION_PASSWORD, "");
		String decryptedPassword = Crypt.decrypt(passwd);
		return decryptedPassword;
	}

	@Override
	public void setRemoteConnectionsPassword(final String password) {
		String encryptedPasswd = Crypt.encrypt(password);
		setProperty(PREFERENCE_REMOTE_CONNECTION_PASSWORD, encryptedPasswd);
	}

	@Override
	public boolean getAutostartScheduler() {
		return getProperty(PREFERENCE_AUTOSTART_SCHEDULER, false);
	}

	@Override
	public void setAutostartScheduler(final boolean bool) {
		setProperty(PREFERENCE_AUTOSTART_SCHEDULER, bool);
	}

	@Override
	public String getLanguageCode() {
		return getProperty(PREFERENCE_LANGUAGE_CODE, PREFERENCE_DEFAULT_LANGUAGE_CODE);
	}

	@Override
	public void setLanguageCode(final String code) {
		setProperty(PREFERENCE_LANGUAGE_CODE, code);
	}

	@Override
	public boolean getHelpShown() {
		return getProperty(PREFERENCE_HELP_SHOWN, false);
	}

	@Override
	public void setHelpShown(final boolean shown) {
		setProperty(PREFERENCE_HELP_SHOWN, shown);
	}

	@Override
	public boolean getSkipWelcomeScreen() {
		return getProperty(PREFERENCE_SKIP_WELCOME_SCREEN, false);
	}

	@Override
	public void setSkipWelcomeScreen(boolean skip) {
		setProperty(PREFERENCE_SKIP_WELCOME_SCREEN, skip);
	}

	@Override
	public String getLastVersion() {
		return lastFullSyncVersion;
	}

	@Override
	public void setWindowMaximized(boolean maximized) {
		setProperty(PREFERENCE_WINDOW_STATE_MAXIMIZED, maximized);
	}

	@Override
	public boolean getWindowMaximized() {
		return getProperty(PREFERENCE_WINDOW_STATE_MAXIMIZED, false);
	}

	@Override
	public void setWindowMinimized(boolean minimized) {
		setProperty(PREFERENCE_WINDOW_STATE_MINIMIZED, minimized);
	}

	@Override
	public boolean getWindowMinimized() {
		return getProperty(PREFERENCE_WINDOW_STATE_MINIMIZED, false);
	}

	@Override
	public void setWindowBounds(Rectangle b) {
		setProperty(PREFERENCE_WINDOW_STATE_X, b.x);
		setProperty(PREFERENCE_WINDOW_STATE_Y, b.y);
		setProperty(PREFERENCE_WINDOW_STATE_WIDTH, b.width);
		setProperty(PREFERENCE_WINDOW_STATE_HEIGHT, b.height);
	}

	@Override
	public Rectangle getWindowBounds() {
		int x = getProperty(PREFERENCE_WINDOW_STATE_X, 0);
		int y = getProperty(PREFERENCE_WINDOW_STATE_Y, 0);
		int width = getProperty(PREFERENCE_WINDOW_STATE_WIDTH, 0);
		int height = getProperty(PREFERENCE_WINDOW_STATE_HEIGHT, 0);
		return new Rectangle(x, y, width, height);
	}
}
