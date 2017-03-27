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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import net.sourceforge.fullsync.rules.filefilter.values.TextValue;

class TextValueRuleComposite extends RuleComposite {
	private Text textValue;

	TextValueRuleComposite(Composite parent, int style, final TextValue value) {
		super(parent, style);
		GridData compositeLayoutData = new GridData();
		compositeLayoutData.horizontalAlignment = SWT.FILL;
		compositeLayoutData.grabExcessHorizontalSpace = true;
		this.setLayoutData(compositeLayoutData);
		this.setLayout(new GridLayout(1, false));

		textValue = new Text(this, SWT.BORDER);
		GridData text1LData = new GridData();
		text1LData.horizontalAlignment = SWT.FILL;
		text1LData.grabExcessHorizontalSpace = true;
		textValue.setLayoutData(text1LData);
		textValue.addModifyListener(e -> {
			value.setValue(textValue.getText());
			valueChanged(new ValueChangedEvent(value));
		});
		if (null != value) {
			textValue.setText(value.toString());
		}
	}
}
