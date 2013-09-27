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
package net.sourceforge.fullsync;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sourceforge.fullsync.remote.RemoteManager;
import net.sourceforge.fullsync.schedule.Schedule;
import net.sourceforge.fullsync.schedule.ScheduleTask;
import net.sourceforge.fullsync.schedule.ScheduleTaskSource;
import net.sourceforge.fullsync.schedule.Scheduler;
import net.sourceforge.fullsync.schedule.SchedulerChangeListener;
import net.sourceforge.fullsync.schedule.SchedulerImpl;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

// TODO remove schedulerChangeListener
/**
 * A ProfileManager handles persistence of Profiles and provides
 * a scheduler for creating events when a Profile should be executed.
 */
public class ProfileManager implements ProfileChangeListener, ScheduleTaskSource, SchedulerChangeListener {
	class ProfileManagerSchedulerTask implements ScheduleTask {
		private Profile profile;
		private long executionTime;

		public ProfileManagerSchedulerTask(Profile profile, long executionTime) {
			this.profile = profile;
			this.executionTime = executionTime;
		}

		@Override
		public void run() {
			Thread worker = new Thread(() -> fireProfileSchedulerEvent(profile));
			worker.start();
			profile.getSchedule().setLastOccurrence(System.currentTimeMillis());
			Thread.yield();
		}

		@Override
		public long getExecutionTime() {
			return executionTime;
		}

		@Override
		public String toString() {
			return "Scheduled execution of " + profile.getName();
		}
	}

	private String configFile;
	private Vector<Profile> profiles;
	private Vector<ProfileListChangeListener> changeListeners;
	private Vector<ProfileSchedulerListener> scheduleListeners;
	private boolean remoteConnected = false;

	// FIXME this list is only needed because we need to give feedback from
	// the local scheduler and a remote scheduler.
	private Vector<SchedulerChangeListener> schedulerChangeListeners;

	// TODO the scheduler shouldn't reside within the profile manager
	// but just use it as task source
	private Scheduler scheduler;

	// FIXME omg, a profilemanager having a remoteprofilemanager?
	// please make a dao of me, with save/load and that's it
	// don't forget calling profilesChangeEvent if dao is changed
	private RemoteManager remoteManager;
	private ProfileListChangeListener remoteListener;

	protected ProfileManager() {
		this.profiles = new Vector<Profile>();
		this.changeListeners = new Vector<ProfileListChangeListener>();
		this.scheduleListeners = new Vector<ProfileSchedulerListener>();
		this.schedulerChangeListeners = new Vector<SchedulerChangeListener>();
		this.scheduler = new SchedulerImpl(this);
		this.scheduler.addSchedulerChangeListener(this);
	}

	public ProfileManager(String configFile) throws SAXException, IOException, ParserConfigurationException, FactoryConfigurationError {
		this();
		this.configFile = configFile;
		loadProfiles(configFile);
		Collections.sort(profiles);
	}

	public void setRemoteConnection(RemoteManager remoteManager) throws MalformedURLException, RemoteException, NotBoundException {
		this.remoteManager = remoteManager;

		remoteListener = new ProfileListChangeListener() {
			@Override
			public void profileListChanged() {
				updateRemoteProfiles();
			}

			@Override
			public void profileChanged(Profile p) {
				// ProfileManager.this.profileChanged(p);
				updateRemoteProfiles();
			}
		};
		remoteManager.addProfileListChangeListener(remoteListener);
		remoteManager.addSchedulerChangeListener(this);
		updateRemoteProfiles();
		fireSchedulerChangedEvent();
	}

	private void updateRemoteProfiles() {
		this.profiles = new Vector<Profile>();

		Profile[] remoteprofiles = remoteManager.getProfiles();
		for (Profile remoteprofile : remoteprofiles) {
			this.profiles.add(remoteprofile);
			remoteprofile.addProfileChangeListener(this);
		}

		fireProfilesChangeEvent();
	}

	public void disconnectRemote() {

		if (remoteManager != null) {
			try {
				remoteManager.removeProfileListChangeListener(remoteListener);
				remoteManager.removeSchedulerChangeListener(this);
			}
			catch (RemoteException e) {
				ExceptionHandler.reportException(e);
			}
			remoteManager = null;

			this.profiles = new Vector<Profile>();

			try {
				loadProfiles(configFile);
			}
			catch (Exception e) {
				ExceptionHandler.reportException(e);
			}
			remoteConnected = false;
			fireProfilesChangeEvent();
		}
	}

	/**
	 * Check for the existence of a successful remote connection.
	 * @return true if a remote connection is active.
	 */
	public final boolean isConnected() {
		return (remoteManager != null);
	}

	/**
	 * This method in necessary to avoid self RMI connections.
	 * @return true if this instance is connected to another instance, or an attempt to connect is running
	 */
	public final boolean isConnectedToRemoteInstance() {
		return remoteConnected;
	}

