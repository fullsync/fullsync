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

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.schedule.IntervalSchedule;
import net.sourceforge.fullsync.schedule.Schedule;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class IntervalScheduleOptions extends ScheduleOptions {
	private Text textCount;
	private Combo cbUnit;

	public IntervalScheduleOptions(Composite parent, int style) {
		super(parent, style);
		initGUI();
		cbUnit.select(0);
	}

	private void initGUI() {
		try {
			GridLayout thisLayout = new GridLayout(3, false);
			this.setLayout(thisLayout);

			Label labelExecuteQuery = new Label(this, SWT.NONE);
			labelExecuteQuery.setText(Messages.getString("IntervalScheduleOptions.ExecuteEvery")); //$NON-NLS-1$
			GridData labelIntervalData = new GridData();
			labelExecuteQuery.setLayoutData(labelIntervalData);

			textCount = new Text(this, SWT.BORDER | SWT.RIGHT);
			textCount.setText("1"); //$NON-NLS-1$
			GridData textCountLData = new GridData();
			textCountLData.grabExcessHorizontalSpace = true;
			textCountLData.horizontalAlignment = SWT.FILL;
			textCount.setLayoutData(textCountLData);

			cbUnit = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
			// TODO sadly we can't support "days","months" as the interval is starting with program startup
			cbUnit.setItems(new java.lang.String[] {
				Messages.getString("IntervalScheduleOptions.seconds"),
				Messages.getString("IntervalScheduleOptions.minutes"),
				Messages.getString("IntervalScheduleOptions.hours"),
			});

			this.layout();
		}
		catch (Exception e) {
			ExceptionHandler.reportException(e);
		}
	}

	@Override
	public String getSchedulingName() {
		return Messages.getString("IntervalScheduleOptions.Interval"); //$NON-NLS-1$
	}

	@Override
	public boolean canHandleSchedule(final Schedule sched) {
		return sched instanceof IntervalSchedule;
	}

	@Override
	public void setSchedule(final Schedule sched) {
		if (sched instanceof IntervalSchedule) {
			IntervalSchedule is = (IntervalSchedule) sched;
			textCount.setText(String.valueOf(is.getInterval() / 1000));
			cbUnit.select(0);
		}
	}

	@Override
	public Schedule getSchedule() {
		long multi = 1;
		switch (cbUnit.getSelectionIndex()) {
			case 2:
				multi *= 60;
			case 1:
				multi *= 60;
			case 0:
				multi *= 1000;
		}
		long interval = Long.parseLong(textCount.getText()) * multi;
		return new IntervalSchedule(interval, interval);
	}
}
