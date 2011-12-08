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
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import net.sourceforge.fullsync.remote.RemoteManager;
import net.sourceforge.fullsync.schedule.CrontabSchedule;
import net.sourceforge.fullsync.schedule.IntervalSchedule;
import net.sourceforge.fullsync.schedule.Schedule;
import net.sourceforge.fullsync.schedule.ScheduleTask;
import net.sourceforge.fullsync.schedule.ScheduleTaskSource;
import net.sourceforge.fullsync.schedule.Scheduler;
import net.sourceforge.fullsync.schedule.SchedulerChangeListener;
import net.sourceforge.fullsync.schedule.SchedulerImpl;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

// TODO remove schedulerChangeListener
/**
 * A ProfileManager handles persistence of Profiles and provides
 * a scheduler for creating events when a Profile should be executed.
 *
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
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
			Thread worker = new Thread(new Runnable() {
				@Override
				public void run() {
					fireProfileSchedulerEvent(profile);
				}
			});
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

	class ProfileComparator implements Comparator<Profile> {
		@Override
		public int compare(Profile p1, Profile p2) {
			return p1.getName().compareTo(p2.getName());
		}
	}

	private String configFile;
	protected Vector<Profile> profiles;
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

		loadProfiles();
		Collections.sort(profiles, new ProfileComparator());
		/*
		 * Digester dig = new Digester();
		 * dig.push( this );
		 * dig.addObjectCreate ( "Profiles/Profile", Profile.class );
		 * dig.addSetProperties( "Profiles/Profile" );
		 * dig.addObjectCreate ( "Profiles/Profile/Source", ConnectionDescription.class );
		 * dig.addSetProperties( "Profiles/Profile/Source" );
		 * dig.addCallMethod ( "Profiles/Profile/Source/Param", "setParameter" );
		 * dig.addCallParam ( "Profiles/Profile/Source/Param", 0, "name" );
		 * dig.addCallParam ( "Profiles/Profile/Source/Param", 1 );
		 * dig.addSetNext ( "Profiles/Profile/Destination", "setSource" );
		 * dig.addObjectCreate ( "Profiles/Profile/Destination", ConnectionDescription.class );
		 * dig.addSetProperties( "Profiles/Profile/Destination" );
		 * dig.addCallMethod ( "Profiles/Profile/Destination/Param", "setParameter" );
		 * dig.addCallParam ( "Profiles/Profile/Destination/Param", 0, "name" );
		 * dig.addCallParam ( "Profiles/Profile/Destination/Param", 1 );
		 * dig.addSetNext ( "Profiles/Profile/Destination", "setDestination" );
		 * dig.addSetNext ( "Profiles/Profile", "addProfile" );
		 * try {
		 * dig.parse( configFile );
		 * } catch( IOException e ) {
		 * ExceptionHandler.reportException( e );
		 * } catch( SAXException e ) {
		 * e.getException().printStackTrace();
		 * }
		 */
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
				loadProfiles();
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

	private void loadProfiles() throws SAXException, IOException, ParserConfigurationException, FactoryConfigurationError {
		File file = new File(configFile);
		if (file.exists()) {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(file);

			NodeList list = doc.getDocumentElement().getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				Node n = list.item(i);
				if (n.getNodeType() == Node.ELEMENT_NODE) {
					Profile profile = unserializeProfile((Element) n);
					profiles.add(profile);
					profile.addProfileChangeListener(this);
				}
			}
		}

	}

	public void addProfile(Profile profile) {
		profiles.add(profile);
		profile.addProfileChangeListener(this);
		fireProfilesChangeEvent();
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
		for (int i = 0; i < profiles.size(); i++) {
			Profile p = profiles.get(i);
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
		else {
			return scheduler.isEnabled();
		}
	}

	@Override
	public ScheduleTask getNextScheduleTask() {
		long now = System.currentTimeMillis();
		long nextTime = Long.MAX_VALUE;
		Profile nextProfile = null;

		Enumeration<Profile> e = profiles.elements();
		while (e.hasMoreElements()) {
			Profile p = e.nextElement();
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
		else {
			return null;
		}
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

	public void removeSchedulerListener(ProfileSchedulerListener listener) {
		scheduleListeners.remove(listener);
	}

	protected void fireProfileSchedulerEvent(Profile profile) {
		for (ProfileSchedulerListener schedulerListener : scheduleListeners) {
			schedulerListener.profileExecutionScheduled(profile);
		}
	}

	/*
	 * public void addSchedulerChangeListener(SchedulerChangeListener listener) {
	 * scheduler.addSchedulerChangeListener(listener);
	 * }
	 * public void removeSchedulerChangeListener(SchedulerChangeListener listener) {
	 * scheduler.removeSchedulerChangeListener(listener);
	 * }
	 */
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

	protected Schedule unserializeSchedule(Element element) {
		if (element == null) {
			return null;
		}
		Schedule schedule = null;
		String type = element.getAttribute("type");
		try {
			if (IntervalSchedule.SCHEDULE_TYPE.equals(type)) {
				schedule = IntervalSchedule.unserialize(element);
			}
			else if (CrontabSchedule.SCHEDULE_TYPE.equals(type)) {
				schedule = CrontabSchedule.unserialize(element);
			}
		}
		catch (Exception ex) {
			ExceptionHandler.reportException(ex);
		}
		return schedule;
	}

	protected Profile unserializeProfile(Element element) {
		Profile p = new Profile();
		p.setName(element.getAttribute("name"));
		p.setDescription(element.getAttribute("description"));
		p.setSynchronizationType(element.getAttribute("type"));
		if (element.hasAttribute("enabled")) {
			p.setEnabled(Boolean.valueOf(element.getAttribute("enabled")).booleanValue());
		}
		if (element.hasAttribute("lastErrorLevel")) {
			p.setLastError(Integer.parseInt(element.getAttribute("lastErrorLevel")), element.getAttribute("lastErrorString"));
		}

		try {
			p.setLastUpdate(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).parse(element.getAttribute("lastUpdate")));
		}
		catch (ParseException e) {
			p.setLastUpdate(new Date());
		}

		p.setRuleSet(RuleSetDescriptor.unserialize((Element) element.getElementsByTagName("RuleSetDescriptor").item(0)));
		p.setSchedule(unserializeSchedule((Element) element.getElementsByTagName("Schedule").item(0)));
		p.setSource(ConnectionDescription.unserialize((Element) element.getElementsByTagName("Source").item(0)));
		p.setDestination(ConnectionDescription.unserialize((Element) element.getElementsByTagName("Destination").item(0)));
		return p;
	}

	protected Element serialize(RuleSetDescriptor desc, String name, Document doc) {
		Element elem = doc.createElement(name);

		elem.setAttribute("type", desc.getType());
		Element ruleDescriptorElement = desc.serialize(doc);
		elem.appendChild(ruleDescriptorElement);

		return elem;
	}

	protected Element serialize(Profile p, Document doc) {
		Element elem = doc.createElement("Profile");
		elem.setAttribute("name", p.getName());
		elem.setAttribute("description", p.getDescription());
		elem.setAttribute("type", p.getSynchronizationType());
		elem.setAttribute("enabled", String.valueOf(p.isEnabled()));
		elem.setAttribute("lastErrorLevel", String.valueOf(p.getLastErrorLevel()));
		elem.setAttribute("lastErrorString", p.getLastErrorString());
		elem.setAttribute("lastUpdate", DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(p.getLastUpdate()));

		elem.appendChild(serialize(p.getRuleSet(), "RuleSetDescriptor", doc));
		Schedule s = p.getSchedule();
		if (null != s) {
			elem.appendChild(s.serialize(doc));
		}
		elem.appendChild(p.getSource().serialize("Source", doc));
		elem.appendChild(p.getDestination().serialize("Destination", doc));

		return elem;
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
			try {
				DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document doc = docBuilder.newDocument();

				Element e = doc.createElement("Profiles");
				e.setAttribute("version", "1.1");
				for (Profile p : profiles) {
					e.appendChild(serialize(p, doc));
				}
				doc.appendChild(e);

				OutputStream out = new FileOutputStream(configFile);

				OutputFormat format = new OutputFormat(doc, "UTF-8", true);
				XMLSerializer serializer = new XMLSerializer(out, format);
				serializer.asDOMSerializer();
				serializer.serialize(doc);

				out.close();
			}
			catch (Exception e) {
				// TODO messagebox ?
				ExceptionHandler.reportException(e);
			}
		}
	}

	public void setRemoteConnected(boolean connected) {
		remoteConnected = connected;
	}
}
