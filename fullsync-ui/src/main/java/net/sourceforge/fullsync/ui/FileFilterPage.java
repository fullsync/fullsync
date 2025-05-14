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

import java.util.ArrayList;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.rules.filefilter.FileFilter;
import net.sourceforge.fullsync.rules.filefilter.FileFilterRule;
import net.sourceforge.fullsync.rules.filefilter.values.OperandValue;
import net.sourceforge.fullsync.ui.filterrule.FilterRuleListItem;

public class FileFilterPage extends WizardDialog {
	private final Provider<FileFilterPage> fileFilterPageProvider;
	private final Provider<FilterRuleListItem> filterRuleListItemProvider;
	private Combo comboFilterType;
	private Combo comboMatchType;
	private Composite compositeRuleList;
	private Button buttonAppliesToDir;
	private FileFilter oldFileFilter;
	private FileFilter newFileFilter;
	private final List<FilterRuleListItem> ruleItems = new ArrayList<>();

	@Inject
	public FileFilterPage(Shell mainShell, Provider<FileFilterPage> fileFilterPageProvider,
		Provider<FilterRuleListItem> filterRuleListItemProvider) {
		super(mainShell);
		this.fileFilterPageProvider = fileFilterPageProvider;
		this.filterRuleListItemProvider = filterRuleListItemProvider;
	}

	public void setFileFilter(FileFilter fileFilter) {
		this.oldFileFilter = fileFilter;
	}

	public Provider<FileFilterPage> getFileFilterPageProvider() {
		return fileFilterPageProvider;
	}

	@Override
	public String getTitle() {
		return Messages.getString("FileFilterPage.Title"); //$NON-NLS-1$
	}

	@Override
	public String getCaption() {
		return Messages.getString("FileFilterPage.Caption"); //$NON-NLS-1$
	}

	@Override
	public String getDescription() {
		return ""; //$NON-NLS-1$
	}

	@Override
	public String getIconName() {
		return "FileFilter_Default.png"; //$NON-NLS-1$
	}

	@Override
	public String getImageName() {
		return "FileFilter_Wizard.png"; //$NON-NLS-1$
	}

	@Override
	public void createContent(Composite content) {
		try {
			content.setLayout(new GridLayout(4, false));
			content.setSize(700, 500);

			// filter type combo
			comboFilterType = new Combo(content, SWT.DROP_DOWN | SWT.READ_ONLY);
			comboFilterType.add(Messages.getString("FileFilterPage.Include")); //$NON-NLS-1$
			comboFilterType.add(Messages.getString("FileFilterPage.Exclude")); //$NON-NLS-1$
			var label1 = new Label(content, SWT.NONE);
			label1.setText(Messages.getString("FileFilterPage.AnyFileThatMatches")); //$NON-NLS-1$

			// match type combo
			comboMatchType = new Combo(content, SWT.DROP_DOWN | SWT.READ_ONLY);
			comboMatchType.add(Messages.getString("FileFilterPage.RequireAll")); //$NON-NLS-1$
			comboMatchType.add(Messages.getString("FileFilterPage.RequireAny")); //$NON-NLS-1$
			var label2 = new Label(content, SWT.NONE);
			label2.setText(Messages.getString("FileFilterPage.OfTheFollowing")); //$NON-NLS-1$

			// applies to directories
			buttonAppliesToDir = new Button(content, SWT.CHECK | SWT.LEFT);
			buttonAppliesToDir.setText(Messages.getString("FileFilterPage.AppliesToDirectories")); //$NON-NLS-1$
			var buttonAppliesToDirLData = new GridData();
			buttonAppliesToDirLData.horizontalSpan = 4;
			buttonAppliesToDir.setLayoutData(buttonAppliesToDirLData);

			// filter list
			var scrolledComposite1LData = new GridData(SWT.FILL, SWT.FILL, true, true);
			scrolledComposite1LData.horizontalSpan = 4;
			var scrolledComposite1 = new ScrolledComposite(content, SWT.V_SCROLL | SWT.BORDER);
			scrolledComposite1.setLayoutData(scrolledComposite1LData);
			scrolledComposite1.setAlwaysShowScrollBars(true);
			scrolledComposite1.setExpandHorizontal(true);
			compositeRuleList = new Composite(scrolledComposite1, SWT.FILL);
			compositeRuleList.setLayout(new GridLayout(4, false));
			scrolledComposite1.setContent(compositeRuleList);

			var rowsAdded = 0;
			if (null != oldFileFilter) {
				comboMatchType.select(oldFileFilter.matchType());
				comboFilterType.select(oldFileFilter.filterType());
				buttonAppliesToDir.setSelection(oldFileFilter.appliesToDirectories());
				for (FileFilterRule rule : oldFileFilter.rules()) {
					addRuleRow(rule.getRuleType(), rule.getOperator(), rule.getValue());
					++rowsAdded;
				}
			}
			else {
				comboMatchType.select(0);
				comboFilterType.select(0);
				buttonAppliesToDir.setSelection(true);
			}
			if (0 == rowsAdded) {
				addRuleRow();
			}

			compositeRuleList.setSize(compositeRuleList.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			content.layout();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean apply() {
		var matchType = comboMatchType.getSelectionIndex();
		var filterType = comboFilterType.getSelectionIndex();
		var appliesToDirectories = buttonAppliesToDir.getSelection();

		var rules = new FileFilterRule[ruleItems.size()];
		for (var i = 0; i < rules.length; i++) {
			var ruleItem = ruleItems.get(i);
			try {
				rules[i] = ruleItem.getFileFilterRule();
			}
			catch (DataParseException e) {
				ruleItem.setError(e.getMessage());
				return false;
			}
		}

		newFileFilter = new FileFilter(matchType, filterType, appliesToDirectories, rules);
		return true;
	}

	@Override
	public boolean cancel() {
		return true;
	}

	public void addRuleRow() {
		addRuleRow(null, -1, null);
		layoutList();
	}

	public void layoutList() {
		compositeRuleList.setSize(compositeRuleList.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		compositeRuleList.layout(true);
	}

	private void addRuleRow(String ruleType, int op, OperandValue value) {
		var ruleItem = filterRuleListItemProvider.get();
		ruleItem.init(this, ruleType, op, value);
		ruleItem.render(compositeRuleList);
		ruleItems.add(ruleItem);
	}

	public boolean deleteRule(FilterRuleListItem item) {
		if (ruleItems.size() > 1) {
			ruleItems.remove(item);
			compositeRuleList.getDisplay().asyncExec(this::layoutList);
			return true;
		}
		return false;
	}

	public FileFilter getFileFilter() {
		return newFileFilter;
	}
}
