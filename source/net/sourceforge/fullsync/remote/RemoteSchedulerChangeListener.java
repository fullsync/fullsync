/*
 * Created on Nov 30, 2004
 */
package net.sourceforge.fullsync.remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import net.sourceforge.fullsync.schedule.SchedulerChangeListener;

/**
 * @author Michele Aiello
 */
public class RemoteSchedulerChangeListener extends UnicastRemoteObject implements RemoteSchedulerChangeListenerInterface 
{
	private transient SchedulerChangeListener localListener;
	
	public RemoteSchedulerChangeListener( SchedulerChangeListener localListener ) 
		throws RemoteException 
	{
		this.localListener = localListener;
	}
	
	public void schedulerStatusChanged( boolean status ) throws RemoteException
    {
	    localListener.schedulerStatusChanged( status );
    }
}
