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
 * This code was generated using CloudGarden's Jigloo
 * SWT/Swing GUI Builder, which is free for non-commercial
 * use. If Jigloo is being used commercially (ie, by a corporation,
 * company or business for any purpose whatever) then you
 * should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details.
 * Use of Jigloo implies acceptance of these licensing terms.
 * *************************************
 * A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED
 * for this machine, so Jigloo or this code cannot be used legally
 * for any corporate or commercial purpose.
 * *************************************
 */
/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class ProfileDetailsPage implements WizardPage {
	private WizardDialog dialog;
	private ProfileManager profileManager;
	private String profileName;

	private ProfileDetails details;

	public ProfileDetailsPage(WizardDialog dialog, ProfileManager profileManager, String profileName) {
		dialog.setPage(this);
		this.dialog = dialog;
		this.profileManager = profileManager;
		this.profileName = profileName;
	}

	public String getTitle() {
		return Messages.getString("ProfileDetailsPage.Profile") + " " + profileName; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public String getCaption() {
		return Messages.getString("ProfileDetailsPage.ProfileDetails"); //$NON-NLS-1$
	}

	public String getDescription() {
		return ""; //$NON-NLS-1$
	}

	public Image getIcon() {
		return GuiController.getInstance().getImage("Profile_Default.png"); //$NON-NLS-1$
	}

	public Image getImage() {
		return GuiController.getInstance().getImage("Profile_Wizard.png"); //$NON-NLS-1$
	}

	public void createContent(Composite content) {
		details = new ProfileDetails(content, SWT.NULL);
		details.setProfileManager(profileManager);
		details.setProfileName(profileName);
	}

	public void createBottom(Composite bottom) {
		bottom.setLayout(new GridLayout(2, false));

		Button okButton = new Button(bottom, SWT.PUSH);
		okButton.setText(Messages.getString("ProfileDetailsPage.Ok")); //$NON-NLS-1$
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				details.apply();
				dialog.dispose();
			}
		});
		okButton.setLayoutData(new GridData(GridData.END, GridData.CENTER, true, true));

		Button cancelButton = new Button(bottom, SWT.PUSH);
		cancelButton.setText(Messages.getString("ProfileDetailsPage.Cancel")); //$NON-NLS-1$
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				dialog.dispose();
			}
		});
		cancelButton.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, true));

		bottom.getShell().setDefaultButton(okButton);
	}
}
