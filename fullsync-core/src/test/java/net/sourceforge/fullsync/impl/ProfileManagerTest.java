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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.Util;
import net.sourceforge.fullsync.event.ProfileChanged;
import net.sourceforge.fullsync.event.ProfileListChanged;
import net.sourceforge.fullsync.schedule.IntervalSchedule;

public class ProfileManagerTest {
	private XmlBackedProfileManager profileManager;
	private EventBus eventBus;
	@TempDir
	public File tmpDir;

	@BeforeEach
	public void beforeEach() {
		eventBus = new EventBus();
		eventBus.register(this);
		profileManager = new XmlBackedProfileManager(eventBus);
	}

	@AfterEach
	public void afterEach() {
		eventBus.unregister(this);
		eventBus = null;
		profileManager = null;
	}

	@Test
	public void unknownProfileUUIDYieldsNull() {
		assertNull(profileManager.getProfileById(""), "Unknown profile UUID yields null");
	}

	@Test
	public void unknownProfileNameYieldsNull() {
		assertNull(profileManager.getProfileByName(""), "Unknown profile name yields null");
	}

	@Test
	public void testLoadV1_1ProfileWithScheduling() {
		profileManager.setProfilesFileName("src/test/resources/profile-1_1-schedule.xml");
		assertTrue(profileManager.loadProfiles(), "Load v1.1 profile with scheduling");
		assertEquals(1, profileManager.getProfiles().size(), "Loaded one profile");
		var p = profileManager.getProfileByName("Test Profile with Schedule");
		assertNotNull(p, "Loaded profile 'Test Profile with Schedule'");
		assertEquals("Profile description", p.getDescription(), "Loaded profile description");
		var dstBuilder = new ConnectionDescription.Builder();
		dstBuilder.setScheme("file");
		dstBuilder.setPath("/b/");
		dstBuilder.setUserDirIsRoot(true);
		assertEquals(dstBuilder.build(), p.getDestination(), "Loaded profile destination");
		assertNotEquals(p.getId(), "", "Loaded profile got autogenerated profile id");
		assertEquals(0, p.getLastErrorLevel(), "Loaded profile has no error level");
		assertEquals("", p.getLastErrorString(), "Loaded profile has no error string");
		assertEquals(0, p.getLastScheduleTime(), "Loaded profile has no last schedule time");
		assertNull(p.getLastUpdate(), "Loaded profile last updated at '1/31/12 7:35 AM' - ignored legacy format");
		assertEquals("never", p.getLastUpdateText(), "Loaded profile last updated at '1/31/12 7:35 AM' - ignored legacy format");
		assertEquals("Test Profile with Schedule", p.getName(), "Loaded profile with known name");
		assertNotEquals("not scheduled", p.getNextUpdateText(), "Loaded profile has scheduling configured");
		assertNotEquals("not enabled", p.getNextUpdateText(), "Loaded profile has scheduling configured");
		assertEquals(new SimplifiedRuleSetDescriptor(false, null, true, null), p.getRuleSet(), "Loaded profile ruleset");
		assertEquals(new IntervalSchedule(3600000, 3600000, "hours"), p.getSchedule(), "Loaded profile schedule");
		var srcBuilder = new ConnectionDescription.Builder();
		srcBuilder.setScheme("file");
		srcBuilder.setPath("/a/");
		srcBuilder.setUserDirIsRoot(true);
		assertEquals(srcBuilder.build(), p.getSource(), "Loaded profile destination");
		assertEquals("Publish/Update", p.getSynchronizationType(), "Loaded profile synchronization type");
	}

	@Test
	public void testLoadV1_2ProfileWithSimplifiedRuleSet() {
		profileManager.setProfilesFileName("src/test/resources/profile-1_2-filefilter.xml");
		assertTrue(profileManager.loadProfiles(), "Load v1.2 profile with simplified rule set");
		assertEquals(1, profileManager.getProfiles().size(), "Loaded one profile");
		var p = profileManager.getProfileById("5ed254fc-ac55-4a50-a0db-ac9c03f071fc");
		assertNotNull(p, "Loaded profile '5ed254fc-ac55-4a50-a0db-ac9c03f071fc'");
		assertEquals("Ignore System Volume Information", p.getName(), "Loaded profile by id has correct name");
	}

