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

import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.ProfileManager;
import net.sourceforge.fullsync.Synchronizer;
import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.TaskGenerationListener;
import net.sourceforge.fullsync.TaskTree;
import net.sourceforge.fullsync.Util;
import net.sourceforge.fullsync.cli.Main;
import net.sourceforge.fullsync.fs.File;

class MainWindow extends Composite implements ProfileListControlHandler, TaskGenerationListener {
	private ToolItem toolItemNew;
	private Menu menuBarMainWindow;
	private StatusLine statusLine;
	private ToolItem toolItemRun;
	private ToolItem toolItemRunNonIter;
	private ToolItem toolItemDelete;
	private ToolItem toolItemEdit;
	private ToolItem toolItemScheduleIcon;
	private ToolItem toolItemScheduleStart;
	private ToolItem toolItemScheduleStop;

	private Composite profileListContainer;
	private ProfileListComposite profileList;
	private GuiController guiController;

	private String statusDelayString;

	MainWindow(Composite parent, int style, GuiController _guiController) {
		super(parent, style);
		guiController = _guiController;
		Shell shell = getShell();
		initGUI(shell);

		shell.addListener(SWT.Close, e -> {
			e.doit = false;
			if (guiController.getPreferences().closeMinimizesToSystemTray()) {
				minimizeToTray(shell);
			}
			else {
				guiController.closeGui();
			}
		});

		shell.addListener(SWT.Iconify, e -> {
			if (guiController.getPreferences().minimizeMinimizesToSystemTray()) {
				e.doit = false;
				minimizeToTray(shell);
			}
		});

		ProfileManager pm = guiController.getProfileManager();
		pm.addSchedulerListener(profile -> {
			Synchronizer sync = guiController.getSynchronizer();
			TaskTree tree = sync.executeProfile(guiController.getFullSync(), profile, false);
			if (null == tree) {
				profile.setLastError(1, Messages.getString("MainWindow.Error_Comparing_Filesystems")); //$NON-NLS-1$
			}
			else {
				int errorLevel = sync.performActions(tree);
				if (errorLevel > 0) {
					profile.setLastError(errorLevel, Messages.getString("MainWindow.Error_Copying_Files")); //$NON-NLS-1$
				}
				else {
					profile.beginUpdate();
					profile.setLastError(0, null);
					profile.setLastUpdate(new Date());
					profile.endUpdate();
				}
			}
		});
		// TODO [Michele] Implement this listener also on the remote interface
		pm.addSchedulerChangeListener(enabled -> {
			getDisplay().syncExec(() -> {
				toolItemScheduleStart.setEnabled(!enabled);
				toolItemScheduleStop.setEnabled(enabled);
			});
		});
		guiController.getSynchronizer().getTaskGenerator().addTaskGenerationListener(this);

		boolean enabled = pm.isSchedulerEnabled();
		toolItemScheduleStart.setEnabled(!enabled);
		toolItemScheduleStop.setEnabled(enabled);
	}

