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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.Util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

class AboutDialog extends Dialog implements DisposeListener, AsyncUIUpdate  {
	private static final long delay = 750;
	private int stIndex = 0;
	private Timer stTimer;
	private Color headerBackgroundColor;
	private Combo componentCombo;
	StyledText licenseText;
	private List<String> licenseNames;
	private List<String> licenseTexts;

	AboutDialog(final Shell parent, int style) {
		super(parent, style);
		try {
			final Shell dialogShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
			dialogShell.addDisposeListener(this);

			GridLayout dialogShellLayout = new GridLayout(1, true);
			dialogShellLayout.marginTop = 0;
			dialogShellLayout.marginHeight = 0;
			dialogShellLayout.marginWidth = 0;
			dialogShellLayout.marginBottom = 5;
			dialogShellLayout.verticalSpacing = 5;
			dialogShellLayout.horizontalSpacing = 5;
			dialogShell.setLayout(dialogShellLayout);
			dialogShell.setText(Messages.getString("AboutDialog.About_FullSync")); //$NON-NLS-1$

			// the FullSync header
			Composite logoComposite = new Composite(dialogShell, SWT.FILL);
			GridLayout headerLayout = new GridLayout(2, false);
			headerLayout.marginRight = 14; // 14px padding as reserved in the About.png
			logoComposite.setLayout(headerLayout);
			GridData logoCompositeLData = new GridData();
			logoCompositeLData.grabExcessHorizontalSpace = true;
			logoCompositeLData.horizontalAlignment = GridData.FILL;
			logoComposite.setLayoutData(logoCompositeLData);
			headerBackgroundColor = new Color(dialogShell.getDisplay(), 192, 204, 214);
			logoComposite.setBackground(headerBackgroundColor);
			logoComposite.setBackgroundMode(SWT.INHERIT_DEFAULT);

			Label labelPicture = new Label(logoComposite, SWT.NONE);
			GridData labelPictureLData = new GridData();
			labelPictureLData.horizontalAlignment = SWT.FILL;
			labelPicture.setLayoutData(labelPictureLData);
			Image aboutImg = GuiController.getInstance().getImage("About.png"); //$NON-NLS-1$
			Rectangle r = aboutImg.getBounds();
			labelPicture.setSize(r.width, r.height);
			labelPicture.setImage(aboutImg);

			Label labelLogo = new Label(logoComposite, SWT.TRANSPARENT);
			GridData labelLogoLData = new GridData();
			labelLogoLData.grabExcessHorizontalSpace = true;
			labelLogoLData.horizontalAlignment = GridData.END;
			labelLogoLData.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;
			labelLogo.setLayoutData(labelLogoLData);
			Image logoImg = GuiController.getInstance().getImage("fullsync72.png"); //$NON-NLS-1$
			labelLogo.setImage(logoImg);

			final TabFolder tabs = new TabFolder(dialogShell, SWT.FILL);
			GridData tabLData = new GridData(GridData.FILL_BOTH);
			tabs.setLayout(new FillLayout());

			final TabItem tabGeneral = new TabItem(tabs, SWT.NONE);
			tabGeneral.setText(Messages.getString("AboutDialog.Tab_About")); //$NON-NLS-1$
			tabGeneral.setControl(initAboutTab(tabs));
			dialogShell.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					// fix the size of the General tab to show all child elements
					Point generalTabSize = tabGeneral.getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT);
					Rectangle tabsClientSize = tabs.getClientArea();
					int width = generalTabSize.x - tabsClientSize.width;
					int height = generalTabSize.y - tabsClientSize.height;
					Point dlgSize = dialogShell.getSize();
					width = dlgSize.x + width;
					height = dlgSize.y + height;
					dialogShell.setSize(width, height);
					Rectangle parentBounds = parent.getBounds();
					dialogShell.setLocation(parentBounds.x + (parentBounds.width / 2) - (width / 2), parentBounds.y + (parentBounds.height / 2) - (height / 2));
				}
			});

			TabItem tabLicenses = new TabItem(tabs, SWT.NONE);
			tabLicenses.setText(Messages.getString("AboutDialog.Tab_Licenses")); //$NON-NLS-1$
			tabLicenses.setControl(initLicensesTab(tabs));

			tabs.setLayoutData(tabLData);
			// ok button
			Button buttonOk = new Button(dialogShell, SWT.PUSH | SWT.CENTER);
			buttonOk.setText(Messages.getString("AboutDialog.Ok")); //$NON-NLS-1$
			buttonOk.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent evt) {
					dialogShell.close();
				}
			});
			GridData buttonOkLData = new GridData();
			buttonOkLData.horizontalAlignment = GridData.CENTER;
			buttonOkLData.heightHint = UISettings.BUTTON_HEIGHT;
			buttonOkLData.widthHint = UISettings.BUTTON_WIDTH;
			buttonOkLData.grabExcessHorizontalSpace = true;
			buttonOk.setLayoutData(buttonOkLData);

			// layout the dialog and show it
			dialogShell.layout(true);

			dialogShell.open();
			buttonOk.setFocus();
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
		headerBackgroundColor.dispose();
	}

	private Composite initAboutTab(Composite parent) {
		final Composite tab = new Composite(parent, SWT.FILL);
		tab.setLayout(new GridLayout(1, true));

		// version label
		String version = Util.getFullSyncVersion();
		Label labelVersion = new Label(tab, SWT.FILL);
		labelVersion.setText(Messages.getString("AboutDialog.Version", version)); //$NON-NLS-1$
		GridData lvd = new GridData(SWT.FILL);
		lvd.grabExcessHorizontalSpace = true;
		labelVersion.setLayoutData(lvd);
		// copyright text
		Link copyright = new Link(tab, SWT.FILL);
		String copyrightText = Util.getResourceAsString("net/sourceforge/fullsync/copyright.txt"); //$NON-NLS-1$
		copyrightText = copyrightText.replaceAll("\\{version\\}", version); //$NON-NLS-1$
		copyright.setText(copyrightText);
		GridData lcd = new GridData(SWT.FILL);
		lcd.grabExcessHorizontalSpace = true;
		copyright.setLayoutData(lcd);
		copyright.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent evt) {
				GuiController.launchProgram(evt.text);
			}
		});
		// separator
		Label labelSeparator1 = new Label(tab, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData labelSeparatorLData = new GridData();
		labelSeparatorLData.horizontalAlignment = SWT.FILL;
		labelSeparatorLData.grabExcessHorizontalSpace = true;
		labelSeparator1.setLayoutData(labelSeparatorLData);
		// credits
		final Label labelThanks = new Label(tab, SWT.CENTER);
		GridData labelThanksLData = new GridData();
		labelThanksLData.grabExcessHorizontalSpace = true;
		labelThanksLData.horizontalAlignment = GridData.CENTER;
		labelThanks.setLayoutData(labelThanksLData);
		labelThanks.setText("\n\n\n"); //$NON-NLS-1$
		labelThanks.setAlignment(SWT.CENTER);
		stTimer = new Timer(false);
		final String[] specialThanks;
		String sp = Util.getResourceAsString("net/sourceforge/fullsync/special-thanks.txt"); //$NON-NLS-1$
		String[] res = sp.split("\n"); //$NON-NLS-1$
		if (null != res) {
			specialThanks = res;
		}
		else {
			sp = ""; //$NON-NLS-1$
			specialThanks = new String[] { sp, sp, sp };
		}
		stTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				Display display = Display.getDefault();
				display.syncExec(new Runnable() {
					@Override
					public void run() {
						if (!labelThanks.isDisposed()) {
							int firstLine = (stIndex) % specialThanks.length;
							int secondLine = (stIndex + 1) % specialThanks.length;
							int thirdLine = (stIndex + 2) % specialThanks.length;

							labelThanks.setText(specialThanks[firstLine] + '\n' + specialThanks[secondLine] + '\n'
									+ specialThanks[thirdLine]);
							labelThanks.pack(true);
							tab.layout(new Control[]{ labelThanks });
							stIndex++;
							stIndex %= specialThanks.length;
						}
					}
				});
			}
		}, delay, delay);
		// separator
		Label labelSeparator2 = new Label(tab, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData labelSeparator2LData = new GridData();
		labelSeparator2LData.grabExcessHorizontalSpace = true;
		labelSeparator2LData.horizontalAlignment = SWT.FILL;
		labelSeparator2.setLayoutData(labelSeparator2LData);
		// buttons composite
		Composite compositeBottom = new Composite(tab, SWT.NONE);
		GridLayout compositeBottomLayout = new GridLayout();
		GridData compositeBottomLData = new GridData();
		compositeBottomLData.horizontalAlignment = SWT.FILL;
		compositeBottom.setLayoutData(compositeBottomLData);
		compositeBottomLayout.makeColumnsEqualWidth = true;
		compositeBottomLayout.numColumns = 2;
		compositeBottom.setLayout(compositeBottomLayout);
		// website link
		Link websiteLink = new Link(compositeBottom, SWT.NONE);
		websiteLink.setText(String.format("<a>%s</a>", Messages.getString("AboutDialog.WebSite"))); //$NON-NLS-1$ //$NON-NLS-2$
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
		Image twitterBird = GuiController.getInstance().getImage("twitter_bird_blue_16.png"); //$NON-NLS-1$
		Label twitterBirdLabel = new Label(compositeTwitter, SWT.NONE);
		Rectangle twitterBirdBounds = twitterBird.getBounds();
		twitterBirdLabel.setSize(twitterBirdBounds.width, twitterBirdBounds.height);
		twitterBirdLabel.setImage(twitterBird);
		Link twitterLink = new Link(compositeTwitter, SWT.NONE);
		twitterLink.setText("<a>@FullSyncNews</a>"); //$NON-NLS-1$
		GridData twitterLinkLData = new GridData();
		twitterLink.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent evt) {
				GuiController.launchProgram(Util.getTwitterURL());
			}
		});
		twitterLink.setLayoutData(twitterLinkLData);
		tab.pack();
		tab.layout();
		return tab;
	}

	private Composite initLicensesTab(Composite parent) throws IOException {
		Composite tab = new Composite(parent, SWT.FILL);
		tab.setLayout(new GridLayout(2, false));

		Label component = new Label(tab, SWT.NONE);
		component.setText(Messages.getString("AboutDialog.Component")); //$NON-NLS-1$

		componentCombo = new Combo(tab, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridData componentComboLData = new GridData(SWT.FILL, SWT.NONE, true, false);
		componentCombo.setLayoutData(componentComboLData);

		licenseText = new StyledText(tab, SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL);
		licenseText.setAlwaysShowScrollBars(false);
		GridData licenseTextLData = new GridData(GridData.FILL_BOTH);
		licenseTextLData.horizontalSpan = 2;
		licenseText.setLayoutData(licenseTextLData);

		componentCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				if (null != licenseTexts) {
					int index = componentCombo.getSelectionIndex();
					licenseText.setText(licenseTexts.get(index));
				}
			}
		});
		GuiController.backgroundExec(this);
		return tab;
	}

	private static class LicenseEntry {
		public String name;
		public String license;
	}

	@Override
	public void execute() {
		int numLicenses = 0;
		List<LicenseEntry> licenses = new ArrayList<LicenseEntry>();
		BufferedReader rdr = new BufferedReader(new InputStreamReader(AboutDialog.class.getResourceAsStream("/net/sourceforge/fullsync/licenses/"))); //$NON-NLS-1$
		String line;
		try {
			while ((line = rdr.readLine()) != null) {
				if (line.endsWith(".txt")) { //$NON-NLS-1$
					++numLicenses;
					LicenseEntry entry = new LicenseEntry();
					entry.name = line.substring(0, line.length() - 4);
					entry.license = Util.getResourceAsString("net/sourceforge/fullsync/licenses/" + line); //$NON-NLS-1$
					licenses.add(entry);
				}
			}
		}
		catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			rdr.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		licenses.sort(new Comparator<LicenseEntry>() {
			@Override
			public int compare(LicenseEntry o1, LicenseEntry o2) {
				return o1.name.compareTo(o2.name);
			}
		});
		licenseNames = new ArrayList<String>(numLicenses);
		licenseTexts = new ArrayList<String>(numLicenses);
		for (LicenseEntry lic : licenses) {
			licenseNames.add(lic.name);
			licenseTexts.add(lic.license);
		}
	}

	@Override
	public void updateUI(boolean succeeded) {
		int idx = 0, fsIdx = 0;
		for (String licenseName : licenseNames) {
			componentCombo.add(licenseName);
			if ("FullSync".equals(licenseName)) { //$NON-NLS-1$
				fsIdx = idx;
			}
			++idx;
		}
		licenseNames = null;
		componentCombo.select(fsIdx);
		licenseText.setText(licenseTexts.get(fsIdx));
	}
}
