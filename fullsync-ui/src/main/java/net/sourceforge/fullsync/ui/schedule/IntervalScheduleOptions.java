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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.schedule.IntervalSchedule;
import net.sourceforge.fullsync.schedule.Schedule;
import net.sourceforge.fullsync.ui.Messages;

public class IntervalScheduleOptions extends ScheduleOptions {
	// TODO sadly we can't support "days","months" as the interval is starting with program startup
	private static final SchedulingIntervalItem[] schedulingIntervals;

	static {
		SchedulingIntervalItem seconds = new SchedulingIntervalItem("seconds", "IntervalScheduleOptions.seconds", 1000);
		SchedulingIntervalItem minutes = new SchedulingIntervalItem("minutes", "IntervalScheduleOptions.minutes", 60 * 1000);
		SchedulingIntervalItem hours = new SchedulingIntervalItem("hours", "IntervalScheduleOptions.hours", 60 * 60 * 1000);
		schedulingIntervals = new SchedulingIntervalItem[] { seconds, minutes, hours };
	}

	private Text textCount;
	private Combo cbUnit;

	public IntervalScheduleOptions(Composite parent) {
		super(parent);
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
			String[] names = new String[schedulingIntervals.length];
			int idx = 0;
			for (SchedulingIntervalItem item : schedulingIntervals) {
				names[idx++] = Messages.getString(item.messageName);
			}
			cbUnit.setItems(names);
			this.layout();
			cbUnit.select(0);
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
			int index = 0;
			for (SchedulingIntervalItem item : schedulingIntervals) {
				if (item.unit.equals(is.getIntervalDisplayUnit())) {
					textCount.setText(String.valueOf(is.getInterval() / item.factor));
					break;
				}
				++index;
			}
			cbUnit.select(index);
		}
	}

	@Override
	public Schedule getSchedule() {
		SchedulingIntervalItem item = schedulingIntervals[cbUnit.getSelectionIndex()];
		long interval = Long.parseLong(textCount.getText()) * item.factor;
		return new IntervalSchedule(interval, interval, item.unit);
	}

	private static class SchedulingIntervalItem {
		public String unit;
		public String messageName;
		public long factor;

		SchedulingIntervalItem(String unit, String messageName, long factor) {
			this.unit = unit;
			this.messageName = messageName;
			this.factor = factor;
		}
	}
}
