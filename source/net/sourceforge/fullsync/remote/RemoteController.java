/*
 * Created on Nov 23, 2004
 */
package net.sourceforge.fullsync.remote;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import net.sourceforge.fullsync.ProfileManager;
import net.sourceforge.fullsync.Synchronizer;

/**
 * @author Michele Aiello
 */
public class RemoteController {

	private static RemoteController _instance;
	private RemoteServer remoteServer;
	private String serverURL;
	private String password;
	private boolean isActive = false;
	
	public static RemoteController getInstance() {
		if (_instance == null) {
			_instance = new RemoteController();
		}
		return _instance;
	}
	
	public void startRemoteServer(int port, String password, ProfileManager profileManager, Synchronizer sync) {
		try {
			serverURL = "rmi://localhost:"+port+"/FullSync";
			this.password = password;
			
			if (remoteServer == null) {
				remoteServer = new RemoteServer(profileManager, sync);
				remoteServer.setPassword(password);
			}
			
			try {
				LocateRegistry.createRegistry(port);
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}	
			
			Naming.rebind(serverURL, remoteServer);
			isActive = true;
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void stopRemoteServer() {
		if (!isActive) {
			return;
		}
		
		try {
			Naming.unbind(serverURL);
			isActive = false;
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean isActive() {
		return isActive;
	}
}
