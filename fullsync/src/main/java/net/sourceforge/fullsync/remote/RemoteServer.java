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
package net.sourceforge.fullsync.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.FullSync;
import net.sourceforge.fullsync.IoStatistics;
import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.ProfileListChangeListener;
import net.sourceforge.fullsync.TaskFinishedListener;
import net.sourceforge.fullsync.TaskTree;
import net.sourceforge.fullsync.schedule.SchedulerChangeListener;

/**
 * This class is the server for remote connections.
 * It handles remote connections to the running instance of FullSync, allowing
 * Profile Management, Scheduling and Execution with user Interaction.
 */
public class RemoteServer extends UnicastRemoteObject implements RemoteInterface {

	private static final long serialVersionUID = 2L;
	private FullSync fullsync;
	private String password;

	private Map<Remote, Object> listenersMap = new HashMap<>();

	private Logger logger = LoggerFactory.getLogger("FullSync");

	public RemoteServer(FullSync _fullsync) throws RemoteException {
		fullsync = _fullsync;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public boolean checkPassword(String passwd) throws RemoteException {
		boolean check = password.equals(passwd);
		if (check) {
			logger.info("Received client connection on remote interface.");
		}
		else {
			logger.info("Client connection on remote interface rejected because of wrong password.");
		}
		return check;
	}

	@Override
	public Profile[] getProfiles() throws RemoteException {
		return fullsync.getProfileManager().getProfiles().toArray(new Profile[] {});
	}

	@Override
	public void addProfileListChangeListener(final RemoteProfileListChangeListenerInterface remotelistener) throws RemoteException {
		ProfileListChangeListener listener = new ProfileListChangeListener() {
			@Override
			public void profileChanged(Profile profile) {
				try {
					remotelistener.profileChanged(profile);
				}
				catch (RemoteException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void profileListChanged() {
				try {
					remotelistener.profileListChanged();
				}
				catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		};
		fullsync.getProfileManager().addProfilesChangeListener(listener);
		listenersMap.put(remotelistener, listener);
	}

	@Override
	public void removeProfileListChangeListener(RemoteProfileListChangeListenerInterface remoteListener) throws RemoteException {
		ProfileListChangeListener listener = (ProfileListChangeListener) listenersMap.remove(remoteListener);
		fullsync.getProfileManager().removeProfilesChangeListener(listener);
	}

	@Override
	public void addSchedulerChangeListener(final RemoteSchedulerChangeListenerInterface remotelistener) throws RemoteException {
		SchedulerChangeListener listener = status -> {
			try {
				remotelistener.schedulerStatusChanged(status);
			}
			catch (RemoteException e) {
				e.printStackTrace();
			}
		};
		fullsync.getProfileManager().addSchedulerChangeListener(listener);
		listenersMap.put(remotelistener, listener);
	}

	@Override
	public void removeSchedulerChangeListener(RemoteSchedulerChangeListenerInterface remoteListener) throws RemoteException {
		SchedulerChangeListener listener = (SchedulerChangeListener) listenersMap.remove(remoteListener);
		fullsync.getProfileManager().removeSchedulerChangeListener(listener);
	}

	@Override
	public void startTimer() throws RemoteException {
		fullsync.getProfileManager().startScheduler();
	}

	@Override
	public void stopTimer() throws RemoteException {
		fullsync.getProfileManager().stopScheduler();
	}

	@Override
	public boolean isSchedulerEnabled() throws RemoteException {
		return fullsync.getProfileManager().isSchedulerEnabled();
	}

	@Override
	public TaskTree executeProfile(String name) throws RemoteException {
		Profile p = fullsync.getProfileManager().getProfile(name);
		TaskTree tree = fullsync.getSynchronizer().executeProfile(fullsync, p, false);
		return tree;
	}

	@Override
	public IoStatistics getIoStatistics(TaskTree taskTree) throws RemoteException {
		return fullsync.getSynchronizer().getIoStatistics(taskTree);
	}

	@Override
	public void performActions(TaskTree tree, final RemoteTaskFinishedListenerInterface remoteListener) throws RemoteException {
		TaskFinishedListener listener = null;
		if (remoteListener != null) {
			listener = event -> {
				try {
					remoteListener.taskFinished(event);
				}
				catch (RemoteException e) {
					ExceptionHandler.reportException(e);
				}
			};
		}
		int result = fullsync.getSynchronizer().performActions(tree, listener);
		if (result != 0) {
			throw new RemoteException("Exception while performing actions");
		}
	}

	@Override
	public void save(Profile[] profiles) throws RemoteException {
		// Check for deleted profiles
		for (Profile p : fullsync.getProfileManager().getProfiles()) {
			boolean found = false;
			for (Profile profile : profiles) {
				if (profile.getName().equals(p.getName())) {
					found = true;
					break;
				}
			}
			if (!found) {
				fullsync.getProfileManager().removeProfile(p);
			}
		}

		// Check for added and modified profiles
		for (Profile profile : profiles) {
			Profile p = fullsync.getProfileManager().getProfile(profile.getName());
			if (p == null) {
				fullsync.getProfileManager().addProfile(profile);
			}
			else {
				p.setName(profile.getName());
				p.setDescription(profile.getDescription());
				p.setSource(profile.getSource());
				p.setDestination(profile.getDestination());
				p.setSynchronizationType(profile.getSynchronizationType());
				p.setRuleSet(profile.getRuleSet());
				p.setSchedule(profile.getSchedule());
				p.setEnabled(profile.isEnabled());

				fullsync.getProfileManager().profileChanged(p);
			}
		}
	}

	@Override
	public boolean isConnectedToRemoteInstance() {
		return fullsync.getProfileManager().isConnectedToRemoteInstance();
	}
}
