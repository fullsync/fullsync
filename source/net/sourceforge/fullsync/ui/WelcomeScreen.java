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

import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import net.sourceforge.fullsync.Util;
import net.sourceforge.fullsync.changelog.ChangeLogEntry;
import net.sourceforge.fullsync.changelog.ChangeLogLoader;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class WelcomeScreen extends Dialog implements AsyncUIUpdate {

	private List<ChangeLogEntry> changelog;
	private StyledText changelogText;

	public WelcomeScreen(Shell parent) {
		super(parent);
		final Shell dialogShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		String title = Messages.getString("WelcomeScreen.WelcomeMessage", Util.getFullSyncVersion()); //$NON-NLS-1$

		GridLayout dialogShellLayout = new GridLayout();
		dialogShell.setLayout(dialogShellLayout);
		dialogShell.setText(title);

		Composite logoComposite = new LogoHeaderComposite(dialogShell, SWT.FILL);
		GridData logoCompositeLData = new GridData();
		logoCompositeLData.grabExcessHorizontalSpace = true;
		logoCompositeLData.horizontalAlignment = GridData.FILL;
		logoComposite.setLayoutData(logoCompositeLData);

		// version label
		Label labelVersion = new Label(dialogShell, SWT.FILL);
		labelVersion.setText(title);
		GridData lvd = new GridData(SWT.FILL);
		lvd.grabExcessHorizontalSpace = true;
		lvd.widthHint = 400;
		labelVersion.setLayoutData(lvd);

		//releases label
		Label labelReleases = new Label(dialogShell, SWT.FILL | SWT.WRAP);
		labelReleases.setText(Messages.getString("WelcomeScreen.ReadBelow")); //$NON-NLS-1$
		GridData lrel = new GridData(SWT.FILL | SWT.WRAP);
		lrel.grabExcessHorizontalSpace = true;
		lrel.widthHint = 400;
		labelReleases.setLayoutData(lrel);

		changelogText = new StyledText(dialogShell, SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL);
		changelogText.setAlwaysShowScrollBars(false);
		GridData changelogTextLData = new GridData(GridData.FILL_BOTH);
		changelogTextLData.heightHint = 200;
		changelogText.setLayoutData(changelogTextLData);

		// ok button
		Button buttonOk = new Button(dialogShell, SWT.PUSH | SWT.CENTER);
		buttonOk.setText(Messages.getString("WelcomeScreen.Ok")); //$NON-NLS-1$
		GridData buttonOkLData = new GridData();
		buttonOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent evt) {
				dialogShell.close();
			}
		});
		buttonOkLData.horizontalAlignment = GridData.CENTER;
		buttonOkLData.heightHint = UISettings.BUTTON_HEIGHT;
		buttonOkLData.widthHint = UISettings.BUTTON_WIDTH;
		buttonOkLData.grabExcessHorizontalSpace = true;
		buttonOk.setLayoutData(buttonOkLData);

		// layout the dialog and show it
		dialogShell.pack();
		dialogShell.layout(true);
		dialogShell.open();
		Display display = dialogShell.getDisplay();

		GuiController.backgroundExec(this);
		while (!dialogShell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	@Override
	public void execute() throws Throwable {
		ChangeLogLoader loader = new ChangeLogLoader();
		changelog = loader.load(Util.getInstalllocation(), ".+\\.html"); //$NON-NLS-1$
	}

	@Override
	public void updateUI(boolean succeeded) {
		if (succeeded) {
			StringWriter sw = new StringWriter();
			DateFormat dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM, Locale.getDefault());
			for(ChangeLogEntry entry : changelog) {
				entry.write("FullSync %s released on %s", " - %s", sw, dateFormat);
			}
			sw.flush();
			changelogText.setText(sw.toString());
		}
		else {
			changelogText.setText("Failed to load Changelogs.");
		}
	}
}
