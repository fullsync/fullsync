/*
 * Created on Nov 29, 2004
 */
package net.sourceforge.fullsync.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

import net.sourceforge.fullsync.TaskFinishedEvent;

/**
 * @author Michele Aiello
 */
public interface RemoteTaskFinishedListenerInterface extends Remote {

	public void taskFinished(TaskFinishedEvent event) throws RemoteException;

}
