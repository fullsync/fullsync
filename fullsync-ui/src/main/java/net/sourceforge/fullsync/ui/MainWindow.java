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
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.google.common.eventbus.Subscribe;

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.FSFile;
import net.sourceforge.fullsync.Preferences;
import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.ProfileManager;
import net.sourceforge.fullsync.RuntimeConfiguration;
import net.sourceforge.fullsync.ScheduleTaskSource;
import net.sourceforge.fullsync.Scheduler;
import net.sourceforge.fullsync.Synchronizer;
import net.sourceforge.fullsync.TaskTree;
import net.sourceforge.fullsync.Util;
import net.sourceforge.fullsync.cli.Main;
import net.sourceforge.fullsync.event.ScheduledProfileExecution;
import net.sourceforge.fullsync.event.SchedulerStatusChanged;
import net.sourceforge.fullsync.event.TaskGenerationFinished;
import net.sourceforge.fullsync.event.TaskTreeFinished;
import net.sourceforge.fullsync.event.TaskTreeStarted;
import net.sourceforge.fullsync.ui.profiledetails.ProfileDetailsTabbedPage;

@Singleton
class MainWindow implements ProfileListControlHandler {
	private final Set<TaskTree> runningTaskTrees = new HashSet<>();
	private final Display display;
	private final ImageRepository imageRepository;
	private final ProfileManager profileManager;
	private final Scheduler scheduler;
	private final ScheduleTaskSource scheduleTaskSource;
	private final Preferences preferences;
	private final ScheduledExecutorService scheduledExecutorService;
	private final Provider<PreferencesPage> preferencesPageProvider;
	private final Provider<Synchronizer> synchronizerProvider;
	private final Provider<ImportProfilesPage> importProfilesPageProvider;
	private final Provider<SystemStatusPage> systemStatusPageProvider;
	private final Provider<AboutDialog> aboutDialogProvider;
	private final Provider<ProfileDetailsTabbedPage> profileDetailsTabbedPageProvider;
	private final Provider<TaskDecisionPage> taskDecisionPageProvider;
	private final ProfileListCompositeFactory profileListCompositeFactory;
	private final Composite mainComposite;
	private final ShellStateHandler shellStateHandler;
	private Menu menuBarMainWindow;
	private Label statusLine;
	private ToolItem toolItemScheduleStart;
	private ToolItem toolItemScheduleStop;
	private Composite profileListContainer;
	private ProfileListComposite profileList;
	private GUIUpdateQueue<FSFile> lastFileChecked;
	private GUIUpdateQueue<String> statusLineText;

