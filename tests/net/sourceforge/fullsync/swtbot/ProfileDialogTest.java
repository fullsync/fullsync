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

package net.sourceforge.fullsync.swtbot;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.junit.jupiter.api.Test;

public class ProfileDialogTest extends GUITestBase {

	@Override
	protected void verifyAfterGUIStopped() throws Exception {
		assertConfigFileExists("profiles.xml");
	}

	@Test
	public void testCreateProfile() {
		bot.menu("File").menu("New Profile").click();
		SWTBot profile = bot.shell("Profile").bot();
		assertTrue("Profile Dialog opens with General Tab selected", profile.tabItem("General").isActive());
		profile.text(0).setText("SWTBot Profile 1");
		profile.text(1).setText("Description");
		profile.button("Ok").click();
	}
}
