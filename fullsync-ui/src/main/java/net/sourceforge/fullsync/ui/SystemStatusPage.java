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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

class SystemStatusPage extends WizardDialog {
	private Label totalMemory;
	private Label maxMemory;
	private Label freeMemory;
	private ProgressBar progressBarMemory;
	private Timer timer;
	private Composite content;

	@Inject
	public SystemStatusPage(Shell shell) {
		super(shell);
	}

	@Override
	public String getTitle() {
		return Messages.getString("SystemStatusPage.Title"); //$NON-NLS-1$
	}

	@Override
	public String getCaption() {
		return Messages.getString("SystemStatusPage.Caption"); //$NON-NLS-1$
	}

	@Override
	public String getDescription() {
		return Messages.getString("SystemStatusPage.Description"); //$NON-NLS-1$
	}

	@Override
	public String getIconName() {
		return null;
	}

	@Override
	public String getImageName() {
		return null;
	}

	@Override
	public void createContent(final Composite content) {
		this.content = content;
		// FIXME: add interesting versions and the system properties used by the launcher,...
		// TODO: add a way to report a bug here?
		try {
			content.setLayout(new GridLayout());
			var groupMemory = new Group(content, SWT.NONE);
			groupMemory.setLayout(new GridLayout(2, false));
			groupMemory.setText(Messages.getString("SystemStatusPage.JVMMemory")); //$NON-NLS-1$

			progressBarMemory = new ProgressBar(groupMemory, SWT.NONE);
			var progressBarMemoryLData = new GridData();
			progressBarMemoryLData.horizontalAlignment = SWT.FILL;
			progressBarMemoryLData.horizontalSpan = 2;
			progressBarMemory.setLayoutData(progressBarMemoryLData);

			// max memory
			var labelMaxMemory = new Label(groupMemory, SWT.NONE);
			labelMaxMemory.setText(Messages.getString("SystemStatusPage.MaxMemory")); //$NON-NLS-1$

			maxMemory = new Label(groupMemory, SWT.RIGHT);
			var maxMemoryLData = new GridData();
			maxMemoryLData.horizontalAlignment = SWT.FILL;
			maxMemory.setLayoutData(maxMemoryLData);

			// total memory
			var labelTotalMemory = new Label(groupMemory, SWT.NONE);
			labelTotalMemory.setText(Messages.getString("SystemStatusPage.TotalMemory")); //$NON-NLS-1$
			totalMemory = new Label(groupMemory, SWT.RIGHT);
			var totalMemoryLData = new GridData();
			totalMemoryLData.horizontalAlignment = SWT.FILL;
			totalMemory.setLayoutData(totalMemoryLData);

			// free memory
			var labelFreeMemory = new Label(groupMemory, SWT.NONE);
			labelFreeMemory.setText(Messages.getString("SystemStatusPage.FreeMemory")); //$NON-NLS-1$

			freeMemory = new Label(groupMemory, SWT.RIGHT);
			freeMemory.setText(""); //$NON-NLS-1$
			var freeMemoryLData = new GridData();
			freeMemoryLData.horizontalAlignment = SWT.FILL;
			freeMemory.setLayoutData(freeMemoryLData);

			// gc button
			var buttonMemoryGc = new Button(groupMemory, SWT.PUSH | SWT.CENTER);
			buttonMemoryGc.setText(Messages.getString("SystemStatusPage.CleanUp")); //$NON-NLS-1$
			var buttonMemoryGcLData = new GridData();
			buttonMemoryGc.addListener(SWT.Selection, e -> System.gc());
			buttonMemoryGcLData.horizontalAlignment = SWT.END;
			buttonMemoryGcLData.horizontalSpan = 2;
			buttonMemoryGc.setLayoutData(buttonMemoryGcLData);

			timerFired();
			timer = new Timer(true);
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					timerFired();
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

	private void timerFired() {
		if (!content.isDisposed()) {
			var display = getDisplay();
			if ((null == display) || display.isDisposed()) {
				timer.cancel();
				return;
			}
			display.asyncExec(this::updateView);
		}
	}

	private void updateView() {
		if (!content.isDisposed()) {
			var rt = Runtime.getRuntime();
			var ltotalMemory = rt.totalMemory();
			var lmaxMemory = rt.maxMemory();
			var lfreeMemory = rt.freeMemory();

			totalMemory.setText(UISettings.formatSize(ltotalMemory));
			maxMemory.setText(UISettings.formatSize(lmaxMemory));
			freeMemory.setText(UISettings.formatSize(lfreeMemory));
			progressBarMemory.setMaximum((int) (ltotalMemory / 1024));
			progressBarMemory.setSelection((int) ((ltotalMemory - lfreeMemory) / 1024));
			content.layout();
		}
	}

	@Override
	public void dispose() {
		timer.cancel();
		super.dispose();
	}
}
