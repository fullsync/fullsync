/*
 * Created on Nov 7, 2004
 */
package net.sourceforge.fullsync.remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.IoStatistics;
import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.ProfileListChangeListener;
import net.sourceforge.fullsync.ProfileManager;
import net.sourceforge.fullsync.Synchronizer;
import net.sourceforge.fullsync.TaskFinishedEvent;
import net.sourceforge.fullsync.TaskFinishedListener;
import net.sourceforge.fullsync.TaskTree;
import net.sourceforge.fullsync.schedule.SchedulerChangeListener;

import org.apache.log4j.Logger;

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
	
	private HashMap listenersMap = new HashMap();

    Logger logger = Logger.getLogger( "FullSync" );
	
	public RemoteServer(ProfileManager profileManager, Synchronizer synchronizer) throws RemoteException {
		this.profileManager = profileManager;
		this.synchronizer = synchronizer;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public boolean checkPassword(String passwd) throws RemoteException {
		boolean check = password.equals(passwd);
		if (check) {
			logger.info("Received client connection on remote interface.");
		}
		else {
			logger.info("Client connection on remote interface rejected because of wrong password.");
		}
		return check;
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

	public void addProfileListChangeListener(final RemoteProfileListChangeListenerInterface remotelistener)
		throws RemoteException 
	{
		ProfileListChangeListener listener = new ProfileListChangeListener() {
			public void profileChanged(Profile profile) {
				try {
					remotelistener.profileChanged(profile);
				} catch (RemoteException e) {
//					ExceptionHandler.reportException(e);
				}
			}
			
			public void profileListChanged() {
				try {
					remotelistener.profileListChanged();
				} catch (RemoteException e) {
//					ExceptionHandler.reportException(e);
				}
			}
		};
		profileManager.addProfilesChangeListener(listener);
		listenersMap.put(remotelistener, listener);
	}
	
	public void removeProfileListChangeListener (RemoteProfileListChangeListenerInterface remoteListener) 
		throws RemoteException
	{
		ProfileListChangeListener listener = (ProfileListChangeListener) listenersMap.remove(remoteListener);
		profileManager.removeProfilesChangeListener(listener);
	}
	
	public void addSchedulerChangeListener(final RemoteSchedulerChangeListenerInterface remotelistener)
		throws RemoteException 
	{
	    SchedulerChangeListener listener = new SchedulerChangeListener() {
	        public void schedulerStatusChanged( boolean status )
            {
	            try {
					remotelistener.schedulerStatusChanged( status );
				} catch (RemoteException e) {
	//				ExceptionHandler.reportException(e);
				}
			}
		};
		profileManager.addSchedulerChangeListener(listener);
		listenersMap.put(remotelistener, listener);
	}
	
	public void removeSchedulerChangeListener (RemoteSchedulerChangeListenerInterface remoteListener) 
		throws RemoteException
	{
	    SchedulerChangeListener listener = (SchedulerChangeListener) listenersMap.remove(remoteListener);
		profileManager.removeSchedulerChangeListener(listener);
	}
	
	
	public void runProfile(String name) throws RemoteException {
		Profile p = profileManager.getProfile(name);
		TaskTree tree = synchronizer.executeProfile(p);
		synchronizer.performActions(tree);
	    p.setLastUpdate(new Date());
	    profileManager.save();
	}
	
	public void startTimer() throws RemoteException {
		profileManager.startScheduler();
	}
	
	public void stopTimer() throws RemoteException {
		profileManager.stopScheduler();
	}
	
	public boolean isSchedulerEnabled() throws RemoteException
    {
        return profileManager.isSchedulerEnabled();
    }
	
	public TaskTree executeProfile(String name) throws RemoteException {
		Profile p = profileManager.getProfile(name);
		TaskTree tree = synchronizer.executeProfile(p);
		return tree;
	}
	
	public IoStatistics getIoStatistics(TaskTree taskTree) throws RemoteException {
		return synchronizer.getIoStatistics(taskTree);
	}
	
	public void performActions(TaskTree tree, final RemoteTaskFinishedListenerInterface remoteListener) throws RemoteException {
		TaskFinishedListener listener = null;
		if (remoteListener != null) {
			listener = new TaskFinishedListener() {
				public void taskFinished(TaskFinishedEvent event) {
					try {
						remoteListener.taskFinished(event);
					} catch (RemoteException e) {
						ExceptionHandler.reportException(e);
					}
				}
			};
		}
		int result = synchronizer.performActions(tree, listener);
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
				p.setEnabled(profiles[i].isEnabled());
				
				profileManager.profileChanged(p);
			}
		}
	}
}
