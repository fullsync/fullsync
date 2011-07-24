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

import net.sourceforge.fullsync.rules.filefilter.values.TextValue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * @author Michele Aiello
 */
public class TextValueRuleComposite extends RuleComposite {

	private Color whiteColor = new Color(null, 255, 255, 255);

	private Text textValue;
	private TextValue value;

	public TextValueRuleComposite(Composite parent, int style, TextValue value) {
		super(parent, style);
		this.value = value;
		initGUI();
	}

	private void initGUI() {
		GridLayout compositeLayout = new GridLayout();
		compositeLayout.numColumns = 1;
		compositeLayout.makeColumnsEqualWidth = false;

		this.setLayout(compositeLayout);
		this.setBackground(whiteColor);

		textValue = new Text(this, SWT.BORDER);

		GridData text1LData = new GridData();
		text1LData.widthHint = 120;
		text1LData.heightHint = 13;
		text1LData.horizontalSpan = 1;
		text1LData.horizontalAlignment = GridData.FILL;
		text1LData.grabExcessHorizontalSpace = false;
		text1LData.horizontalAlignment = GridData.BEGINNING;
		textValue.setLayoutData(text1LData);

		textValue.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				value.setValue(textValue.getText());
				valueChanged(new ValueChangedEvent(value));
			}
		});

		if (value != null) {
			textValue.setText(value.toString());
		}

	}
}
