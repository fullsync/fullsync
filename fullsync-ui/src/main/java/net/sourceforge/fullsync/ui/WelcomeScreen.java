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

import javax.inject.Inject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.fullsync.Preferences;
import net.sourceforge.fullsync.Util;

class WelcomeScreen extends Dialog {
	private final Preferences preferences;
	private final ImageRepository imageRepository;
	private final BackgroundExecutor backgroundExecutor;

	@Inject
	public WelcomeScreen(Shell shell, Preferences preferences, ImageRepository imageRepository, BackgroundExecutor backgroundExecutor) {
		super(shell);
		this.preferences = preferences;
		this.imageRepository = imageRepository;
		this.backgroundExecutor = backgroundExecutor;
	}

	void show() {
		final var dialogShell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		var title = Messages.getString("WelcomeScreen.WelcomeMessage", Util.getFullSyncVersion()); //$NON-NLS-1$

		var dialogShellLayout = new GridLayout();
		dialogShell.setLayout(dialogShellLayout);
		dialogShell.setText(title);

		Composite logoComposite = new LogoHeaderComposite(dialogShell, SWT.FILL, imageRepository);
		var logoCompositeLData = new GridData();
		logoCompositeLData.grabExcessHorizontalSpace = true;
		logoCompositeLData.horizontalAlignment = GridData.FILL;
		logoCompositeLData.widthHint = 600;
		logoComposite.setLayoutData(logoCompositeLData);

		// version label
		var labelVersion = new Label(dialogShell, SWT.FILL);
		labelVersion.setText(title);
		var lvd = new GridData(SWT.FILL);
		lvd.grabExcessHorizontalSpace = true;
		labelVersion.setLayoutData(lvd);

		// releases label
		var labelReleases = new Label(dialogShell, SWT.FILL);
		labelReleases.setText(Messages.getString("WelcomeScreen.ReadBelow")); //$NON-NLS-1$
		var lrel = new GridData(SWT.FILL);
		lrel.grabExcessHorizontalSpace = true;
		labelReleases.setLayoutData(lrel);

		var changelogText = new ChangeLogBox(dialogShell, preferences.getLastVersion(), backgroundExecutor);
		var changelogTextLData = new GridData(GridData.FILL_BOTH);
		changelogTextLData.heightHint = 300;
		changelogText.setLayoutData(changelogTextLData);

		// ok button
		var buttonOk = new Button(dialogShell, SWT.PUSH | SWT.CENTER);
		buttonOk.setText(Messages.getString("WelcomeScreen.Ok")); //$NON-NLS-1$
		var buttonOkLData = new GridData();
		buttonOk.addListener(SWT.Selection, e -> dialogShell.close());
		buttonOkLData.horizontalAlignment = GridData.CENTER;
		buttonOkLData.heightHint = UISettings.BUTTON_HEIGHT;
		buttonOkLData.widthHint = UISettings.BUTTON_WIDTH;
		buttonOkLData.grabExcessHorizontalSpace = true;
		buttonOk.setLayoutData(buttonOkLData);

		// layout the dialog and show it
		dialogShell.pack();
		dialogShell.layout(true);
		dialogShell.open();
		var display = dialogShell.getDisplay();

		while (!dialogShell.isDisposed()) { // TODO: remove nested event loop
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}