	@Inject
	MainWindow(Display display, ImageRepository imageRepository, Shell shell, ProfileManager profileManager, Scheduler scheduler,
		ScheduleTaskSource scheduleTaskSource, RuntimeConfiguration runtimeConfiguration, Preferences preferences,
		ScheduledExecutorService scheduledExecutorService, Provider<PreferencesPage> preferencesPageProvider,
		Provider<Synchronizer> synchronizerProvider, Provider<ImportProfilesPage> importProfilesPageProvider,
		Provider<SystemStatusPage> systemStatusPageProvider, Provider<AboutDialog> aboutDialogProvider,
		Provider<ProfileDetailsTabbedPage> profileDetailsTabbedPageProvider, Provider<TaskDecisionPage> taskDecisionPageProvider,
		ProfileListCompositeFactory profileListCompositeFactory) {
		this.display = display;
		this.imageRepository = imageRepository;
		this.profileManager = profileManager;
		this.scheduler = scheduler;
		this.scheduleTaskSource = scheduleTaskSource;
		this.preferences = preferences;
		this.scheduledExecutorService = scheduledExecutorService;
		this.preferencesPageProvider = preferencesPageProvider;
		this.synchronizerProvider = synchronizerProvider;
		this.importProfilesPageProvider = importProfilesPageProvider;
		this.systemStatusPageProvider = systemStatusPageProvider;
		this.aboutDialogProvider = aboutDialogProvider;
		this.profileDetailsTabbedPageProvider = profileDetailsTabbedPageProvider;
		this.taskDecisionPageProvider = taskDecisionPageProvider;
		this.profileListCompositeFactory = profileListCompositeFactory;
		shell.setLayout(new FillLayout());
		shell.setText("FullSync"); //$NON-NLS-1$
		shell.setImage(imageRepository.getImage("fullsync48.png")); //$NON-NLS-1$

		shell.addListener(SWT.Close, e -> {
			e.doit = false;
			if (preferences.closeMinimizesToSystemTray()) {
				minimizeToTray();
			}
			else {
				closeGui();
			}
		});

		shell.addListener(SWT.Iconify, e -> {
			if (preferences.minimizeMinimizesToSystemTray()) {
				e.doit = false;
				minimizeToTray();
			}
		});
		mainComposite = new Composite(shell, SWT.NULL);
		initGUI();

		statusLineText = new GUIUpdateQueue<>(display, texts -> {
			String statusMessage = texts.get(texts.size() - 1);
			statusLine.setText(statusMessage);
			Control[] changed = new Control[] {
				statusLine
			};
			mainComposite.layout(changed); // workaround SWT layout bug on GTK
		});

		lastFileChecked = new GUIUpdateQueue<>(display, files -> {
			FSFile lastCheckedFile = files.get(files.size() - 1);
			statusLineText.add(Messages.getString("MainWindow.Checking_File", lastCheckedFile.getPath())); //$NON-NLS-1$
		});

		boolean enabled = scheduler.isEnabled();
		toolItemScheduleStart.setEnabled(!enabled);
		toolItemScheduleStop.setEnabled(enabled);
		Point size = mainComposite.getSize();
		Rectangle shellBounds = shell.computeTrim(0, 0, size.x, size.y);
		shell.setSize(shellBounds.width, shellBounds.height);
		shellStateHandler = ShellStateHandler.apply(preferences, shell, MainWindow.class);
		Optional<Boolean> minimized = runtimeConfiguration.isStartMinimized();
		if (minimized.orElse(false)) {
			shell.setVisible(false);
		}
	}

	@Subscribe
	private void schedulerStatusChanged(SchedulerStatusChanged schedulerStatusChanged) {
		display.syncExec(() -> {
			if (!mainComposite.isDisposed()) {
				toolItemScheduleStart.setEnabled(!schedulerStatusChanged.isEnabled());
				toolItemScheduleStop.setEnabled(schedulerStatusChanged.isEnabled());
			}
		});
	}

	@Subscribe
	private void executeScheduledProfile(ScheduledProfileExecution scheduledProfileExecution) {
		Profile profile = scheduledProfileExecution.getProfile();
		Synchronizer synchronizer = synchronizerProvider.get();
		TaskTree tree = synchronizer.executeProfile(profile, false);
		if (null == tree) {
			profile.setLastError(1, Messages.getString("MainWindow.Error_Comparing_Filesystems")); //$NON-NLS-1$
		}
		else {
			int errorLevel = synchronizer.performActions(tree);
			if (errorLevel > 0) {
				profile.setLastError(errorLevel, Messages.getString("MainWindow.Error_Copying_Files")); //$NON-NLS-1$
			}
			else {
				profile.setLastError(0, null);
				profile.setLastUpdate(new Date());
			}
		}
	}

	public void setVisible(boolean visible) {
		Shell shell = mainComposite.getShell();
		shell.setVisible(visible);
		shell.setMinimized(!visible);
	}

