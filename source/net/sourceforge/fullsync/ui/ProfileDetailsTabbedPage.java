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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class ProfileDetailsTabbedPage extends SelectionAdapter implements WizardPage {
	private WizardDialog dialog;
	private ProfileManager profileManager;
	private String profileName;
	private Button okButton;

	private ProfileDetailsTabbed details;

	public ProfileDetailsTabbedPage(WizardDialog dialog, ProfileManager profileManager, String profileName) {
		dialog.setPage(this);
		this.dialog = dialog;
		this.profileManager = profileManager;
		this.profileName = profileName;
	}

	@Override
	public String getTitle() {
		String title = Messages.getString("ProfileDetailsPage.Profile"); //$NON-NLS-1$
		if (this.profileName != null) {
			title = title + " " + profileName; //$NON-NLS-1$
		}
		return title;
	}

	@Override
	public String getCaption() {
		return Messages.getString("ProfileDetailsPage.ProfileDetails"); //$NON-NLS-1$
	}

	@Override
	public String getDescription() {
		return ""; //$NON-NLS-1$
	}

	@Override
	public Image getIcon() {
		return GuiController.getInstance().getImage("Profile_Default.png"); //$NON-NLS-1$
	}

	@Override
	public Image getImage() {
		return GuiController.getInstance().getImage("Profile_Wizard.png"); //$NON-NLS-1$
	}

	@Override
	public void createContent(Composite content) {
		details = new ProfileDetailsTabbed(content, SWT.NULL);
		details.setProfileManager(profileManager);
		details.setProfileName(profileName);
	}

	@Override
	public void createBottom(final Composite bottom) {
		bottom.setLayout(new GridLayout(2, false));

		okButton = new Button(bottom, SWT.PUSH);
		okButton.setText(Messages.getString("ProfileDetailsPage.Ok")); //$NON-NLS-1$
		okButton.addSelectionListener(this);
		GridData okButtonLayoutData = new GridData(SWT.END, SWT.CENTER, true, true);
		okButtonLayoutData.widthHint = UISettings.BUTTON_WIDTH;
		okButtonLayoutData.heightHint = UISettings.BUTTON_HEIGHT;
		okButton.setLayoutData(okButtonLayoutData);

		Button cancelButton = new Button(bottom, SWT.PUSH);
		cancelButton.setText(Messages.getString("ProfileDetailsPage.Cancel")); //$NON-NLS-1$
		cancelButton.addSelectionListener(this);
		GridData cancelButtonLayoutData = new GridData(SWT.END, SWT.CENTER, false, true);
		cancelButtonLayoutData.widthHint = UISettings.BUTTON_WIDTH;
		cancelButtonLayoutData.heightHint = UISettings.BUTTON_HEIGHT;
		cancelButton.setLayoutData(cancelButtonLayoutData);

		bottom.getShell().setDefaultButton(okButton);
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		if (e.widget == okButton) {
			details.apply();
		}
		dialog.dispose();
	}
}
