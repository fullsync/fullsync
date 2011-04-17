/**
 *	@license
 *	This program is free software; you can redistribute it and/or
 *	modify it under the terms of the GNU General Public License
 *	as published by the Free Software Foundation; either version 2
 *	of the License, or (at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program; if not, write to the Free Software
 *	Foundation, Inc., 51 Franklin Street, Fifth Floor,
 *	Boston, MA  02110-1301, USA.
 *
 *	---
 *	@copyright Copyright (C) 2005, Michele Aiello
 *	@copyright Copyright (C) 2011, Obexer Christoph <cobexer@gmail.com>
 */
/*
 * Created on Jun 4, 2005
 */
package net.sourceforge.fullsync.ui.filterrule;

import java.text.SimpleDateFormat;
import java.util.Hashtable;

import net.sourceforge.fullsync.SystemDate;
import net.sourceforge.fullsync.rules.filefilter.FileAgeFileFilterRule;
import net.sourceforge.fullsync.rules.filefilter.FileFilter;
import net.sourceforge.fullsync.rules.filefilter.FileFilterManager;
import net.sourceforge.fullsync.rules.filefilter.FileModificationDateFileFilterRule;
import net.sourceforge.fullsync.rules.filefilter.FileNameFileFilterRule;
import net.sourceforge.fullsync.rules.filefilter.FilePathFileFilterRule;
import net.sourceforge.fullsync.rules.filefilter.FileSizeFileFilterRule;
import net.sourceforge.fullsync.rules.filefilter.FileTypeFileFilterRule;
import net.sourceforge.fullsync.rules.filefilter.SubfilterFileFilerRule;
import net.sourceforge.fullsync.rules.filefilter.values.AgeValue;
import net.sourceforge.fullsync.rules.filefilter.values.DateValue;
import net.sourceforge.fullsync.rules.filefilter.values.FilterValue;
import net.sourceforge.fullsync.rules.filefilter.values.OperandValue;
import net.sourceforge.fullsync.rules.filefilter.values.SizeValue;
import net.sourceforge.fullsync.rules.filefilter.values.TextValue;
import net.sourceforge.fullsync.rules.filefilter.values.TypeValue;
import net.sourceforge.fullsync.ui.FileFilterDetails;
import net.sourceforge.fullsync.ui.GuiController;
import net.sourceforge.fullsync.ui.Messages;

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

	private final SimpleDateFormat dateFormat = new SimpleDateFormat(Messages.getString("FilterRuleListItem.DateFormat")); //$NON-NLS-1$

	private final Color whiteColor = new Color(null, 255, 255, 255);
	private final Color redColor = new Color(null, 255, 0, 0);

	private String ruleType;
	private int op;
	private OperandValue value;

	private RuleComposite ruleComposite;

	private final FileFilterDetails root;

	private final FileFilterManager fileFilterManager;

	private Hashtable rulesTable;
	private Hashtable ruleNamesConversionTable;
	private Hashtable reverseRuleNamesConversionTable;
	private String[] ruleTypeNames;

	public FilterRuleListItem(FileFilterDetails root, Composite composite,
			FileFilterManager fileFilterManager, String ruleType,
			int op, OperandValue value)
	{
		this.fileFilterManager = fileFilterManager;
		this.ruleType = ruleType;
		this.op = op;
		this.root = root;
		this.value = value;

		initConversionTables();
		init(composite);
	}

	private void initConversionTables() {
		ruleTypeNames = new String[] {
				Messages.getString("FilterRuleListItem.FileNameFilter"),
				Messages.getString("FilterRuleListItem.FilePathFilter"),
				Messages.getString("FilterRuleListItem.FileTypeFilter"),
				Messages.getString("FilterRuleListItem.FilSizeFilter"),
				Messages.getString("FilterRuleListItem.FileModificationDateFilter"),
				Messages.getString("FilterRuleListItem.FileAgeFilter"),
				Messages.getString("FilterRuleListItem.NestedFilter")
		};

		rulesTable = new Hashtable(15, 0.75f);
		rulesTable.put(Messages.getString("FilterRuleListItem.FileNameFilter"), FileNameFileFilterRule.class); //$NON-NLS-1$
		rulesTable.put(Messages.getString("FilterRuleListItem.FilePathFilter"), FilePathFileFilterRule.class);  //$NON-NLS-1$
		rulesTable.put(Messages.getString("FilterRuleListItem.FileTypeFilter"), FileTypeFileFilterRule.class); //$NON-NLS-1$
		rulesTable.put(Messages.getString("FilterRuleListItem.FilSizeFilter"), FileSizeFileFilterRule.class); //$NON-NLS-1$
		rulesTable.put(Messages.getString("FilterRuleListItem.FileModificationDateFilter"), FileModificationDateFileFilterRule.class); //$NON-NLS-1$
		rulesTable.put(Messages.getString("FilterRuleListItem.FileAgeFilter"), FileAgeFileFilterRule.class); //$NON-NLS-1$
		rulesTable.put(Messages.getString("FilterRuleListItem.NestedFilter"), SubfilterFileFilerRule.class); //$NON-NLS-1$

		ruleNamesConversionTable = new Hashtable(15, 0.75f);
		ruleNamesConversionTable.put(FileNameFileFilterRule.typeName, Messages.getString("FilterRuleListItem.FileNameFilter")); //$NON-NLS-1$
		ruleNamesConversionTable.put(FilePathFileFilterRule.typeName, Messages.getString("FilterRuleListItem.FilePathFilter")); //$NON-NLS-1$
		ruleNamesConversionTable.put(FileTypeFileFilterRule.typeName, Messages.getString("FilterRuleListItem.FileTypeFilter")); //$NON-NLS-1$
		ruleNamesConversionTable.put(FileSizeFileFilterRule.typeName, Messages.getString("FilterRuleListItem.FilSizeFilter")); //$NON-NLS-1$
		ruleNamesConversionTable.put(FileModificationDateFileFilterRule.typeName, Messages.getString("FilterRuleListItem.FileModificationDateFilter")); //$NON-NLS-1$
		ruleNamesConversionTable.put(FileAgeFileFilterRule.typeName, Messages.getString("FilterRuleListItem.FileAgeFilter")); //$NON-NLS-1$
		ruleNamesConversionTable.put(SubfilterFileFilerRule.typeName, Messages.getString("FilterRuleListItem.NestedFilter")); //$NON-NLS-1$

		reverseRuleNamesConversionTable = new Hashtable(15, 0.75f);
		reverseRuleNamesConversionTable.put(Messages.getString("FilterRuleListItem.FileNameFilter"), FileNameFileFilterRule.typeName); //$NON-NLS-1$
		reverseRuleNamesConversionTable.put(Messages.getString("FilterRuleListItem.FilePathFilter"), FilePathFileFilterRule.typeName); //$NON-NLS-1$
		reverseRuleNamesConversionTable.put(Messages.getString("FilterRuleListItem.FileTypeFilter"), FileTypeFileFilterRule.typeName); //$NON-NLS-1$
		reverseRuleNamesConversionTable.put(Messages.getString("FilterRuleListItem.FilSizeFilter"), FileSizeFileFilterRule.typeName); //$NON-NLS-1$
		reverseRuleNamesConversionTable.put(Messages.getString("FilterRuleListItem.FileModificationDateFilter"), FileModificationDateFileFilterRule.typeName); //$NON-NLS-1$
		reverseRuleNamesConversionTable.put(Messages.getString("FilterRuleListItem.FileAgeFilter"), FileAgeFileFilterRule.typeName); //$NON-NLS-1$
		reverseRuleNamesConversionTable.put(Messages.getString("FilterRuleListItem.NestedFilter"), SubfilterFileFilerRule.typeName); //$NON-NLS-1$
	}

	private Class getRuleClass(String typeName) {
		return (Class) rulesTable.get(ruleNamesConversionTable.get(typeName));
	}

	private String getRuleTypeName(String guiName) {
		return (String) reverseRuleNamesConversionTable.get(guiName);
	}

	private String getRuleGUIName(String typeName) {
		return (String) ruleNamesConversionTable.get(typeName);
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

	@Override
	public void onValueChanged(ValueChangedEvent evt) {
		value = evt.getValue();
	}

	public void init(final Composite composite) {
		final FilterRuleListItem ruleItem = this;

		final Combo comboRuleTypes = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);

		comboRuleTypes.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				ruleType = getRuleTypeName(comboRuleTypes.getText());
				root.recreateRuleList();
			}
		});

		comboRuleTypes.removeAll();
		for (int i = 0; i < ruleTypeNames.length; i++) {
			comboRuleTypes.add(ruleTypeNames[i]);
		}

		if ((ruleType == null) || (ruleType.equals(""))) { //$NON-NLS-1$
			comboRuleTypes.select(0);
			ruleType = getRuleTypeName(comboRuleTypes.getText());
		}
		else {
			comboRuleTypes.setText(getRuleGUIName(ruleType));
		}

		Combo comboOperators = null;
		if (!getRuleClass(ruleType).equals(SubfilterFileFilerRule.class)) {
			comboOperators = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
			final Combo comboOp = comboOperators;
			comboOperators.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent evt) {
					op = comboOp.getSelectionIndex();
					comboOp.getParent().layout();
				}
			});
		}

		if (!getRuleClass(ruleType).equals(SubfilterFileFilerRule.class)) {
			String[] ops = fileFilterManager.getOperatorsForRuleType(ruleType);
			comboOperators.removeAll();
			for (int i = 0; i < ops.length; i++) {
				comboOperators.add(ops[i]);
			}
			if ((op < 0) || (op >= comboOperators.getItemCount())) {
				op = 0;
			}
			comboOperators.select(op);
			if (ops.length == 0) {
				comboOperators.setVisible(false);
			}
		}

		if ((getRuleClass(ruleType).equals(FileNameFileFilterRule.class)) ||
				(getRuleClass(ruleType).equals(FilePathFileFilterRule.class)))
		{
			if (!(value instanceof TextValue)) {
				value = new TextValue();
			}

			ruleComposite = new TextValueRuleComposite(composite, SWT.NULL, (TextValue)value);
			ruleComposite.addValueChangedListener(this);
		}
		else if (getRuleClass(ruleType).equals(FileTypeFileFilterRule.class)) {
			if (!(value instanceof TypeValue)) {
				value = new TypeValue();
			}

			ruleComposite = new TypeValueRuleComposite(composite, SWT.NULL, (TypeValue)value);
			ruleComposite.addValueChangedListener(this);
		}
		else if (getRuleClass(ruleType).equals(FileSizeFileFilterRule.class)) {
			if (!(value instanceof SizeValue)) {
				value = new SizeValue();
			}

			ruleComposite = new SizeValueRuleComposite(composite, SWT.NULL, (SizeValue)value);
			ruleComposite.addValueChangedListener(this);
		}
		else if (getRuleClass(ruleType).equals(FileAgeFileFilterRule.class)) {
			if (!(value instanceof AgeValue)) {
				value = new AgeValue();
			}

			ruleComposite = new AgeValueRuleComposite(composite, SWT.NULL, (AgeValue)value);
			ruleComposite.addValueChangedListener(this);
		}
		else if (getRuleClass(ruleType).equals(FileModificationDateFileFilterRule.class)) {
			if (!(value instanceof DateValue)) {
				value = new DateValue(SystemDate.getInstance().currentTimeMillis());
			}

			ruleComposite = new DateValueRuleComposite(composite, SWT.NULL, (DateValue)value);
			ruleComposite.addValueChangedListener(this);
		}
		else if (getRuleClass(ruleType).equals(SubfilterFileFilerRule.class)) {
			if (!(value instanceof FilterValue)) {
				value = new FilterValue(new FileFilter());
			}
			ruleComposite = new SubfilterRuleComposite(composite, SWT.NULL, (FilterValue)value);
			ruleComposite.addValueChangedListener(this);

			GridData ruleCompositeLData = new GridData();
			ruleCompositeLData.horizontalSpan = 2;
			ruleCompositeLData.horizontalAlignment = GridData.FILL;
			ruleCompositeLData.grabExcessHorizontalSpace = true;
			ruleCompositeLData.horizontalAlignment = GridData.BEGINNING;

			ruleComposite.setLayoutData(ruleCompositeLData);
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

			textValue.setText("Missing Rule Composite"); //$NON-NLS-1$
			textValue.setForeground(redColor);

			textValue.setEditable(false);
			textValue.setBackground(whiteColor);
		}
		{
            ToolBar toolBar = new ToolBar(composite, SWT.FLAT);
            toolBar.setBackground(whiteColor);
            ToolItem toolItemDelete = new ToolItem(toolBar, SWT.PUSH);
            toolItemDelete.setImage(GuiController.getInstance().getImage("Rule_Delete.png")); //$NON-NLS-1$
            toolItemDelete.setToolTipText(Messages.getString("FilterRuleListItem.Delete")); //$NON-NLS-1$
            toolItemDelete.addSelectionListener(new SelectionAdapter() {
            	@Override
				public void widgetSelected(SelectionEvent evt) {
            		root.deleteRule(ruleItem);
            	}
            });
		}
	}

}
