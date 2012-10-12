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

import net.sourceforge.fullsync.ExceptionHandler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;

public class ImportProfilesPage implements WizardPage {
	private Composite composite;
	private ImportProfilesComposite importProfiles;
	public ImportProfilesPage(WizardDialog dialog) {
		dialog.setPage(this);
	}

	@Override
	public String getTitle() {
		return Messages.getString("ImportProfilesPage.ImportProfiles"); //$NON-NLS-1$
	}

	@Override
	public String getCaption() {
		return Messages.getString("ImportProfilesPage.ImportProfiles"); //$NON-NLS-1$
	}

	@Override
	public String getDescription() {
		return Messages.getString("ImportProfilesPage.ImportProfilesDescription"); //$NON-NLS-1$
	}

	@Override
	public Image getIcon() {
		return null;
	}

	@Override
	public Image getImage() {
		return null;
	}

	@Override
	public void createContent(Composite content) {
		composite = content;
		importProfiles = new ImportProfilesComposite(content);
	}

	@Override
	public boolean apply() {
		try {
			if (GuiController.getInstance().getProfileManager().loadProfiles(importProfiles.getSelectedFilename())) {
				return true;
			}
			else {
				MessageBox mb = new MessageBox(composite.getShell(), SWT.ICON_WARNING | SWT.OK);
				mb.setText(Messages.getString("ImportProfilesPage.ProfilesFileNotFoundTitle")); //$NON-NLS-1$
				mb.setMessage(Messages.getString("ImportProfilesPage.ProfilesFileNotFound")); //$NON-NLS-1$
				mb.open();
			}
		}
		catch (Exception e) {
			ExceptionHandler.reportException(e);
		}
		return false;
	}

	@Override
	public boolean cancel() {
		return true;
	}
}
