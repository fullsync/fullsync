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

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.fullsync.FullSync;
import net.sourceforge.fullsync.Preferences;

public class PreferencesPage extends WizardDialog {
	/**
	 * supported language codes.
	 */
	private static String[] languageCodes = { "en", "it", "de", "fr", "es", "ar", "el" };
	/**
	 * supported language names.
	 */
	private static String[] languageNames = { "English", "Italiano", "Deutsch", "Français", "Español", "Arabic", "Ελληνικά" };

	/**
	 * search an element in an array and get the result from another array at the same index.
	 *
	 * @param in
	 *            array to search in
	 * @param result
	 *            array to take the result from
	 * @param key
	 *            key to search
	 * @return the element in the result array on the same index as the key in the in array
	 */
	private static String arraySearch(final String[] in, final String[] result, final String key) {
		int i = 0;
		for (String s : in) {
			if (s.equals(key)) {
				return result[i];
			}
			++i;
		}
		return "";
	}

	/**
	 * map language name to code.
	 *
	 * @param name
	 *            language name
	 * @return language code
	 */
	private static String getLanguageCode(final String name) {
		return arraySearch(languageNames, languageCodes, name);
	}

	/**
	 * map language code to name.
	 *
	 * @param code
	 *            language code
	 * @return language name
	 */
	private static String getLanguageName(final String code) {
		return arraySearch(languageCodes, languageNames, code);
	}

	private Group groupInterface;
	private Button cbConfirmExit;
	private Button cbCloseMinimizesToSystemTray;
	private Button cbMinimizeMinimizesToSystemTray;
	private Combo comboLanguage;
	private Button cbAutostartScheduler;
	private Combo comboProfileList;

	private final FullSync fullsync;

	public PreferencesPage(Shell parent, FullSync _fullsync) {
		super(parent);
		fullsync = _fullsync;
	}

	@Override
	public String getTitle() {
		return Messages.getString("PreferencesPage.Preferences"); //$NON-NLS-1$
	}

	@Override
	public String getCaption() {
		return Messages.getString("PreferencesPage.Preferences"); //$NON-NLS-1$
	}

