/*
 * Created on Nov 7, 2004
 */
package net.sourceforge.fullsync.remoteinterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

import net.sourceforge.fullsync.Profile;

/**
 * FullSync Remote RMI Interface.
 * 
 * @author Michele Aiello
 */
public interface RemoteInterface extends Remote {

	Profile getProfile(String name) throws RemoteException;
	
	Profile[] getProfiles() throws RemoteException;

}
