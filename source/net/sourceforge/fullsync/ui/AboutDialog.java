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

import java.util.Timer;
import java.util.TimerTask;

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.Util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

class AboutDialog extends Dialog implements DisposeListener {

	private Shell dialogShell;
	private Label labelPicture;
	private Composite compositeBottom;
	private Label labelThanks;
	private Composite composite1;
	private Button buttonOk;
	private Link websiteLink;

	private static final long delay = 750;

	private int stIndex = 0;
	private Timer stTimer;
	private Link twitterLink;

	AboutDialog(Shell parent, int style) {
		super(parent, style);
		try {
			dialogShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
			dialogShell.setBackground(UISettings.COLOR_WHITE);
			dialogShell.setBackgroundMode(SWT.INHERIT_DEFAULT);

			dialogShell.addDisposeListener(this);

			GridLayout dialogShellLayout = new GridLayout();
			dialogShell.setLayout(dialogShellLayout);
			dialogShellLayout.verticalSpacing = 0;
			dialogShellLayout.marginHeight = 0;
			dialogShellLayout.marginWidth = 0;
			dialogShellLayout.horizontalSpacing = 0;
			dialogShell.setText(Messages.getString("AboutDialog.About_FullSync")); //$NON-NLS-1$
			// the fullsync picture
			labelPicture = new Label(dialogShell, SWT.NONE);
			GridData labelPictureLData = new GridData();
			labelPictureLData.grabExcessHorizontalSpace = true;
			labelPictureLData.grabExcessVerticalSpace = true;
			labelPictureLData.horizontalAlignment = SWT.FILL;
			labelPictureLData.verticalAlignment = SWT.FILL;
			labelPicture.setLayoutData(labelPictureLData);
			Image aboutImg = GuiController.getInstance().getImage("About.png"); //$NON-NLS-1$
			Rectangle r = aboutImg.getBounds();
			labelPicture.setSize(r.width, r.height);
			labelPicture.setImage(aboutImg);
			// version label
			String version = Util.getFullSyncVersion();
			Label labelVersion = new Label(dialogShell, SWT.FILL);
			labelVersion.setForeground(UISettings.COLOR_LIGHT_GREY);
			labelVersion.setText(Messages.getString("AboutDialog.Version", version));
			GridData lvd = new GridData(SWT.FILL);
			lvd.grabExcessHorizontalSpace = true;
			lvd.horizontalIndent = 17;
			labelVersion.setLayoutData(lvd);
			// copyright text
			Link copyright = new Link(dialogShell, SWT.FILL);
			copyright.setForeground(UISettings.COLOR_LIGHT_GREY);
			String copyrightText = Util.getResourceAsString("net/sourceforge/fullsync/copyright.txt");
			copyrightText = copyrightText.replaceAll("\\{version\\}", version);
			copyright.setText(copyrightText);
			GridData lcd = new GridData(SWT.FILL);
			lcd.grabExcessHorizontalSpace = true;
			lcd.horizontalIndent = 17;
			copyright.setLayoutData(lcd);
			copyright.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent evt) {
					GuiController.launchProgram(evt.text);
				}
			});
			// separator
			Label labelSeparator1 = new Label(dialogShell, SWT.SEPARATOR | SWT.HORIZONTAL);
			GridData labelSeparatorLData = new GridData();
			labelSeparatorLData.horizontalAlignment = SWT.FILL;
			labelSeparatorLData.grabExcessHorizontalSpace = true;
			labelSeparator1.setLayoutData(labelSeparatorLData);
			// credits background
			composite1 = new Composite(dialogShell, SWT.NONE);
			composite1.setLayout(new FillLayout());
			GridLayout composite1Layout = new GridLayout(1, true);
			GridData composite1LData = new GridData();
			composite1LData.horizontalAlignment = SWT.FILL;
			composite1LData.heightHint = 57;
			composite1.setLayoutData(composite1LData);
			composite1.setLayout(composite1Layout);
			composite1.setBackground(UISettings.COLOR_WHITE);
			// credits
			labelThanks = new Label(composite1, SWT.CENTER);
			GridData labelThanksLData = new GridData();
			labelThanksLData.grabExcessHorizontalSpace = true;
			labelThanksLData.horizontalAlignment = GridData.CENTER;
			labelThanks.setLayoutData(labelThanksLData);
			labelThanks.setBackground(UISettings.COLOR_WHITE);
			labelThanks.setText("\n\n\n");
			labelThanks.setAlignment(SWT.CENTER);
			stTimer = new Timer(false);
			final String[] specialThanks;
			String sp = Util.getResourceAsString("net/sourceforge/fullsync/special-thanks.txt");
			String[] res = sp.split("\n");
			if (null != res) {
				specialThanks = res;
			}
			else {
				specialThanks = new String[] { "", "", "" };
			}
			stTimer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					Display display = Display.getDefault();
					display.syncExec(new Runnable() {
						@Override
						public void run() {
							int firstLine = (stIndex) % specialThanks.length;
							int secondLine = (stIndex + 1) % specialThanks.length;
							int thirdLine = (stIndex + 2) % specialThanks.length;

							labelThanks.setText(specialThanks[firstLine] + '\n' + specialThanks[secondLine] + '\n'
									+ specialThanks[thirdLine]);
							dialogShell.layout(true);
							composite1.layout(true);
							stIndex++;
							stIndex %= specialThanks.length;
						}
					});
				}
			}, delay, delay);
			// separator
			Label labelSeparator2 = new Label(dialogShell, SWT.SEPARATOR | SWT.HORIZONTAL);
			GridData labelSeparator2LData = new GridData();
			labelSeparator2LData.grabExcessHorizontalSpace = true;
			labelSeparator2LData.horizontalAlignment = SWT.FILL;
			labelSeparator2.setLayoutData(labelSeparator2LData);
			// buttons composite
			compositeBottom = new Composite(dialogShell, SWT.NONE);
			GridLayout compositeBottomLayout = new GridLayout();
			GridData compositeBottomLData = new GridData();
			compositeBottomLData.horizontalAlignment = SWT.FILL;
			compositeBottom.setLayoutData(compositeBottomLData);
			compositeBottomLayout.makeColumnsEqualWidth = true;
			compositeBottomLayout.numColumns = 2;
			compositeBottom.setLayout(compositeBottomLayout);
			// website link
			websiteLink = new Link(compositeBottom, SWT.NONE);
			websiteLink.setText("<a>" + Messages.getString("AboutDialog.WebSite") + "</a>"); //$NON-NLS-1$
			GridData websiteLinkLData = new GridData();
			websiteLink.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent evt) {
					GuiController.launchProgram(Util.getWebsiteURL());
				}
			});
			websiteLinkLData.grabExcessHorizontalSpace = false;
			websiteLinkLData.horizontalAlignment = SWT.CENTER;
			websiteLink.setLayoutData(websiteLinkLData);
			// twitter link
			Composite compositeTwitter = new Composite(compositeBottom, SWT.NONE);
			compositeTwitter.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
			compositeTwitter.setLayout(new GridLayout(2, false));
			Image twitterBird = GuiController.getInstance().getImage("twitter_bird_blue_16.png");
			Label twitterBirdLabel = new Label(compositeTwitter, SWT.NONE);
			Rectangle twitterBirdBounds = twitterBird.getBounds();
			twitterBirdLabel.setSize(twitterBirdBounds.width, twitterBirdBounds.height);
			twitterBirdLabel.setImage(twitterBird);
			twitterLink = new Link(compositeTwitter, SWT.NONE);
			twitterLink.setText("<a>@FullSyncNews</a>"); //$NON-NLS-1$
			GridData twitterLinkLData = new GridData();
			twitterLink.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent evt) {
					GuiController.launchProgram(Util.getTwitterURL());
				}
			});
			twitterLink.setLayoutData(twitterLinkLData);

			// ok button
			buttonOk = new Button(dialogShell, SWT.PUSH | SWT.CENTER);
			buttonOk.setText("Ok");
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
			while (!dialogShell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		}
		catch (Exception e) {
			ExceptionHandler.reportException(e);
		}
	}

	@Override
	public final void widgetDisposed(final DisposeEvent e) {
		if (stTimer != null) {
			stTimer.cancel();
		}
	}

}
