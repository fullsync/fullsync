/*
 * Created on Nov 30, 2004
 */
package net.sourceforge.fullsync.remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.ProfileListChangeListener;

/**
 * @author Michele Aiello
 */
public class RemoteProfileListChangeListener extends UnicastRemoteObject implements RemoteProfileListChangeListenerInterface {

	private transient ProfileListChangeListener localListener;
	
	public RemoteProfileListChangeListener(ProfileListChangeListener localListener) throws RemoteException {
		this.localListener = localListener;
	}
	
	public void profileChanged(Profile p) throws RemoteException {
		localListener.profileChanged(p);
	}
	
	public void profileListChanged() throws RemoteException {
		localListener.profileListChanged();
	}
	
}
