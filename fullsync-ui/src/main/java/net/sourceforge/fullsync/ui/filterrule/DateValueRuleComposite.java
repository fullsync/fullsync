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

import java.text.DateFormat;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;

import net.sourceforge.fullsync.SystemDate;
import net.sourceforge.fullsync.rules.filefilter.values.DateValue;
import net.sourceforge.fullsync.rules.filefilter.values.OperandValue;
import net.sourceforge.fullsync.ui.Messages;

class DateValueRuleComposite extends RuleComposite {
	private final DateFormat dateFormat = DateFormat.getDateInstance();
	private Date value = new Date(SystemDate.getInstance().currentTimeMillis());

	DateValueRuleComposite(Composite parent, final DateValue initialValue) {
		super(parent);
		if (null != initialValue) {
			value = initialValue.getDate();
		}
		render();
	}

	private void render() {
		this.setLayout(new FillLayout());

		textValue = new Text(this, SWT.BORDER);
		textValue.setEditable(false);
		if (null != value) {
			textValue.setText(dateFormat.format(value));
		}

		Button buttonCalendar = new Button(this, SWT.PUSH | SWT.CENTER);
		buttonCalendar.setText(Messages.getString("DateValueRuleComposite.ChooseDate")); //$NON-NLS-1$
		buttonCalendar.addListener(SWT.Selection, this::onChooseDate);
	}

	private void onChooseDate(Event e) {
		SWTCalendarDialog swtCalDialog = new SWTCalendarDialog(getDisplay().getActiveShell(), value);
		swtCalDialog.open();
		value = swtCalDialog.getDate();
		textValue.setText(dateFormat.format(value));
	}

	@Override
	public OperandValue getValue() {
		return new DateValue(value);
	}
}