	private void initGUI() {
		try {
			mainComposite.setSize(600, 300);

			GridLayout thisLayout = new GridLayout();
			thisLayout.horizontalSpacing = 0;
			thisLayout.marginHeight = 0;
			thisLayout.marginWidth = 0;
			thisLayout.verticalSpacing = 0;
			mainComposite.setLayout(thisLayout);

			Shell shell = mainComposite.getShell();
			menuBarMainWindow = new Menu(shell, SWT.BAR);
			shell.setMenuBar(menuBarMainWindow);

			// toolbar
			Composite cToolBar = new Composite(mainComposite, SWT.FILL);
			GridLayout toolBarLayout = new GridLayout(2, false);
			toolBarLayout.marginHeight = 0;
			toolBarLayout.marginWidth = 0;
			toolBarLayout.horizontalSpacing = 0;
			toolBarLayout.verticalSpacing = 0;
			cToolBar.setLayout(toolBarLayout);

			ToolBar toolBarProfile = new ToolBar(cToolBar, SWT.FLAT);

			ToolItem toolItemNew = new ToolItem(toolBarProfile, SWT.PUSH);
			toolItemNew.setImage(imageRepository.getImage("Button_New.png")); //$NON-NLS-1$
			toolItemNew.setToolTipText(Messages.getString("MainWindow.New_Profile")); //$NON-NLS-1$
			toolItemNew.addListener(SWT.Selection, e -> createNewProfile());

			ToolItem toolItemEdit = new ToolItem(toolBarProfile, SWT.PUSH);
			toolItemEdit.setImage(imageRepository.getImage("Button_Edit.png")); //$NON-NLS-1$
			toolItemEdit.setToolTipText(Messages.getString("MainWindow.Edit_Profile")); //$NON-NLS-1$
			toolItemEdit.addListener(SWT.Selection, e -> editProfile(profileList.getSelectedProfile()));

			ToolItem toolItemDelete = new ToolItem(toolBarProfile, SWT.PUSH);
			toolItemDelete.setImage(imageRepository.getImage("Button_Delete.png")); //$NON-NLS-1$
			toolItemDelete.setToolTipText(Messages.getString("MainWindow.Delete_Profile")); //$NON-NLS-1$
			toolItemDelete.addListener(SWT.Selection, e -> deleteProfile(profileList.getSelectedProfile()));

			ToolItem toolItemRun = new ToolItem(toolBarProfile, SWT.PUSH);
			toolItemRun.setImage(imageRepository.getImage("Button_Run.png")); //$NON-NLS-1$
			toolItemRun.setToolTipText(Messages.getString("MainWindow.Run_Profile")); //$NON-NLS-1$
			toolItemRun.addListener(SWT.Selection, e -> runProfile(profileList.getSelectedProfile(), true));

			ToolItem toolItemRunNonIter = new ToolItem(toolBarProfile, SWT.PUSH);
			toolItemRunNonIter.setImage(imageRepository.getImage("Button_Run_Non_Inter.png")); //$NON-NLS-1$
			toolItemRunNonIter.setToolTipText(Messages.getString("MainWindow.RunProfileNonInteractive")); //$NON-NLS-1$
			toolItemRunNonIter.addListener(SWT.Selection, e -> runProfile(profileList.getSelectedProfile(), false));

			ToolBar toolBarScheduling = new ToolBar(cToolBar, SWT.FLAT);
			new ToolItem(toolBarScheduling, SWT.SEPARATOR);

			// FIXME: do we still need this toolbar item?
			ToolItem toolItemScheduleIcon = new ToolItem(toolBarScheduling, SWT.NULL);
			toolItemScheduleIcon.setImage(imageRepository.getImage("Scheduler_Icon.png")); //$NON-NLS-1$
			toolItemScheduleIcon.setDisabledImage(imageRepository.getImage("Scheduler_Icon.png")); //$NON-NLS-1$
			toolItemScheduleIcon.setEnabled(false);

			toolItemScheduleStart = new ToolItem(toolBarScheduling, SWT.NULL);
			toolItemScheduleStart.setToolTipText(Messages.getString("MainWindow.Start_Scheduler")); //$NON-NLS-1$
			toolItemScheduleStart.setImage(imageRepository.getImage("Scheduler_Start.png")); //$NON-NLS-1$
			toolItemScheduleStart.addListener(SWT.Selection, e -> scheduler.start());

			toolItemScheduleStop = new ToolItem(toolBarScheduling, SWT.PUSH);
			toolItemScheduleStop.setToolTipText(Messages.getString("MainWindow.Stop_Scheduler")); //$NON-NLS-1$
			toolItemScheduleStop.setImage(imageRepository.getImage("Scheduler_Stop.png")); //$NON-NLS-1$
			toolItemScheduleStop.addListener(SWT.Selection, e -> scheduler.stop());

			// profile list
			profileListContainer = new Composite(mainComposite, SWT.NULL);
			profileListContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			profileListContainer.setLayout(new FillLayout());

			// status line
			statusLine = new Label(mainComposite, SWT.NONE);
			statusLine.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

			createMenu();
			createProfileList();
			mainComposite.layout();
		}
		catch (Exception e) {
			ExceptionHandler.reportException(e);
		}
	}

