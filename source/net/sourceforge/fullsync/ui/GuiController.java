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

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.Preferences;
import net.sourceforge.fullsync.ProfileManager;
import net.sourceforge.fullsync.Synchronizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 *
 *         TODO this class should also handle images
 */
public class GuiController implements Runnable {
	private static GuiController singleton;

	private final Preferences preferences;
	private final ProfileManager profileManager;
	private final Synchronizer synchronizer;

	private ExceptionHandler oldExceptionHandler;

	private Display display;
	private ImageRepository imageRepository;
	private FontRepository fontRepository;
	private Shell mainShell;
	private MainWindow mainWindow;
	private SystemTrayItem systemTrayItem;

	public GuiController(Preferences preferences, ProfileManager profileManager, Synchronizer synchronizer) {
		this.preferences = preferences;
		this.profileManager = profileManager;
		this.synchronizer = synchronizer;

		singleton = this;
	}

	private void createMainShell(boolean minimized) {
		try {
			mainShell = new Shell(display);
			mainWindow = new MainWindow(mainShell, SWT.NULL, this);
			mainShell.setLayout(new FillLayout());
			Rectangle shellBounds = mainShell.computeTrim(0, 0, mainWindow.getSize().x, mainWindow.getSize().y);
			mainShell.setSize(shellBounds.width, shellBounds.height);
			mainShell.setText("FullSync"); //$NON-NLS-1$
			mainShell.setImage(getImage("fullsync48.png")); //$NON-NLS-1$
			WelcomeScreen w = new WelcomeScreen(mainShell);
			if (!minimized) {
				mainShell.setVisible(true);
			}
		}
		catch (Exception e) {
			ExceptionHandler.reportException(e);
		}
	}

	public void setMainShellVisible(boolean visible) {
		mainShell.setVisible(visible);
		mainShell.setMinimized(!visible);
	}

	public Shell getMainShell() {
		return mainShell;
	}

	public MainWindow getMainWindow() {
		return mainWindow;
	}

	public Preferences getPreferences() {
		return preferences;
	}

	public ProfileManager getProfileManager() {
		return profileManager;
	}

	public Synchronizer getSynchronizer() {
		return synchronizer;
	}

	public Display getDisplay() {
		return display;
	}

	public Image getImage(String imageName) {
		return imageRepository.getImage(imageName);
	}

	public void startGui(boolean minimized) {
		display = Display.getDefault();
		imageRepository = new ImageRepository(display);
		fontRepository = new FontRepository(display);
		createMainShell(minimized);
		systemTrayItem = new SystemTrayItem(this);
		oldExceptionHandler = ExceptionHandler.registerExceptionHandler(new ExceptionHandler() {
			@Override
			protected void doReportException(final String message, final Throwable exception) {
				exception.printStackTrace();

				display.syncExec(new Runnable() {
					@Override
					public void run() {
						new ExceptionDialog(mainShell, message, exception);
					}
				});
			}
		});
	}

	@Override
	public void run() {
		while (!mainShell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	public void closeGui() {
		// TODO before closing anything we need to find out whether there are operations
		// currently running / windows open that should/may not be closed

		// Close the application, but give him a chance to
		// confirm his action first
		if ((profileManager.getNextScheduleTask() != null) && preferences.confirmExit()) {
			MessageBox mb = new MessageBox(mainShell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
			mb.setText(Messages.getString("GuiController.Confirmation")); //$NON-NLS-1$
			mb.setMessage(Messages.getString("GuiController.Do_You_Want_To_Quit") + "\n" //$NON-NLS-1$ //$NON-NLS-2$
					+ Messages.getString("GuiController.Schedule_is_stopped")); //$NON-NLS-1$

			// check whether the user really wants to close
			if (mb.open() != SWT.YES) {
				return;
			}
		}

		GuiController.getInstance().getProfileManager().disconnectRemote();
		GuiController.getInstance().getSynchronizer().disconnectRemote();

		disposeGui();
	}

	public void disposeGui() {
		ExceptionHandler.registerExceptionHandler(oldExceptionHandler);
		if ((mainShell != null) && !mainShell.isDisposed()) {
			mainShell.dispose();
		}
		if (imageRepository != null) {
			imageRepository.dispose();
		}
		if (fontRepository != null) {
			fontRepository.dispose();
		}
		if ((systemTrayItem != null) && !systemTrayItem.isDisposed()) {
			systemTrayItem.dispose();
		}
		if ((display != null) && !display.isDisposed()) {
			display.dispose();
		}
	}

	// TODO the busy cursor should be applied only to the window that is busy
	// difficulty: getShell() can only be accessed by the display thread :-/
	public void showBusyCursor(final boolean show) {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
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
			}
		});
	}

	public static GuiController getInstance() {
		return singleton;
	}

	public static void launchProgram(final String uri) {
		if (System.getProperty("os.name").toLowerCase().indexOf("linux") > -1) {
			Thread t = new Thread() {
				@Override
				public void run() {
					try {
						Process p = Runtime.getRuntime().exec(new String[] { "xdg-open", uri });
						p.waitFor();
					}
					catch (IOException e) {
						ExceptionHandler.reportException("Error opening " + uri + ".", e);
					}
					catch (InterruptedException e) {
						ExceptionHandler.reportException("Error opening " + uri + ".", e);
					}
				};
			};
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
}
