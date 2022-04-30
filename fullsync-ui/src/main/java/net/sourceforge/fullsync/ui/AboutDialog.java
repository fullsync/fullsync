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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.Preferences;
import net.sourceforge.fullsync.Util;

class AboutDialog {
	private static final String FULLSYNC_LICENSES_DIRECTORY = "net/sourceforge/fullsync/licenses/"; //$NON-NLS-1$
	private static final long ANIMATION_DELAY = 750;
	private final Shell shell;
	private final Preferences preferences;
	private final ImageRepository imageRepository;
	private final BackgroundExecutor backgroundExecutor;
	private final List<LicenseEntry> licenses = new ArrayList<>();
	private int stIndex;
	private Combo componentCombo;
	private StyledText licenseText;

	@Inject
	AboutDialog(Shell shell, Preferences preferences, ImageRepository imageRepository, BackgroundExecutor backgroundExecutor) {
		this.shell = shell;
		this.preferences = preferences;
		this.imageRepository = imageRepository;
		this.backgroundExecutor = backgroundExecutor;
	}

	void show() {
		try {
			final var dialogShell = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);

			var dialogShellLayout = new GridLayout(1, true);
			dialogShellLayout.marginTop = 0;
			dialogShellLayout.marginHeight = 0;
			dialogShellLayout.marginWidth = 0;
			dialogShellLayout.marginBottom = 5;
			dialogShellLayout.verticalSpacing = 5;
			dialogShellLayout.horizontalSpacing = 5;
			dialogShell.setLayout(dialogShellLayout);
			dialogShell.setText(Messages.getString("AboutDialog.About_FullSync")); //$NON-NLS-1$

			Composite logoComposite = new LogoHeaderComposite(dialogShell, SWT.FILL, imageRepository);
			var logoCompositeLData = new GridData();
			logoCompositeLData.grabExcessHorizontalSpace = true;
			logoCompositeLData.horizontalAlignment = GridData.FILL;
			logoComposite.setLayoutData(logoCompositeLData);

			final var tabs = new TabFolder(dialogShell, SWT.FILL);
			var tabLData = new GridData(GridData.FILL_BOTH);
			tabs.setLayoutData(tabLData);
			tabs.setLayout(new FillLayout());

			final var tabGeneral = new TabItem(tabs, SWT.NONE);
			tabGeneral.setText(Messages.getString("AboutDialog.Tab_About")); //$NON-NLS-1$
			final var stTimer = new Timer(false);
			dialogShell.addDisposeListener(e -> stTimer.cancel());
			tabGeneral.setControl(initAboutTab(tabs, stTimer));
			dialogShell.getDisplay().asyncExec(() -> {
				// fix the size of the General tab to show all child elements
				var generalTabSize = tabGeneral.getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT);
				var tabsClientSize = tabs.getClientArea();
				var width = generalTabSize.x - tabsClientSize.width;
				var height = generalTabSize.y - tabsClientSize.height;
				var dlgSize = dialogShell.getSize();
				width = dlgSize.x + width;
				height = dlgSize.y + height;
				dialogShell.setSize(width, height);
				var parentBounds = shell.getBounds();
				var dlgX = (parentBounds.x + (parentBounds.width / 2)) - (width / 2);
				var dlgY = (parentBounds.y + (parentBounds.height / 2)) - (height / 2);
				dialogShell.setLocation(dlgX, dlgY);
			});

			var tabLicenses = new TabItem(tabs, SWT.NONE);
			tabLicenses.setText(Messages.getString("AboutDialog.Tab_Licenses")); //$NON-NLS-1$
			tabLicenses.setControl(initLicensesTab(tabs));

			var tabChangelog = new TabItem(tabs, SWT.NONE);
			tabChangelog.setText(Messages.getString("AboutDialog.Tab_Changelog")); //$NON-NLS-1$
			tabChangelog.setControl(initChangelogTab(tabs));

			var buttonOk = new Button(dialogShell, SWT.PUSH | SWT.CENTER);
			buttonOk.setText(Messages.getString("AboutDialog.Ok")); //$NON-NLS-1$
			buttonOk.addListener(SWT.Selection, e -> dialogShell.close());
			var buttonOkLData = new GridData();
			buttonOkLData.horizontalAlignment = GridData.CENTER;
			buttonOkLData.heightHint = UISettings.BUTTON_HEIGHT;
			buttonOkLData.widthHint = UISettings.BUTTON_WIDTH;
			buttonOkLData.grabExcessHorizontalSpace = true;
			buttonOk.setLayoutData(buttonOkLData);

