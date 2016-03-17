/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA 02110-1301, USA.
 *
 * For information about the authors of this project Have a look
 * at the AUTHORS file in the root of this project.
 */
/*
 * Created on Nov 7, 2004
 */
package net.sourceforge.fullsync.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

import net.sourceforge.fullsync.IoStatistics;
import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.TaskTree;

/**
 * FullSync Remote RMI Interface.
 */
public interface RemoteInterface extends Remote {

	boolean checkPassword(String passwd) throws RemoteException;

	Profile[] getProfiles() throws RemoteException;

	void addProfileListChangeListener(RemoteProfileListChangeListenerInterface listener) throws RemoteException;

	void removeProfileListChangeListener(RemoteProfileListChangeListenerInterface listener) throws RemoteException;

	void addSchedulerChangeListener(RemoteSchedulerChangeListenerInterface remotelistener) throws RemoteException;

	void removeSchedulerChangeListener(RemoteSchedulerChangeListenerInterface remotelistener) throws RemoteException;

	void startTimer() throws RemoteException;

	void stopTimer() throws RemoteException;

	boolean isSchedulerEnabled() throws RemoteException;

	TaskTree executeProfile(String name) throws RemoteException;

	IoStatistics getIoStatistics(TaskTree taskTree) throws RemoteException;

	void performActions(TaskTree tree, RemoteTaskFinishedListenerInterface listener) throws RemoteException;

	void save(Profile[] profiles) throws RemoteException;

	boolean isConnectedToRemoteInstance() throws RemoteException;

}
