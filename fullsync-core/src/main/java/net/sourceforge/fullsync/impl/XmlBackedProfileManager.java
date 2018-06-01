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
package net.sourceforge.fullsync.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.ProfileChangeListener;
import net.sourceforge.fullsync.ProfileListChangeListener;
import net.sourceforge.fullsync.ProfileManager;
import net.sourceforge.fullsync.ProfileSchedulerListener;
import net.sourceforge.fullsync.schedule.Schedule;
import net.sourceforge.fullsync.schedule.ScheduleTask;
import net.sourceforge.fullsync.schedule.ScheduleTaskSource;
import net.sourceforge.fullsync.schedule.Scheduler;

/**
 * A ProfileManager handles persistence of Profiles and provides
 * a scheduler for creating events when a Profile should be executed.
 */
@Singleton
public class XmlBackedProfileManager implements ScheduleTaskSource, ProfileManager, ProfileChangeListener {
	private final Provider<Scheduler> schedulerProvider;
	private String profilesFileName;
	private Set<Profile> profiles = new TreeSet<>();
	private List<ProfileListChangeListener> changeListeners = new CopyOnWriteArrayList<>();
	private List<ProfileSchedulerListener> scheduleListeners = new CopyOnWriteArrayList<>();

	@Inject
	public XmlBackedProfileManager(Provider<Scheduler> schedulerProvider) {
		this.schedulerProvider = schedulerProvider;
	}

	@Override
	public void setProfilesFileName(String profilesFileName) {
		this.profilesFileName = profilesFileName;
	}

	@Override
	public boolean loadProfiles() {
		return loadProfiles(profilesFileName);
	}

	@Override
	public boolean loadProfiles(String profilesFileName) {
		File file = new File(profilesFileName);
		if (file.exists() && (file.length() > 0)) {
			try {
				DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				deserializeProfileList(builder.parse(file));
			}
			catch (ParserConfigurationException | SAXException | IOException ex) {
				ExceptionHandler.reportException("Profile loading failed", ex);
			}
			fireProfilesChangeEvent();
			return true;
		}
		return false;
	}

	private void deserializeProfileList(Document doc) {
		NodeList list = doc.getDocumentElement().getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node n = list.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				try {
					deserializeProfile(n);
				}
				catch (Exception ex) {
					String message = String.format("Failed to load Profile %d, ignoring and continuing with the rest", i + 1);
					ExceptionHandler.reportException(message, ex);
				}
			}
		}
	}

	private void deserializeProfile(Node n) throws DataParseException {
		Profile p = Profile.unserialize((Element) n);
		String name = p.getName();
		int j = 1;
		while (null != getProfile(name)) {
			name = p.getName() + " (" + (j++) + ")";
		}
		if (!name.equals(p.getName())) {
			Profile newProfile = new Profile(name, p.getDescription(), p.getSynchronizationType(), p.getSource(), p.getDestination(),
				p.getRuleSet(), p.isSchedulingEnabled(), p.getSchedule());
			newProfile.setLastError(p.getLastErrorLevel(), p.getLastErrorString());
			newProfile.setLastUpdate(p.getLastUpdate());
			doAddProfile(newProfile, false);
		}
		else {
			doAddProfile(p, false);
		}
	}

	private void doAddProfile(Profile profile, boolean fireChangedEvent) {
		profiles.add(profile);
		profile.addProfileChangeListener(this);
		if (fireChangedEvent) {
			fireProfilesChangeEvent();
		}
	}

	@Override
	public void addProfile(Profile profile) {
		doAddProfile(profile, true);
	}

	@Override
	public void updateProfile(Profile oldProfile, Profile newProfile) {
		oldProfile.removeProfileChangeListener(this);
		profiles.remove(oldProfile);
		doAddProfile(newProfile, true);
	}

	@Override
	public void removeProfile(Profile profile) {
		profile.removeProfileChangeListener(this);
		profiles.remove(profile);
		fireProfilesChangeEvent();
	}

	@Override
	public Collection<Profile> getProfiles() {
		return profiles;
	}

	@Override
	public Profile getProfile(String name) {
		for (Profile p : profiles) {
			if (p.getName().equals(name)) {
				return p;
			}
		}
		return null;
	}

	@Override
	public ScheduleTask getNextScheduleTask() {
		long now = System.currentTimeMillis();
		long nextTime = Long.MAX_VALUE;
		Profile nextProfile = null;

		for (Profile p : profiles) {
			Schedule s = p.getSchedule();
			if (p.isSchedulingEnabled() && (null != s)) {
				long o = s.getNextOccurrence(p.getLastScheduleTime(), now);
				if (nextTime > o) {
					nextTime = o;
					nextProfile = p;
				}
			}
		}

		if (null != nextProfile) {
			return new ProfileManagerSchedulerTask(this, nextProfile, nextTime);
		}
		return null;
	}

	@Override
	public void addProfilesChangeListener(ProfileListChangeListener listener) {
		changeListeners.add(listener);
	}

	@Override
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
		schedulerProvider.get().refresh();
		for (ProfileListChangeListener changeListener : changeListeners) {
			changeListener.profileChanged(profile);
		}
	}

	@Override
	public void addSchedulerListener(ProfileSchedulerListener listener) {
		scheduleListeners.add(listener);
	}

	@Override
	public void removeSchedulerListener(ProfileSchedulerListener listener) {
		scheduleListeners.remove(listener);
	}

	protected void fireProfileSchedulerEvent(Profile profile) {
		for (ProfileSchedulerListener schedulerListener : scheduleListeners) {
			schedulerListener.profileExecutionScheduled(profile);
		}
	}

	@Override
	public void save() {
		try {
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = docBuilder.newDocument();

			Element e = doc.createElement("Profiles");
			e.setAttribute("version", "1.1");
			profiles.stream().map(p -> p.serialize(doc)).forEachOrdered(e::appendChild);
			doc.appendChild(e);

			TransformerFactory fac = TransformerFactory.newInstance();
			fac.setAttribute("indent-number", 2);
			Transformer tf = fac.newTransformer();
			tf.setOutputProperty(OutputKeys.METHOD, "xml");
			tf.setOutputProperty(OutputKeys.VERSION, "1.0");
			tf.setOutputProperty(OutputKeys.INDENT, "yes");
			tf.setOutputProperty(OutputKeys.STANDALONE, "no");
			DOMSource source = new DOMSource(doc);
			File newCfgFile = new File(profilesFileName + ".tmp");
			try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(newCfgFile), StandardCharsets.UTF_8)) {
				tf.transform(source, new StreamResult(osw));
				osw.flush();
			}
			try {
				if (0 == newCfgFile.length()) {
					throw new Exception("Storing profiles failed (size = 0)");
				}
				Files.move(newCfgFile.toPath(), new File(profilesFileName).toPath(), StandardCopyOption.ATOMIC_MOVE);
			}
			finally {
				Files.deleteIfExists(newCfgFile.toPath());
			}
		}
		catch (Exception ex) {
			ExceptionHandler.reportException(ex);
		}
	}
}
