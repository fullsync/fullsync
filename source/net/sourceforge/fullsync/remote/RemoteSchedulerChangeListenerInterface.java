/*
 * Created on Nov 30, 2004
 */
package net.sourceforge.fullsync.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Michele Aiello
 */
public interface RemoteSchedulerChangeListenerInterface extends Remote
{
    public void schedulerStatusChanged( boolean status ) throws RemoteException;
}
