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
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.google.inject.Injector;

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.Preferences;
import net.sourceforge.fullsync.ProfileManager;
import net.sourceforge.fullsync.Util;
import net.sourceforge.fullsync.cli.Main;

@Singleton
public class GuiController {
	private static GuiController singleton;
	private final Display display;
	private final Shell shell;
	private final ImageRepository imageRepository;
	private final FontRepository fontRepository;
	private final Provider<MainWindow> mainWindowProvider;
	private final Provider<SystemTrayItem> systemTrayItemProvider;
	private final Provider<WelcomeScreen> welcomeScreenProvider;
	private final Preferences preferences;
	private final ProfileManager profileManager;
	private final ScheduledThreadPoolExecutor executorService;
	private ExceptionHandler oldExceptionHandler;

	@Inject
	private GuiController(Display display, Shell shell, ImageRepository imageRepository, FontRepository fontRepository,
		Provider<MainWindow> mainWindowProvider, Provider<SystemTrayItem> systemTrayItemProvider,
		Provider<WelcomeScreen> welcomeScreenProvider, Preferences preferences, ProfileManager profileManager) {
		this.display = display;
		this.shell = shell;
		this.imageRepository = imageRepository;
		this.fontRepository = fontRepository;
		this.mainWindowProvider = mainWindowProvider;
		this.systemTrayItemProvider = systemTrayItemProvider;
		this.welcomeScreenProvider = welcomeScreenProvider;
		this.preferences = preferences;
		this.profileManager = profileManager;
		executorService = new ScheduledThreadPoolExecutor(1);
	}

	public Preferences getPreferences() {
		return preferences;
	}

	public Image getImage(String imageName) {
		return imageRepository.getImage(imageName);
	}

	private void startGui() {
		mainWindowProvider.get();
		systemTrayItemProvider.get().show();
		oldExceptionHandler = ExceptionHandler.registerExceptionHandler(new ExceptionHandler() {
			@Override
			protected void doReportException(final String message, final Throwable exception) {
				exception.printStackTrace();

				display.syncExec(() -> new ExceptionDialog(shell, message, exception));
			}
		});
		createWelcomeScreen();
	}

	public static void launchUI(Injector injector) {
		Injector uiInjector = injector.createChildInjector(new FullSyncUiModule());
		GuiController guiController = uiInjector.getInstance(GuiController.class);
		singleton = guiController;
		guiController.run(uiInjector);
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
		if (null != imageRepository) {
			imageRepository.dispose();
		}
		if (null != fontRepository) {
			fontRepository.dispose();
		}
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

	public static GuiController getInstance() {
		return singleton;
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

	public Font getFont(String name, int height, int style) {
		return fontRepository.getFont(name, height, style);
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

	public static void backgroundExec(AsyncUIUpdate job) {
		GuiController gc = getInstance();
		gc.executorService.execute(new ExecuteBackgroundJob(job, gc.display));
	}

	public static String getTwitterURL() {
		return Util.getResourceAsString("net/sourceforge/fullsync/twitter-url.txt").trim();
	}

	public static String getWebsiteURL() {
		return Util.getResourceAsString("net/sourceforge/fullsync/website-url.txt").trim();
	}
}
