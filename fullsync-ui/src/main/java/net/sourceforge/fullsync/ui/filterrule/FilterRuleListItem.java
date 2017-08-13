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

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.fullsync.DataParseException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import net.sourceforge.fullsync.SystemDate;
import net.sourceforge.fullsync.rules.filefilter.FileAgeFileFilterRule;
import net.sourceforge.fullsync.rules.filefilter.FileFilter;
import net.sourceforge.fullsync.rules.filefilter.FileFilterManager;
import net.sourceforge.fullsync.rules.filefilter.FileFilterRule;
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
import net.sourceforge.fullsync.ui.FileFilterPage;
import net.sourceforge.fullsync.ui.GuiController;
import net.sourceforge.fullsync.ui.Messages;

public class FilterRuleListItem {
	private static final Map<String, Class<? extends FileFilterRule>> rulesTable;
	private static final Map<String, String> ruleNamesConversionTable;
	private static final Map<String, String> reverseRuleNamesConversionTable;
	private static final String[] ruleTypeNames;

	static {
		final String name = Messages.getString("FilterRuleListItem.FileNameFilter");
		final String path = Messages.getString("FilterRuleListItem.FilePathFilter");
		final String type = Messages.getString("FilterRuleListItem.FileTypeFilter");
		final String size = Messages.getString("FilterRuleListItem.FilSizeFilter");
		final String modificationDate = Messages.getString("FilterRuleListItem.FileModificationDateFilter");
		final String age = Messages.getString("FilterRuleListItem.FileAgeFilter");
		final String nested = Messages.getString("FilterRuleListItem.NestedFilter");

		ruleTypeNames = new String[] { name, path, type, size, modificationDate, age, nested };

		rulesTable = new HashMap<>();
		rulesTable.put(name, FileNameFileFilterRule.class);
		rulesTable.put(path, FilePathFileFilterRule.class);
		rulesTable.put(type, FileTypeFileFilterRule.class);
		rulesTable.put(size, FileSizeFileFilterRule.class);
		rulesTable.put(modificationDate, FileModificationDateFileFilterRule.class);
		rulesTable.put(age, FileAgeFileFilterRule.class);
		rulesTable.put(nested, SubfilterFileFilerRule.class);

		ruleNamesConversionTable = new HashMap<>();
		ruleNamesConversionTable.put(FileNameFileFilterRule.TYPE_NAME, name);
		ruleNamesConversionTable.put(FilePathFileFilterRule.TYPE_NAME, path);
		ruleNamesConversionTable.put(FileTypeFileFilterRule.TYPE_NAME, type);
		ruleNamesConversionTable.put(FileSizeFileFilterRule.TYPE_NAME, size);
		ruleNamesConversionTable.put(FileModificationDateFileFilterRule.TYPE_NAME, modificationDate);
		ruleNamesConversionTable.put(FileAgeFileFilterRule.TYPE_NAME, age);
		ruleNamesConversionTable.put(SubfilterFileFilerRule.TYPE_NAME, nested);

		reverseRuleNamesConversionTable = new HashMap<>();
		reverseRuleNamesConversionTable.put(name, FileNameFileFilterRule.TYPE_NAME);
		reverseRuleNamesConversionTable.put(path, FilePathFileFilterRule.TYPE_NAME);
		reverseRuleNamesConversionTable.put(type, FileTypeFileFilterRule.TYPE_NAME);
		reverseRuleNamesConversionTable.put(size, FileSizeFileFilterRule.TYPE_NAME);
		reverseRuleNamesConversionTable.put(modificationDate, FileModificationDateFileFilterRule.TYPE_NAME);
		reverseRuleNamesConversionTable.put(age, FileAgeFileFilterRule.TYPE_NAME);
		reverseRuleNamesConversionTable.put(nested, SubfilterFileFilerRule.TYPE_NAME);
	}

	private String ruleType;
	private int op;
	private OperandValue value;

	private RuleComposite ruleComposite;

	private final FileFilterPage root;

	private final FileFilterManager fileFilterManager = new FileFilterManager();

	public FilterRuleListItem(FileFilterPage root, Composite composite, String ruleType, int op,
		OperandValue value) {
		this.ruleType = ruleType;
		this.op = op;
		this.root = root;
		this.value = value;
		init(composite);
	}

	private static Class<? extends FileFilterRule> getRuleClass(String typeName) {
		return rulesTable.get(ruleNamesConversionTable.get(typeName));
	}

	private static String getRuleTypeName(String guiName) {
		return reverseRuleNamesConversionTable.get(guiName);
	}

	private static String getRuleGUIName(String typeName) {
		return ruleNamesConversionTable.get(typeName);
	}

	public FileFilterRule getFileFilterRule() throws DataParseException {
		return fileFilterManager.createFileFilterRule(ruleType, op, value);
	}

