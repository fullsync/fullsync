/*
 * Created on Nov 7, 2004
 */
package net.sourceforge.fullsync.remoteinterface;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import net.sourceforge.fullsync.Profile;

/**
 * @author Michele Aiello
 */
public class RemoteInterfaceClient {

    public static void main(String args[]) throws MalformedURLException, RemoteException, NotBoundException {
    	RemoteInterface remoteInterface = (RemoteInterface) Naming.lookup("rmi://localhost:10000/FullSync");
    	Profile profile = remoteInterface.getProfile("Test");
    	System.out.println(profile.getSource().getUri());    	

    	profile = remoteInterface.getProfile("Fotografie");
    	System.out.println(profile.getSource().getUri());    	

    }

	
}
