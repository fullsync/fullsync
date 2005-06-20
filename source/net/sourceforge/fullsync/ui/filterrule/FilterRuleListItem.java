/*
 * Created on Jun 4, 2005
 */
package net.sourceforge.fullsync.ui.filterrule;

import java.text.SimpleDateFormat;

import net.sourceforge.fullsync.SystemDate;
import net.sourceforge.fullsync.rules.filefilter.FileAgeFileFilterRule;
import net.sourceforge.fullsync.rules.filefilter.FileFilterManager;
import net.sourceforge.fullsync.rules.filefilter.FileModificationDateFileFilterRule;
import net.sourceforge.fullsync.rules.filefilter.FileNameFileFilterRule;
import net.sourceforge.fullsync.rules.filefilter.FilePathFileFilterRule;
import net.sourceforge.fullsync.rules.filefilter.FileSizeFileFilterRule;
import net.sourceforge.fullsync.rules.filefilter.FileTypeFileFilterRule;
import net.sourceforge.fullsync.rules.filefilter.values.AgeValue;
import net.sourceforge.fullsync.rules.filefilter.values.DateValue;
import net.sourceforge.fullsync.rules.filefilter.values.OperandValue;
import net.sourceforge.fullsync.rules.filefilter.values.SizeValue;
import net.sourceforge.fullsync.rules.filefilter.values.TextValue;
import net.sourceforge.fullsync.rules.filefilter.values.TypeValue;
import net.sourceforge.fullsync.ui.FileFilterDetails;
import net.sourceforge.fullsync.ui.GuiController;

import org.eclipse.swt.SWT;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * @author Michele Aiello
 */
public class FilterRuleListItem implements ValueChangedListener {
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	
	private Color whiteColor = new Color(null, 255, 255, 255);
	private Color redColor = new Color(null, 255, 0, 0);
	
	private String ruleType;
	private int op;
	private OperandValue value;
	
	private RuleComposite ruleComposite;
	
	private FileFilterDetails root;
	
	private FileFilterManager fileFilterManager;
	
	public FilterRuleListItem(FileFilterDetails root, Composite composite, 
			FileFilterManager fileFilterManager, String ruleType, 
			int op, OperandValue value) 
	{
		this.fileFilterManager = fileFilterManager;
		this.ruleType = ruleType;
		this.op = op;
		this.root = root;
		this.value = value;
		init(composite);
	}
	
	public String getRuleType() {
		return ruleType;
	}
	
	public int getOperator() {
		return op;
	}
	
	public OperandValue getValue() {
		return value;
	}
	
	public void onValueChanged(ValueChangedEvent evt) {
		value = evt.getValue();
	}
	
	public void init(final Composite composite) {
		final FilterRuleListItem ruleItem = this;
		
		final Combo comboRuleTypes = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		final Combo comboOperators = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		
		comboRuleTypes.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				ruleType = comboRuleTypes.getText();
				// TODO should I dispose all the widgets created here or are they disposed 
				// automatically because the parent is disposed?
				root.recreateRuleList();
			}
		});
		
		comboOperators.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				op = comboOperators.getSelectionIndex();
				comboOperators.getParent().layout();
			}
		});
		
		comboRuleTypes.removeAll();
		comboRuleTypes.add(FileNameFileFilterRule.typeName); 
		comboRuleTypes.add(FilePathFileFilterRule.typeName); 
		comboRuleTypes.add(FileTypeFileFilterRule.typeName);
		comboRuleTypes.add(FileSizeFileFilterRule.typeName);
		comboRuleTypes.add(FileModificationDateFileFilterRule.typeName);
		comboRuleTypes.add(FileAgeFileFilterRule.typeName);
		
		if ((ruleType == null) || (ruleType.equals(""))) {
			comboRuleTypes.select(0);
			ruleType = comboRuleTypes.getText();
		}
		else {
			comboRuleTypes.setText(ruleType);
		}
		
		String[] ops = fileFilterManager.getOperatorsForRuleType(comboRuleTypes.getText());
		comboOperators.removeAll();
		for (int i = 0; i < ops.length; i++) {
			comboOperators.add(ops[i]);
		}
		if ((op < 0) || (op >= comboOperators.getItemCount())) {
			op = 0;
		}
		comboOperators.select(op);
				
		if ((ruleType.equals(FileNameFileFilterRule.typeName)) ||
				(ruleType.equals(FilePathFileFilterRule.typeName)))
		{
			if (!(value instanceof TextValue)) {
				value = new TextValue();
			}
			
			ruleComposite = new TextValueRuleComposite(composite, SWT.NULL, (TextValue)value);
			ruleComposite.addValueChangedListener(this);
		}
		else if (ruleType.equals(FileTypeFileFilterRule.typeName)) {
			if (!(value instanceof TypeValue)) {
				value = new TypeValue();
			}

			ruleComposite = new TypeValueRuleComposite(composite, SWT.NULL, (TypeValue)value);
			ruleComposite.addValueChangedListener(this);
		}
		else if (ruleType.equals(FileSizeFileFilterRule.typeName)) {
			if (!(value instanceof SizeValue)) {
				value = new SizeValue();
			}

			ruleComposite = new SizeValueRuleComposite(composite, SWT.NULL, (SizeValue)value);
			ruleComposite.addValueChangedListener(this);
		}
		else if (ruleType.equals(FileAgeFileFilterRule.typeName)) {
			if (!(value instanceof AgeValue)) {
				value = new AgeValue();
			}

			ruleComposite = new AgeValueRuleComposite(composite, SWT.NULL, (AgeValue)value);
			ruleComposite.addValueChangedListener(this);
		}
		else if (ruleType.equals(FileModificationDateFileFilterRule.typeName)) {
			if (!(value instanceof DateValue)) {
				value = new DateValue(SystemDate.getInstance().currentTimeMillis());
			}

			ruleComposite = new DateValueRuleComposite(composite, SWT.NULL, (DateValue)value);
			ruleComposite.addValueChangedListener(this);
		}
		else {
			Composite valueComposite = new Composite(composite, SWT.NULL);
			GridLayout compositeLayout = new GridLayout();
			compositeLayout.numColumns = 2;
			compositeLayout.makeColumnsEqualWidth = false;
			
			valueComposite.setLayout(compositeLayout);
			valueComposite.setBackground(whiteColor);

			Text textValue = new Text(valueComposite, SWT.BORDER);
			
			GridData text1LData = new GridData();
			text1LData.widthHint = 120;
			text1LData.heightHint = 13;
			text1LData.horizontalSpan = 1;
			text1LData.horizontalAlignment = GridData.FILL;
			text1LData.grabExcessHorizontalSpace = false;
			text1LData.horizontalAlignment = GridData.BEGINNING;
			textValue.setLayoutData(text1LData);
			
			textValue.setText("Missing Rule Composite");
			textValue.setForeground(redColor);
			
			textValue.setEditable(false);
			textValue.setBackground(whiteColor);
		}		
		{
            ToolBar toolBar = new ToolBar(composite, SWT.FLAT);
            toolBar.setBackground(whiteColor);
            ToolItem toolItemDelete = new ToolItem(toolBar, SWT.PUSH);
            toolItemDelete.setImage(GuiController.getInstance().getImage("Rule_Delete.png")); //$NON-NLS-1$
            toolItemDelete.setToolTipText("Delete");
            toolItemDelete.addSelectionListener(new SelectionAdapter() {
            	public void widgetSelected(SelectionEvent evt) {
            		root.deleteRule(ruleItem);
            	}
            });
		}
	}
	
}