	private void createMenu() {
		// Menu Bar
		MenuItem menuItemFile = new MenuItem(menuBarMainWindow, SWT.CASCADE);
		menuItemFile.setText(Messages.getString("MainWindow.File_Menu")); //$NON-NLS-1$

		Menu menuFile = new Menu(menuItemFile);
		menuItemFile.setMenu(menuFile);

		MenuItem menuItemNewProfile = new MenuItem(menuFile, SWT.PUSH);
		menuItemNewProfile.setText(Messages.getString("MainWindow.New_Profile_Menu")); //$NON-NLS-1$
		menuItemNewProfile.setImage(imageRepository.getImage("Button_New.png")); //$NON-NLS-1$
		menuItemNewProfile.setAccelerator(SWT.CTRL + 'N');
		menuItemNewProfile.addListener(SWT.Selection, e -> createNewProfile());

		new MenuItem(menuFile, SWT.SEPARATOR);

		MenuItem menuItemEditProfile = new MenuItem(menuFile, SWT.PUSH);
		menuItemEditProfile.setText(Messages.getString("MainWindow.Edit_Profile_Menu")); //$NON-NLS-1$
		menuItemEditProfile.setImage(imageRepository.getImage("Button_Edit.png")); //$NON-NLS-1$
		menuItemEditProfile.addListener(SWT.Selection, e -> editProfile(profileList.getSelectedProfile()));

		MenuItem menuItemRunProfile = new MenuItem(menuFile, SWT.PUSH);
		menuItemRunProfile.setText(Messages.getString("MainWindow.Run_Profile_Menu")); //$NON-NLS-1$
		menuItemRunProfile.setImage(imageRepository.getImage("Button_Run.png")); //$NON-NLS-1$
		menuItemRunProfile.addListener(SWT.Selection, e -> runProfile(profileList.getSelectedProfile(), true));

		MenuItem menuItemRunProfileNonInter = new MenuItem(menuFile, SWT.PUSH);
		menuItemRunProfileNonInter.setText(Messages.getString("MainWindow.RunProfileNonInteractive")); //$NON-NLS-1$
		menuItemRunProfileNonInter.setImage(imageRepository.getImage("Button_Run_Non_Inter.png")); //$NON-NLS-1$
		menuItemRunProfileNonInter.addListener(SWT.Selection, e -> runProfile(profileList.getSelectedProfile(), false));

		new MenuItem(menuFile, SWT.SEPARATOR);

		MenuItem menuItemDeleteProfile = new MenuItem(menuFile, SWT.PUSH);
		menuItemDeleteProfile.setText(Messages.getString("MainWindow.Delete_Profile_Menu")); //$NON-NLS-1$
		menuItemDeleteProfile.setImage(imageRepository.getImage("Button_Delete.png")); //$NON-NLS-1$
		menuItemDeleteProfile.addListener(SWT.Selection, e -> deleteProfile(profileList.getSelectedProfile()));

		new MenuItem(menuFile, SWT.SEPARATOR);

		MenuItem menuItemExitProfile = new MenuItem(menuFile, SWT.PUSH);
		menuItemExitProfile.setText(Messages.getString("MainWindow.Exit_Menu")); //$NON-NLS-1$
		menuItemExitProfile.setAccelerator(SWT.CTRL + 'Q');
		menuItemExitProfile.addListener(SWT.Selection, e -> Display.getCurrent().asyncExec(this::closeGui));

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
		preferencesItem.addListener(SWT.Selection, e -> preferencesPageProvider.get().show());

		MenuItem importItem = new MenuItem(menuEdit, SWT.PUSH);
		importItem.setText(Messages.getString("MainWindow.Import_Menu")); //$NON-NLS-1$
		importItem.addListener(SWT.Selection, e -> importProfilesPageProvider.get().show());

		MenuItem menuItemHelp = new MenuItem(menuBarMainWindow, SWT.CASCADE);
		menuItemHelp.setText(Messages.getString("MainWindow.Help_Menu")); //$NON-NLS-1$

		Menu menuHelp = new Menu(menuItemHelp);
		menuItemHelp.setMenu(menuHelp);

		MenuItem menuItemHelpContent = new MenuItem(menuHelp, SWT.PUSH);
		menuItemHelpContent.setText(Messages.getString("MainWindow.Help_Menu_Item")); //$NON-NLS-1$
		menuItemHelpContent.addListener(SWT.Selection, e -> {
			File helpIndex = new File(Util.getInstalllocation(), "docs/manual/manual.html").getAbsoluteFile(); //$NON-NLS-1$
			if (helpIndex.exists()) {
				GuiController.launchProgram(helpIndex.getAbsolutePath());
			}
			else {
				String url = String.format("%sdocs/manual-%s/manual.html", GuiController.getWebsiteURL(), Util.getFullSyncVersion()); //$NON-NLS-1$
				GuiController.launchProgram(url);
			}
		});

		MenuItem menuItemTwitter = new MenuItem(menuHelp, SWT.PUSH);
		menuItemTwitter.setImage(imageRepository.getImage("twitter_bird_blue_16.png")); //$NON-NLS-1$
		menuItemTwitter.setText(Messages.getString("MainWindow.Menu_Twitter")); //$NON-NLS-1$
		menuItemTwitter.addListener(SWT.Selection, e -> GuiController.launchProgram(GuiController.getTwitterURL()));

		new MenuItem(menuHelp, SWT.SEPARATOR);

		MenuItem menuItemSystem = new MenuItem(menuHelp, SWT.PUSH);
		menuItemSystem.setText(Messages.getString("MainWindow.Menu_SystemInfo")); //$NON-NLS-1$
		menuItemSystem.addListener(SWT.Selection, e -> systemStatusPageProvider.get().show());

		new MenuItem(menuHelp, SWT.SEPARATOR);

		MenuItem menuItemAbout = new MenuItem(menuHelp, SWT.PUSH);
		menuItemAbout.setAccelerator(SWT.CTRL + 'A');
		menuItemAbout.setText(Messages.getString("MainWindow.About_Menu")); //$NON-NLS-1$
		menuItemAbout.addListener(SWT.Selection, e -> aboutDialogProvider.get().show());
	}

