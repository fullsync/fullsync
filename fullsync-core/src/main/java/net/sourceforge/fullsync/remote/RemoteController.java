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

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.FullSync;

public class RemoteController {

	private static RemoteController instance;
	private RemoteServer remoteServer;
	private URI serverURI;
	private String serverPassword;
	private boolean isActive = false;
	private Registry registry;

	public static RemoteController getInstance() {
		if (null == instance) {
			instance = new RemoteController();
		}
		return instance;
	}

	public void startServer(InetSocketAddress listenAddress, String password, FullSync fullsync) throws RemoteException {
		try {
			serverURI = new URI("rmi", null, listenAddress.getHostString(), listenAddress.getPort(), "/FullSync", null, null);
			serverPassword = password;

			if (null == remoteServer) {
				remoteServer = new RemoteServer(fullsync);
				remoteServer.setPassword(password);
			}

			if (null == registry) {
				registry = LocateRegistry.createRegistry(listenAddress.getPort());
			}

			Naming.rebind(serverURI.toString(), remoteServer);
			isActive = true;
		}
		catch (URISyntaxException | MalformedURLException ex) {
			ExceptionHandler.reportException(ex);
		}
	}

	public void stopServer() throws RemoteException {
		try {
			if (isActive) {
				Naming.unbind(serverURI.toString());
				isActive = false;
			}
		}
		catch (MalformedURLException | NotBoundException e) {
			ExceptionHandler.reportException(e);
		}
	}

	public boolean isActive() {
		return isActive;
	}

	public InetSocketAddress getListenAddres() {
		return new InetSocketAddress(serverURI.getHost(), serverURI.getPort());
	}

	public String getPassword() {
		return serverPassword;
	}

	public void setPassword(String password) {
		serverPassword = password;
		remoteServer.setPassword(password);
	}
}