	public void init(final Composite composite) {
		final FilterRuleListItem ruleItem = this;

		final Combo comboRuleTypes = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);

		comboRuleTypes.addListener(SWT.Selection, e -> {
			ruleType = getRuleTypeName(comboRuleTypes.getText());
			root.recreateRuleList();
		});

		comboRuleTypes.removeAll();
		for (String ruleTypeName : ruleTypeNames) {
			comboRuleTypes.add(ruleTypeName);
		}

		if ((null == ruleType) || ruleType.isEmpty()) {
			comboRuleTypes.select(0);
			ruleType = getRuleTypeName(comboRuleTypes.getText());
		}
		else {
			comboRuleTypes.setText(getRuleGUIName(ruleType));
		}

		Class<? extends FileFilterRule> ruleClass = getRuleClass(ruleType);
		Combo comboOperators = null;
		if (!ruleClass.equals(SubfilterFileFilerRule.class)) {
			comboOperators = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
			comboOperators.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			final Combo comboOp = comboOperators;
			comboOperators.addListener(SWT.Selection, e -> {
				op = comboOp.getSelectionIndex();
				comboOp.getParent().layout();
			});
		}

		if (!ruleClass.equals(SubfilterFileFilerRule.class)) {
			String[] ops = fileFilterManager.getOperatorsForRuleType(ruleType);
			comboOperators.removeAll();
			for (String op2 : ops) {
				comboOperators.add(op2);
			}
			if ((op < 0) || (op >= comboOperators.getItemCount())) {
				op = 0;
			}
			comboOperators.select(op);
			if (ops.length == 0) {
				comboOperators.setVisible(false);
			}
		}

		if ((ruleClass.equals(FileNameFileFilterRule.class)) || (ruleClass.equals(FilePathFileFilterRule.class))) {
			if (!(value instanceof TextValue)) {
				value = new TextValue();
			}

			ruleComposite = new TextValueRuleComposite(composite, SWT.NULL, (TextValue) value);
		}
		else if (ruleClass.equals(FileTypeFileFilterRule.class)) {
			if (!(value instanceof TypeValue)) {
				value = new TypeValue();
			}

			ruleComposite = new TypeValueRuleComposite(composite, SWT.NULL, (TypeValue) value);
		}
		else if (ruleClass.equals(FileSizeFileFilterRule.class)) {
			if (!(value instanceof SizeValue)) {
				value = new SizeValue();
			}

			ruleComposite = new SizeValueRuleComposite(composite, SWT.NULL, (SizeValue) value);
		}
		else if (ruleClass.equals(FileAgeFileFilterRule.class)) {
			if (!(value instanceof AgeValue)) {
				value = new AgeValue();
			}

			ruleComposite = new AgeValueRuleComposite(composite, SWT.NULL, (AgeValue) value);
		}
		else if (ruleClass.equals(FileModificationDateFileFilterRule.class)) {
			if (!(value instanceof DateValue)) {
				value = new DateValue(SystemDate.getInstance().currentTimeMillis());
			}

			ruleComposite = new DateValueRuleComposite(composite, SWT.NULL, (DateValue) value);
		}
		else if (ruleClass.equals(SubfilterFileFilerRule.class)) {
			if (!(value instanceof FilterValue)) {
				value = new FilterValue(new FileFilter());
			}
			ruleComposite = new SubfilterRuleComposite(composite, SWT.NULL, (FilterValue) value);
		}
		else {
			Composite valueComposite = new Composite(composite, SWT.NULL);

			Text textValue = new Text(valueComposite, SWT.BORDER);
			textValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			textValue.setText("Missing Rule Composite");
			textValue.setEditable(false);
		}
		if (null != ruleComposite) {
			ruleComposite.addValueChangedListener(e -> value = e.getValue());
		}

		ToolBar toolBar = new ToolBar(composite, SWT.FLAT);
		ToolItem toolItemDelete = new ToolItem(toolBar, SWT.PUSH);
		toolItemDelete.setImage(GuiController.getInstance().getImage("Rule_Delete.png")); //$NON-NLS-1$
		toolItemDelete.setToolTipText(Messages.getString("FilterRuleListItem.Delete")); //$NON-NLS-1$
		toolItemDelete.addListener(SWT.Selection, e -> root.deleteRule(ruleItem));

		ToolItem toolItemAdd = new ToolItem(toolBar, SWT.PUSH);
		toolItemAdd.setImage(GuiController.getInstance().getImage("Rule_Add.png")); //$NON-NLS-1$
		toolItemAdd.setToolTipText(Messages.getString("FilterRuleListItem.Add")); //$NON-NLS-1$
		toolItemAdd.addListener(SWT.Selection, e -> root.addRuleRow());
	}

	public void setError(String message) {
		ruleComposite.setError(message);
	}
}