	private Menu createPopupMenu() {
		// PopUp Menu for the Profile list.
		Menu profilePopupMenu = new Menu(mainComposite.getShell(), SWT.POP_UP);

		MenuItem runItem = new MenuItem(profilePopupMenu, SWT.PUSH);
		runItem.setText(Messages.getString("MainWindow.Run_Profile")); //$NON-NLS-1$
		runItem.setImage(imageRepository.getImage("Button_Run.png")); //$NON-NLS-1$
		runItem.addListener(SWT.Selection, e -> runProfile(profileList.getSelectedProfile(), true));

		MenuItem runNonInterItem = new MenuItem(profilePopupMenu, SWT.PUSH);
		runNonInterItem.setText(Messages.getString("MainWindow.RunProfileNonInteractive")); //$NON-NLS-1$
		runNonInterItem.setImage(imageRepository.getImage("Button_Run_Non_Inter.png")); //$NON-NLS-1$
		runNonInterItem.addListener(SWT.Selection, e -> runProfile(profileList.getSelectedProfile(), false));

		MenuItem editItem = new MenuItem(profilePopupMenu, SWT.PUSH);
		editItem.setText(Messages.getString("MainWindow.Edit_Profile")); //$NON-NLS-1$
		editItem.setImage(imageRepository.getImage("Button_Edit.png")); //$NON-NLS-1$
		editItem.addListener(SWT.Selection, e -> editProfile(profileList.getSelectedProfile()));

		MenuItem deleteItem = new MenuItem(profilePopupMenu, SWT.PUSH);
		deleteItem.setText(Messages.getString("MainWindow.Delete_Profile")); //$NON-NLS-1$
		deleteItem.setImage(imageRepository.getImage("Button_Delete.png")); //$NON-NLS-1$
		deleteItem.addListener(SWT.Selection, e -> deleteProfile(profileList.getSelectedProfile()));

		new MenuItem(profilePopupMenu, SWT.SEPARATOR);

		MenuItem addItem = new MenuItem(profilePopupMenu, SWT.PUSH);
		addItem.setText(Messages.getString("MainWindow.New_Profile")); //$NON-NLS-1$
		addItem.setImage(imageRepository.getImage("Button_New.png")); //$NON-NLS-1$
		addItem.addListener(SWT.Selection, e -> createNewProfile());
		return profilePopupMenu;
	}

