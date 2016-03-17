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

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.rules.filefilter.FileFilter;
import net.sourceforge.fullsync.rules.filefilter.values.FilterValue;
import net.sourceforge.fullsync.ui.FileFilterPage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

class SubfilterRuleComposite extends RuleComposite {
	private Text textValue;
	private Button buttonFilter;

	SubfilterRuleComposite(Composite parent, int style, final FilterValue filterValue) {
		super(parent, style);
		this.setLayout(new GridLayout(4, true));
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		layoutData.horizontalSpan = 2;
		layoutData.grabExcessHorizontalSpace = true;
		this.setLayoutData(layoutData);

		textValue = new Text(this, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		GridData textValueData = new GridData(SWT.FILL, SWT.FILL, true, true);
		textValueData.horizontalSpan = 3;
		textValue.setLayoutData(textValueData);
		if (filterValue != null) {
			textValue.setText(filterValue.toString());
		}
		textValue.setEditable(false);

		buttonFilter = new Button(this, SWT.PUSH);
		buttonFilter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		buttonFilter.setText("Set Filter...");
		buttonFilter.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent evt) {
				try {
					FileFilterPage dialog = new FileFilterPage(getShell(), filterValue.getValue());
					dialog.show();
					FileFilter newfilter = dialog.getFileFilter();
					if (newfilter != null) {
						filterValue.setValue(newfilter);
						textValue.setText(filterValue.toString());
						textValue.setToolTipText(filterValue.toString());
					}
				}
				catch (Exception e) {
					ExceptionHandler.reportException(e);
				}
			}
		});
	}
}
