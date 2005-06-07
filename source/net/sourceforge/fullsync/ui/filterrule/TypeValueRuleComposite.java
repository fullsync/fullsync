/*
 * Created on Jun 5, 2005
 */
package net.sourceforge.fullsync.ui.filterrule;

import net.sourceforge.fullsync.rules.filefilter.values.TypeValue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Michele Aiello
 */
public class TypeValueRuleComposite extends RuleComposite {

	private Color whiteColor = new Color(null, 255, 255, 255);

	private Combo comboTypes;
	private TypeValue value;
	
	public TypeValueRuleComposite(Composite parent, int style, TypeValue value) {
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

		comboTypes = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);

		GridData comboTypesLData = new GridData();
		comboTypesLData.horizontalSpan = 1;
		comboTypesLData.horizontalAlignment = GridData.FILL;
		comboTypesLData.grabExcessHorizontalSpace = false;
		comboTypesLData.horizontalAlignment = GridData.BEGINNING;
		comboTypes.setLayoutData(comboTypesLData);
		String[] types = TypeValue.getAllTypes();
		for (int i = 0; i < types.length; i++) {
			comboTypes.add(types[i]);				
		}
		comboTypes.select(((TypeValue)value).getType());
		
		comboTypes.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				((TypeValue)value).setType(comboTypes.getSelectionIndex());
				valueChanged(new ValueChangedEvent(value));
			}
			public void widgetDefaultSelected(SelectionEvent arg0) {
				((TypeValue)value).setType(comboTypes.getSelectionIndex());
				valueChanged(new ValueChangedEvent(value));
			}
		});
		

	}
}
