/*
 * Created on Nov 23, 2004
 */
package net.sourceforge.fullsync.remote;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

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
	
	private Registry registry = null;
	
	public static RemoteController getInstance() {
		if (_instance == null) {
			_instance = new RemoteController();
		}
		return _instance;
	}
	
	public void startServer(int port, String password, ProfileManager profileManager, Synchronizer sync) 
		throws RemoteException 
	{
		try {
			serverURL = "rmi://localhost:"+port+"/FullSync";
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
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public void stopServer() 
		throws RemoteException 
	{
		if (!isActive) {
			return;
		}
		
		try {
			Naming.unbind(serverURL);
			isActive = false;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isActive() {
		return isActive;
	}
}
