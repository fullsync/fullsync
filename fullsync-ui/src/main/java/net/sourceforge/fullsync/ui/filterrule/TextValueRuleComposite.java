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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import net.sourceforge.fullsync.rules.filefilter.values.OperandValue;
import net.sourceforge.fullsync.rules.filefilter.values.TextValue;

class TextValueRuleComposite extends RuleComposite {
	private String value = "";

	TextValueRuleComposite(Composite parent, final TextValue initialValue) {
		super(parent);
		if (null != initialValue) {
			value = initialValue.getValue();
		}
		render(parent);
	}

	private void render(Composite parent) {
		this.setLayout(new FillLayout());
		textValue = new Text(this, SWT.BORDER);
		textValue.addModifyListener(this::onTextValueChanged);
		textValue.setText(value.toString());
	}

	private void onTextValueChanged(ModifyEvent e) {
		value = textValue.getText();
	}

	@Override
	public OperandValue getValue() {
		return new TextValue(value);
	}
}