	void createProfileList() {
		for (Control c : profileListContainer.getChildren()) {
			c.dispose();
		}
		if ("NiceListView".equals(preferences.getProfileListStyle())) { //$NON-NLS-1$
			profileList = profileListCompositeFactory.createNiceListViewComposite(profileListContainer, this);
		}
		else {
			profileList = profileListCompositeFactory.createListViewComposite(profileListContainer, this);
		}
		profileList.setMenu(createPopupMenu());
		profileListContainer.layout();
	}

	@Subscribe
	private void taskTreeStarted(TaskTreeStarted taskTreeStarted) {
		runningTaskTrees.add(taskTreeStarted.getTaskTree());
	}

	@Subscribe
	private void taskGenerationFinished(TaskGenerationFinished taskGenerationFinished) {
		if (runningTaskTrees.contains(taskGenerationFinished.getTaskTree())) {
			lastFileChecked.add(taskGenerationFinished.getTask().getSource());
		}
	}

	@Subscribe
	private void taskTreeFinished(TaskTreeFinished taskTreeFinished) {
		runningTaskTrees.remove(taskTreeFinished.getTaskTree());
		statusLineText.add(Messages.getString("MainWindow.Sync_Finished")); //$NON-NLS-1$
	}

	@Override
	public void createNewProfile() {
		try {
			profileDetailsTabbedPageProvider.get().show();
		}
		catch (Exception e) {
			ExceptionHandler.reportException(e);
		}
	}

