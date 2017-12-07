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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;

public class SystemStatusPage extends WizardDialog {
	private Label totalMemory;
	private Label maxMemory;
	private Label freeMemory;
	private ProgressBar progressBarMemory;
	private Timer timer;
	private Composite content;

	@Inject
	public SystemStatusPage(MainWindow mainWindow) {
		super(mainWindow.getShell());
	}

	@Override
	public String getTitle() {
		return "System Status";
	}

	@Override
	public String getCaption() {
		return "System Status";
	}

	@Override
	public String getDescription() {
		return "";
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
	public void createContent(final Composite content) {
		this.content = content;
		// FIXME: add interesting versions and the system properties used by the launcher,...
		// TODO: add a way to report a bug here?
		try {
			content.setLayout(new GridLayout());
			Group groupMemory = new Group(content, SWT.NONE);
			groupMemory.setLayout(new GridLayout(2, false));
			groupMemory.setText("VM Memory");

			progressBarMemory = new ProgressBar(groupMemory, SWT.NONE);
			GridData progressBarMemoryLData = new GridData();
			progressBarMemoryLData.horizontalAlignment = SWT.FILL;
			progressBarMemoryLData.horizontalSpan = 2;
			progressBarMemory.setLayoutData(progressBarMemoryLData);

			// total memory
			Label labelTotalMemory = new Label(groupMemory, SWT.NONE);
			labelTotalMemory.setText("Total Memory:");
			totalMemory = new Label(groupMemory, SWT.RIGHT);
			GridData totalMemoryLData = new GridData();
			totalMemoryLData.horizontalAlignment = SWT.FILL;
			totalMemory.setLayoutData(totalMemoryLData);

			// max memory
			Label labelMaxMemory = new Label(groupMemory, SWT.NONE);
			labelMaxMemory.setText("Max Memory:");

			maxMemory = new Label(groupMemory, SWT.RIGHT);
			GridData maxMemoryLData = new GridData();
			maxMemoryLData.horizontalAlignment = SWT.FILL;
			maxMemory.setLayoutData(maxMemoryLData);

			// free memory
			Label labelFreeMemory = new Label(groupMemory, SWT.NONE);
			labelFreeMemory.setText("Free Memory:");

			freeMemory = new Label(groupMemory, SWT.RIGHT);
			freeMemory.setText("<free memory>");
			GridData freeMemoryLData = new GridData();
			freeMemoryLData.horizontalAlignment = SWT.FILL;
			freeMemory.setLayoutData(freeMemoryLData);

			// gc button
			Button buttonMemoryGc = new Button(groupMemory, SWT.PUSH | SWT.CENTER);
			buttonMemoryGc.setText("Clean up");
			GridData buttonMemoryGcLData = new GridData();
			buttonMemoryGc.addListener(SWT.Selection, e -> System.gc());
			buttonMemoryGcLData.horizontalAlignment = SWT.END;
			buttonMemoryGcLData.horizontalSpan = 2;
			buttonMemoryGc.setLayoutData(buttonMemoryGcLData);

			updateView();
			timer = new Timer(true);
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					updateView();
				}
			}, 1000, 1000);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean apply() {
		return true;
	}

	@Override
	public boolean cancel() {
		return true;
	}

	public void updateView() {
		if (!content.isDisposed()) {
			Display display = getDisplay();
			if ((null == display) || display.isDisposed()) {
				timer.cancel();
				return;
			}
			display.syncExec(() -> {
				Runtime rt = Runtime.getRuntime();
				long ltotalMemory = rt.totalMemory();
				long lmaxMemory = rt.maxMemory();
				long lfreeMemory = rt.freeMemory();

				totalMemory.setText(UISettings.formatSize(ltotalMemory));
				maxMemory.setText(UISettings.formatSize(lmaxMemory));
				freeMemory.setText(UISettings.formatSize(lfreeMemory));
				progressBarMemory.setMaximum((int) (ltotalMemory / 1024));
				progressBarMemory.setSelection((int) ((ltotalMemory - lfreeMemory) / 1024));
				content.layout();
			});
		}
	}

	@Override
	public void dispose() {
		timer.cancel();
		super.dispose();
	}
}
