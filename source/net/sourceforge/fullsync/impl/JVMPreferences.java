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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;

import net.sourceforge.fullsync.Crypt;
import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.Preferences;

/**
 * Preference store using the JVMs built in Preferences API.
 * @author cobexer
 */
public class JVMPreferences implements Preferences {

	private static String keyConfirmExit = "confirmExit";
	private static String keyCloseMinimizesToSystray = "closeMinimizesToSystray";
	private static String keyMinimizeMinimizesToSystray = "minimizeMinimizesToSystray";
	private static String keySystrayEnabled = "systrayEnabled";
	private static String keyProfilelistStyle = "profilelistStyle";
	private static String keyRemoteConnectionActive = "remoteConnectionActive";
	private static String keyRemoteConnectionPort = "remoteConnectionPort";
	private static String keyRemoteConnectionPassword = "remoteConnectionPassword";
	private static String keyAutostartScheduler = "autostartScheduler";
	private static String keyLanguageCode = "languageCode";
	private static String keyHelpShown = "helpShown";

	/**
	 * preferences node for FullSync.
	 */
	private java.util.prefs.Preferences fullsyncRoot; //TODO: store version number, upgrade infos,...

	/**
	 * preferences node for FullSync settings.
	 */
	private java.util.prefs.Preferences prefs;

	/**
	 * name of the file where the preferences backup will be stored.
	 */
	private String backupFileName;
	/**
	 * Constructor for JVMPreferences loading the users preference Node.
	 */
	public JVMPreferences(final String _backupFileName) {
		fullsyncRoot = java.util.prefs.Preferences.userRoot().node("/net/sourceforge/fullsync");
		prefs = fullsyncRoot.node("config");
		backupFileName = _backupFileName;
		File backupFile = new File(backupFileName);
		FileInputStream is;
		try {
			is = new FileInputStream(backupFile);
			java.util.prefs.Preferences.importPreferences(is);
		}
		catch (FileNotFoundException e) {
			/* ignore */
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (InvalidPreferencesFormatException e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fullsync.Preferences#save()
	 */
	@Override
	public void save() {
		try {
			prefs.flush();
			prefs.sync();
			fullsyncRoot.flush();
			fullsyncRoot.sync();
			FileOutputStream os = new FileOutputStream(new File(backupFileName));
			fullsyncRoot.exportSubtree(os);
			os.flush();
			os.close();
		}
		catch (BackingStoreException e) {
			ExceptionHandler.reportException(e);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fullsync.Preferences#confirmExit()
	 */
	@Override
	public boolean confirmExit() {
		return prefs.getBoolean(keyConfirmExit, true);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fullsync.Preferences#setConfirmExit(boolean)
	 */
	@Override
	public void setConfirmExit(boolean bool) {
		prefs.putBoolean(keyConfirmExit, bool);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fullsync.Preferences#closeMinimizesToSystemTray()
	 */
	@Override
	public boolean closeMinimizesToSystemTray() {
		return prefs.getBoolean(keyCloseMinimizesToSystray, true);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fullsync.Preferences#setCloseMinimizesToSystemTray(boolean)
	 */
	@Override
	public void setCloseMinimizesToSystemTray(boolean bool) {
		prefs.putBoolean(keyCloseMinimizesToSystray, bool);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fullsync.Preferences#minimizeMinimizesToSystemTray()
	 */
	@Override
	public boolean minimizeMinimizesToSystemTray() {
		return prefs.getBoolean(keyMinimizeMinimizesToSystray, true);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fullsync.Preferences#setMinimizeMinimizesToSystemTray(boolean)
	 */
	@Override
	public void setMinimizeMinimizesToSystemTray(boolean bool) {
		prefs.putBoolean(keyMinimizeMinimizesToSystray, bool);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fullsync.Preferences#systemTrayEnabled()
	 */
	@Override
	public boolean systemTrayEnabled() {
		return prefs.getBoolean(keySystrayEnabled, true);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fullsync.Preferences#setSystemTrayEnabled(boolean)
	 */
	@Override
	public void setSystemTrayEnabled(boolean bool) {
		prefs.putBoolean(keySystrayEnabled, bool);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fullsync.Preferences#getProfileListStyle()
	 */
	@Override
	public String getProfileListStyle() {
		return prefs.get(keyProfilelistStyle, "NiceListView");
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fullsync.Preferences#setProfileListStyle(java.lang.String)
	 */
	@Override
	public void setProfileListStyle(String profileListStyle) {
		prefs.put(keyProfilelistStyle, profileListStyle);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fullsync.Preferences#listeningForRemoteConnections()
	 */
	@Override
	public boolean listeningForRemoteConnections() {
		return prefs.getBoolean(keyRemoteConnectionActive, false);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fullsync.Preferences#setListeningForRemoteConnections(boolean)
	 */
	@Override
	public void setListeningForRemoteConnections(boolean bool) {
		prefs.putBoolean(keyRemoteConnectionActive, bool);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fullsync.Preferences#getRemoteConnectionsPort()
	 */
	@Override
	public int getRemoteConnectionsPort() {
		return prefs.getInt(keyRemoteConnectionPort, 10000);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fullsync.Preferences#setRemoteConnectionsPort(int)
	 */
	@Override
	public void setRemoteConnectionsPort(int port) {
		prefs.putInt(keyRemoteConnectionPort, port);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fullsync.Preferences#getRemoteConnectionsPassword()
	 */
	@Override
	public String getRemoteConnectionsPassword() {
		return Crypt.decrypt(prefs.get(keyRemoteConnectionPassword, "admin"));
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fullsync.Preferences#setRemoteConnectionsPassword(java.lang.String)
	 */
	@Override
	public void setRemoteConnectionsPassword(String password) {
		prefs.put(keyRemoteConnectionPassword, Crypt.encrypt(password));
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fullsync.Preferences#getAutostartScheduler()
	 */
	@Override
	public boolean getAutostartScheduler() {
		return prefs.getBoolean(keyAutostartScheduler, false);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fullsync.Preferences#setAutostartScheduler(boolean)
	 */
	@Override
	public void setAutostartScheduler(boolean bool) {
		prefs.putBoolean(keyAutostartScheduler, bool);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fullsync.Preferences#getLanguageCode()
	 */
	@Override
	public String getLanguageCode() {
		return prefs.get(keyLanguageCode, "en");
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.fullsync.Preferences#setLanguageCode(java.lang.String)
	 */
	@Override
	public void setLanguageCode(String code) {
		prefs.put(keyLanguageCode, code);
	}

	@Override
	public boolean getHelpShown() {
		return prefs.getBoolean(keyHelpShown, false);
	}

	@Override
	public void setHelpShown(boolean shown) {
		prefs.putBoolean(keyHelpShown, shown);
	}
}