	@Override
	public void runProfile(final Profile p, final boolean interactive) {
		if (null != p) {
			if (!interactive) {
				MessageBox mb = new MessageBox(mainComposite.getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				mb.setText(Messages.getString("MainWindow.Confirmation")); //$NON-NLS-1$
				mb.setMessage(Messages.getString("MainWindow.RunProfileNonInteractiveConfirmationMessage")); //$NON-NLS-1$
				if (mb.open() != SWT.YES) {
					return;
				}
			}
			scheduledExecutorService.submit(() -> doRunProfile(p, interactive));
		}
	}

	private synchronized void doRunProfile(Profile p, boolean interactive) {
		showBusyCursor(true);
		try {
			Synchronizer synchronizer = synchronizerProvider.get();
			statusLineText.add(Messages.getString("MainWindow.Starting_Profile", p.getName())); //$NON-NLS-1$
			final TaskTree taskTree = synchronizer.executeProfile(p, interactive);
			if (null == taskTree) {
				p.setLastError(1, Messages.getString("MainWindow.Error_Comparing_Filesystems")); //$NON-NLS-1$
				statusLineText.add(Messages.getString("MainWindow.Error_Processing_Profile", p.getName())); //$NON-NLS-1$
			}
			else {
				statusLineText.add(Messages.getString("MainWindow.Finished_Profile", p.getName())); //$NON-NLS-1$
				mainComposite.getDisplay().asyncExec(() -> {
					try {
						TaskDecisionPage dialog = taskDecisionPageProvider.get();
						dialog.setTaskTree(taskTree);
						dialog.setInteractive(interactive);
						dialog.show();
					}
					catch (Exception ex) {
						ExceptionHandler.reportException(ex);
					}
				});
			}
		}
		catch (Exception e) {
			ExceptionHandler.reportException(e);
		}
		finally {
			showBusyCursor(false);
		}
	}

	@Override
	public void editProfile(final Profile p) {
		try {
			ProfileDetailsTabbedPage dialog = profileDetailsTabbedPageProvider.get();
			dialog.setProfile(p);
			dialog.show();
		}
		catch (Exception e) {
			ExceptionHandler.reportException(e);
		}
	}

	@Override
	public void deleteProfile(final Profile p) {
		if (null != p) {
			MessageBox mb = new MessageBox(mainComposite.getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
			mb.setText(Messages.getString("MainWindow.Confirmation")); //$NON-NLS-1$
			mb.setMessage(Messages.getString("MainWindow.Do_You_Want_To_Delete_Profile", p.getName())); //$NON-NLS-1$
			if (mb.open() == SWT.YES) {
				profileManager.removeProfile(p);
				profileManager.save();
			}
		}
	}

	private void minimizeToTray() {
		Shell shell = mainComposite.getShell();
		// FIXME: on OSX use this:
		// mainWindow.setMinimized(true);
		shell.setMinimized(true);
		shell.setVisible(false);
		// TODO make sure Tray is visible here
	}

	// TODO the busy cursor should be applied only to the window that is busy
	// difficulty: getShell() can only be accessed by the display thread :-/
	public void showBusyCursor(final boolean show) {
		display.asyncExec(() -> {
			try {
				Cursor cursor = show ? display.getSystemCursor(SWT.CURSOR_WAIT) : null;
				Shell[] shells = display.getShells();

				for (Shell shell : shells) {
					shell.setCursor(cursor);
				}
			}
			catch (Exception ex) {
				ExceptionHandler.reportException(ex);
			}
		});
	}

	public void closeGui() {
		// TODO before closing anything we need to find out whether there are operations
		// currently running / windows open that should/may not be closed

		// Close the application, but give him a chance to
		// confirm his action first
		long now = System.currentTimeMillis();
		if (scheduler.isEnabled() && (null != scheduleTaskSource.getNextScheduleTask(now)) && preferences.confirmExit()) {
			MessageBox mb = new MessageBox(mainComposite.getShell(), SWT.ICON_WARNING | SWT.YES | SWT.NO);
			mb.setText(Messages.getString("GuiController.Confirmation")); //$NON-NLS-1$
			String doYouWantToQuit = Messages.getString("GuiController.Do_You_Want_To_Quit"); //$NON-NLS-1$
			String scheduleIsStopped = Messages.getString("GuiController.Schedule_is_stopped"); //$NON-NLS-1$
			mb.setMessage(String.format("%s%n%s", doYouWantToQuit, scheduleIsStopped)); //$NON-NLS-1$

			// check whether the user really wants to close
			if (mb.open() != SWT.YES) {
				return;
			}
		}
		shellStateHandler.saveWindowState();
		display.dispose();
	}
}
