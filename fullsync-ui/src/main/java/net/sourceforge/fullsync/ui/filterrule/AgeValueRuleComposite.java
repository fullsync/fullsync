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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;

import net.sourceforge.fullsync.rules.filefilter.values.AgeValue;
import net.sourceforge.fullsync.rules.filefilter.values.AgeValue.Unit;
import net.sourceforge.fullsync.rules.filefilter.values.OperandValue;

class AgeValueRuleComposite extends RuleComposite {
	private AgeValue.Unit unit = AgeValue.Unit.SECONDS;
	private double value = 0.0;
	private Combo comboUnits;

	AgeValueRuleComposite(Composite parent, final AgeValue initialValue) {
		super(parent);
		if (null != initialValue) {
			unit = initialValue.getUnit();
			value = initialValue.getValue();
		}
		render();
	}

	private void render() {
		this.setLayout(new FillLayout());

		textValue = new Text(this, SWT.BORDER);
		textValue.setText(String.valueOf(value));
		textValue.addModifyListener(this::onTextValueChanged);
		textValue.addListener(SWT.KeyDown, this::onKeyEvent);
		textValue.addListener(SWT.KeyUp, this::onKeyEvent);

		comboUnits = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
		for (Unit u : AgeValue.Unit.values()) {
			comboUnits.add(u.name()); // FIXME: TRANSLATE!!
		}
		comboUnits.select(unit.ordinal());
		comboUnits.addSelectionListener(widgetSelectedAdapter(this::onUnitChanged));
		comboUnits.addSelectionListener(widgetDefaultSelectedAdapter(this::onUnitChanged));
	}

	private void onTextValueChanged(ModifyEvent e) {
		try {
			value = Double.valueOf(textValue.getText());
		}
		catch (NumberFormatException ex) {
			setError("Invalid Number Format"); //TODO: is a double needed really?
		}
	}

	private void onKeyEvent(Event e) {
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
	}

	private void onUnitChanged(SelectionEvent e) {
		unit = AgeValue.Unit.values()[comboUnits.getSelectionIndex()];
	}

	@Override
	public OperandValue getValue() {
		return new AgeValue(value, unit);
	}
}
