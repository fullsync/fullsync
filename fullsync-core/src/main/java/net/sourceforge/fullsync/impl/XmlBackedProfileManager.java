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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.google.common.eventbus.EventBus;

import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.ProfileBuilder;
import net.sourceforge.fullsync.ProfileManager;
import net.sourceforge.fullsync.event.ProfileChanged;
import net.sourceforge.fullsync.event.ProfileListChanged;
import net.sourceforge.fullsync.utils.XmlUtils;

/**
 * A ProfileManager handles persistence of Profiles and provides
 * a scheduler for creating events when a Profile should be executed.
 */
@Singleton
public class XmlBackedProfileManager implements ProfileManager {
	private final EventBus eventBus;
	private String profilesFileName;
	private final Map<String, Profile> profiles = new HashMap<>();

	@Inject
	public XmlBackedProfileManager(EventBus eventBus) {
		this.eventBus = eventBus;
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
		var file = new File(profilesFileName);
		if (file.exists() && (file.length() > 0)) {
			try {
				var builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				deserializeProfileList(builder.parse(file));
			}
			catch (ParserConfigurationException | SAXException | IOException ex) {
				ExceptionHandler.reportException("Profile loading failed", ex);
			}
			eventBus.post(new ProfileListChanged());
			return true;
		}
		return false;
	}

	private void deserializeProfileList(Document doc) {
		final var profileIndex = new AtomicInteger();
		XmlUtils.forEachChildElement(doc.getDocumentElement(), profile -> {
			var index = profileIndex.incrementAndGet();
			try {
				deserializeProfile(profile);
			}
			catch (Exception ex) {
				var message = String.format("Failed to load Profile %d, ignoring and continuing with the rest", index);
				ExceptionHandler.reportException(message, ex);
			}
		});
	}

	private void deserializeProfile(Node n) throws DataParseException {
		var p = ProfileImpl.unserialize(eventBus, (Element) n);
		var id = p.getId();
		while ((null == id) || id.trim().isEmpty() || (null != getProfileById(id))) {
			id = getUnusedProfileId();
		}
		if (!id.equals(p.getId())) {
			p = getProfileBuilder(p).setId(id).build();
		}
		profiles.put(p.getId(), p);
	}

	@Override
	public void addProfile(Profile profile) {
		profiles.put(profile.getId(), profile);
		eventBus.post(new ProfileListChanged());
	}

	@Override
	public synchronized void updateProfile(Profile oldProfile, Profile newProfile) {
		profiles.remove(oldProfile.getId());
		profiles.put(newProfile.getId(), newProfile);
		eventBus.post(new ProfileChanged(newProfile));
	}

	@Override
	public synchronized void removeProfile(Profile profile) {
		profiles.remove(profile.getId());
		eventBus.post(new ProfileListChanged());
	}

	@Override
	public synchronized List<Profile> getProfiles() {
		List<Profile> profileList = new ArrayList<>(profiles.values());
		profileList.sort(new Profile.SortByNameAndIdComparator());
		return profileList;
	}

	@Override
	public synchronized Profile getProfileByName(String name) {
		for (Map.Entry<String, Profile> entry : profiles.entrySet()) {
			if (entry.getValue().getName().equals(name)) {
				return entry.getValue();
			}
		}
		return null;
	}

	@Override
	public synchronized Profile getProfileById(String uuid) {
		return profiles.get(uuid);
	}

	@Override
	public synchronized void save() {
		try {
			var docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			var doc = docBuilder.newDocument();

			var e = doc.createElement("Profiles"); //$NON-NLS-1$
			e.setAttribute("version", "1.2"); //$NON-NLS-1$ //$NON-NLS-2$
			profiles.values().stream().map(p -> ((ProfileImpl) p).serialize(doc)).forEachOrdered(e::appendChild);
			doc.appendChild(e);

			var fac = TransformerFactory.newInstance();
			fac.setAttribute("indent-number", 2); //$NON-NLS-1$
			var tf = fac.newTransformer();
			tf.setOutputProperty(OutputKeys.METHOD, "xml"); //$NON-NLS-1$
			tf.setOutputProperty(OutputKeys.VERSION, "1.0"); //$NON-NLS-1$
			tf.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
			tf.setOutputProperty(OutputKeys.STANDALONE, "no"); //$NON-NLS-1$
			var source = new DOMSource(doc);
			var newCfgFile = new File(profilesFileName + ".tmp"); //$NON-NLS-1$
			try (var osw = new OutputStreamWriter(new FileOutputStream(newCfgFile), StandardCharsets.UTF_8)) {
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

	private String getUnusedProfileId() {
		UUID uuid;
		do {
			uuid = UUID.randomUUID();
		} while (getProfileById(uuid.toString()) != null);
		return uuid.toString();
	}

	@Override
	public ProfileBuilder getProfileBuilder() {
		return new ProfileBuilderImpl(eventBus, null, this::getUnusedProfileId);
	}

	@Override
	public ProfileBuilder getProfileBuilder(Profile profile) {
		return new ProfileBuilderImpl(eventBus, profile, this::getUnusedProfileId);
	}
}
