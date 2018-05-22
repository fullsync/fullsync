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

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TrayItem;

import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.TaskGenerationListener;
import net.sourceforge.fullsync.TaskGenerator;
import net.sourceforge.fullsync.TaskTree;
import net.sourceforge.fullsync.fs.File;

@Singleton
public class SystemTrayItem implements TaskGenerationListener {
	private final Shell shell;
	private final Provider<ImageRepository> imageRepositoryProvider;
	private final Provider<MainWindow> mainWindowProvider;
	private final Provider<TaskGenerator> taskGeneratorProvider;
	private TrayItem trayItem;
	private Image[] imageList;
	private Menu menu;
	private int imageActive;
	private Timer timer;
	private boolean isBusy;

	@Inject
	public SystemTrayItem(Shell shell, Provider<ImageRepository> imageRepositoryProvider, Provider<MainWindow> mainWindowProvider,
		Provider<TaskGenerator> taskGeneratorProvider) {
		this.shell = shell;
		this.imageRepositoryProvider = imageRepositoryProvider;
		this.mainWindowProvider = mainWindowProvider;
		this.taskGeneratorProvider = taskGeneratorProvider;
	}

	public void show() {
		ImageRepository imageRepository = imageRepositoryProvider.get();
		trayItem = new TrayItem(shell.getDisplay().getSystemTray(), SWT.NULL);
		shell.getDisplay().addListener(SWT.Dispose, this::displayDisposed);

		imageList = new Image[2];
		imageList[0] = imageRepository.getImage("fullsync48.png"); //$NON-NLS-1$
		imageList[1] = imageRepository.getImage("fullsync48_r.png"); //$NON-NLS-1$
		imageActive = 0;

		// initialize trayItem
		trayItem.setImage(imageList[0]);
		trayItem.setToolTipText("FullSync"); //$NON-NLS-1$
		trayItem.addListener(SWT.Selection, e -> mainWindowProvider.get().setVisible(true));
		trayItem.addListener(SWT.DefaultSelection, e -> mainWindowProvider.get().setVisible(true));
		trayItem.addListener(SWT.MenuDetect, e -> menu.setVisible(true));

		// initialize popup menu
		menu = new Menu(shell, SWT.POP_UP);
		MenuItem item;
		item = new MenuItem(menu, SWT.NULL);
		item.setImage(imageRepository.getImage("fullsync16.png"));
		item.setText(Messages.getString("SystemTrayItem.OpenFullSync")); //$NON-NLS-1$
		item.addListener(SWT.Selection, e -> mainWindowProvider.get().setVisible(true));

		item = new MenuItem(menu, SWT.NULL);
		item.setText(Messages.getString("SystemTrayItem.Exit")); //$NON-NLS-1$
		item.addListener(SWT.Selection, e -> mainWindowProvider.get().closeGui());

		taskGeneratorProvider.get().addTaskGenerationListener(this);
	}

	public void setVisible(boolean visible) {
		trayItem.setVisible(visible);
	}

	private void displayDisposed(Event e) {
		taskGeneratorProvider.get().removeTaskGenerationListener(this);
		trayItem.dispose();
		menu.dispose();
		if (null != timer) {
			timer.cancel();
		}
	}

	@Override
	public void taskGenerationStarted(File source, File destination) {
		// not relevant
	}

	@Override
	public void taskGenerationFinished(Task task) {
		// not relevant
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
