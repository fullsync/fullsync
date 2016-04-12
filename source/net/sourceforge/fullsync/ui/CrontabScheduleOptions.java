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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.schedule.CrontabPart;
import net.sourceforge.fullsync.schedule.CrontabSchedule;
import net.sourceforge.fullsync.schedule.Schedule;

class CrontabScheduleOptions extends ScheduleOptions {
	class PartContainer {
		private CrontabPart part;
		private Button cbAll;
		private Text text;
		private Button buttonChoose;

		PartContainer(CrontabPart crontabPart) {
			this.part = crontabPart;

			Label label = new Label(CrontabScheduleOptions.this, SWT.NULL);
			label.setText(part.name);

			cbAll = new Button(CrontabScheduleOptions.this, SWT.CHECK);
			cbAll.setText(Messages.getString("CrontabScheduleOptions.all")); //$NON-NLS-1$
			cbAll.setSelection(true);
			cbAll.addListener(SWT.Selection, e -> {
				text.setEnabled(!cbAll.getSelection());
				buttonChoose.setEnabled(!cbAll.getSelection());
			});

			text = new Text(CrontabScheduleOptions.this, SWT.BORDER);
			text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			text.setText("*"); //$NON-NLS-1$
			text.setEnabled(false);

			buttonChoose = new Button(CrontabScheduleOptions.this, SWT.NULL);
			buttonChoose.setText("..."); //$NON-NLS-1$
			buttonChoose.setEnabled(false);
			buttonChoose.addListener(SWT.Selection, e -> {
				final Shell shell = new Shell(getShell(), SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM | SWT.TOOL);
				shell.setLayout(new GridLayout(2, false));
				shell.setText(Messages.getString("CrontabScheduleOptions.Select") + part.name); //$NON-NLS-1$

				final List table = new List(shell, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
				GridData data = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
				table.setLayoutData(data);
				for (int i = part.low; i <= part.high; ++i) {
					table.add(String.valueOf(i));
				}
				try {
					table.select(part.createInstance(text.getText()).getIntArray(-part.low));
				}
				catch (DataParseException dpe) {
					ExceptionHandler.reportException(dpe);
				}

				Button buttonOk = new Button(shell, SWT.NULL);
				GridData buttonOkLData = new GridData();
				buttonOkLData.grabExcessHorizontalSpace = true;
				buttonOkLData.horizontalAlignment = SWT.RIGHT;
				buttonOkLData.widthHint = UISettings.BUTTON_WIDTH;
				buttonOkLData.heightHint = UISettings.BUTTON_HEIGHT;
				buttonOk.setLayoutData(buttonOkLData);
				buttonOk.setText(Messages.getString("CrontabScheduleOptions.Ok")); //$NON-NLS-1$
				buttonOk.addListener(SWT.Selection, evt -> {
					text.setText(part.createInstance(table.getSelectionIndices(), -part.low).pattern);
					shell.dispose();
				});

				Button buttonClose = new Button(shell, SWT.NULL);
				GridData buttonCloseLData = new GridData();
				buttonCloseLData.horizontalAlignment = SWT.FILL;
				buttonCloseLData.widthHint = UISettings.BUTTON_WIDTH;
				buttonCloseLData.heightHint = UISettings.BUTTON_HEIGHT;
				buttonClose.setLayoutData(buttonCloseLData);
				buttonClose.setText(Messages.getString("CrontabScheduleOptions.Close")); //$NON-NLS-1$
				buttonClose.addListener(SWT.Selection, evt -> shell.dispose());

				shell.setLocation(buttonChoose.toDisplay(0, 0));
				shell.setSize(UISettings.BUTTON_WIDTH * 3, 300);
				shell.layout();
				shell.open();
				Display display = getDisplay();
				while (!shell.isDisposed()) {
					if (!display.readAndDispatch()) {
						display.sleep();
					}
				}
			});
		}

		public void setInstance(final CrontabPart.Instance instance) {
			if (instance.all) {
				text.setText("*"); //$NON-NLS-1$
			}
			else {
				text.setText(instance.pattern);
			}
			cbAll.setSelection(instance.all);
			text.setEnabled(!instance.all);
			buttonChoose.setEnabled(!instance.all);
		}

		public CrontabPart.Instance getInstance() throws DataParseException {
			String pattern = text.getText();
			if (cbAll.getSelection()) {
				pattern = "*"; //$NON-NLS-1$
			}
			return part.createInstance(pattern);
		}
	}

	private PartContainer[] parts;

	CrontabScheduleOptions(Composite parent, int style) {
		super(parent, style);
		try {
			this.setLayout(new GridLayout(4, false));
			CrontabPart[] cronParts = CrontabPart.ALL_PARTS;
			parts = new PartContainer[cronParts.length];
			for (int i = 0; i < parts.length; i++) {
				parts[i] = new PartContainer(cronParts[i]);
			}
			this.layout();
		}
		catch (Exception e) {
			ExceptionHandler.reportException(e);
		}
	}

	@Override
	public String getSchedulingName() {
		return Messages.getString("CrontabScheduleOptions.Crontab"); //$NON-NLS-1$
	}

	@Override
	public boolean canHandleSchedule(final Schedule schedule) {
		return schedule instanceof CrontabSchedule;
	}

	@Override
	public void setSchedule(final Schedule schedule) {
		if (schedule instanceof CrontabSchedule) {
			CrontabPart.Instance[] instances = ((CrontabSchedule) schedule).getParts();
			for (int i = 0; i < instances.length; i++) {
				parts[i].setInstance(instances[i]);
			}
		}
	}

	@Override
	public Schedule getSchedule() throws DataParseException {
		return new CrontabSchedule(parts[0].getInstance(), parts[1].getInstance(), parts[2].getInstance(), parts[3].getInstance(),
				parts[4].getInstance());
	}
}
