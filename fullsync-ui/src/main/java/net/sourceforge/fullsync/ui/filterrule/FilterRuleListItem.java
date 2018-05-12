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

import javax.inject.Inject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.rules.filefilter.FileAgeFileFilterRule;
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
import net.sourceforge.fullsync.ui.ImageRepository;
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

	private final ImageRepository imageRepository;

	private String ruleType;
	private int op;
	private FileFilterPage root;
	private OperandValue value;

	private Composite ruleCompositeWrapper;
	private RuleComposite ruleComposite;

	private final FileFilterManager fileFilterManager = new FileFilterManager();
	private Combo comboOperators;

	@Inject
	public FilterRuleListItem(ImageRepository imageRepository) {
		this.imageRepository = imageRepository;
	}

	public void init(FileFilterPage root, String ruleType, int op, OperandValue value) {
		this.ruleType = ruleType;
		this.op = op;
		this.root = root;
		this.value = value;
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
		return fileFilterManager.createFileFilterRule(ruleType, op, ruleComposite.getValue());
	}

	public void render(final Composite composite) {
		final FilterRuleListItem ruleItem = this;

		final Combo comboRuleTypes = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);

		comboRuleTypes.addListener(SWT.Selection, e -> {
			ruleType = getRuleTypeName(comboRuleTypes.getText());
			value = ruleComposite.getValue();
			composite.getDisplay().asyncExec(() -> {
				for (Control c : ruleCompositeWrapper.getChildren()) {
					c.dispose();
				}
				renderRuleComposite();
				composite.layout();
			});
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

		comboOperators = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		comboOperators.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		comboOperators.addListener(SWT.Selection, e -> {
			op = comboOperators.getSelectionIndex();
			comboOperators.getParent().layout();
		});

		ruleCompositeWrapper = new Composite(composite, SWT.FILL);
		ruleCompositeWrapper.setLayout(new FillLayout());
		ruleCompositeWrapper.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		renderRuleComposite();

		ToolBar toolBar = new ToolBar(composite, SWT.FLAT);
		ToolItem toolItemDelete = new ToolItem(toolBar, SWT.PUSH);
		toolItemDelete.setImage(imageRepository.getImage("Rule_Delete.png")); //$NON-NLS-1$
		toolItemDelete.setToolTipText(Messages.getString("FilterRuleListItem.Delete")); //$NON-NLS-1$
		toolItemDelete.addListener(SWT.Selection, e -> root.deleteRule(composite, ruleItem));

		ToolItem toolItemAdd = new ToolItem(toolBar, SWT.PUSH);
		toolItemAdd.setImage(imageRepository.getImage("Rule_Add.png")); //$NON-NLS-1$
		toolItemAdd.setToolTipText(Messages.getString("FilterRuleListItem.Add")); //$NON-NLS-1$
		toolItemAdd.addListener(SWT.Selection, e -> root.addRuleRow());
	}

	private void renderRuleComposite() {
		Class<? extends FileFilterRule> ruleClass = getRuleClass(ruleType);
		String[] ops = fileFilterManager.getOperatorsForRuleType(ruleType);
		comboOperators.removeAll();
		for (String op2 : ops) {
			comboOperators.add(op2);
		}
		if ((op < 0) || (op >= comboOperators.getItemCount())) {
			op = 0;
		}
		comboOperators.select(op);
		GridData comboOperatosLD = (GridData) comboOperators.getLayoutData();
		boolean hasOperators = 0 != ops.length;
		comboOperatosLD.exclude = !hasOperators;
		comboOperators.setEnabled(hasOperators);
		comboOperators.setVisible(hasOperators);
		if (ruleClass.equals(SubfilterFileFilerRule.class)) {
			comboOperators.setEnabled(false);
		}

		GridData compositeWrapperLD = (GridData) ruleCompositeWrapper.getLayoutData();
		compositeWrapperLD.horizontalSpan = 1;

		if ((ruleClass.equals(FileNameFileFilterRule.class)) || (ruleClass.equals(FilePathFileFilterRule.class))) {
			if (!(value instanceof TextValue)) {
				value = null;
			}

			ruleComposite = new TextValueRuleComposite(ruleCompositeWrapper, (TextValue) value);
		}
		else if (ruleClass.equals(FileTypeFileFilterRule.class)) {
			if (!(value instanceof TypeValue)) {
				value = null;
			}

			ruleComposite = new TypeValueRuleComposite(ruleCompositeWrapper, (TypeValue) value);
		}
		else if (ruleClass.equals(FileSizeFileFilterRule.class)) {
			if (!(value instanceof SizeValue)) {
				value = null;
			}

			ruleComposite = new SizeValueRuleComposite(ruleCompositeWrapper, (SizeValue) value);
		}
		else if (ruleClass.equals(FileAgeFileFilterRule.class)) {
			if (!(value instanceof AgeValue)) {
				value = null;
			}

			ruleComposite = new AgeValueRuleComposite(ruleCompositeWrapper, (AgeValue) value);
		}
		else if (ruleClass.equals(FileModificationDateFileFilterRule.class)) {
			if (!(value instanceof DateValue)) {
				value = null;
			}

			ruleComposite = new DateValueRuleComposite(ruleCompositeWrapper, (DateValue) value);
		}
		else if (ruleClass.equals(SubfilterFileFilerRule.class)) {
			if (!(value instanceof FilterValue)) {
				value = null;
			}
			compositeWrapperLD.horizontalSpan = 2;
			ruleComposite = new SubfilterRuleComposite(root.getFileFilterPageProvider(), ruleCompositeWrapper, (FilterValue) value);
		}
		else {
			compositeWrapperLD.horizontalSpan = 2;
			Text textValue = new Text(ruleCompositeWrapper, SWT.BORDER);
			textValue.setText("Missing Rule Composite");
			textValue.setEditable(false);
		}
	}

	public void setError(String message) {
		ruleComposite.setError(message);
	}
}
