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

import net.sourceforge.fullsync.rules.filefilter.values.AgeValue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * @author Michele Aiello
 */
public class AgeValueRuleComposite extends RuleComposite {

	private Text textValue;

	public AgeValueRuleComposite(Composite parent, int style, final AgeValue value) {
		super(parent, style);
		this.setLayout(new GridLayout(2, true));
		this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		textValue = new Text(this, SWT.BORDER);
		textValue.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		final Combo comboUnits = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);

		if (value != null) {
			textValue.setText(String.valueOf((value).getValue()));
		}

		textValue.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent evt) {
				(value).fromString(textValue.getText() + " " + comboUnits.getText());
				valueChanged(new ValueChangedEvent(value));
			}
		});

		textValue.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(final KeyEvent evt) {
				// FIXME: the dot should be language specific, find a better way to achieve the same
				if (((evt.character < '0') || (evt.character > '9')) && (evt.character != '.') && (evt.keyCode != SWT.DEL)
						&& (evt.keyCode != SWT.BS) && (evt.keyCode != SWT.ARROW_LEFT) && (evt.keyCode != SWT.ARROW_UP)
						&& (evt.keyCode != SWT.ARROW_DOWN) && (evt.keyCode != SWT.ARROW_RIGHT)) {
					evt.doit = false;
				}
			}
			@Override
			public void keyReleased(final KeyEvent evt) {
				// FIXME: the dot should be language specific, find a better way to achieve the same
				if (((evt.character < '0') || (evt.character > '9')) && (evt.character != '.') && (evt.keyCode != SWT.DEL)
						&& (evt.keyCode != SWT.BS) && (evt.keyCode != SWT.ARROW_LEFT) && (evt.keyCode != SWT.ARROW_UP)
						&& (evt.keyCode != SWT.ARROW_DOWN) && (evt.keyCode != SWT.ARROW_RIGHT)) {
					evt.doit = false;
				}
			}
		});

		comboUnits.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		for (String unit : AgeValue.getAllUnits()) {
			comboUnits.add(unit);
		}
		comboUnits.select(value.getUnit());

		comboUnits.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(final SelectionEvent evt) {
				value.setUnit(comboUnits.getSelectionIndex());
				valueChanged(new ValueChangedEvent(value));
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent evt) {
				value.setUnit(comboUnits.getSelectionIndex());
				valueChanged(new ValueChangedEvent(value));
			}
		});

	}
}
