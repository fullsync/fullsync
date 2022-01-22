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
package net.sourceforge.fullsync.ui.filterrule;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.fullsync.ui.Messages;

class SWTCalendarDialog {
	private final Shell shell;
	private final Calendar calendar;

	SWTCalendarDialog(Shell parent, Date date) {
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL | SWT.SHEET);
		shell.setText(Messages.getString("SWTCalendarDialog.Title")); //$NON-NLS-1$
		shell.setLayout(new GridLayout());
		final var dateTime = new DateTime(shell, SWT.CALENDAR | SWT.BORDER);
		calendar = Calendar.getInstance();
		calendar.setTime(date);
		shell.addDisposeListener(e -> calendar.set(dateTime.getYear(), dateTime.getMonth(), dateTime.getDay()));
		dateTime.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
	}

	public void open() {
		final var display = shell.getDisplay();
		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	public Date getDate() {
		return calendar.getTime();
	}
}
