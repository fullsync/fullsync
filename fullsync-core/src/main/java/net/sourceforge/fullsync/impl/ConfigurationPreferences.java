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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import com.google.common.eventbus.Subscribe;

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.FullSync;
import net.sourceforge.fullsync.Preferences;
import net.sourceforge.fullsync.Util;
import net.sourceforge.fullsync.WindowState;
import net.sourceforge.fullsync.event.ShutdownEvent;

@Singleton
public class ConfigurationPreferences implements Preferences {
	private static final String PREFERENCE_DEFAULT_PROFILE_LIST_STYLE = "NiceListView"; //$NON-NLS-1$
	private static final String PREFERENCE_DEFAULT_LANGUAGE_CODE = "en"; //$NON-NLS-1$
	private static final String PREFERENCE_SKIP_WELCOME_SCREEN = "Interface.SkipWelcomeScreen"; //$NON-NLS-1$
	private static final String PREFERENCE_HELP_SHOWN = "Interface.HelpShown"; //$NON-NLS-1$
	private static final String PREFERENCE_LANGUAGE_CODE = "Interface.LanguageCode"; //$NON-NLS-1$
	private static final String PREFERENCE_AUTOSTART_SCHEDULER = "Interface.AutostartScheduler"; //$NON-NLS-1$
	private static final String PREFERENCE_PROFILE_LIST_STYLE = "Interface.ProfileList.Style"; //$NON-NLS-1$
	private static final String PREFERENCE_MINIMIZE_MINIMIZES_TO_SYSTEM_TRAY = "Interface.MinimizeMinimizesToSystemTray"; //$NON-NLS-1$
	private static final String PREFERENCE_CLOSE_MINIMIZES_TO_SYSTEM_TRAY = "Interface.CloseMinimizesToSystemTray"; //$NON-NLS-1$
	private static final String PREFERENCE_CONFIRM_EXIT = "Interface.ConfirmExit"; //$NON-NLS-1$
	private static final String PREFERENCE_FULLSYNC_VERSION = "FullSync.Version"; //$NON-NLS-1$
	private static final String PREFERENCE_WINDOW_STATE_PREFIX = "Interface.WindowState"; //$NON-NLS-1$
	private static final String PROPERTY_HEIGHT = "height"; //$NON-NLS-1$
	private static final String PROPERTY_WIDTH = "width"; //$NON-NLS-1$
	private static final String PROPERTY_Y = "y"; //$NON-NLS-1$
	private static final String PROPERTY_X = "x"; //$NON-NLS-1$
	private static final String PROPERTY_MINIMIZED = "minimized"; //$NON-NLS-1$
	private static final String PROPERTY_MAXIMIZED = "maximized"; //$NON-NLS-1$
	private final String configFileName;
	private final Properties props;
	private final String lastFullSyncVersion;

	@Inject
	public ConfigurationPreferences(@Named(FullSync.PREFERENCES_PROPERTIES) String configFile) {
		configFileName = configFile;
		props = new Properties();

		var file = new File(configFileName);
		if (file.exists()) {
			try (Reader reader = new FileReader(file)) {
				props.load(reader);
			}
			catch (IOException e) {
				ExceptionHandler.reportException(e);
			}
		}
		lastFullSyncVersion = props.getProperty(PREFERENCE_FULLSYNC_VERSION, ""); //$NON-NLS-1$
	}

	@Subscribe
	private void onShutdown(ShutdownEvent shutdownEvent) {
		save();
	}

	private boolean getProperty(String name, boolean defaultValue) {
		var sValue = props.getProperty(name);
		return null == sValue ? defaultValue : Boolean.parseBoolean(sValue);
	}

	private String getProperty(String name, String defaultValue) {
		return props.getProperty(name, defaultValue);
	}

	private int getProperty(String name, int defaultValue) {
		var sValue = props.getProperty(name);
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
		var currentFullSyncVersion = Util.getFullSyncVersion();
		props.setProperty(PREFERENCE_FULLSYNC_VERSION, currentFullSyncVersion);
		try {
			var targetPath = Paths.get(configFileName);
			var tmpFile = File.createTempFile(FullSync.PREFERENCES_PROPERTIES, ".tmp", targetPath.getParent().toFile()); //$NON-NLS-1$
			try (Writer writer = new FileWriter(tmpFile)) {
				props.store(writer, null);
				writer.flush();
				writer.close();
				Files.move(Paths.get(tmpFile.toURI()), targetPath, StandardCopyOption.ATOMIC_MOVE);
			}
			catch (IOException e) {
				ExceptionHandler.reportException(e);
			}
			Files.deleteIfExists(Paths.get(tmpFile.toURI()));
		}
		catch (IOException tmpFileError) {
			ExceptionHandler.reportException(tmpFileError);
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
	public String getProfileListStyle() {
		return getProperty(PREFERENCE_PROFILE_LIST_STYLE, PREFERENCE_DEFAULT_PROFILE_LIST_STYLE);
	}

	@Override
	public void setProfileListStyle(final String profileListStyle) {
		setProperty(PREFERENCE_PROFILE_LIST_STYLE, profileListStyle);
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

	private String buildName(String prefix, String instance, String suffix) {
		var sb = new StringBuilder(prefix);
		if ((null != instance) && !instance.isEmpty()) {
			sb.append('.');
			sb.append(instance);
		}
		sb.append('.');
		sb.append(suffix);
		return sb.toString();
	}

	@Override
	public WindowState getWindowState(String name) {
		var ws = new WindowState();
		ws.setMaximized(getProperty(buildName(PREFERENCE_WINDOW_STATE_PREFIX, name, PROPERTY_MAXIMIZED), false));
		ws.setMinimized(getProperty(buildName(PREFERENCE_WINDOW_STATE_PREFIX, name, PROPERTY_MINIMIZED), false));
		ws.setX(getProperty(buildName(PREFERENCE_WINDOW_STATE_PREFIX, name, PROPERTY_X), 0));
		ws.setY(getProperty(buildName(PREFERENCE_WINDOW_STATE_PREFIX, name, PROPERTY_Y), 0));
		ws.setWidth(getProperty(buildName(PREFERENCE_WINDOW_STATE_PREFIX, name, PROPERTY_WIDTH), 0));
		ws.setHeight(getProperty(buildName(PREFERENCE_WINDOW_STATE_PREFIX, name, PROPERTY_HEIGHT), 0));
		return ws;
	}

	@Override
	public void setWindowState(String name, WindowState state) {
		setProperty(buildName(PREFERENCE_WINDOW_STATE_PREFIX, name, PROPERTY_MAXIMIZED), state.isMaximized());
		setProperty(buildName(PREFERENCE_WINDOW_STATE_PREFIX, name, PROPERTY_MINIMIZED), state.isMinimized());
		setProperty(buildName(PREFERENCE_WINDOW_STATE_PREFIX, name, PROPERTY_X), state.getX());
		setProperty(buildName(PREFERENCE_WINDOW_STATE_PREFIX, name, PROPERTY_Y), state.getY());
		setProperty(buildName(PREFERENCE_WINDOW_STATE_PREFIX, name, PROPERTY_WIDTH), state.getWidth());
		setProperty(buildName(PREFERENCE_WINDOW_STATE_PREFIX, name, PROPERTY_HEIGHT), state.getHeight());
	}
}
