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

import net.sourceforge.fullsync.rules.filefilter.values.TypeValue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;

class TypeValueRuleComposite extends RuleComposite {
	private Combo comboTypes;

	TypeValueRuleComposite(Composite parent, int style, final TypeValue value) {
		super(parent, style);
		GridData compositeLayoutData = new GridData();
		compositeLayoutData.horizontalAlignment = SWT.FILL;
		compositeLayoutData.grabExcessHorizontalSpace = true;
		this.setLayoutData(compositeLayoutData);
		this.setLayout(new GridLayout(1, false));

		comboTypes = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridData comboTypesLData = new GridData();
		comboTypesLData.horizontalAlignment = SWT.FILL;
		comboTypesLData.grabExcessHorizontalSpace = true;
		comboTypes.setLayoutData(comboTypesLData);
		for (TypeValue.Type type : TypeValue.Type.values()) {
			comboTypes.add(type.name());
		}
		comboTypes.select(value.getType().ordinal());
		Listener comboSelectionListener = e -> {
			value.setType(TypeValue.Type.values()[comboTypes.getSelectionIndex()]);
			valueChanged(new ValueChangedEvent(value));
		};
		comboTypes.addListener(SWT.Selection, comboSelectionListener);
		comboTypes.addListener(SWT.DefaultSelection, comboSelectionListener);
	}
}