	/**
	 * Initializes the GUI.
	 * @throws IOException
	 */
	private void initGUI(Shell shell) {
		try {
			this.setSize(600, 300);

			GridLayout thisLayout = new GridLayout();
			thisLayout.horizontalSpacing = 0;
			thisLayout.marginHeight = 0;
			thisLayout.marginWidth = 0;
			thisLayout.verticalSpacing = 0;
			this.setLayout(thisLayout);

			menuBarMainWindow = new Menu(shell, SWT.BAR);
			shell.setMenuBar(menuBarMainWindow);

			// toolbar
			Composite cToolBar = new Composite(this, SWT.FILL);
			GridLayout toolBarLayout = new GridLayout(2, false);
			toolBarLayout.marginHeight = 0;
			toolBarLayout.marginWidth = 0;
			toolBarLayout.horizontalSpacing = 0;
			toolBarLayout.verticalSpacing = 0;
			cToolBar.setLayout(toolBarLayout);

			ToolBar toolBarProfile = new ToolBar(cToolBar, SWT.FLAT);

			toolItemNew = new ToolItem(toolBarProfile, SWT.PUSH);
			toolItemNew.setImage(guiController.getImage("Button_New.png")); //$NON-NLS-1$
			toolItemNew.setToolTipText(Messages.getString("MainWindow.New_Profile")); //$NON-NLS-1$
			toolItemNew.addListener(SWT.Selection, e -> createNewProfile());

			toolItemEdit = new ToolItem(toolBarProfile, SWT.PUSH);
			toolItemEdit.setImage(guiController.getImage("Button_Edit.png")); //$NON-NLS-1$
			toolItemEdit.setToolTipText(Messages.getString("MainWindow.Edit_Profile")); //$NON-NLS-1$
			toolItemEdit.addListener(SWT.Selection, e -> editProfile(profileList.getSelectedProfile()));

			toolItemDelete = new ToolItem(toolBarProfile, SWT.PUSH);
			toolItemDelete.setImage(guiController.getImage("Button_Delete.png")); //$NON-NLS-1$
			toolItemDelete.setToolTipText(Messages.getString("MainWindow.Delete_Profile")); //$NON-NLS-1$
			toolItemDelete.addListener(SWT.Selection, e -> deleteProfile(profileList.getSelectedProfile()));

			toolItemRun = new ToolItem(toolBarProfile, SWT.PUSH);
			toolItemRun.setImage(guiController.getImage("Button_Run.png")); //$NON-NLS-1$
			toolItemRun.setToolTipText(Messages.getString("MainWindow.Run_Profile")); //$NON-NLS-1$
			toolItemRun.addListener(SWT.Selection, e -> runProfile(profileList.getSelectedProfile(), true));

			toolItemRunNonIter = new ToolItem(toolBarProfile, SWT.PUSH);
			toolItemRunNonIter.setImage(guiController.getImage("Button_Run_Non_Inter.png")); //$NON-NLS-1$
			toolItemRunNonIter.setToolTipText("Run Profile - Non Interactive mode");
			toolItemRunNonIter.addListener(SWT.Selection, e -> runProfile(profileList.getSelectedProfile(), false));

			ToolBar toolBarScheduling = new ToolBar(cToolBar, SWT.FLAT);
			new ToolItem(toolBarScheduling, SWT.SEPARATOR);

			//FIXME: do we still need this toolbar item?
			toolItemScheduleIcon = new ToolItem(toolBarScheduling, SWT.NULL);
			toolItemScheduleIcon.setImage(guiController.getImage("Scheduler_Icon.png")); //$NON-NLS-1$
			toolItemScheduleIcon.setDisabledImage(guiController.getImage("Scheduler_Icon.png")); //$NON-NLS-1$
			toolItemScheduleIcon.setEnabled(false);

			toolItemScheduleStart = new ToolItem(toolBarScheduling, SWT.NULL);
			toolItemScheduleStart.setToolTipText(Messages.getString("MainWindow.Start_Scheduler")); //$NON-NLS-1$
			toolItemScheduleStart.setImage(guiController.getImage("Scheduler_Start.png")); //$NON-NLS-1$
			toolItemScheduleStart.addListener(SWT.Selection, e -> guiController.getProfileManager().startScheduler());

			toolItemScheduleStop = new ToolItem(toolBarScheduling, SWT.PUSH);
			toolItemScheduleStop.setToolTipText(Messages.getString("MainWindow.Stop_Scheduler")); //$NON-NLS-1$
			toolItemScheduleStop.setImage(guiController.getImage("Scheduler_Stop.png")); //$NON-NLS-1$
			toolItemScheduleStop.addListener(SWT.Selection, e -> guiController.getProfileManager().stopScheduler());

			// profile list
			profileListContainer = new Composite(this, SWT.NULL);
			profileListContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			profileListContainer.setLayout(new FillLayout());

			// status line
			statusLine = new StatusLine(this, SWT.NULL);
			statusLine.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

			createMenu(shell);
			createProfileList();
			this.layout();
		}
		catch (Exception e) {
			ExceptionHandler.reportException(e);
		}
	}

