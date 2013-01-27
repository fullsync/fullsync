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

import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.ProfileManager;

import org.eclipse.swt.widgets.Composite;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public abstract class ProfileListComposite extends Composite {
	public ProfileListComposite(Composite parent, int style) {
		super(parent, style);
	}

	public abstract Profile getSelectedProfile();

	public abstract void setProfileManager(ProfileManager manager);

	public abstract ProfileManager getProfileManager();

	public abstract void setHandler(ProfileListControlHandler handler);

	public abstract ProfileListControlHandler getHandler();
}
