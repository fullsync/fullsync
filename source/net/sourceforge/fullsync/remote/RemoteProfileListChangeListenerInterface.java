/*
 * Created on Nov 30, 2004
 */
package net.sourceforge.fullsync.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

import net.sourceforge.fullsync.Profile;

/**
 * @author Michele Aiello
 */
public interface RemoteProfileListChangeListenerInterface extends Remote {

	void profileListChanged() throws RemoteException;

	void profileChanged(Profile p) throws RemoteException;

}
