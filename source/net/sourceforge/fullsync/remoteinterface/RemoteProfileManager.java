/*
 * Created on Nov 18, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sourceforge.fullsync.remoteinterface;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Vector;

import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.ProfileManager;

/**
 * @author Michele Aiello
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class RemoteProfileManager extends ProfileManager {

	private Vector profiles;
	private RemoteInterface remoteInterface;

	public RemoteProfileManager(int port) throws MalformedURLException, RemoteException, NotBoundException {
		remoteInterface = (RemoteInterface) Naming.lookup("rmi://localhost"+port+"/FullSync");
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.fullsync.ProfileManager#getProfile(java.lang.String)
	 */
	public Profile getProfile(String name) {
		try {
			Profile remoteprofile = remoteInterface.getProfile(name);
			return remoteprofile;
		} catch (RemoteException e) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.fullsync.ProfileManager#getProfiles()
	 */
	public Enumeration getProfiles() {
		this.profiles = new Vector();
		try {
			Profile[] remoteprofiles = remoteInterface.getProfiles();
			for (int i = 0; i < remoteprofiles.length; i++) {
				this.profiles.add(remoteprofiles[i]);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		return this.profiles.elements();
	}

	
	
}