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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import net.sourceforge.fullsync.rules.filefilter.values.AgeValue;
import net.sourceforge.fullsync.rules.filefilter.values.AgeValue.Unit;
import net.sourceforge.fullsync.rules.filefilter.values.OperandValue;

class AgeValueRuleComposite extends RuleComposite {
	private AgeValue value;

	AgeValueRuleComposite(Composite parent, final AgeValue initialValue) {
		super(parent);
		value = initialValue;
		this.setLayout(new GridLayout(2, true));
		this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		textValue = new Text(this, SWT.BORDER);
		final Combo comboUnits = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);

		textValue.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		textValue.setText(String.valueOf(value.getValue()));
		textValue.addModifyListener(e -> {
			value.fromString(textValue.getText() + " " + comboUnits.getText());
		});
		Listener numbersOnlyKeyboardListener = e -> {
			// FIXME: the dot should be language specific, find a better way to achieve the same
			if (((e.character < '0') || (e.character > '9'))
				&& (e.character != '.')
				&& (e.keyCode != SWT.DEL)
				&& (e.keyCode != SWT.BS)
				&& (e.keyCode != SWT.ARROW_LEFT)
				&& (e.keyCode != SWT.ARROW_UP)
				&& (e.keyCode != SWT.ARROW_DOWN)
				&& (e.keyCode != SWT.ARROW_RIGHT)) {
				e.doit = false;
			}
		};
		textValue.addListener(SWT.KeyDown, numbersOnlyKeyboardListener);
		textValue.addListener(SWT.KeyUp, numbersOnlyKeyboardListener);

		comboUnits.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		for (Unit unit : AgeValue.Unit.values()) {
			comboUnits.add(unit.name()); // FIXME: TRANSLATE!!
		}
		comboUnits.select(value.getUnit().ordinal());
		comboUnits.addListener(SWT.Selection, e -> {
			value.setUnit(AgeValue.Unit.values()[comboUnits.getSelectionIndex()]);
		});
		comboUnits.addListener(SWT.DefaultSelection, e -> {
			value.setUnit(AgeValue.Unit.values()[comboUnits.getSelectionIndex()]);
		});
	}

	@Override
	public OperandValue getValue() {
		return value;
	}
}
