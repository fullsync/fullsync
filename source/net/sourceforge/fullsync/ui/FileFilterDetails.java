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
package net.sourceforge.fullsync.ui;

import java.util.Vector;

import net.sourceforge.fullsync.rules.filefilter.FileFilter;
import net.sourceforge.fullsync.rules.filefilter.FileFilterManager;
import net.sourceforge.fullsync.rules.filefilter.FileFilterRule;
import net.sourceforge.fullsync.rules.filefilter.values.OperandValue;
import net.sourceforge.fullsync.ui.filterrule.FilterRuleListItem;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class FileFilterDetails extends Composite {

	private Combo comboFilterType;
	private Label label1;
	private Combo comboMatchType;
	private Label label2;
	private ScrolledComposite scrolledComposite1;
	private Composite compositeRuleList;
	private Button buttonAppliesToDir;

	private FileFilterManager fileFilterManager = new FileFilterManager();
	private FileFilter fileFilter;

	private Vector<FilterRuleListItem> ruleItems = new Vector<FilterRuleListItem>();

	public FileFilterDetails(Composite parent, int style, FileFilter fileFilter) {
		super(parent, style);
		this.fileFilter = fileFilter;
		try {
			this.setLayout(new GridLayout(4, false));
			this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			this.setSize(700, 500);

			// filter type combo
			comboFilterType = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
			comboFilterType.add("Include");
			comboFilterType.add("Exclude");
			label1 = new Label(this, SWT.NONE);
			label1.setText(" any file that matches ");

			// match type combo
			comboMatchType = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
			comboMatchType.add("all");
			comboMatchType.add("any");
			label2 = new Label(this, SWT.NONE);
			label2.setText("of the following");

			// applies to directories
			buttonAppliesToDir = new Button(this, SWT.CHECK | SWT.LEFT);
			buttonAppliesToDir.setText("Applies to directories");
			GridData buttonAppliesToDirLData = new GridData();
			buttonAppliesToDirLData.horizontalSpan = 4;
			buttonAppliesToDir.setLayoutData(buttonAppliesToDirLData);

			// filter list
			GridData scrolledComposite1LData = new GridData(SWT.FILL, SWT.FILL, true, true);
			scrolledComposite1LData.horizontalSpan = 4;
			scrolledComposite1 = new ScrolledComposite(this, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
			createCompositeRuleList();
			scrolledComposite1.setLayoutData(scrolledComposite1LData);
			scrolledComposite1.setAlwaysShowScrollBars(false);

			if (fileFilter != null) {
				comboMatchType.select(fileFilter.getMatchType());
				comboFilterType.select(fileFilter.getFilterType());
				buttonAppliesToDir.setSelection(fileFilter.appliesToDirectories());
				FileFilterRule[] rules = fileFilter.getFileFiltersRules();
				if (rules.length > 0) {
					for (FileFilterRule rule : rules) {
						addRuleRow(rule.getRuleType(), rule.getOperator(), rule.getValue());
					}
				}
				else {
					addRuleRow();
				}
			}
			else {
				comboMatchType.select(0);
				comboFilterType.select(0);
				buttonAppliesToDir.setSelection(true);
				addRuleRow();
			}

			this.layout();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addRuleRow() {
		addRuleRow(null, -1, null);
	}

	protected void addRuleRow(String ruleType, int op, OperandValue value) {
		FilterRuleListItem ruleItem = new FilterRuleListItem(this, compositeRuleList, fileFilterManager, ruleType, op, value);
		ruleItems.add(ruleItem);
		compositeRuleList.pack();
	}

	public void recreateRuleList() {
		compositeRuleList.dispose();
		createCompositeRuleList();
		for (FilterRuleListItem item : ruleItems) {
			item.init(compositeRuleList);
		}
		compositeRuleList.pack();
	}

	public void deleteRule(FilterRuleListItem item) {
		if (ruleItems.size() > 1) {
			ruleItems.remove(item);
			recreateRuleList();
		}
	}

	protected void createCompositeRuleList() {
		compositeRuleList = new Composite(scrolledComposite1, SWT.FILL);
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		compositeRuleList.setLayoutData(layoutData);
		scrolledComposite1.setContent(compositeRuleList);
		compositeRuleList.setLayout(new GridLayout(4, false));
	}

	public FileFilter getFileFilter() {
		fileFilter = new FileFilter();

		fileFilter.setMatchType(comboMatchType.getSelectionIndex());
		fileFilter.setFilterType(comboFilterType.getSelectionIndex());
		fileFilter.setAppliesToDirectories(buttonAppliesToDir.getSelection());

		FileFilterRule[] rules = new FileFilterRule[ruleItems.size()];
		for (int i = 0; i < rules.length; i++) {
			FilterRuleListItem ruleItem = ruleItems.get(i);
			rules[i] = fileFilterManager.createFileFilterRule(ruleItem.getRuleType(), ruleItem.getOperator(), ruleItem.getValue());
		}

		fileFilter.setFileFilterRules(rules);
		return fileFilter;
	}

}
