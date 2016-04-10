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

import java.util.Timer;
import java.util.TimerTask;

import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.TaskGenerationListener;
import net.sourceforge.fullsync.TaskTree;
import net.sourceforge.fullsync.fs.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

public class SystemTrayItem implements TaskGenerationListener {
	private TrayItem trayItem;
	private Menu menu;
	private Image[] imageList;
	private int imageActive;
	private Timer timer;
	private boolean isBusy;

	public SystemTrayItem(final GuiController guiController) {
		Tray tray = guiController.getDisplay().getSystemTray();
		this.trayItem = new TrayItem(tray, SWT.NULL);

		imageList = new Image[2];
		imageList[0] = guiController.getImage("fullsync48.png"); //$NON-NLS-1$
		imageList[1] = guiController.getImage("fullsync48_r.png"); //$NON-NLS-1$
		imageActive = 0;

		// initialize trayItem
		trayItem.setImage(imageList[0]);
		trayItem.setToolTipText("FullSync"); //$NON-NLS-1$
		trayItem.addListener(SWT.Selection, e -> guiController.setMainShellVisible(true));
		trayItem.addListener(SWT.DefaultSelection, e -> guiController.setMainShellVisible(true));
		trayItem.addListener(SWT.MenuDetect, e -> menu.setVisible(true));

		// initialize popup menu
		menu = new Menu(guiController.getMainShell(), SWT.POP_UP);
		MenuItem item;
		item = new MenuItem(menu, SWT.NULL);
		item.setImage(guiController.getImage("fullsync16.png"));
		item.setText(Messages.getString("SystemTrayItem.OpenFullSync")); //$NON-NLS-1$
		item.addListener(SWT.Selection, e -> guiController.setMainShellVisible(true));

		item = new MenuItem(menu, SWT.NULL);
		item.setText(Messages.getString("SystemTrayItem.Exit")); //$NON-NLS-1$
		item.addListener(SWT.Selection, e -> guiController.closeGui());

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
					imageActive = (imageActive + 1) % imageList.length;
					trayItem.getDisplay().asyncExec(() -> trayItem.setImage(imageList[imageActive]));
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
			trayItem.getDisplay().asyncExec(() -> trayItem.setImage(imageList[imageActive]));
		}
	}
}
