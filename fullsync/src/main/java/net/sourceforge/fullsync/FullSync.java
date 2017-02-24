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

import java.util.Stack;

import net.sourceforge.fullsync.impl.ConfigurationPreferences;

public class FullSync {
	private final Stack<PromptQuestion> questionHandler = new Stack<>();
	private final ConfigurationPreferences preferences;
	private final ProfileManager profileManager;
	private final Synchronizer synchronizer;

	public FullSync(ConfigurationPreferences _preferences, ProfileManager _profileManager, Synchronizer _synchronizer) {
		preferences = _preferences;
		profileManager = _profileManager;
		synchronizer = _synchronizer;
	}

	public PromptQuestion getQuestionHandler() {
		return questionHandler.peek();
	}

	public void pushQuestionHandler(PromptQuestion handler) {
		questionHandler.push(handler);
	}

	public void popQuestionHandler() {
		questionHandler.pop();
	}

	public ConfigurationPreferences getPreferences() {
		return preferences;
	}

	public ProfileManager getProfileManager() {
		return profileManager;
	}

	public Synchronizer getSynchronizer() {
		return synchronizer;
	}

	public void disconnectRemote() {
		profileManager.disconnectRemote();
		synchronizer.disconnectRemote();
	}
}
