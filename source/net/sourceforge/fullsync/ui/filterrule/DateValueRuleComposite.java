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
/*
 * Created on Jun 5, 2005
 */
package net.sourceforge.fullsync.ui.filterrule;

import java.text.DateFormat;

import net.sourceforge.fullsync.rules.filefilter.values.DateValue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * @author Michele Aiello
 */
public class DateValueRuleComposite extends RuleComposite {

	private Text textValue;
	private Button buttonCalendar;
	private DateFormat dateFormat;

	public DateValueRuleComposite(Composite parent, int style, final DateValue value) {
		super(parent, style);
		this.setLayout(new GridLayout(2, true));
		dateFormat = DateFormat.getDateInstance();

		textValue = new Text(this, SWT.BORDER);
		textValue.setEditable(false);
		textValue.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		if (value != null) {
			textValue.setText(dateFormat.format(value.getDate()));
		}

		buttonCalendar = new Button(this, SWT.PUSH | SWT.CENTER);
		buttonCalendar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		buttonCalendar.setText("Choose Date...");
		buttonCalendar.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent evt) {
				SWTCalendarDialog swtCalDialog = new SWTCalendarDialog(getDisplay().getActiveShell(), value.getDate());
				swtCalDialog.open();
				value.setDate(swtCalDialog.getDate());
				textValue.setText(dateFormat.format(value.getDate()));
				valueChanged(new ValueChangedEvent(value));
			}
		});
	}
}
