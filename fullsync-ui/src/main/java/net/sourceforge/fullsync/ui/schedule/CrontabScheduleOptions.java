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
package net.sourceforge.fullsync.ui.schedule;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.schedule.CrontabPart;
import net.sourceforge.fullsync.schedule.CrontabSchedule;
import net.sourceforge.fullsync.schedule.Schedule;
import net.sourceforge.fullsync.ui.Messages;
import net.sourceforge.fullsync.ui.UISettings;

public class CrontabScheduleOptions extends ScheduleOptions {
	private static class PartContainer {
		private static final String CRON_ALL = "*"; //$NON-NLS-1$
		private final CrontabPart part;
		private final Button cbAll;
		private final Text text;
		private final Button buttonChoose;

		PartContainer(CrontabScheduleOptions parent, CrontabPart crontabPart, String shellTitle, String partName) {
			part = crontabPart;

			var label = new Label(parent, SWT.NULL);
			label.setText(partName);

			cbAll = new Button(parent, SWT.CHECK);
			cbAll.setText(Messages.getString("CrontabScheduleOptions.all")); //$NON-NLS-1$
			cbAll.setSelection(true);
			cbAll.addListener(SWT.Selection, this::cbAllSelected);

			text = new Text(parent, SWT.BORDER);
			text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			text.setText(CRON_ALL);
			text.setEnabled(false);

			buttonChoose = new Button(parent, SWT.NULL);
			buttonChoose.setText(Messages.getString("CrontabScheduleOptions.Choose")); //$NON-NLS-1$
			buttonChoose.setEnabled(false);
			buttonChoose.addListener(SWT.Selection, e -> {
				final var shell = new Shell(parent.getShell(), SWT.PRIMARY_MODAL | SWT.DIALOG_TRIM | SWT.TOOL);
				shell.setLayout(new GridLayout(2, false));
				shell.setText(shellTitle);

				final var table = new List(shell, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
				var data = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
				table.setLayoutData(data);
				for (var i = part.low(); i <= part.high(); ++i) {
					table.add(String.valueOf(i));
				}
				try {
					table.select(part.createInstance(text.getText()).getIntArray(-part.low()));
				}
				catch (DataParseException dpe) {
					ExceptionHandler.reportException(dpe);
				}

				var buttonOk = new Button(shell, SWT.NULL);
				var buttonOkLData = new GridData();
				buttonOkLData.grabExcessHorizontalSpace = true;
				buttonOkLData.horizontalAlignment = SWT.RIGHT;
				buttonOkLData.widthHint = UISettings.BUTTON_WIDTH;
				buttonOkLData.heightHint = UISettings.BUTTON_HEIGHT;
				buttonOk.setLayoutData(buttonOkLData);
				buttonOk.setText(Messages.getString("CrontabScheduleOptions.Ok")); //$NON-NLS-1$
				buttonOk.addListener(SWT.Selection, evt -> {
					text.setText(part.createInstance(table.getSelectionIndices(), -part.low()).pattern);
					shell.dispose();
				});

				var buttonClose = new Button(shell, SWT.NULL);
				var buttonCloseLData = new GridData();
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
				var display = parent.getDisplay();
				while (!shell.isDisposed()) { // TODO: remove nested event loop
					if (!display.readAndDispatch()) {
						display.sleep();
					}
				}
			});
		}

		private void cbAllSelected(Event e) {
			text.setEnabled(!cbAll.getSelection());
			buttonChoose.setEnabled(!cbAll.getSelection());
		}

		public void setInstance(final CrontabPart.Instance instance) {
			if (instance.all) {
				text.setText(CRON_ALL);
			}
			else {
				text.setText(instance.pattern);
			}
			cbAll.setSelection(instance.all);
			text.setEnabled(!instance.all);
			buttonChoose.setEnabled(!instance.all);
		}

		public CrontabPart.Instance getInstance() throws DataParseException {
			var pattern = text.getText();
			if (cbAll.getSelection()) {
				pattern = CRON_ALL;
			}
			return part.createInstance(pattern);
		}
	}

	private final java.util.List<PartContainer> parts = new ArrayList<>();

	public CrontabScheduleOptions(Composite parent) {
		super(parent);
		try {
			setLayout(new GridLayout(4, false));
			var minutes = Messages.getString("CrontabPart.minutes"); //$NON-NLS-1$
			var hours = Messages.getString("CrontabPart.hours"); //$NON-NLS-1$
			var daysOfMonth = Messages.getString("CrontabPart.daysOfMonth"); //$NON-NLS-1$
			var months = Messages.getString("CrontabPart.months"); //$NON-NLS-1$
			var daysOfWeek = Messages.getString("CrontabPart.daysOfWeek"); //$NON-NLS-1$
			parts.add(new PartContainer(this, CrontabPart.MINUTES, Messages.getString("CrontabScheduleOptions.Select", minutes), minutes)); //$NON-NLS-1$
			parts.add(new PartContainer(this, CrontabPart.HOURS, Messages.getString("CrontabScheduleOptions.Select", hours), hours)); //$NON-NLS-1$
			parts.add(new PartContainer(this, CrontabPart.DAYSOFMONTH, Messages.getString("CrontabScheduleOptions.Select", daysOfMonth), //$NON-NLS-1$
				daysOfMonth));
			parts.add(new PartContainer(this, CrontabPart.MONTHS, Messages.getString("CrontabScheduleOptions.Select", months), months)); //$NON-NLS-1$
			parts.add(new PartContainer(this, CrontabPart.DAYSOFWEEK, Messages.getString("CrontabScheduleOptions.Select", daysOfWeek), //$NON-NLS-1$
				daysOfWeek));
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
			var instances = ((CrontabSchedule) schedule).getParts();
			for (var i = 0; i < instances.length; i++) {
				parts.get(i).setInstance(instances[i]);
			}
		}
	}

	@Override
	public Schedule getSchedule() throws DataParseException {
		var minutes = parts.get(0).getInstance();
		var hours = parts.get(1).getInstance();
		var daysOfMonth = parts.get(2).getInstance();
		var months = parts.get(3).getInstance();
		var daysOfWeek = parts.get(4).getInstance();
		return new CrontabSchedule(minutes, hours, daysOfMonth, months, daysOfWeek);
	}
}
