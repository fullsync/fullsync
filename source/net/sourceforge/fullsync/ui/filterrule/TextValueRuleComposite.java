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
