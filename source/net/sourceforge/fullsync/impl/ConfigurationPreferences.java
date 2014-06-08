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

import javax.xml.parsers.FactoryConfigurationError;

import net.sourceforge.fullsync.Crypt;
import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.Preferences;
import net.sourceforge.fullsync.Util;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.eclipse.swt.graphics.Rectangle;

public class ConfigurationPreferences implements Preferences {
	/**
	 * configuration object.
	 */
	private PropertiesConfiguration config;

	private String lastFullSyncVersion;

	/**
	 * constructor.
	 *
	 * @param configFile
	 *            config file name
	 */
	public ConfigurationPreferences(final String configFile) {
		this.config = new PropertiesConfiguration();

		try {
			File file = new File(configFile);
			config.setFile(file);
			if (file.exists()) {
				config.load();
			}
			lastFullSyncVersion = config.getString("FullSync.Version", "");
		}
		catch (ConfigurationException e) {
			ExceptionHandler.reportException(e);
		}
		catch (FactoryConfigurationError e) {
			ExceptionHandler.reportException(e);
		}
		if (null == lastFullSyncVersion) {
			lastFullSyncVersion = "";
		}
	}

	@Override
	public void save() {
		try {
			String currentFullSyncVersion = Util.getFullSyncVersion();
			config.setProperty("FullSync.Version", currentFullSyncVersion);
			config.save();
		}
		catch (ConfigurationException e) {
			ExceptionHandler.reportException(e);
		}
	}

	@Override
	public boolean confirmExit() {
		return config.getBoolean("Interface.ConfirmExit", true);
	}

	@Override
	public void setConfirmExit(final boolean bool) {
		config.setProperty("Interface.ConfirmExit", Boolean.valueOf(bool));
	}

	@Override
	public boolean closeMinimizesToSystemTray() {
		return config.getBoolean("Interface.CloseMinimizesToSystemTray", false);
	}

	@Override
	public void setCloseMinimizesToSystemTray(final boolean bool) {
		config.setProperty("Interface.CloseMinimizesToSystemTray", Boolean.valueOf(bool));
	}

	@Override
	public boolean minimizeMinimizesToSystemTray() {
		return config.getBoolean("Interface.MinimizeMinimizesToSystemTray", false);
	}

	@Override
	public void setMinimizeMinimizesToSystemTray(final boolean bool) {
		config.setProperty("Interface.MinimizeMinimizesToSystemTray", Boolean.valueOf(bool));
	}

	@Override
	public boolean systemTrayEnabled() {
		return config.getBoolean("Interface.SystemTray.Enabled", true);
	}

	@Override
	public void setSystemTrayEnabled(final boolean bool) {
		config.setProperty("Interface.SystemTray.Enabled", Boolean.valueOf(bool));
	}

	@Override
	public String getProfileListStyle() {
		return config.getString("Interface.ProfileList.Style", "NiceListView");
	}

	@Override
	public void setProfileListStyle(final String profileListStyle) {
		config.setProperty("Interface.ProfileList.Style", profileListStyle);
	}

	@Override
	public boolean listeningForRemoteConnections() {
		return config.getBoolean("RemoteConnection.active", false);
	}

	@Override
	public void setListeningForRemoteConnections(final boolean bool) {
		config.setProperty("RemoteConnection.active", Boolean.valueOf(bool));
	}

	@Override
	public int getRemoteConnectionsPort() {
		return config.getInt("RemoteConnection.port", 10000);
	}

	@Override
	public void setRemoteConnectionsPort(final int port) {
		config.setProperty("RemoteConnection.port", Integer.valueOf(port));
	}

	@Override
	public String getRemoteConnectionsPassword() {
		String passwd = config.getString("RemoteConnection.password", "admin");
		String decryptedPassword = Crypt.decrypt(passwd);
		return decryptedPassword;
	}

	@Override
	public void setRemoteConnectionsPassword(final String password) {
		String encryptedPasswd = Crypt.encrypt(password);
		config.setProperty("RemoteConnection.password", encryptedPasswd);
	}

	@Override
	public boolean getAutostartScheduler() {
		return config.getBoolean("Interface.AutostartScheduler", false);
	}

	@Override
	public void setAutostartScheduler(final boolean bool) {
		config.setProperty("Interface.AutostartScheduler", Boolean.valueOf(bool));
	}

	@Override
	public String getLanguageCode() {
		return config.getString("Interface.LanguageCode", "en");
	}

	@Override
	public void setLanguageCode(final String code) {
		config.setProperty("Interface.LanguageCode", code);
	}

	@Override
	public boolean getHelpShown() {
		return config.getBoolean("Interface.HelpShown", false);
	}

	@Override
	public void setHelpShown(final boolean shown) {
		config.setProperty("Interface.HelpShown", Boolean.valueOf(shown));
	}

	@Override
	public boolean getSkipWelcomeScreen() {
		return config.getBoolean("Interface.SkipWelcomeScreen", false);
	}

	@Override
	public void setSkipWelcomeScreen(boolean skip) {
		config.setProperty("Interface.SkipWelcomeScreen", Boolean.valueOf(skip));
	}

	@Override
	public String getLastVersion() {
		return lastFullSyncVersion;
	}

	@Override
	public void setWindowMaximized(boolean maximized) {
		config.setProperty("Interface.WindowState.maximized", Boolean.valueOf(maximized));
	}

	@Override
	public boolean getWindowMaximized() {
		return config.getBoolean("Interface.WindowState.maximized", false);
	}

	@Override
	public void setWindowMinimized(boolean minimized) {
		config.setProperty("Interface.WindowState.minimized", Boolean.valueOf(minimized));
	}

	@Override
	public boolean getWindowMinimized() {
		return config.getBoolean("Interface.WindowState.minimized", false);
	}

	@Override
	public void setWindowBounds(Rectangle b) {
		config.setProperty("Interface.WindowState.x", Integer.valueOf(b.x));
		config.setProperty("Interface.WindowState.y", Integer.valueOf(b.y));
		config.setProperty("Interface.WindowState.width", Integer.valueOf(b.width));
		config.setProperty("Interface.WindowState.height", Integer.valueOf(b.height));
	}

	@Override
	public Rectangle getWindowBounds() {
		int x  = config.getInt("Interface.WindowState.x", 0);
		int y = config.getInt("Interface.WindowState.y", 0);
		int width  = config.getInt("Interface.WindowState.width", 0);
		int height = config.getInt("Interface.WindowState.height", 0);
		return new Rectangle(x, y, width, height);
	}
}
