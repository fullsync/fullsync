/*
 * Created on Nov 7, 2004
 */
package net.sourceforge.fullsync.remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.ProfileManager;
import net.sourceforge.fullsync.Synchronizer;
import net.sourceforge.fullsync.TaskTree;

/**
 * This class is the server for remote connections.
 * It handles remote connections to the running instance of FullSync, allowing
 * Profile Management, Scheduling and Execution with user Iteraction.
 * 
 * @author Michele Aiello
 */
public class RemoteServer extends UnicastRemoteObject implements RemoteInterface {
		
	private ProfileManager profileManager;
	private Synchronizer synchronizer;
	private String password;
		
	public RemoteServer(ProfileManager profileManager, Synchronizer synchronizer) throws RemoteException {
		this.profileManager = profileManager;
		this.synchronizer = synchronizer;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public boolean checkPassword(String passwd) throws RemoteException {
		return this.password.equals(passwd);
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

	public void runProfile(String name) throws RemoteException {
		Profile p = profileManager.getProfile(name);
		TaskTree tree = synchronizer.executeProfile(p);
		synchronizer.performActions(tree);
	    p.setLastUpdate(new Date());
	    profileManager.save();
	}
	
	public TaskTree executeProfile(String name) throws RemoteException {
		Profile p = profileManager.getProfile(name);
		TaskTree tree = synchronizer.executeProfile(p);
		return tree;
	}
	
	public void performActions(TaskTree tree) throws RemoteException {
		int result = synchronizer.performActions(tree);
		if (result != 0) {
			throw new RemoteException("Exception while performing actions");
		}
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