	@Override
	public String getDescription() {
		return ""; //$NON-NLS-1$
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
		content.setLayout(new GridLayout());
		GridData thisLData = new GridData();
		thisLData.horizontalAlignment = SWT.FILL;
		thisLData.grabExcessHorizontalSpace = true;
		thisLData.grabExcessVerticalSpace = true;
		content.setLayoutData(thisLData);

		groupInterface = new Group(content, SWT.FILL);
		GridLayout generalPreferencesGroupLayout = new GridLayout(2, false);
		GridData generalPreferencesGroupLData = new GridData();
		generalPreferencesGroupLData.grabExcessHorizontalSpace = true;
		generalPreferencesGroupLData.grabExcessVerticalSpace = true;
		generalPreferencesGroupLData.horizontalAlignment = SWT.FILL;
		generalPreferencesGroupLData.verticalAlignment = SWT.FILL;
		groupInterface.setLayoutData(generalPreferencesGroupLData);
		groupInterface.setLayout(generalPreferencesGroupLayout);
		groupInterface.setText(Messages.getString("PreferencesComposite.Interface")); //$NON-NLS-1$

		// confirm exit
		cbConfirmExit = new Button(groupInterface, SWT.CHECK | SWT.LEFT);
		cbConfirmExit.setText(Messages.getString("PreferencesComposite.ConfirmExit")); //$NON-NLS-1$
		GridData askOnClosingCheckBoxLData = new GridData();
		askOnClosingCheckBoxLData.horizontalAlignment = SWT.FILL;
		askOnClosingCheckBoxLData.horizontalSpan = 2;
		askOnClosingCheckBoxLData.grabExcessHorizontalSpace = true;
		cbConfirmExit.setLayoutData(askOnClosingCheckBoxLData);

		// close minimizes to systray
		cbCloseMinimizesToSystemTray = new Button(groupInterface, SWT.CHECK | SWT.LEFT);
		cbCloseMinimizesToSystemTray.setText(Messages.getString("PreferencesComposite.CloseMinimizes")); //$NON-NLS-1$
		GridData closeButtonMinimizesCheckBoxLData = new GridData();
		closeButtonMinimizesCheckBoxLData.horizontalAlignment = SWT.FILL;
		closeButtonMinimizesCheckBoxLData.horizontalSpan = 2;
		cbCloseMinimizesToSystemTray.setLayoutData(closeButtonMinimizesCheckBoxLData);

		// minimize minimizes to systray
		cbMinimizeMinimizesToSystemTray = new Button(groupInterface, SWT.CHECK | SWT.LEFT);
		cbMinimizeMinimizesToSystemTray.setText(Messages.getString("PreferencesComposite.MinimizeMinimizes")); //$NON-NLS-1$
		GridData cbMinimizeMinimizesToSystemTrayLData = new GridData();
		cbMinimizeMinimizesToSystemTrayLData.horizontalAlignment = SWT.FILL;
		cbMinimizeMinimizesToSystemTrayLData.horizontalSpan = 2;
		cbMinimizeMinimizesToSystemTray.setLayoutData(cbMinimizeMinimizesToSystemTrayLData);

		// auto start scheduler
		cbAutostartScheduler = new Button(groupInterface, SWT.CHECK | SWT.LEFT);
		cbAutostartScheduler.setText(Messages.getString("PreferencesComposite.AutostartScheduler")); //$NON-NLS-1$
		GridData cbAutostartSchedulerLData = new GridData();
		cbAutostartSchedulerLData.horizontalAlignment = SWT.FILL;
		cbAutostartSchedulerLData.horizontalSpan = 2;
		cbAutostartScheduler.setLayoutData(cbAutostartSchedulerLData);

		// profile list style
		Label labelProfileListStyle = new Label(groupInterface, SWT.NONE);
		labelProfileListStyle.setText(Messages.getString("PreferencesComposite.ProfileListStyle") + ": "); //$NON-NLS-1$ //$NON-NLS-2$

		comboProfileList = new Combo(groupInterface, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridData comboProfileListLData = new GridData();
		comboProfileListLData.horizontalAlignment = SWT.FILL;
		comboProfileList.setLayoutData(comboProfileListLData);
		comboProfileList.add(Messages.getString("PreferencesComposite.Table")); //$NON-NLS-1$
		comboProfileList.add(Messages.getString("PreferencesComposite.NiceListView")); //$NON-NLS-1$

		// language
		Label labelLanguage = new Label(groupInterface, SWT.NONE);
		labelLanguage.setText(Messages.getString("PreferencesComposite.Language") + ":"); //$NON-NLS-1$ //$NON-NLS-2$

		comboLanguage = new Combo(groupInterface, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridData comboLanguageLData = new GridData();
		comboLanguageLData.horizontalAlignment = SWT.FILL;
		comboLanguage.setLayoutData(comboLanguageLData);
		String[] languages = new String[languageNames.length];
		System.arraycopy(languageNames, 0, languages, 0, languageNames.length);
		Arrays.sort(languages);
		for (String language : languages) {
			comboLanguage.add(language);
		}

		// line below the language combo telling you that a change needs a restart
		new Label(groupInterface, SWT.NONE);
		Label labelNeedsRestart = new Label(groupInterface, SWT.NONE);
		labelNeedsRestart.setText(Messages.getString("PreferencesComposite.NeedsRestart")); //$NON-NLS-1$
		GridData labelNeedsRestartLData = new GridData();
		labelNeedsRestartLData.horizontalAlignment = SWT.FILL;
		labelNeedsRestart.setLayoutData(labelNeedsRestartLData);

		updateComponent();
		content.layout();
	}

	/**
	 * update all controls with the settings from the preferences object.
	 */
	public void updateComponent() {
		Preferences preferences = fullsync.getPreferences();
		cbConfirmExit.setSelection(preferences.confirmExit());
		cbCloseMinimizesToSystemTray.setSelection(preferences.closeMinimizesToSystemTray());
		cbMinimizeMinimizesToSystemTray.setSelection(preferences.minimizeMinimizesToSystemTray());
		comboProfileList.setText(preferences.getProfileListStyle());
		comboLanguage.setText(getLanguageName(preferences.getLanguageCode()));
		cbAutostartScheduler.setSelection(preferences.getAutostartScheduler());
	}

	@Override
	public boolean apply() {
		Preferences preferences = fullsync.getPreferences();
		preferences.setConfirmExit(cbConfirmExit.getSelection());
		preferences.setCloseMinimizesToSystemTray(cbCloseMinimizesToSystemTray.getSelection());
		preferences.setMinimizeMinimizesToSystemTray(cbMinimizeMinimizesToSystemTray.getSelection());
		boolean profileListStyleChanged = !preferences.getProfileListStyle().equals(comboProfileList.getText());
		preferences.setProfileListStyle(comboProfileList.getText());
		preferences.setLanguageCode(getLanguageCode(comboLanguage.getText()));
		preferences.setAutostartScheduler(cbAutostartScheduler.getSelection());

		if (profileListStyleChanged) {
			GuiController.getInstance().getMainWindow().createProfileList();
		}

		preferences.save();
		return true; //FIXME: return false if failed
	}

	@Override
	public boolean cancel() {
		return true;
	}
}
