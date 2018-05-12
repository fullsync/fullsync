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

import static org.eclipse.swt.events.SelectionListener.widgetDefaultSelectedAdapter;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import net.sourceforge.fullsync.rules.filefilter.values.AgeValue;
import net.sourceforge.fullsync.rules.filefilter.values.AgeValue.Unit;
import net.sourceforge.fullsync.rules.filefilter.values.OperandValue;

class AgeValueRuleComposite extends RuleComposite {
	private AgeValue.Unit unit = AgeValue.Unit.SECONDS;
	private double value = 0.0;

	AgeValueRuleComposite(Composite parent, final AgeValue initialValue) {
		super(parent);
		if (null != initialValue) {
			unit = initialValue.getUnit();
			value = initialValue.getValue();
		}
		render(parent);
	}

	private void render(Composite parent) {
		this.setLayout(new FillLayout());

		textValue = new Text(this, SWT.BORDER);
		textValue.setText(String.valueOf(value));
		textValue.addModifyListener(e -> {
			try {
				value = Double.valueOf(textValue.getText());
			}
			catch (NumberFormatException ex) {
				setError("Invalid Number Format"); //TODO: is a double needed really?
			}
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

		final Combo comboUnits = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
		for (Unit unit : AgeValue.Unit.values()) {
			comboUnits.add(unit.name()); // FIXME: TRANSLATE!!
		}
		comboUnits.select(unit.ordinal());
		Consumer<SelectionEvent> comboSelectionListener = e -> {
			unit = AgeValue.Unit.values()[comboUnits.getSelectionIndex()];
		};
		comboUnits.addSelectionListener(widgetSelectedAdapter(comboSelectionListener));
		comboUnits.addSelectionListener(widgetDefaultSelectedAdapter(comboSelectionListener));
	}

	@Override
	public OperandValue getValue() {
		return new AgeValue(value, unit);
	}
}
