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
import net.sourceforge.fullsync.ui.WizardDialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * @author Michele Aiello
 */
public class SubfilterRuleComposite extends RuleComposite {

	private Color whiteColor = new Color(null, 255, 255, 255);

	private Text textValue;
	private Button buttonFilter;

	private FilterValue filterValue;

	public SubfilterRuleComposite(Composite parent, int style, FilterValue filterValue) {
		super(parent, style);
		this.filterValue = filterValue;
		initGUI();
	}

	private void initGUI() {
		GridLayout compositeLayout = new GridLayout();
		compositeLayout.numColumns = 2;
		compositeLayout.marginWidth = 0;
		compositeLayout.makeColumnsEqualWidth = false;

		this.setLayout(compositeLayout);
		this.setBackground(whiteColor);

		textValue = new Text(this, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		buttonFilter = new Button(this, SWT.PUSH | SWT.CENTER);

		GridData text1LData = new GridData();
		text1LData.widthHint = 210;
		text1LData.heightHint = 26;
		text1LData.horizontalSpan = 1;
		text1LData.horizontalAlignment = GridData.FILL;
		text1LData.grabExcessHorizontalSpace = false;
		text1LData.horizontalAlignment = GridData.BEGINNING;
		textValue.setLayoutData(text1LData);

		if (filterValue != null) {
			textValue.setText(filterValue.toString());
		}

		textValue.setEditable(false);
		textValue.setBackground(whiteColor);

		GridData buttonFilterLData = new GridData();
		buttonFilterLData.horizontalSpan = 1;
		buttonFilter.setLayoutData(buttonFilterLData);
		buttonFilter.setText("Set Filter...");
		buttonFilter.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				try {
					WizardDialog dialog = new WizardDialog(getShell(), SWT.APPLICATION_MODAL);
					FileFilterPage page = new FileFilterPage(dialog, filterValue.getValue());
					dialog.show();
					FileFilter newfilter = page.getFileFilter();
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
