/*
 * Created on Nov 7, 2004
 */
package net.sourceforge.fullsync.remoteinterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Enumeration;
import java.util.Vector;

import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.ProfileManager;

/**
 * This class is the server for remote connections.
 * It handles remote connections to the running instance of FullSync, allowing
 * Profile Management, Scheduling and Execution with user Iteraction.
 * 
 * @author Michele Aiello
 */
public class RemoteInterfaceServer extends UnicastRemoteObject implements RemoteInterface {
		
	private ProfileManager profileManager;
	
//	public static void main(String[] args) throws RemoteException, MalformedURLException, InterruptedException {
//		java.rmi.registry.LocateRegistry.createRegistry(10000);
//		RemoteInterfaceServer server = new RemoteInterfaceServer();
//		Naming.rebind("rmi://localhost:10000/FullSync", server);
//		System.out.println("Server ready.");
//		synchronized(server) {
//			while (true) {
//				server.wait();
//			}
//		}
//	}
//	
//	public RemoteInterfaceServer() throws RemoteException {
//		try {
//			profileManager = new ProfileManager("profiles.xml");
//		} catch (SAXException e) {
//			throw new RemoteException("SAXException", e);
//		} catch (IOException e) {
//			throw new RemoteException("IOException", e);
//		} catch (ParserConfigurationException e) {
//			throw new RemoteException("ParserConfigurationException", e);
//		} catch (FactoryConfigurationError e) {
//			throw new RemoteException("FactoryConfigurationError", e);
//		}
//	}
	
	public RemoteInterfaceServer(ProfileManager profileManager) throws RemoteException {
		this.profileManager = profileManager;
	}
	
	public Profile getProfile(String name) throws RemoteException {
		return profileManager.getProfile(name);
	}
	
	public Profile[] getProfiles() throws RemoteException {
		Enumeration enum = profileManager.getProfiles();
		Vector profilesVector = new Vector();
		while (enum.hasMoreElements()) {
			profilesVector.add(enum.nextElement());
		}
		
		return (Profile[]) profilesVector.toArray(new Profile[] {});
	}
	
	public void save(Profile[] profiles) throws RemoteException {
		// Check for deleted profiles
		Enumeration enum = profileManager.getProfiles();
		while (enum.hasMoreElements()) {
			Profile p = (Profile) enum.nextElement();
			boolean found = false;
			for (int i = 0; i < profiles.length; i++) {
				if (profiles[i].getName().equals(p.getName())) {
					found = true;
					break;
				}
			}
			if (!found) {
				profileManager.removeProfile(p);
			}
		}
		
		// Check for added and modified profiles
		for (int i = 0; i < profiles.length; i++) {
			Profile p = profileManager.getProfile(profiles[i].getName());
			if (p == null) {
				profileManager.addProfile(profiles[i]);
			}
			else {
				p.setName(profiles[i].getName());
				p.setDescription(profiles[i].getDescription());
				p.setSource(profiles[i].getSource());
				p.setDestination(profiles[i].getDestination());
				p.setSynchronizationType(profiles[i].getSynchronizationType());
				p.setRuleSet(profiles[i].getRuleSet());
				p.setSchedule(profiles[i].getSchedule());
			}
		}
	}
}
