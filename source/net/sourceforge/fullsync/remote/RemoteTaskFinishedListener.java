/*
 * Created on Nov 29, 2004
 */
package net.sourceforge.fullsync.remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import net.sourceforge.fullsync.TaskFinishedEvent;
import net.sourceforge.fullsync.TaskFinishedListener;
/**
 * @author Michele Aiello
 */
public class RemoteTaskFinishedListener extends UnicastRemoteObject implements RemoteTaskFinishedListenerInterface {

	private transient TaskFinishedListener localListener;
	
	public RemoteTaskFinishedListener(TaskFinishedListener localListener) throws RemoteException {
		this.localListener = localListener;
	}
	
	public void taskFinished(TaskFinishedEvent event) throws RemoteException {
		localListener.taskFinished(event);
	}
	
}