	@Test
	public void testLoadAndSaveProfiles() throws IOException {
		profileManager.setProfilesFileName("src/test/resources/profile-1_2-filefilter.xml");
		assertTrue(profileManager.loadProfiles(), "Load v1.2 profile with simplified rule set");
		assertEquals(1, profileManager.getProfiles().size(), "Loaded one profile");
		var tmpFile = new File(tmpDir.getAbsolutePath() + UUID.randomUUID() + ".xml");
		assertFalse(tmpFile.exists(), "tmp file not yet existing");
		profileManager.setProfilesFileName(tmpFile.getAbsolutePath());
		profileManager.save();
		assertTrue(tmpFile.exists(), "Profile manager stored to configured file");
		try (var referenceFile = new FileInputStream("src/test/resources/profile-1_2-filefilter.xml")) {
			try (var savedFile = new FileInputStream(tmpFile)) {
				assertEquals(Util.getInputStreamAsString(referenceFile), Util.getInputStreamAsString(savedFile));
			}
		}
	}

	@Test
	public void testAddProfile() throws InterruptedException {
		assertEquals(0, profileManager.getProfiles().size(), "No profiles exist");
		final var latch = new CountDownLatch(1);
		final Object eventHandler = new Object() {
			@Subscribe
			public void handleProfileListChanged(ProfileListChanged plc) {
				latch.countDown();
			}
		};
		eventBus.register(eventHandler);
		var p = profileManager.getProfileBuilder().setName("Test Profile").build();
		profileManager.addProfile(p);
		assertTrue(latch.await(1, TimeUnit.SECONDS), "ProfileListChanged event triggered");
		assertEquals(1, profileManager.getProfiles().size(), "One Profile added");
	}

	@Test
	public void testUpdateProfileErrorLevel() throws InterruptedException {
		profileManager.setProfilesFileName("src/test/resources/profile-1_2-filefilter.xml");
		assertTrue(profileManager.loadProfiles(), "Load v1.2 profile with simplified rule set");
		assertEquals(1, profileManager.getProfiles().size(), "Loaded one profile");
		var p = profileManager.getProfileById("5ed254fc-ac55-4a50-a0db-ac9c03f071fc");
		assertNotNull(p, "Loaded profile '5ed254fc-ac55-4a50-a0db-ac9c03f071fc'");
		assertEquals("Ignore System Volume Information", p.getName(), "Loaded profile by id has correct name");
		assertEquals(0, p.getLastErrorLevel(), "Profile last error level");
		assertNull(p.getLastErrorString(), "Profile last error message");
		final var latch = new CountDownLatch(1);
		final Object eventHandler = new Object() {
			@Subscribe
			public void handleProfileChanged(ProfileChanged pc) {
				assertEquals(42, pc.profile().getLastErrorLevel(), "Updated Profile last error level");
				assertEquals("Test Error Message", pc.profile().getLastErrorString(), "Updated Profile last error message");
				latch.countDown();
			}
		};
		eventBus.register(eventHandler);
		p.setLastError(42, "Test Error Message");
		assertTrue(latch.await(1, TimeUnit.SECONDS), "ProfileChanged event triggered");
		assertEquals(42, p.getLastErrorLevel(), "Updated Profile last error level");
		assertEquals("Test Error Message", p.getLastErrorString(), "Updated Profile last error message");
	}

	@Test
	public void testUpdateProfile() throws InterruptedException {
		profileManager.setProfilesFileName("src/test/resources/profile-1_2-filefilter.xml");
		assertTrue(profileManager.loadProfiles(), "Load v1.2 profile with simplified rule set");
		assertEquals(1, profileManager.getProfiles().size(), "Loaded one profile");
		var p = profileManager.getProfileById("5ed254fc-ac55-4a50-a0db-ac9c03f071fc");
		assertNotNull(p, "Loaded profile '5ed254fc-ac55-4a50-a0db-ac9c03f071fc'");
		assertEquals("Ignore System Volume Information", p.getName(), "Loaded profile by id has correct name");
		var pb = profileManager.getProfileBuilder(p);
		pb.setName("Updated Profile Name");
		final var latch = new CountDownLatch(1);
		final Object eventHandler = new Object() {
			@Subscribe
			public void handleProfileChanged(ProfileChanged pc) {
				assertEquals(p.getId(), pc.profile().getId(), "Existing Profile Changed");
				assertEquals("Updated Profile Name", pc.profile().getName());
				latch.countDown();
			}
		};
		eventBus.register(eventHandler);
		profileManager.updateProfile(p, pb.build());
		assertTrue(latch.await(1, TimeUnit.SECONDS), "ProfileChanged event triggered");
		var p2 = profileManager.getProfileById(p.getId());
		assertEquals("Updated Profile Name", p2.getName());
	}
}
