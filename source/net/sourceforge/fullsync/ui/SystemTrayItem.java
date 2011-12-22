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
/*
 * Created on 16.10.2004
 */
package net.sourceforge.fullsync.ui;

import java.util.Timer;
import java.util.TimerTask;

import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.TaskGenerationListener;
import net.sourceforge.fullsync.TaskTree;
import net.sourceforge.fullsync.fs.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class SystemTrayItem implements TaskGenerationListener {
	private GuiController guiController;
	private Tray tray;
	private TrayItem trayItem;
	private Menu menu;

	private Image[] imageList;
	private int imageActive;

	private Timer timer;
	private boolean isBusy;

	public SystemTrayItem(GuiController gui) {
		this.guiController = gui;
		this.tray = guiController.getDisplay().getSystemTray();
		this.trayItem = new TrayItem(tray, SWT.NULL);

		imageList = new Image[2];
		imageList[0] = GuiController.getInstance().getImage("Tray_Active_01.png"); //$NON-NLS-1$
		imageList[1] = GuiController.getInstance().getImage("Tray_Active_02.png"); //$NON-NLS-1$
		imageActive = 0;

		// initialize trayItem
		trayItem.setImage(imageList[0]);
		trayItem.setToolTipText("FullSync"); //$NON-NLS-1$
		trayItem.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				guiController.setMainShellVisible(true);
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				guiController.setMainShellVisible(true);
			}
		});
		trayItem.addListener(SWT.MenuDetect, new Listener() {
			@Override
			public void handleEvent(final Event evt) {
				menu.setVisible(true);
			}
		});

		// initialize popup menu
		menu = new Menu(guiController.getMainShell(), SWT.POP_UP);
		MenuItem item;
		item = new MenuItem(menu, SWT.NULL);
		item.setText(Messages.getString("SystemTrayItem.OpenFullSync")); //$NON-NLS-1$
		item.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				guiController.setMainShellVisible(true);
			}
		});

		item = new MenuItem(menu, SWT.NULL);
		item.setText(Messages.getString("SystemTrayItem.Exit")); //$NON-NLS-1$
		item.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				guiController.closeGui();
			}
		});

		guiController.getSynchronizer().getTaskGenerator().addTaskGenerationListener(this);
	}

	public void setVisible(boolean visible) {
		trayItem.setVisible(visible);
	}

	public boolean isDisposed() {
		return trayItem.isDisposed();
	}

	public void dispose() {
		trayItem.dispose();
		menu.dispose();
		for (Image element : imageList) {
			element.dispose();
		}
	}

	@Override
	public void taskGenerationStarted(File source, File destination) {

	}

	@Override
	public void taskGenerationFinished(Task task) {

	}

	@Override
	public synchronized void taskTreeStarted(TaskTree tree) {
		if (!isBusy) {
			this.timer = new Timer();
			isBusy = true;
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					imageActive++;
					if (imageActive >= imageList.length) {
						imageActive = 0;
					}
					trayItem.getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							trayItem.setImage(imageList[imageActive]);
						}
					});
				}
			}, 0, 500);
		}
	}

	@Override
	public synchronized void taskTreeFinished(TaskTree tree) {
		if (isBusy) {
			isBusy = false;
			timer.cancel();
			timer = null;

			imageActive = 0;
			trayItem.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					trayItem.setImage(imageList[imageActive]);
				}
			});
		}
	}
}