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
package net.sourceforge.fullsync.ui.profiledetails;

import javax.inject.Inject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.schedule.Schedule;
import net.sourceforge.fullsync.ui.ImageRepository;
import net.sourceforge.fullsync.ui.Messages;
import net.sourceforge.fullsync.ui.UISettings;
import net.sourceforge.fullsync.ui.schedule.CrontabScheduleOptions;
import net.sourceforge.fullsync.ui.schedule.IntervalScheduleOptions;
import net.sourceforge.fullsync.ui.schedule.NullScheduleOptions;
import net.sourceforge.fullsync.ui.schedule.ScheduleOptions;

public class ScheduleSelectionDialog {
	private final ImageRepository imageRepository;
	private Group groupOptions;
	private Combo cbType;
	private Schedule schedule;

	@Inject
	public ScheduleSelectionDialog(ImageRepository imageRepository) {
		this.imageRepository = imageRepository;
	}

	void open(Shell parent) {
		try {
			var dialogShell = new Shell(parent, SWT.PRIMARY_MODAL | SWT.DIALOG_TRIM | SWT.RESIZE);
			dialogShell.setText(Messages.getString("ScheduleSelectionDialog.EditScheduling")); //$NON-NLS-1$
			dialogShell.setImage(imageRepository.getImage("Scheduler_Icon.png")); //$NON-NLS-1$
			var thisLayout = new GridLayout();
			thisLayout.numColumns = 2;
			dialogShell.setLayout(thisLayout);

			// schedule type
			var compositeTop = new Composite(dialogShell, SWT.NONE);
			var compositeTopLayout = new GridLayout();
			var compositeTopLData = new GridData();
			compositeTopLData.horizontalSpan = 2;
			compositeTopLData.horizontalAlignment = SWT.FILL;
			compositeTop.setLayoutData(compositeTopLData);
			compositeTopLayout.numColumns = 2;
			compositeTop.setLayout(compositeTopLayout);

			var labelScheduleType = new Label(compositeTop, SWT.NONE);
			labelScheduleType.setText(Messages.getString("ScheduleSelectionDialog.SchedulingType")); //$NON-NLS-1$
			var labelScheduleTypeLData = new GridData();
			labelScheduleType.setLayoutData(labelScheduleTypeLData);

			cbType = new Combo(compositeTop, SWT.DROP_DOWN | SWT.READ_ONLY);
			var cbTypeLData = new GridData();
			cbTypeLData.horizontalAlignment = SWT.FILL;
			cbTypeLData.grabExcessHorizontalSpace = true;
			cbType.setLayoutData(cbTypeLData);
			cbType.addListener(SWT.Modify, e -> {
				var children = groupOptions.getChildren();
				if ((cbType.getSelectionIndex() > -1) && (cbType.getSelectionIndex() < children.length)) {
					((StackLayout) groupOptions.getLayout()).topControl = children[cbType.getSelectionIndex()];
					groupOptions.layout();
				}
			});

			// scheduling options
			groupOptions = new Group(dialogShell, SWT.FILL);
			var groupOptionsLayout = new StackLayout();
			var groupOptionsLData = new GridData();
			groupOptionsLData.grabExcessVerticalSpace = true;
			groupOptionsLData.grabExcessHorizontalSpace = true;
			groupOptionsLData.horizontalAlignment = SWT.FILL;
			groupOptionsLData.verticalAlignment = SWT.FILL;
			groupOptionsLData.horizontalSpan = 2;
			groupOptions.setLayoutData(groupOptionsLData);
			groupOptions.setLayout(groupOptionsLayout);
			groupOptions.setText(Messages.getString("ScheduleSelectionDialog.Options")); //$NON-NLS-1$

			// dialog buttons
			var buttonOk = new Button(dialogShell, SWT.PUSH | SWT.CENTER);
			buttonOk.setText(Messages.getString("ScheduleSelectionDialog.Ok")); //$NON-NLS-1$
			var buttonOkLData = new GridData();
			buttonOkLData.horizontalAlignment = SWT.END;
			buttonOkLData.grabExcessHorizontalSpace = true;
			buttonOkLData.heightHint = UISettings.BUTTON_HEIGHT;
			buttonOkLData.widthHint = UISettings.BUTTON_WIDTH;
			buttonOk.setLayoutData(buttonOkLData);
			buttonOk.addListener(SWT.Selection, e -> {
				try {
					schedule = ((ScheduleOptions) ((StackLayout) groupOptions.getLayout()).topControl).getSchedule();
					dialogShell.dispose();
				}
				catch (Exception ex) {
					ExceptionHandler.reportException(ex);
				}
			});

			// cancel button
			var buttonCancel = new Button(dialogShell, SWT.PUSH | SWT.CENTER);
			buttonCancel.setText(Messages.getString("ScheduleSelectionDialog.Cancel")); //$NON-NLS-1$
			var buttonCancelLData = new GridData();
			buttonCancelLData.heightHint = UISettings.BUTTON_HEIGHT;
			buttonCancelLData.widthHint = UISettings.BUTTON_WIDTH;
			buttonCancel.setLayoutData(buttonCancelLData);
			buttonCancel.addListener(SWT.Selection, e -> dialogShell.dispose());

			addScheduleOptions(new NullScheduleOptions(groupOptions));
			cbType.select(0);
			addScheduleOptions(new IntervalScheduleOptions(groupOptions));
			addScheduleOptions(new CrontabScheduleOptions(groupOptions));

			var display = dialogShell.getDisplay();
			dialogShell.setSize(350, 350);

			var rect = parent.getBounds();
			var dialogSize = dialogShell.getSize();
			var x = (rect.x + (rect.width / 2)) - (dialogSize.x / 2);
			var y = (rect.y + (rect.height / 2)) - (dialogSize.y / 2);
			dialogShell.setLocation(x, y);
			dialogShell.layout();
			dialogShell.open();
			while (!dialogShell.isDisposed()) { // TODO: remove nested event loop
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		}
		catch (Exception e) {
			ExceptionHandler.reportException(e);
		}
	}

	private void addScheduleOptions(ScheduleOptions options) {
		cbType.add(options.getSchedulingName());
		if (options.canHandleSchedule(schedule)) {
			cbType.setText(options.getSchedulingName());
			options.setSchedule(schedule);
		}
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	public Schedule getSchedule() {
		return this.schedule;
	}
}
