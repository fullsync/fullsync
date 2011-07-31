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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;

public class SystemStatusComposite extends Composite {
	private Label totalMemory;
	private Label maxMemory;
	private Label freeMemory;
	private ProgressBar progressBarMemory;
	private Timer timer;

	public SystemStatusComposite(Composite parent, int style) {
		super(parent, style);
		initGUI();
		updateView();
		timer = new Timer(true);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				updateView();
			}
		}, 1000, 1000);
	}

	private void initGUI() {
		try {
			this.setLayout(new GridLayout());
			Group groupMemory = new Group(this, SWT.NONE);
			groupMemory.setLayout(new GridLayout(2, false));
			groupMemory.setText("VM Memory");

			progressBarMemory = new ProgressBar(groupMemory, SWT.NONE);
			GridData progressBarMemoryLData = new GridData();
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
			buttonMemoryGc.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent evt) {
					System.gc();
				}
			});
			buttonMemoryGcLData.horizontalAlignment = SWT.END;
			buttonMemoryGcLData.horizontalSpan = 2;
			buttonMemoryGc.setLayoutData(buttonMemoryGcLData);

			this.layout();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateView() {
		Display display = getDisplay();
		if (display.isDisposed()) {
			timer.cancel();
			return;
		}
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				Runtime rt = Runtime.getRuntime();

				long ltotalMemory = rt.totalMemory();
				long lmaxMemory = rt.maxMemory();
				long lfreeMemory = rt.freeMemory();

				totalMemory.setText(String.valueOf(ltotalMemory));
				maxMemory.setText(String.valueOf(lmaxMemory));
				freeMemory.setText(String.valueOf(lfreeMemory));
				progressBarMemory.setMaximum((int) (ltotalMemory / 1024));
				progressBarMemory.setSelection((int) ((ltotalMemory - lfreeMemory) / 1024));
			}
		});
	}

	@Override
	public void dispose() {
		timer.cancel();
		super.dispose();
	}
}