	public boolean loadProfiles(String profilesFileName)
			throws SAXException, IOException, ParserConfigurationException, FactoryConfigurationError {
		File file = new File(profilesFileName);
		if (file.exists() && (file.length() > 0)) {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(file);

			NodeList list = doc.getDocumentElement().getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				Node n = list.item(i);
				if (n.getNodeType() == Node.ELEMENT_NODE) {
					try {
						Profile p = Profile.unserialize((Element) n);
						String name = p.getName();
						int j = 1;
						while (null != getProfile(name)) {
							name = p.getName() + " (" + (j++) + ")";
						}
						p.setName(name);
						doAddProfile(p, false);
					}
					catch (Throwable t) {
						ExceptionHandler.reportException("Failed to load a Profile, ignoring and continuing with the rest", t);
					}
				}
			}
			fireProfilesChangeEvent();
			return true;
		}
		return false;
	}

	private void doAddProfile(Profile profile, boolean fireChangedEvent) {
		profiles.add(profile);
		profile.addProfileChangeListener(this);
		if (fireChangedEvent) {
			fireProfilesChangeEvent();
		}
	}

	public void addProfile(Profile profile) {
		doAddProfile(profile, true);
	}

	public void removeProfile(Profile profile) {
		profile.removeProfileChangeListener(this);
		profiles.remove(profile);
		fireProfilesChangeEvent();
	}

	public Vector<Profile> getProfiles() {
		return profiles;
	}

	public Profile getProfile(String name) {
		for (Profile p : profiles) {
			if (p.getName().equals(name)) {
				return p;
			}
		}
		return null;
	}

	public void startScheduler() {
		if (remoteManager != null) {
			remoteManager.startTimer();
		}
		else {
			scheduler.start();
		}
	}

	public void stopScheduler() {
		if (remoteManager != null) {
			remoteManager.stopTimer();
		}
		else {
			scheduler.stop();
		}
	}

	public boolean isSchedulerEnabled() {
		if (remoteManager != null) {
			return remoteManager.isSchedulerEnabled();
		}
		return scheduler.isEnabled();
	}

	@Override
	public ScheduleTask getNextScheduleTask() {
		long now = System.currentTimeMillis();
		long nextTime = Long.MAX_VALUE;
		Profile nextProfile = null;

		for (Profile p : profiles) {
			Schedule s = p.getSchedule();
			if (p.isEnabled() && (s != null)) {
				long o = s.getNextOccurrence(now);
				if (nextTime > o) {
					nextTime = o;
					nextProfile = p;
				}
			}
		}

		if (nextProfile != null) {
			return new ProfileManagerSchedulerTask(nextProfile, nextTime);
		}
		return null;
	}

	public void addProfilesChangeListener(ProfileListChangeListener listener) {
		changeListeners.add(listener);
	}

	public void removeProfilesChangeListener(ProfileListChangeListener listener) {
		changeListeners.remove(listener);
	}

	protected void fireProfilesChangeEvent() {
		for (ProfileListChangeListener changeListener : changeListeners) {
			changeListener.profileListChanged();
		}
	}

	@Override
	public void profileChanged(Profile profile) {
		scheduler.refresh();
		for (ProfileListChangeListener changeListener : changeListeners) {
			changeListener.profileChanged(profile);
		}
	}

	public void addSchedulerListener(ProfileSchedulerListener listener) {
		scheduleListeners.add(listener);
	}

	protected void fireProfileSchedulerEvent(Profile profile) {
		for (ProfileSchedulerListener schedulerListener : scheduleListeners) {
			schedulerListener.profileExecutionScheduled(profile);
		}
	}

	@Override
	public void schedulerStatusChanged(boolean status) {
		fireSchedulerChangedEvent();
	}

	public void addSchedulerChangeListener(SchedulerChangeListener listener) {
		schedulerChangeListeners.add(listener);
	}

	public void removeSchedulerChangeListener(SchedulerChangeListener listener) {
		schedulerChangeListeners.remove(listener);
	}

	protected void fireSchedulerChangedEvent() {
		boolean enabled = isSchedulerEnabled();
		for (SchedulerChangeListener listener : schedulerChangeListeners) {
			listener.schedulerStatusChanged(enabled);
		}
	}

	public void save()// throws ParserConfigurationException, FactoryConfigurationError, IOException
	{
		if (remoteManager != null) {
			try {
				remoteManager.removeProfileListChangeListener(remoteListener);
				remoteManager.save(profiles.toArray(new Profile[0]));
				remoteManager.addProfileListChangeListener(remoteListener);
			}
			catch (RemoteException e) {
				ExceptionHandler.reportException(e);
			}
		}
		else {
			OutputStreamWriter osw = null;
			try {
				DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document doc = docBuilder.newDocument();

				Element e = doc.createElement("Profiles");
				e.setAttribute("version", "1.1");
				for (Profile p : profiles) {
					e.appendChild(p.serialize(doc));
				}
				doc.appendChild(e);

				TransformerFactory fac = TransformerFactory.newInstance();
				fac.setAttribute("indent-number", 2);
				Transformer tf = fac.newTransformer();
				DOMSource source = new DOMSource(doc);
				osw = new OutputStreamWriter(new FileOutputStream(configFile + ".tmp"), "UTF-8");
				StreamResult out = new StreamResult(osw);

				tf.setOutputProperty(OutputKeys.METHOD, "xml");
				tf.setOutputProperty(OutputKeys.VERSION, "1.0");
				tf.setOutputProperty(OutputKeys.INDENT, "yes");
				tf.setOutputProperty(OutputKeys.STANDALONE, "no");
				tf.transform(source, out);
				osw.flush();
				osw.close();

				File newCfgFile = new File(configFile + ".tmp");
				if (0 == newCfgFile.length()) {
					throw new Exception("Storing profiles failed (size = 0)");
				}

				Util.fileRenameToPortableLegacy(configFile + ".tmp", configFile);
			}
			catch (Exception e) {
				if (null != osw) {
					try {
						osw.close();
						new File(configFile + ".tmp").delete();
					}
					catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				ExceptionHandler.reportException(e);
			}
		}
	}

	public void setRemoteConnected(boolean connected) {
		remoteConnected = connected;
	}
}
