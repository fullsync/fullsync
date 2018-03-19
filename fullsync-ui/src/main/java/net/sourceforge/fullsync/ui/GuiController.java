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
import java.util.MissingResourceException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import com.google.inject.Injector;

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.FullSync;
import net.sourceforge.fullsync.Preferences;
import net.sourceforge.fullsync.ProfileManager;
import net.sourceforge.fullsync.Util;
import net.sourceforge.fullsync.cli.Main;

@Singleton
public class GuiController {
	private final FullSync fullSync;
	private final Display display;
	private final Shell shell;
	private final Provider<ImageRepository> imageRepositoryProvider;
	private final Provider<FontRepository> fontRepositoryProvider;
	private final Provider<MainWindow> mainWindowProvider;
	private final Provider<SystemTrayItem> systemTrayItemProvider;
	private final Provider<WelcomeScreen> welcomeScreenProvider;
	private final Preferences preferences;
	private final ProfileManager profileManager;
	private final ScheduledThreadPoolExecutor executorService;
	private ExceptionHandler oldExceptionHandler;

	@Inject
	private GuiController(FullSync fullSync, Display display, Shell shell, Provider<ImageRepository> imageRepositoryProvider,
		Provider<FontRepository> fontRepositoryProvider, Provider<MainWindow> mainWindowProvider,
		Provider<SystemTrayItem> systemTrayItemProvider, Provider<WelcomeScreen> welcomeScreenProvider, Preferences preferences,
		ProfileManager profileManager) {
		this.fullSync = fullSync;
		this.display = display;
		this.shell = shell;
		this.imageRepositoryProvider = imageRepositoryProvider;
		this.fontRepositoryProvider = fontRepositoryProvider;
		this.mainWindowProvider = mainWindowProvider;
		this.systemTrayItemProvider = systemTrayItemProvider;
		this.welcomeScreenProvider = welcomeScreenProvider;
		this.preferences = preferences;
		this.profileManager = profileManager;
		executorService = new ScheduledThreadPoolExecutor(1);
		String languageCode = preferences.getLanguageCode();
		try {
			Messages.setLanguage(languageCode);
		}
		catch (MissingResourceException ex) {
			ExceptionHandler.reportException("Failed to set language to " + languageCode, ex);
		}
	}

	public Preferences getPreferences() {
		return preferences;
	}

	private void startGui() {
		oldExceptionHandler = ExceptionHandler.registerExceptionHandler(new ExceptionHandler() {
			@Override
			protected void doReportException(final String message, final Throwable exception) {
				exception.printStackTrace();

				display.syncExec(() -> new ExceptionDialog(shell, message, exception));
			}
		});
		fullSync.pushQuestionHandler(this::showQuestion);
		mainWindowProvider.get();
		systemTrayItemProvider.get().show();
		createWelcomeScreen();
	}

	public static void launchUI(Injector injector) {
		Injector uiInjector = injector.createChildInjector(new FullSyncUiModule());
		GuiController guiController = uiInjector.getInstance(GuiController.class);
		guiController.run(uiInjector);
	}

	private Future<Boolean> showQuestion(String question) {
		Future<Boolean> answer;
		if (display == Display.findDisplay(Thread.currentThread())) {
			answer = Futures.immediateCheckedFuture(doShowQuestion(question));
			display.syncExec(null);
		}
		else {
			SettableFuture<Boolean> settableAnswer = SettableFuture.create();
			answer = settableAnswer;
			display.asyncExec(() -> {
				settableAnswer.set(doShowQuestion(question));
			});
		}
		return answer;
	}

	private boolean doShowQuestion(String question) {
		MessageBox mb = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		mb.setText(Messages.getString("SFTP.YesNoQuestion")); //$NON-NLS-1$
		mb.setMessage(question);
		return SWT.YES == mb.open();
	}

	public void run(Injector uiInjector) {
		startGui();
		executorService.submit(() -> Main.finishStartup(uiInjector));
		while (!display.isDisposed()) {
			try {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
			catch (SWTException ex) {
				ex.printStackTrace();
			}
		}
		disposeGui();
		executorService.shutdown();
	}

	public void closeGui() {
		// TODO before closing anything we need to find out whether there are operations
		// currently running / windows open that should/may not be closed

		// Close the application, but give him a chance to
		// confirm his action first
		if ((null != profileManager.getNextScheduleTask()) && preferences.confirmExit()) {
			MessageBox mb = new MessageBox(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
			mb.setText(Messages.getString("GuiController.Confirmation")); //$NON-NLS-1$
			String doYouWantToQuit = Messages.getString("GuiController.Do_You_Want_To_Quit"); //$NON-NLS-1$
			String scheduleIsStopped = Messages.getString("GuiController.Schedule_is_stopped"); //$NON-NLS-1$
			mb.setMessage(String.format("%s%n%s", doYouWantToQuit, scheduleIsStopped)); //$NON-NLS-1$

			// check whether the user really wants to close
			if (mb.open() != SWT.YES) {
				return;
			}
		}
		mainWindowProvider.get().storeWindowState();
		disposeGui();
	}

	private void disposeGui() {
		ExceptionHandler.registerExceptionHandler(oldExceptionHandler);
		mainWindowProvider.get().dispose();
		imageRepositoryProvider.get().dispose();
		fontRepositoryProvider.get().dispose();
		SystemTrayItem systemTrayItem = systemTrayItemProvider.get();
		if (!systemTrayItem.isDisposed()) {
			systemTrayItem.dispose();
		}
		if ((null != display) && !display.isDisposed()) {
			display.dispose();
		}
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

	public static void launchProgram(final String uri) {
		if (System.getProperty("os.name").toLowerCase().indexOf("linux") > -1) {
			Thread t = new Thread(() -> {
				try {
					Process p = Runtime.getRuntime().exec(new String[] { "xdg-open", uri });
					p.waitFor();
				}
				catch (IOException | InterruptedException e) {
					ExceptionHandler.reportException("Error opening " + uri + ".", e);
				}
			});
			// set this thread as a daemon to avoid hanging the FullSync shutdown
			// this might happen if xdg-open opens the browser directly and the
			// browser is still running
			t.setDaemon(true);
			t.start();
		}
		else {
			try {
				Program.launch(uri);
			}
			catch (Exception e) {
				ExceptionHandler.reportException("Error opening " + uri + ".", e);
			}
		}
	}

	private void createWelcomeScreen() {
		if ((null != System.getProperty("net.sourceforge.fullsync.skipWelcomeScreen", null)) || preferences.getSkipWelcomeScreen()) {
			return;
		}
		if (!preferences.getLastVersion().equals(Util.getFullSyncVersion())) {
			// update the stored version number
			preferences.save();
			try {
				welcomeScreenProvider.get().show();
			}
			catch (Exception e) {
				ExceptionHandler.reportException(e);
			}
		}
	}

	public void backgroundExec(AsyncUIUpdate job) {
		executorService.execute(new ExecuteBackgroundJob(job, display));
	}

	public static String getTwitterURL() {
		return Util.getResourceAsString("net/sourceforge/fullsync/twitter-url.txt").trim();
	}

	public static String getWebsiteURL() {
		return Util.getResourceAsString("net/sourceforge/fullsync/website-url.txt").trim();
	}
}
