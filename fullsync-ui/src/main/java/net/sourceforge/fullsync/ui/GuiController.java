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
import java.util.concurrent.ScheduledExecutorService;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.google.common.util.concurrent.SettableFuture;
import com.google.inject.Injector;

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.FullSync;
import net.sourceforge.fullsync.Preferences;
import net.sourceforge.fullsync.Util;
import net.sourceforge.fullsync.cli.Main;

@Singleton
public class GuiController { // NO_UCD (use default)
	private final FullSync fullSync;
	private final Display display;
	private final Shell shell;
	private final Provider<MainWindow> mainWindowProvider;
	private final Provider<SystemTrayItem> systemTrayItemProvider;
	private final Provider<WelcomeScreen> welcomeScreenProvider;
	private final Preferences preferences;
	private final ScheduledExecutorService scheduledExecutorService;
	private ExceptionHandler oldExceptionHandler;

	@Inject
	private GuiController(FullSync fullSync, ScheduledExecutorService scheduledExecutorService, Display display, Shell shell,
		Provider<MainWindow> mainWindowProvider, Provider<SystemTrayItem> systemTrayItemProvider,
		Provider<WelcomeScreen> welcomeScreenProvider, Preferences preferences) {
		this.fullSync = fullSync;
		this.display = display;
		this.shell = shell;
		this.mainWindowProvider = mainWindowProvider;
		this.systemTrayItemProvider = systemTrayItemProvider;
		this.welcomeScreenProvider = welcomeScreenProvider;
		this.preferences = preferences;
		this.scheduledExecutorService = scheduledExecutorService;
		var languageCode = preferences.getLanguageCode();
		try {
			Messages.setLanguage(languageCode);
		}
		catch (MissingResourceException ex) {
			ExceptionHandler.reportException(Messages.getString("GuiController.FailedToSetLanguageTo", languageCode), ex); //$NON-NLS-1$
		}
	}

	private void startGui() {
		oldExceptionHandler = ExceptionHandler.registerExceptionHandler(new ExceptionHandler() {
			@Override
			protected void doReportException(final String message, final Throwable exception) {
				exception.printStackTrace();

				display.syncExec(() -> new ExceptionDialog(shell, message, exception));
			}
		});
		display.addListener(SWT.Dispose, this::disposeGui);
		fullSync.pushQuestionHandler(this::showQuestion);
		mainWindowProvider.get();
		systemTrayItemProvider.get().show();
		createWelcomeScreen();
	}

	static void launchUI(Injector injector) {
		var uiInjector = injector.createChildInjector(new FullSyncUiModule());
		var guiController = uiInjector.getInstance(GuiController.class);
		guiController.run(uiInjector);
	}

	private Future<Boolean> showQuestion(String question) {
		SettableFuture<Boolean> answer = SettableFuture.create();
		if (display == Display.findDisplay(Thread.currentThread())) {
			answer.set(doShowQuestion(question));
		}
		else {
			display.asyncExec(() -> answer.set(doShowQuestion(question)));
		}
		return answer;
	}

	private boolean doShowQuestion(String question) {
		var mb = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		mb.setText(Messages.getString("GuiController.YesNoQuestion")); //$NON-NLS-1$
		mb.setMessage(question);
		return SWT.YES == mb.open();
	}

	private void run(Injector uiInjector) {
		startGui();
		scheduledExecutorService.submit(() -> Main.finishStartup(uiInjector));
		try {
			while (!display.isDisposed()) {
				try {
					if (!display.readAndDispatch()) {
						display.sleep();
					}
				} catch (SWTException ex) {
					ex.printStackTrace();
				}
				//TODO: other exceptions can escape here and crash the application, log somewhere?
			}
		} finally {
			if (!display.isDisposed()) {
				display.dispose();
			}
		}
	}

	private void disposeGui(Event e) {
		ExceptionHandler.registerExceptionHandler(oldExceptionHandler);
	}

	public static void launchProgram(final String uri) { // NO_UCD (use default)
		if (System.getProperty("os.name").toLowerCase().contains("linux")) { //$NON-NLS-1$ //$NON-NLS-2$
			var t = new Thread(() -> {
				try {
					String[] args = {
						"xdg-open", //$NON-NLS-1$
						uri
					};
					var p = Runtime.getRuntime().exec(args);
					p.waitFor();
				}
				catch (IOException | InterruptedException e) {
					ExceptionHandler.reportException(Messages.getString("GuiController.FailedToLaunchProgramOrUrl", uri), e); //$NON-NLS-1$
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
				ExceptionHandler.reportException(Messages.getString("GuiController.FailedToLaunchProgramOrUrl", uri), e); //$NON-NLS-1$
			}
		}
	}

	private void createWelcomeScreen() {
		if ((null != System.getProperty("net.sourceforge.fullsync.skipWelcomeScreen", null)) || preferences.getSkipWelcomeScreen()) { //$NON-NLS-1$
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

	public static String getTwitterURL() { // NO_UCD (use default)
		return Util.getResourceAsString("net/sourceforge/fullsync/twitter-url.txt").trim(); //$NON-NLS-1$
	}

	public static String getWebsiteURL() { // NO_UCD (use default)
		return Util.getResourceAsString("net/sourceforge/fullsync/website-url.txt").trim(); //$NON-NLS-1$
	}
}
