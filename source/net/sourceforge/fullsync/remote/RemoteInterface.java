/*
 * Created on Nov 7, 2004
 */
package net.sourceforge.fullsync.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.TaskTree;

/**
 * FullSync Remote RMI Interface.
 * 
 * @author Michele Aiello
 */
public interface RemoteInterface extends Remote {

	boolean checkPassword(String passwd) throws RemoteException;
	
	Profile getProfile(String name) throws RemoteException;
	
	Profile[] getProfiles() throws RemoteException;

	void runProfile(String name) throws RemoteException;

	TaskTree executeProfile(String name) throws RemoteException;
	
	void preformActions(String profilename, TaskTree tree) throws RemoteException;
	
	void save(Profile[] profiles) throws RemoteException;

}
