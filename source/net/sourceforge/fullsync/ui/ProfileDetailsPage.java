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

import net.sourceforge.fullsync.ProfileManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class ProfileDetailsPage implements WizardPage {
	private WizardDialog dialog;
	private ProfileManager profileManager;
	private String profileName;
	private Button okButton;

	private ProfileDetails details;

	public ProfileDetailsPage(WizardDialog dialog, ProfileManager profileManager, String profileName) {
		dialog.setPage(this);
		this.dialog = dialog;
		this.profileManager = profileManager;
		this.profileName = profileName;
	}

	@Override
	public final String getTitle() {
		return Messages.getString("ProfileDetailsPage.Profile") + " " + profileName; //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public final String getCaption() {
		return Messages.getString("ProfileDetailsPage.ProfileDetails"); //$NON-NLS-1$
	}

	@Override
	public final String getDescription() {
		return ""; //$NON-NLS-1$
	}

	@Override
	public final Image getIcon() {
		return GuiController.getInstance().getImage("Profile_Default.png"); //$NON-NLS-1$
	}

	@Override
	public final Image getImage() {
		return GuiController.getInstance().getImage("Profile_Wizard.png"); //$NON-NLS-1$
	}

	@Override
	public final void createContent(final Composite content) {
		details = new ProfileDetails(content, SWT.NULL);
		details.setProfileManager(profileManager);
		details.setProfileName(profileName);
	}

	@Override
	public boolean apply() {
		details.apply();
		return true; //FIXME: return false if failed
	}
	
	@Override
	public boolean cancel() {
		return true;
	}

}