	private void createMenu(final Shell shell) {
		// Menu Bar
		MenuItem menuItemFile = new MenuItem(menuBarMainWindow, SWT.CASCADE);
		menuItemFile.setText(Messages.getString("MainWindow.File_Menu")); //$NON-NLS-1$

		Menu menuFile = new Menu(menuItemFile);
		menuItemFile.setMenu(menuFile);

		MenuItem menuItemNewProfile = new MenuItem(menuFile, SWT.PUSH);
		menuItemNewProfile.setText(Messages.getString("MainWindow.New_Profile_Menu")); //$NON-NLS-1$
		menuItemNewProfile.setImage(guiController.getImage("Button_New.png")); //$NON-NLS-1$
		menuItemNewProfile.setAccelerator(SWT.CTRL + 'N');
		menuItemNewProfile.addListener(SWT.Selection, e -> createNewProfile());

		new MenuItem(menuFile, SWT.SEPARATOR);

		MenuItem menuItemEditProfile = new MenuItem(menuFile, SWT.PUSH);
		menuItemEditProfile.setText(Messages.getString("MainWindow.Edit_Profile_Menu")); //$NON-NLS-1$
		menuItemEditProfile.setImage(guiController.getImage("Button_Edit.png")); //$NON-NLS-1$
		menuItemEditProfile.addListener(SWT.Selection, e -> editProfile(profileList.getSelectedProfile()));

		MenuItem menuItemRunProfile = new MenuItem(menuFile, SWT.PUSH);
		menuItemRunProfile.setText(Messages.getString("MainWindow.Run_Profile_Menu")); //$NON-NLS-1$
		menuItemRunProfile.setImage(guiController.getImage("Button_Run.png")); //$NON-NLS-1$
		menuItemRunProfile.addListener(SWT.Selection, e -> runProfile(profileList.getSelectedProfile(), true));

		MenuItem menuItemRunProfileNonInter = new MenuItem(menuFile, SWT.PUSH);
		menuItemRunProfileNonInter.setText("Run Profile - Non Interactive mode");
		menuItemRunProfileNonInter.setImage(guiController.getImage("Button_Run_Non_Inter.png")); //$NON-NLS-1$
		menuItemRunProfileNonInter.addListener(SWT.Selection, e -> runProfile(profileList.getSelectedProfile(), false));

		new MenuItem(menuFile, SWT.SEPARATOR);

		MenuItem menuItemDeleteProfile = new MenuItem(menuFile, SWT.PUSH);
		menuItemDeleteProfile.setText(Messages.getString("MainWindow.Delete_Profile_Menu")); //$NON-NLS-1$
		menuItemDeleteProfile.setImage(guiController.getImage("Button_Delete.png")); //$NON-NLS-1$
		menuItemDeleteProfile.addListener(SWT.Selection, e -> deleteProfile(profileList.getSelectedProfile()));

		new MenuItem(menuFile, SWT.SEPARATOR);

		MenuItem menuItemExitProfile = new MenuItem(menuFile, SWT.PUSH);
		menuItemExitProfile.setText(Messages.getString("MainWindow.Exit_Menu")); //$NON-NLS-1$
		menuItemExitProfile.setAccelerator(SWT.CTRL + 'Q');
		menuItemExitProfile.addListener(SWT.Selection, e -> Display.getCurrent().asyncExec(() -> guiController.closeGui()));

		MenuItem menuItemEdit = new MenuItem(menuBarMainWindow, SWT.CASCADE);
		menuItemEdit.setText(Messages.getString("MainWindow.Edit_Menu")); //$NON-NLS-1$

		Menu menuEdit = new Menu(menuItemEdit);
		menuItemEdit.setMenu(menuEdit);

		MenuItem logItem = new MenuItem(menuEdit, SWT.PUSH);
		logItem.setText(Messages.getString("MainWindow.Show_Log_Menu")); //$NON-NLS-1$
		logItem.setAccelerator(SWT.CTRL | (SWT.SHIFT + 'L'));
		logItem.addListener(SWT.Selection, e -> GuiController.launchProgram(Main.getLogFileName()));

		MenuItem preferencesItem = new MenuItem(menuEdit, SWT.PUSH);
		preferencesItem.setText(Messages.getString("MainWindow.Preferences_Menu")); //$NON-NLS-1$
		preferencesItem.setAccelerator(SWT.CTRL | (SWT.SHIFT + 'P'));
		preferencesItem.addListener(SWT.Selection, e -> {
			// show the Preferences Dialog.
			PreferencesPage dialog = new PreferencesPage(shell, guiController.getFullSync());
			dialog.show();
		});

		MenuItem importItem = new MenuItem(menuEdit, SWT.PUSH);
		importItem.setText(Messages.getString("MainWindow.Import_Menu")); //$NON-NLS-1$
		importItem.addListener(SWT.Selection, e -> {
			// show the Import Dialog.
			WizardDialog dialog = new ImportProfilesPage(shell);
			dialog.show();
		});

		MenuItem menuItemRemoteConnection = new MenuItem(menuBarMainWindow, SWT.CASCADE);
		menuItemRemoteConnection.setText(Messages.getString("MainWindow.Remote_Connection_Menu")); //$NON-NLS-1$

		Menu menuRemoteConnection = new Menu(menuItemRemoteConnection);
		menuItemRemoteConnection.setMenu(menuRemoteConnection);

		final MenuItem connectItem = new MenuItem(menuRemoteConnection, SWT.PUSH);
		connectItem.setText(Messages.getString("MainWindow.Connect_Menu")); //$NON-NLS-1$
		connectItem.setAccelerator(SWT.CTRL | (SWT.SHIFT + 'C'));
		connectItem.setEnabled(true);

		final MenuItem disconnectItem = new MenuItem(menuRemoteConnection, SWT.PUSH);
		disconnectItem.setText(Messages.getString("MainWindow.Disconnect_Menu")); //$NON-NLS-1$
		disconnectItem.setAccelerator(SWT.CTRL | (SWT.SHIFT + 'D'));
		disconnectItem.setEnabled(false);

		connectItem.addListener(SWT.Selection, e -> {
			WizardDialog dialog = new ConnectionPage(shell);
			dialog.show();
			GuiController gc = GuiController.getInstance();
			if (gc.getProfileManager().isConnected()) {
				connectItem.setEnabled(false);
				disconnectItem.setEnabled(true);
				gc.getMainShell().setImage(gc.getImage("Remote_Connect.png")); //$NON-NLS-1$
			}
		});

		disconnectItem.addListener(SWT.Selection, e -> {
			MessageBox mb = new MessageBox(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
			mb.setText(Messages.getString("MainWindow.Confirmation")); //$NON-NLS-1$
			mb.setMessage(Messages.getString("MainWindow.Do_You_Want_To_Disconnect") + " \n"); //$NON-NLS-1$ //$NON-NLS-2$

			if (mb.open() == SWT.YES) {
				guiController.getFullSync().disconnectRemote();
				connectItem.setEnabled(true);
				disconnectItem.setEnabled(false);
				guiController.getMainShell().setImage(guiController.getImage("fullsync48.png")); //$NON-NLS-1$
			}
		});

		MenuItem menuItemHelp = new MenuItem(menuBarMainWindow, SWT.CASCADE);
		menuItemHelp.setText(Messages.getString("MainWindow.Help_Menu")); //$NON-NLS-1$

		Menu menuHelp = new Menu(menuItemHelp);
		menuItemHelp.setMenu(menuHelp);

		MenuItem menuItemHelpContent = new MenuItem(menuHelp, SWT.PUSH);
		menuItemHelpContent.setText(Messages.getString("MainWindow.Help_Menu_Item")); //$NON-NLS-1$
		menuItemHelpContent.addListener(SWT.Selection, e -> {
			java.io.File helpIndex = new java.io.File("docs/manual/manual.html").getAbsoluteFile(); //$NON-NLS-1$
			if (helpIndex.exists()) {
				GuiController.launchProgram(helpIndex.getAbsolutePath());
			}
			else {
				GuiController.launchProgram(GuiController.getWebsiteURL() + "docs/manual-" + Util.getFullSyncVersion() + "/manual.html"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		});

		MenuItem menuItemTwitter = new MenuItem(menuHelp, SWT.PUSH);
		menuItemTwitter.setImage(guiController.getImage("twitter_bird_blue_16.png")); //$NON-NLS-1$
		menuItemTwitter.setText(Messages.getString("MainWindow.Menu_Twitter")); //$NON-NLS-1$
		menuItemTwitter.addListener(SWT.Selection, e -> GuiController.launchProgram(GuiController.getTwitterURL()));

		new MenuItem(menuHelp, SWT.SEPARATOR);

		MenuItem menuItemSystem = new MenuItem(menuHelp, SWT.PUSH);
		menuItemSystem.setText(Messages.getString("MainWindow.Menu_SystemInfo"));
		menuItemSystem.addListener(SWT.Selection, e -> {
			WizardDialog dialog = new SystemStatusPage(shell);
			dialog.show();
		});

		new MenuItem(menuHelp, SWT.SEPARATOR);

		MenuItem menuItemAbout = new MenuItem(menuHelp, SWT.PUSH);
		menuItemAbout.setAccelerator(SWT.CTRL + 'A');
		menuItemAbout.setText(Messages.getString("MainWindow.About_Menu")); //$NON-NLS-1$
		menuItemAbout.addListener(SWT.Selection, e -> new AboutDialog(shell));
	}

	private Menu createPopupMenu() {
		// PopUp Menu for the Profile list.
		Menu profilePopupMenu = new Menu(getShell(), SWT.POP_UP);

		MenuItem runItem = new MenuItem(profilePopupMenu, SWT.PUSH);
		runItem.setText(Messages.getString("MainWindow.Run_Profile")); //$NON-NLS-1$
		runItem.setImage(guiController.getImage("Button_Run.png")); //$NON-NLS-1$
		runItem.addListener(SWT.Selection, e -> runProfile(profileList.getSelectedProfile(), true));

		MenuItem runNonInterItem = new MenuItem(profilePopupMenu, SWT.PUSH);
		runNonInterItem.setText("Run Profile - Non Interactive mode");
		runNonInterItem.setImage(guiController.getImage("Button_Run_Non_Inter.png")); //$NON-NLS-1$
		runNonInterItem.addListener(SWT.Selection, e -> runProfile(profileList.getSelectedProfile(), false));

		MenuItem editItem = new MenuItem(profilePopupMenu, SWT.PUSH);
		editItem.setText(Messages.getString("MainWindow.Edit_Profile")); //$NON-NLS-1$
		editItem.setImage(guiController.getImage("Button_Edit.png")); //$NON-NLS-1$
		editItem.addListener(SWT.Selection, e -> editProfile(profileList.getSelectedProfile()));

		MenuItem deleteItem = new MenuItem(profilePopupMenu, SWT.PUSH);
		deleteItem.setText(Messages.getString("MainWindow.Delete_Profile")); //$NON-NLS-1$
		deleteItem.setImage(guiController.getImage("Button_Delete.png")); //$NON-NLS-1$
		deleteItem.addListener(SWT.Selection, e -> deleteProfile(profileList.getSelectedProfile()));

		new MenuItem(profilePopupMenu, SWT.SEPARATOR);

		MenuItem addItem = new MenuItem(profilePopupMenu, SWT.PUSH);
		addItem.setText(Messages.getString("MainWindow.New_Profile")); //$NON-NLS-1$
		addItem.setImage(guiController.getImage("Button_New.png")); //$NON-NLS-1$
		addItem.addListener(SWT.Selection, e -> createNewProfile());
		return profilePopupMenu;
	}

	void createProfileList() {
		for (Control c : profileListContainer.getChildren()) {
			c.dispose();
		}
		if ("NiceListView".equals(guiController.getPreferences().getProfileListStyle())) {
			profileList = new NiceListViewProfileListComposite(profileListContainer, SWT.NULL);
		}
		else {
			profileList = new ListViewProfileListComposite(profileListContainer, SWT.NULL);
		}
		profileList.setMenu(createPopupMenu());
		profileList.setHandler(this);
		profileList.setProfileManager(guiController.getProfileManager());

		profileListContainer.layout();
	}

	public StatusLine getStatusLine() {
		return statusLine;
	}

	public GuiController getGuiController() {
		return guiController;
	}

	private void minimizeToTray(final Shell shell) {
		// FIXME: on OSX use this:
		// mainWindow.setMinimized(true);
		shell.setMinimized(true);
		shell.setVisible(false);
		// TODO make sure Tray is visible here
	}

	@Override
	public void taskTreeStarted(TaskTree tree) {
	}

	@Override
	public void taskGenerationStarted(final File source, final File destination) {
		statusDelayString = Messages.getString("MainWindow.Checking_File", source.getPath()); //$NON-NLS-1$
	}

	@Override
	public void taskGenerationFinished(Task task) {

	}

	@Override
	public void taskTreeFinished(TaskTree tree) {
		statusLine.setMessage(Messages.getString("MainWindow.Sync_Finished")); //$NON-NLS-1$
	}

	@Override
	public void createNewProfile() {
		try {
			WizardDialog dialog = new ProfileDetailsTabbedPage(getShell(), guiController.getFullSync(), null);
			dialog.show();
		}
		catch (Exception e) {
			ExceptionHandler.reportException(e);
		}
	}

	@Override
	public void runProfile(final Profile p, final boolean interactive) {
		if (null == p) {
			if (!interactive) {
				MessageBox mb = new MessageBox(getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				mb.setText(Messages.getString("MainWindow.Confirmation")); //$NON-NLS-1$
				mb.setMessage("You're about to start the profile in non-interactive mode.\n Are you sure?");
				if (mb.open() != SWT.YES) {
					return;
				}
			}

			Thread worker = new Thread(() -> doRunProfile(p, interactive));
			worker.start();
		}
	}

	private synchronized void doRunProfile(Profile p, boolean interactive) {
		TaskTree t = null;
		Timer statusDelayTimer = null;
		try {
			guiController.showBusyCursor(true);
			// REVISIT wow, a timer here is pretty much overhead / specific for
			// this generell problem
			// FIXME: do we really need this Timer?
			statusDelayTimer = new Timer(true);
			try {
				statusDelayTimer.schedule(new TimerTask() {
					@Override
					public void run() {
						statusLine.setMessage(statusDelayString);
					}
				}, 10, 100);
				statusDelayString = Messages.getString("MainWindow.Starting_Profile") + p.getName() + "..."; //$NON-NLS-1$ //$NON-NLS-2$
				statusLine.setMessage(statusDelayString);
				t = guiController.getSynchronizer().executeProfile(guiController.getFullSync(), p, interactive);
				if (null == t) {
					p.setLastError(1, Messages.getString("MainWindow.Error_Comparing_Filesystems")); //$NON-NLS-1$
					statusLine.setMessage(Messages.getString("MainWindow.Error_Processing_Profile", p.getName())); //$NON-NLS-1$
				}
				else {
					statusLine.setMessage(Messages.getString("MainWindow.Finished_Profile", p.getName())); //$NON-NLS-1$
				}
			}
			catch (Error e) {
				ExceptionHandler.reportException(e);
			}
			finally {
				statusDelayTimer.cancel();
				guiController.showBusyCursor(false);
			}
			if (null != t) {
				TaskDecisionList.show(guiController, p, t, interactive);
			}

		}
		catch (Exception e) {
			ExceptionHandler.reportException(e);
		}
	}

	@Override
	public void editProfile(final Profile p) {
		try {
			WizardDialog dialog = new ProfileDetailsTabbedPage(getShell(), guiController.getFullSync(), p);
			dialog.show();
		}
		catch (Exception e) {
			ExceptionHandler.reportException(e);
		}
	}

	@Override
	public void deleteProfile(final Profile p) {
		if (null != p) {
			ProfileManager profileManager = guiController.getProfileManager();

			MessageBox mb = new MessageBox(getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
			mb.setText(Messages.getString("MainWindow.Confirmation")); //$NON-NLS-1$
			mb.setMessage(Messages.getString("MainWindow.Do_You_Want_To_Delete_Profile") + " " + p.getName() + " ?"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			if (mb.open() == SWT.YES) {
				profileManager.removeProfile(p);
				profileManager.save();
			}
		}
	}

	protected void toolItemScheduleWidgedSelected(SelectionEvent evt) {
		ProfileManager profileManager = guiController.getProfileManager();
		if (profileManager.isSchedulerEnabled()) {
			profileManager.stopScheduler();
		}
		else {
			profileManager.startScheduler();
		}
		// updateTimerEnabled();
	}
}
