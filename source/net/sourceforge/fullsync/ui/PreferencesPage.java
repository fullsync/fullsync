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

import java.rmi.RemoteException;
import java.util.Arrays;

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.Preferences;
import net.sourceforge.fullsync.remote.RemoteController;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

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
	private Text textPassword;
	private Label labelPassword;
	private Group groupRemoteConnection;
	private Text textListeningPort;
	private Label labelPort;
	private Button cbListenForIncomming;
	private Combo comboProfileList;
	// private Button cbEnableSystemTray;

	private Preferences preferences;

	public PreferencesPage(Shell parent, Preferences preferences) {
		super(parent);
		this.preferences = preferences;
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

		// system tray enabled
//		cbEnableSystemTray = new Button(groupInterface, SWT.CHECK | SWT.LEFT);
//		cbEnableSystemTray.setText("Enable System Tray Icon");
//		GridData cbEnableSystemTrayLData = new GridData();
//		cbEnableSystemTrayLData.horizontalAlignment = SWT.FILL;
//		cbEnableSystemTrayLData.horizontalSpan = 2;
//		cbEnableSystemTray.setLayoutData(cbEnableSystemTrayLData);

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
		GridData labelNeedsRestartLData =  new GridData();
		labelNeedsRestartLData.horizontalAlignment = SWT.FILL;
		labelNeedsRestart.setLayoutData(labelNeedsRestartLData);


		groupRemoteConnection = new Group(content, SWT.NONE);
		GridLayout groupRemoteConnectionLayout = new GridLayout();
		GridData groupRemoteConnectionLData = new GridData();
		groupRemoteConnectionLData.horizontalAlignment = SWT.FILL;
		groupRemoteConnectionLData.grabExcessVerticalSpace = true;
		groupRemoteConnectionLData.verticalAlignment = SWT.FILL;
		groupRemoteConnection.setLayoutData(groupRemoteConnectionLData);
		groupRemoteConnectionLayout.numColumns = 2;
		groupRemoteConnection.setLayout(groupRemoteConnectionLayout);
		groupRemoteConnection.setText(Messages.getString("PreferencesComposite.RemoteConnection")); //$NON-NLS-1$

		// enable remote connections
		cbListenForIncomming = new Button(groupRemoteConnection, SWT.CHECK | SWT.LEFT);
		cbListenForIncomming.setText(Messages.getString("PreferencesComposite.EnableRemoteConnections")); //$NON-NLS-1$
		GridData cbListenForIncommingLData = new GridData();
		cbListenForIncomming.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent evt) {
				updateRemoteConnectionGroup();
			}
		});
		cbListenForIncommingLData.horizontalSpan = 2;
		cbListenForIncomming.setLayoutData(cbListenForIncommingLData);

		// remote listen port
		labelPort = new Label(groupRemoteConnection, SWT.NONE);
		labelPort.setText(Messages.getString("PreferencesComposite.IncomingPort") + ":"); //$NON-NLS-1$ //$NON-NLS-2$
		labelPort.setEnabled(false);

		textListeningPort = new Text(groupRemoteConnection, SWT.BORDER);
		textListeningPort.setText("10000"); //$NON-NLS-1$
		GridData textListeningPortLData = new GridData();
		textListeningPortLData.horizontalAlignment = SWT.FILL;
		textListeningPortLData.grabExcessHorizontalSpace = true;
		textListeningPort.setLayoutData(textListeningPortLData);
		textListeningPort.setEnabled(false);

		// remote password
		labelPassword = new Label(groupRemoteConnection, SWT.NONE);
		labelPassword.setText(Messages.getString("PreferencesComposite.IncomingPassword") + ":"); //$NON-NLS-1$ //$NON-NLS-2$
		labelPassword.setEnabled(false);

		textPassword = new Text(groupRemoteConnection, SWT.BORDER);
		GridData textPasswordLData = new GridData();
		textPassword.setEnabled(false);
		textPasswordLData.horizontalAlignment = SWT.FILL;
		textPasswordLData.grabExcessHorizontalSpace = true;
		textPassword.setLayoutData(textPasswordLData);

		updateComponent();
		content.layout();
	}

	/**
	 * update all controls with the settings from the preferences object.
	 */
	public void updateComponent() {
		textPassword.setEchoChar('*');

		cbConfirmExit.setSelection(preferences.confirmExit());
		cbCloseMinimizesToSystemTray.setSelection(preferences.closeMinimizesToSystemTray());
		cbMinimizeMinimizesToSystemTray.setSelection(preferences.minimizeMinimizesToSystemTray());
		// cbEnableSystemTray.setSelection(preferences.systemTrayEnabled());
		comboProfileList.setText(preferences.getProfileListStyle());
		comboLanguage.setText(getLanguageName(preferences.getLanguageCode()));
		cbListenForIncomming.setSelection(preferences.listeningForRemoteConnections());
		textListeningPort.setText(String.valueOf(preferences.getRemoteConnectionsPort()));
		textPassword.setText(preferences.getRemoteConnectionsPassword());
		cbAutostartScheduler.setSelection(preferences.getAutostartScheduler());
		updateRemoteConnectionGroup();
	}

	/**
	 * enable / disable the controls for remote connections according to the checkbox.
	 */
	private void updateRemoteConnectionGroup() {
		if (cbListenForIncomming.getSelection()) {
			labelPort.setEnabled(true);
			textListeningPort.setEnabled(true);
			labelPassword.setEnabled(true);
			textPassword.setEnabled(true);
		}
		else {
			labelPort.setEnabled(false);
			textListeningPort.setEnabled(false);
			labelPassword.setEnabled(false);
			textPassword.setEnabled(false);
		}

	}

	@Override
	public boolean apply() {
		preferences.setConfirmExit(cbConfirmExit.getSelection());
		preferences.setCloseMinimizesToSystemTray(cbCloseMinimizesToSystemTray.getSelection());
		preferences.setMinimizeMinimizesToSystemTray(cbMinimizeMinimizesToSystemTray.getSelection());
		// preferences.setSystemTrayEnabled(cbEnableSystemTray.getSelection());
		boolean profileListStyleChanged = (!preferences.getProfileListStyle().equals(comboProfileList.getText()));
		preferences.setProfileListStyle(comboProfileList.getText());
		preferences.setLanguageCode(getLanguageCode(comboLanguage.getText()));
		preferences.setAutostartScheduler(cbAutostartScheduler.getSelection());

		if (profileListStyleChanged) {
			GuiController.getInstance().getMainWindow().createProfileList();
		}

		boolean listenForIncoming = cbListenForIncomming.getSelection();
		preferences.setListeningForRemoteConnections(listenForIncoming);
		int port = -1;
		String password = null;

		if (listenForIncoming) {
			try {
				port = Integer.parseInt(textListeningPort.getText());
			}
			catch (NumberFormatException e) {
				ExceptionHandler.reportException(e);
			}

			preferences.setRemoteConnectionsPort(port);

			password = textPassword.getText();
			preferences.setRemoteConnectionsPassword(password);

			if (RemoteController.getInstance().isActive()) {
				int oldPort = RemoteController.getInstance().getPort();

				RemoteController.getInstance().setPassword(password);

				if (oldPort != port) {
					MessageBox mb = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.OK);
					mb.setText(Messages.getString("PreferencesComposite.Warning")); //$NON-NLS-1$
					mb.setMessage(Messages.getString("PreferencesComposite.RequiresRestart")); //$NON-NLS-1$
					mb.open();
				}

			}
			else {
				if (port > 0) {
					try {
						RemoteController.getInstance().startServer(port, password, GuiController.getInstance().getProfileManager(),
								GuiController.getInstance().getSynchronizer());
					}
					catch (RemoteException e) {
						ExceptionHandler.reportException(e);
						MessageBox mb = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
						mb.setText(Messages.getString("PreferencesComposite.ConnectionError")); //$NON-NLS-1$
						mb.setMessage(Messages.getString("PreferencesComposite.UnableToStart") + ".\n(" + e.getMessage() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						mb.open();
					}
				}
			}
		}
		else {
			try {
				RemoteController.getInstance().stopServer();
			}
			catch (RemoteException e) {
				ExceptionHandler.reportException(e);
				MessageBox mb = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
				mb.setText(Messages.getString("PreferencesComposite.ConnectionError")); //$NON-NLS-1$
				mb.setMessage(Messages.getString("PreferencesComposite.UnableToStop") + ".\n(" + e.getMessage() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				mb.open();
			}
		}

		preferences.save();
		return true; //FIXME: return false if failed
	}

	@Override
	public boolean cancel() {
		return true;
	}
}
