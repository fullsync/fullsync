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

import net.sourceforge.fullsync.rules.filefilter.values.SizeValue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * @author Michele Aiello
 */
public class SizeValueRuleComposite extends RuleComposite {

	private Color whiteColor = new Color(null, 255, 255, 255);

	private Text textValue;
	private Combo comboUnits;
	private SizeValue value;

	public SizeValueRuleComposite(Composite parent, int style, SizeValue value) {
		super(parent, style);
		this.value = value;
		initGUI();
	}

	private void initGUI() {
		GridLayout compositeLayout = new GridLayout();
		compositeLayout.numColumns = 2;
		compositeLayout.makeColumnsEqualWidth = false;

		this.setLayout(compositeLayout);
		this.setBackground(whiteColor);

		textValue = new Text(this, SWT.BORDER);
		comboUnits = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);

		GridData text1LData = new GridData();
		text1LData.widthHint = 50;
		text1LData.heightHint = 13;
		text1LData.horizontalSpan = 1;
		text1LData.horizontalAlignment = GridData.FILL;
		text1LData.grabExcessHorizontalSpace = false;
		text1LData.horizontalAlignment = GridData.BEGINNING;
		textValue.setLayoutData(text1LData);

		if (value != null) {
			textValue.setText(String.valueOf((value).getValue()));
		}

		textValue.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				(value).fromString(textValue.getText() + " " + comboUnits.getText());
				valueChanged(new ValueChangedEvent(value));
			}
		});

		textValue.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (((arg0.character < '0') || (arg0.character > '9')) && (arg0.character != '.') && (arg0.keyCode != SWT.DEL)
						&& (arg0.keyCode != SWT.BS) && (arg0.keyCode != SWT.ARROW_LEFT) && (arg0.keyCode != SWT.ARROW_UP)
						&& (arg0.keyCode != SWT.ARROW_DOWN) && (arg0.keyCode != SWT.ARROW_RIGHT)) {
					arg0.doit = false;
				}
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				if (((arg0.character < '0') || (arg0.character > '9')) && (arg0.character != '.') && (arg0.keyCode != SWT.DEL)
						&& (arg0.keyCode != SWT.BS) && (arg0.keyCode != SWT.ARROW_LEFT) && (arg0.keyCode != SWT.ARROW_UP)
						&& (arg0.keyCode != SWT.ARROW_DOWN) && (arg0.keyCode != SWT.ARROW_RIGHT)) {
					arg0.doit = false;
				}
			}
		});

		GridData comboUnitsLData = new GridData();
		comboUnitsLData.horizontalSpan = 1;
		comboUnitsLData.horizontalAlignment = GridData.FILL;
		comboUnitsLData.grabExcessHorizontalSpace = false;
		comboUnitsLData.horizontalAlignment = GridData.BEGINNING;
		comboUnits.setLayoutData(comboUnitsLData);
		String[] units = SizeValue.getAllUnits();
		for (String unit : units) {
			comboUnits.add(unit);
		}
		comboUnits.select((value).getUnit());

		comboUnits.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				(value).setUnit(comboUnits.getSelectionIndex());
				valueChanged(new ValueChangedEvent(value));
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				(value).setUnit(comboUnits.getSelectionIndex());
				valueChanged(new ValueChangedEvent(value));
			}
		});

	}
}
