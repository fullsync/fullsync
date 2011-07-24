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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;

/**
 * This code was generated using CloudGarden's Jigloo
 * SWT/Swing GUI Builder, which is free for non-commercial
 * use. If Jigloo is being used commercially (ie, by a corporation,
 * company or business for any purpose whatever) then you
 * should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details.
 * Use of Jigloo implies acceptance of these licensing terms.
 * *************************************
 * A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED
 * for this machine, so Jigloo or this code cannot be used legally
 * for any corporate or commercial purpose.
 * *************************************
 */
public class SystemStatusComposite extends org.eclipse.swt.widgets.Composite {
	private Group groupMemory;
	private Label label1;
	private Label labelMemoryTotal;
	private Label label2;
	private Button buttonMemoryGc;
	private ProgressBar progressBarMemory;
	private Label labelMemoryMax;
	private Label label3;
	private Label labelMemoryFree;
	private Timer timer;

	public SystemStatusComposite(org.eclipse.swt.widgets.Composite parent, int style) {
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
			{
				groupMemory = new Group(this, SWT.NONE);
				GridLayout groupMemoryLayout = new GridLayout();
				groupMemoryLayout.numColumns = 2;
				groupMemory.setLayout(groupMemoryLayout);
				groupMemory.setText("VM Memory");
				{
					progressBarMemory = new ProgressBar(groupMemory, SWT.NONE);
					GridData progressBarMemoryLData = new GridData();
					progressBarMemoryLData.horizontalSpan = 2;
					progressBarMemory.setLayoutData(progressBarMemoryLData);
				}
				{
					label1 = new Label(groupMemory, SWT.NONE);
					label1.setText("Total Memory:");
				}
				{
					labelMemoryTotal = new Label(groupMemory, SWT.RIGHT);
					labelMemoryTotal.setText("<total memory>");
					GridData labelMemoryTotalLData = new GridData();
					labelMemoryTotalLData.horizontalAlignment = GridData.FILL;
					labelMemoryTotal.setLayoutData(labelMemoryTotalLData);
				}
				{
					label3 = new Label(groupMemory, SWT.NONE);
					label3.setText("Max Memory:");
				}
				{
					labelMemoryMax = new Label(groupMemory, SWT.RIGHT);
					labelMemoryMax.setText("<max memory>");
					GridData labelMemoryMaxLData = new GridData();
					labelMemoryMaxLData.horizontalAlignment = GridData.FILL;
					labelMemoryMax.setLayoutData(labelMemoryMaxLData);
				}
				{
					label2 = new Label(groupMemory, SWT.NONE);
					label2.setText("Free Memory:");
				}
				{
					labelMemoryFree = new Label(groupMemory, SWT.RIGHT);
					labelMemoryFree.setText("<free memory>");
					GridData labelMemoryFreeLData = new GridData();
					labelMemoryFreeLData.horizontalAlignment = GridData.FILL;
					labelMemoryFree.setLayoutData(labelMemoryFreeLData);
				}
				{
					buttonMemoryGc = new Button(groupMemory, SWT.PUSH | SWT.CENTER);
					buttonMemoryGc.setText("Clean up");
					GridData buttonMemoryGcLData = new GridData();
					buttonMemoryGc.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent evt) {
							System.gc();
						}
					});
					buttonMemoryGcLData.horizontalAlignment = GridData.END;
					buttonMemoryGcLData.horizontalSpan = 2;
					buttonMemoryGc.setLayoutData(buttonMemoryGcLData);
				}
			}
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

				long totalMemory = rt.totalMemory();
				long maxMemory = rt.maxMemory();
				long freeMemory = rt.freeMemory();
				
				labelMemoryTotal.setText(String.valueOf(totalMemory));
				labelMemoryMax.setText(String.valueOf(maxMemory));
				labelMemoryFree.setText(String.valueOf(freeMemory));
				progressBarMemory.setMaximum((int) (totalMemory / 1024));
				progressBarMemory.setSelection((int) ((totalMemory - freeMemory) / 1024));
			}
		});
	}

	@Override
	public void dispose() {
		timer.cancel();
		super.dispose();
	}
}
