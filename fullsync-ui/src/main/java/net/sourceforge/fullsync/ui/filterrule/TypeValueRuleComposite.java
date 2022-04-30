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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import net.sourceforge.fullsync.rules.filefilter.values.OperandValue;
import net.sourceforge.fullsync.rules.filefilter.values.TypeValue;

class TypeValueRuleComposite extends RuleComposite {
	private TypeValue.Type value = TypeValue.Type.FILE;
	private final Combo comboTypes;

	TypeValueRuleComposite(Composite parent, final TypeValue initialValue) {
		super(parent);
		if (null != initialValue) {
			value = initialValue.getType();
		}
		this.setLayout(new FillLayout());
		comboTypes = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
		for (TypeValue.Type type : TypeValue.Type.values()) {
			comboTypes.add(type.name());
		}
		comboTypes.select(value.ordinal());
		comboTypes.addSelectionListener(widgetSelectedAdapter(this::onTypeChanged));
		comboTypes.addSelectionListener(widgetDefaultSelectedAdapter(this::onTypeChanged));
	}

	private void onTypeChanged(SelectionEvent e) {
		value = TypeValue.Type.values()[comboTypes.getSelectionIndex()];
	}

	@Override
	public void setError(String message) {
		// impossible
	}

	@Override
	public OperandValue getValue() {
		return new TypeValue(value);
	}
}
