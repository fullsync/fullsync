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

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * @author Michele Aiello
 * @deprecated this class should be removed in a few stable releases.
 */
@Deprecated
public class ConfigurationPreferences implements Preferences {
	/**
	 * configuration object.
	 */
	private PropertiesConfiguration config;

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
		}
		catch (ConfigurationException e) {
			ExceptionHandler.reportException(e);
		}
		catch (FactoryConfigurationError e) {
			ExceptionHandler.reportException(e);
		}

	}

	@Override
	public void save() {
		try {
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
		config.setProperty("Interface.ConfirmExit", new Boolean(bool));
	}

	@Override
	public boolean closeMinimizesToSystemTray() {
		return config.getBoolean("Interface.CloseMinimizesToSystemTray", true);
	}

	@Override
	public void setCloseMinimizesToSystemTray(final boolean bool) {
		config.setProperty("Interface.CloseMinimizesToSystemTray", new Boolean(bool));
	}

	@Override
	public boolean minimizeMinimizesToSystemTray() {
		return config.getBoolean("Interface.MinimizeMinimizesToSystemTray", false);
	}

	@Override
	public void setMinimizeMinimizesToSystemTray(final boolean bool) {
		config.setProperty("Interface.MinimizeMinimizesToSystemTray", new Boolean(bool));
	}

	@Override
	public boolean systemTrayEnabled() {
		return config.getBoolean("Interface.SystemTray.Enabled", true);
	}

	@Override
	public void setSystemTrayEnabled(final boolean bool) {
		config.setProperty("Interface.SystemTray.Enabled", new Boolean(bool));
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
		config.setProperty("RemoteConnection.active", new Boolean(bool));
	}

	@Override
	public int getRemoteConnectionsPort() {
		return config.getInt("RemoteConnection.port", 10000);
	}

	@Override
	public void setRemoteConnectionsPort(final int port) {
		config.setProperty("RemoteConnection.port", new Integer(port));
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
		config.setProperty("Interface.AutostartScheduler", new Boolean(bool));
	}

	@Override
	public String getLanguageCode() {
		return config.getString("Interface.LanguageCode", new String("en"));
	}

	@Override
	public void setLanguageCode(final String code) {
		config.setProperty("Interface.LanguageCode", code);
	}

	@Override
	public boolean getHelpShown() {
		return false;
	}

	@Override
	public void setHelpShown(boolean shown) {
		// ignore
	}

}
