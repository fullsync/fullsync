/*
 * Created on Nov 7, 2004
 */
package net.sourceforge.fullsync.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

import net.sourceforge.fullsync.IoStatistics;
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

	void addProfileListChangeListener(RemoteProfileListChangeListenerInterface listener) throws RemoteException;
	
	void removeProfileListChangeListener (RemoteProfileListChangeListenerInterface listener) throws RemoteException;
	
	void addSchedulerChangeListener(RemoteSchedulerChangeListenerInterface remotelistener) throws RemoteException;
	
	void removeSchedulerChangeListener(RemoteSchedulerChangeListenerInterface remotelistener) throws RemoteException;
	
	void runProfile(String name) throws RemoteException;

	void startTimer() throws RemoteException;
	
	void stopTimer() throws RemoteException;
	
	boolean isSchedulerEnabled() throws RemoteException;
	
	TaskTree executeProfile(String name) throws RemoteException;
	
    public IoStatistics getIoStatistics(TaskTree taskTree) throws RemoteException;
	
	void performActions(TaskTree tree, RemoteTaskFinishedListenerInterface listener) throws RemoteException;
	
	void save(Profile[] profiles) throws RemoteException;

}
