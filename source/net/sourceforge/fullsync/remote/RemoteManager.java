/*
 * Created on Nov 18, 2004
 */
package net.sourceforge.fullsync.remote;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.IoStatistics;
import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.ProfileListChangeListener;
import net.sourceforge.fullsync.TaskFinishedListener;
import net.sourceforge.fullsync.TaskTree;
import net.sourceforge.fullsync.schedule.SchedulerChangeListener;

/**
 * @author Michele Aiello
 */
public class RemoteManager {

	private RemoteInterface remoteInterface;
	private HashMap listenersMap = new HashMap();
	private boolean useRemoteListener = false;

	public RemoteManager(String host, int port, String password) 
		throws MalformedURLException, RemoteException, NotBoundException 
	{
		remoteInterface = (RemoteInterface) Naming.lookup("rmi://"+host+":"+port+"/FullSync");
		if (!remoteInterface.checkPassword(password)) {
			throw new RemoteException("Wrong password");
		}
	}
	
	public Profile getProfile(String name) {
		try {
			Profile remoteprofile = remoteInterface.getProfile(name);
			return remoteprofile;
		} catch (RemoteException e) {
			ExceptionHandler.reportException( e );
			return null;
		}
	}

	public Profile[] getProfiles() {
		try {
			Profile[] remoteprofiles = remoteInterface.getProfiles();
			return remoteprofiles;
		} catch (RemoteException e) {
			ExceptionHandler.reportException( e );
		}
		return null;
	}

	public void addProfileListChangeListener(ProfileListChangeListener listener)
		throws RemoteException
	{
		RemoteProfileListChangeListener remoteListener = new RemoteProfileListChangeListener(listener);
		remoteInterface.addProfileListChangeListener(remoteListener);
		listenersMap.put(listener, remoteListener);
	}
	
	public void removeProfileListChangeListener (ProfileListChangeListener listener)
		throws RemoteException
	{
		RemoteProfileListChangeListener remoteListener = (RemoteProfileListChangeListener) listenersMap.remove(listener);
		remoteInterface.removeProfileListChangeListener(remoteListener);
	}
	
	public void addSchedulerChangeListener(SchedulerChangeListener listener)
		throws RemoteException
	{
		RemoteSchedulerChangeListener remoteListener = new RemoteSchedulerChangeListener(listener);
		remoteInterface.addSchedulerChangeListener(remoteListener);
		listenersMap.put(listener, remoteListener);
	}
	
	public void removeSchedulerChangeListener (SchedulerChangeListener listener)
		throws RemoteException
	{
		RemoteSchedulerChangeListener remoteListener = (RemoteSchedulerChangeListener) listenersMap.remove(listener);
		remoteInterface.removeSchedulerChangeListener(remoteListener);
	}
	
	public void runProfile(String name) throws RemoteException {
		remoteInterface.runProfile(name);
	}

	public void startTimer() {
		try {
			remoteInterface.startTimer();
		} catch (RemoteException e) {
			ExceptionHandler.reportException( e );
		}
	}
	
	public void stopTimer() {
		try {
			remoteInterface.stopTimer();
		} catch (RemoteException e) {
			ExceptionHandler.reportException( e );
		}
	}
	
	public boolean isSchedulerEnabled() {
	    try {
	        return remoteInterface.isSchedulerEnabled();
	    } catch( RemoteException e ) {
	        ExceptionHandler.reportException( e );
	        return false;
	    }
	}
	
	public TaskTree executeProfile(String name) throws RemoteException {
		return remoteInterface.executeProfile(name);
	}
	
	public IoStatistics getIoStatistics(TaskTree taskTree) throws RemoteException {
		return remoteInterface.getIoStatistics(taskTree);
	}
		
	public void performActions(TaskTree taskTree, TaskFinishedListener listener) throws RemoteException {
		RemoteTaskFinishedListener remoteListener = null;
		if (useRemoteListener) {
			remoteListener = new RemoteTaskFinishedListener(listener);
		}
		remoteInterface.performActions(taskTree, remoteListener);
	}
	
	public void setUseRemoteListener(boolean bool) {
		this.useRemoteListener = bool;
	}
	
	public boolean getUseRemoteListener() {
		return this.useRemoteListener;
	}

	public void save(Profile[] profiles) throws RemoteException {
		remoteInterface.save(profiles);
	}

}