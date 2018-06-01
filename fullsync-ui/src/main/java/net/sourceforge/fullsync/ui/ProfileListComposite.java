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
package net.sourceforge.fullsync.ui;

import java.util.Objects;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.widgets.Composite;

import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.ProfileListChangeListener;
import net.sourceforge.fullsync.ProfileManager;

public abstract class ProfileListComposite extends Composite implements ProfileListChangeListener {
	private final ProfileManager profileManager;
	private final ProfileListControlHandler profileListControlHandler;

	public ProfileListComposite(Composite parent, ProfileManager pm, ProfileListControlHandler handler) {
		super(parent, SWT.NULL);
		parent.addDisposeListener(this::onDisposed);
		Objects.requireNonNull(pm);
		Objects.requireNonNull(handler);
		profileManager = pm;
		profileManager.addProfilesChangeListener(this);
		profileListControlHandler = handler;
	}

	public ProfileManager getProfileManager() {
		return profileManager;
	}

	private void onDisposed(DisposeEvent e) {
		dispose();
	}

	@Override
	public void dispose() {
		profileManager.removeProfilesChangeListener(this);
		super.dispose();
	}

	public ProfileListControlHandler getHandler() {
		return profileListControlHandler;
	}

	public abstract Profile getSelectedProfile();
}
