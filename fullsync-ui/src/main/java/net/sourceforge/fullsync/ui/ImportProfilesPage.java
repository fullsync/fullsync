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

import java.io.File;
import java.io.IOException;

import jakarta.inject.Inject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.ProfileManager;

class ImportProfilesPage extends WizardDialog {
	private static final String[] PROFILE_EXTENSIONS = {
		"profiles.xml", //$NON-NLS-1$
		"*.xml", //$NON-NLS-1$
		"*" //$NON-NLS-1$
	};
	private final ProfileManager profileManager;
	private Composite composite;
	private Text textPath;

	@Inject
	public ImportProfilesPage(Shell shell, ProfileManager profileManager) {
		super(shell);
		this.profileManager = profileManager;
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
	public String getIconName() {
		return null;
	}

	@Override
	public String getImageName() {
		return null;
	}

	@Override
	public void createContent(final Composite content) {
		composite = content;
		content.setLayout(new GridLayout(2, false));
		textPath = new Text(content, SWT.BORDER);
		var textData = new GridData();
		textData.horizontalAlignment = SWT.FILL;
		textData.grabExcessHorizontalSpace = true;
		textData.grabExcessVerticalSpace = true;
		textData.verticalAlignment = SWT.CENTER;
		textPath.setLayoutData(textData);
		var buttonBrowse = new Button(content, SWT.NONE);
		buttonBrowse.setText(Messages.getString("ImportProfilesPage.Browse")); //$NON-NLS-1$
		buttonBrowse.addListener(SWT.Selection, e -> {
			var fd = new FileDialog(content.getShell());
			fd.setFileName("profiles.xml"); //$NON-NLS-1$
			fd.setFilterExtensions(PROFILE_EXTENSIONS);
			fd.setFilterIndex(0);
			fd.setFilterPath(textPath.getText());
			var file = fd.open();
			if (null != file) {
				var f = new File(file);
				try {
					textPath.setText(f.getCanonicalPath());
				}
				catch (IOException ex) {
					textPath.setText(""); //$NON-NLS-1$
					ex.printStackTrace();
				}
			}
		});
	}

	@Override
	public boolean apply() {
		try {
			if (profileManager.loadProfiles(textPath.getText())) {
				return true;
			}
			else {
				var mb = new MessageBox(composite.getShell(), SWT.ICON_WARNING | SWT.OK);
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
