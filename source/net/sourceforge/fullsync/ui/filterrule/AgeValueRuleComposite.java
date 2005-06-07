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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * @author Michele Aiello
 */
public class AgeValueRuleComposite extends RuleComposite {

	private Color whiteColor = new Color(null, 255, 255, 255);

	private Text textValue;
	private Combo comboUnits;
	private AgeValue value;
	
	public AgeValueRuleComposite(Composite parent, int style, AgeValue value) {
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
			textValue.setText(String.valueOf(((AgeValue)value).getValue()));
		}
		
		textValue.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				((AgeValue)value).fromString(textValue.getText()+" "+comboUnits.getText());
				valueChanged(new ValueChangedEvent(value));
			}
		});
		
		textValue.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent arg0) {
				if (((arg0.character < '0' ) || (arg0.character > '9')) &&
						(arg0.character != '.') &&
						(arg0.keyCode != SWT.DEL) && 
						(arg0.keyCode != SWT.BS) &&
						(arg0.keyCode != SWT.ARROW_LEFT) && 
						(arg0.keyCode != SWT.ARROW_UP) && 
						(arg0.keyCode != SWT.ARROW_DOWN) && 
						(arg0.keyCode != SWT.ARROW_RIGHT))
				{
					arg0.doit = false;
				}
			}
			public void keyReleased(KeyEvent arg0) {
				if (((arg0.character < '0' ) || (arg0.character > '9')) &&
						(arg0.character != '.') &&
						(arg0.keyCode != SWT.DEL) && 
						(arg0.keyCode != SWT.BS) &&
						(arg0.keyCode != SWT.ARROW_LEFT) && 
						(arg0.keyCode != SWT.ARROW_UP) && 
						(arg0.keyCode != SWT.ARROW_DOWN) && 
						(arg0.keyCode != SWT.ARROW_RIGHT))
				{
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
		String[] units = AgeValue.getAllUnits();
		for (int i = 0; i < units.length; i++) {
			comboUnits.add(units[i]);				
		}
		comboUnits.select(((AgeValue)value).getUnit());
		
		comboUnits.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				((AgeValue)value).setUnit(comboUnits.getSelectionIndex());
				valueChanged(new ValueChangedEvent(value));
			}
			public void widgetDefaultSelected(SelectionEvent arg0) {
				((AgeValue)value).setUnit(comboUnits.getSelectionIndex());
				valueChanged(new ValueChangedEvent(value));
			}
		});
		

	}
}