			// layout the dialog and show it
			dialogShell.layout(true);

			dialogShell.open();
			ShellStateHandler.apply(preferences, dialogShell, AboutDialog.class);
			buttonOk.setFocus();
			var display = dialogShell.getDisplay();
			while (!dialogShell.isDisposed()) { // TODO: remove nested event loop
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		}
		catch (Exception e) {
			ExceptionHandler.reportException(e);
		}
	}

	private Control initChangelogTab(Composite parent) {
		final var tab = new Composite(parent, SWT.FILL);
		tab.setLayout(new GridLayout(1, true));
		var changeLogBox = new ChangeLogBox(tab, "", backgroundExecutor); //$NON-NLS-1$
		var changelogBoxLData = new GridData(GridData.FILL_BOTH);
		changelogBoxLData.heightHint = 300;
		changeLogBox.setLayoutData(changelogBoxLData);
		return tab;
	}

	private Composite initAboutTab(Composite parent, Timer stTimer) {
		final var tab = new Composite(parent, SWT.FILL);
		tab.setLayout(new GridLayout(1, true));

		// version label
		var version = Util.getFullSyncVersion();
		var labelVersion = new Label(tab, SWT.FILL);
		labelVersion.setText(Messages.getString("AboutDialog.Version", version)); //$NON-NLS-1$
		var lvd = new GridData(SWT.FILL);
		lvd.grabExcessHorizontalSpace = true;
		labelVersion.setLayoutData(lvd);
		// copyright text
		var copyright = new Link(tab, SWT.FILL);
		var copyrightText = Util.getResourceAsString("net/sourceforge/fullsync/copyright.txt"); //$NON-NLS-1$
		copyrightText = copyrightText.replaceAll("\\{version\\}", version); //$NON-NLS-1$
		copyright.setText(copyrightText);
		var lcd = new GridData(SWT.FILL);
		lcd.grabExcessHorizontalSpace = true;
		copyright.setLayoutData(lcd);
		copyright.addListener(SWT.Selection, e -> GuiController.launchProgram(e.text));
		// separator
		var labelSeparator1 = new Label(tab, SWT.SEPARATOR | SWT.HORIZONTAL);
		var labelSeparatorLData = new GridData();
		labelSeparatorLData.horizontalAlignment = SWT.FILL;
		labelSeparatorLData.grabExcessHorizontalSpace = true;
		labelSeparator1.setLayoutData(labelSeparatorLData);
		// credits
		final var labelThanks = new Label(tab, SWT.CENTER);
		var labelThanksLData = new GridData();
		labelThanksLData.grabExcessHorizontalSpace = true;
		labelThanksLData.horizontalAlignment = GridData.CENTER;
		labelThanks.setLayoutData(labelThanksLData);
		labelThanks.setText("\n\n\n"); //$NON-NLS-1$
		labelThanks.setAlignment(SWT.CENTER);
		final String[] specialThanks;
		var sp = Util.getResourceAsString("net/sourceforge/fullsync/special-thanks.txt"); //$NON-NLS-1$
		var res = sp.split("\n"); //$NON-NLS-1$
		if (null != res) {
			specialThanks = res;
		}
		else {
			sp = ""; //$NON-NLS-1$
			specialThanks = new String[] {
				sp,
				sp,
				sp
			};
		}
		stTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				var display = Display.getDefault();
				display.syncExec(() -> {
					if (!labelThanks.isDisposed()) {
						var firstLine = stIndex % specialThanks.length;
						var secondLine = (stIndex + 1) % specialThanks.length;
						var thirdLine = (stIndex + 2) % specialThanks.length;

						labelThanks.setText(specialThanks[firstLine] + '\n' + specialThanks[secondLine] + '\n' + specialThanks[thirdLine]);
						labelThanks.pack(true);
						Control[] changed = {
							labelThanks
						};
						tab.layout(changed);
						stIndex = (stIndex + 1) % specialThanks.length;
					}
				});
			}
		}, ANIMATION_DELAY, ANIMATION_DELAY);
		// separator
		var labelSeparator2 = new Label(tab, SWT.SEPARATOR | SWT.HORIZONTAL);
		var labelSeparator2LData = new GridData();
		labelSeparator2LData.grabExcessHorizontalSpace = true;
		labelSeparator2LData.horizontalAlignment = SWT.FILL;
		labelSeparator2.setLayoutData(labelSeparator2LData);
		// buttons composite
		var compositeBottom = new Composite(tab, SWT.NONE);
		var compositeBottomLayout = new GridLayout();
		var compositeBottomLData = new GridData();
		compositeBottomLData.horizontalAlignment = SWT.FILL;
		compositeBottom.setLayoutData(compositeBottomLData);
		compositeBottomLayout.makeColumnsEqualWidth = true;
		compositeBottomLayout.numColumns = 2;
		compositeBottom.setLayout(compositeBottomLayout);
		// website link
		var websiteLink = new Link(compositeBottom, SWT.NONE);
		websiteLink.setText(String.format("<a>%s</a>", Messages.getString("AboutDialog.WebSite"))); //$NON-NLS-1$ //$NON-NLS-2$
		var websiteLinkLData = new GridData();
		websiteLink.addListener(SWT.Selection, e -> GuiController.launchProgram(GuiController.getWebsiteURL()));
		websiteLinkLData.grabExcessHorizontalSpace = false;
		websiteLinkLData.horizontalAlignment = SWT.CENTER;
		websiteLink.setLayoutData(websiteLinkLData);
		// twitter link
		var compositeTwitter = new Composite(compositeBottom, SWT.NONE);
		compositeTwitter.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		compositeTwitter.setLayout(new GridLayout(2, false));
		var twitterBird = imageRepository.getImage("twitter_bird_blue_16.png"); //$NON-NLS-1$
		var twitterBirdLabel = new Label(compositeTwitter, SWT.NONE);
		var twitterBirdBounds = twitterBird.getBounds();
		twitterBirdLabel.setSize(twitterBirdBounds.width, twitterBirdBounds.height);
		twitterBirdLabel.setImage(twitterBird);
		var twitterLink = new Link(compositeTwitter, SWT.NONE);
		twitterLink.setText("<a>@FullSyncNews</a>"); //$NON-NLS-1$
		var twitterLinkLData = new GridData();
		twitterLink.addListener(SWT.Selection, e -> GuiController.launchProgram(GuiController.getTwitterURL()));
		twitterLink.setLayoutData(twitterLinkLData);
		tab.pack();
		tab.layout();
		return tab;
	}

	private Composite initLicensesTab(Composite parent) {
		var tab = new Composite(parent, SWT.FILL);
		tab.setLayout(new GridLayout(2, false));

		var component = new Label(tab, SWT.NONE);
		component.setText(Messages.getString("AboutDialog.Component")); //$NON-NLS-1$

		componentCombo = new Combo(tab, SWT.DROP_DOWN | SWT.READ_ONLY);
		var componentComboLData = new GridData(SWT.FILL, SWT.NONE, true, false);
		componentCombo.setLayoutData(componentComboLData);

		licenseText = new StyledText(tab, SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL);
		licenseText.setAlwaysShowScrollBars(false);
		var licenseTextLData = new GridData(GridData.FILL_BOTH);
		licenseTextLData.horizontalSpan = 2;
		licenseText.setLayoutData(licenseTextLData);

		componentCombo.addModifyListener(e -> {
			var index = componentCombo.getSelectionIndex();
			licenseText.setText(licenses.get(index).license);
		});
		backgroundExecutor.runAsync(this::loadLicenses, this::updateLicenses, this::loadLicensesFailed);

		return tab;
	}

	private record LicenseEntry(String name, String license) {
	}

	private List<LicenseEntry> loadLicenses() throws Exception {
		return Util.loadDirectoryFromClasspath(FULLSYNC_LICENSES_DIRECTORY)
			.stream()
			.filter(name -> name.endsWith(".txt")) //$NON-NLS-1$
			.map(name -> {
				var n = name.substring(0, name.length() - 4);
				var license = Util.getResourceAsString(FULLSYNC_LICENSES_DIRECTORY + name);
				return new LicenseEntry(n, license);
			})
			.sorted((o1, o2) -> o1.name.compareToIgnoreCase(o2.name))
			.collect(Collectors.toList());
	}

	private void updateLicenses(List<LicenseEntry> licenseList) {
		var idx = 0;
		var fsIdx = 0;
		licenses.clear();
		licenses.addAll(licenseList);
		for (var license : licenses) {
			componentCombo.add(license.name);
			if ("FullSync".equals(license.name)) { //$NON-NLS-1$
				fsIdx = idx;
			}
			++idx;
		}
		componentCombo.select(fsIdx);
		licenseText.setText(licenses.get(fsIdx).license);
	}

	private void loadLicensesFailed(Exception ex) {
		if (!shell.isDisposed()) {
			ExceptionHandler.reportException(ex);
		}
	}
}
