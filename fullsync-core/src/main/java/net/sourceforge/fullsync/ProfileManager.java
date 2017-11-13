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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
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

import net.sourceforge.fullsync.schedule.Schedule;
import net.sourceforge.fullsync.schedule.ScheduleTask;
import net.sourceforge.fullsync.schedule.ScheduleTaskSource;
import net.sourceforge.fullsync.schedule.Scheduler;
import net.sourceforge.fullsync.schedule.SchedulerChangeListener;
import net.sourceforge.fullsync.schedule.SchedulerImpl;

// TODO remove schedulerChangeListener
/**
 * A ProfileManager handles persistence of Profiles and provides
 * a scheduler for creating events when a Profile should be executed.
 */
public class ProfileManager implements ProfileChangeListener, ScheduleTaskSource {
	static class ProfileManagerSchedulerTask implements ScheduleTask {
		private ProfileManager profileManager;
		private Profile profile;
		private long executionTime;

		ProfileManagerSchedulerTask(ProfileManager pm, Profile p, long ts) {
			profileManager = pm;
			profile = p;
			executionTime = ts;
		}

		@Override
		public void run() {
			Thread worker = new Thread(() -> profileManager.fireProfileSchedulerEvent(profile));
			worker.start();
			profile.getSchedule().setLastOccurrence(System.currentTimeMillis());
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
	private final Scheduler scheduler;
	private List<Profile> profiles = new ArrayList<>();
	private List<ProfileListChangeListener> changeListeners = new ArrayList<>();
	private List<ProfileSchedulerListener> scheduleListeners = new ArrayList<>();

	public ProfileManager(String configFile) throws SAXException, IOException, ParserConfigurationException, FactoryConfigurationError {
		scheduler = new SchedulerImpl(this);
		this.configFile = configFile;
		loadProfiles(configFile);
		Collections.sort(profiles);
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
					catch (Exception ex) {
						String message = String.format("Failed to load Profile %d, ignoring and continuing with the rest", i + 1);
						ExceptionHandler.reportException(message, ex);
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

	public List<Profile> getProfiles() {
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
		scheduler.start();
	}

	public void stopScheduler() {
		scheduler.stop();
	}

	public boolean isSchedulerEnabled() {
		return scheduler.isEnabled();
	}

	@Override
	public ScheduleTask getNextScheduleTask() {
		long now = System.currentTimeMillis();
		long nextTime = Long.MAX_VALUE;
		Profile nextProfile = null;

		for (Profile p : profiles) {
			Schedule s = p.getSchedule();
			if (p.isEnabled() && (null != s)) {
				long o = s.getNextOccurrence(now);
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

	public void addSchedulerChangeListener(SchedulerChangeListener listener) {
		scheduler.addSchedulerChangeListener(listener);
	}

	public void removeSchedulerChangeListener(SchedulerChangeListener listener) {
		scheduler.removeSchedulerChangeListener(listener);
	}

	public void save() {
		try {
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = docBuilder.newDocument();

			Element e = doc.createElement("Profiles");
			e.setAttribute("version", "1.1");
			profiles.stream()
				.map(p -> p.serialize(doc))
				.forEachOrdered(e::appendChild);
			doc.appendChild(e);

			TransformerFactory fac = TransformerFactory.newInstance();
			fac.setAttribute("indent-number", 2);
			Transformer tf = fac.newTransformer();
			tf.setOutputProperty(OutputKeys.METHOD, "xml");
			tf.setOutputProperty(OutputKeys.VERSION, "1.0");
			tf.setOutputProperty(OutputKeys.INDENT, "yes");
			tf.setOutputProperty(OutputKeys.STANDALONE, "no");
			DOMSource source = new DOMSource(doc);
			File newCfgFile = new File(configFile + ".tmp");
			try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(newCfgFile), StandardCharsets.UTF_8)) {
				tf.transform(source, new StreamResult(osw));
				osw.flush();
			}
			try {
				if (0 == newCfgFile.length()) {
					throw new Exception("Storing profiles failed (size = 0)");
				}
				Util.fileRenameToPortableLegacy(newCfgFile.getName(), configFile);
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
