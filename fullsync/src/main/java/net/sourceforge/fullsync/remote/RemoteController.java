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
package net.sourceforge.fullsync.remote;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.ProfileManager;
import net.sourceforge.fullsync.Synchronizer;

public class RemoteController {

	private static RemoteController instance;
	private RemoteServer remoteServer;
	private String serverURL;
	private int port;
	private String password;
	private boolean isActive = false;

	private Registry registry;

	public static RemoteController getInstance() {
		if (instance == null) {
			instance = new RemoteController();
		}
		return instance;
	}

	public void startServer(int port, String password, ProfileManager profileManager, Synchronizer sync) throws RemoteException {
		try {
			this.port = port;
			serverURL = "rmi://localhost:" + port + "/FullSync";
			this.password = password;

			if (remoteServer == null) {
				remoteServer = new RemoteServer(profileManager, sync);
				remoteServer.setPassword(password);
			}

			if (registry == null) {
				registry = LocateRegistry.createRegistry(port);
			}

			Naming.rebind(serverURL, remoteServer);
			isActive = true;
		}
		catch (MalformedURLException e) {
			ExceptionHandler.reportException(e);
		}
	}

	public void stopServer() throws RemoteException {
		if (!isActive) {
			return;
		}

		try {
			Naming.unbind(serverURL);
			isActive = false;
		}
		catch (MalformedURLException | NotBoundException e) {
			ExceptionHandler.reportException(e);
		}
	}

	public boolean isActive() {
		return isActive;
	}

	public int getPort() {
		return port;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
		remoteServer.setPassword(password);
	}
}
