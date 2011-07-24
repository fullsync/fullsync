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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * This code was generated using CloudGarden's Jigloo
 * SWT/Swing GUI Builder, which is free for non-commercial
 * use. If Jigloo is being used commercially (ie, by a corporation,
 * company or business for any purpose whatever) then you
 * should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details.
 * Use of Jigloo implies acceptance of these licensing terms.
 * *************************************
 * A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED
 * for this machine, so Jigloo or this code cannot be used legally
 * for any corporate or commercial purpose.
 * *************************************
 */
public class FileFilterDetails extends Composite {

	private Combo comboFilterType;
	private Label label1;
	private Combo comboMatchType;
	private Label label2;
	private ScrolledComposite scrolledComposite1;
	private Composite compositeRuleList;
	private Button buttonFewer;
	private Button buttonMore;
	private Button buttonAppliesToDir;

	private Color whiteColor = new Color(null, 255, 255, 255);

	private FileFilterManager fileFilterManager = new FileFilterManager();
	private FileFilter fileFilter;

	private Vector ruleItems = new Vector();

	/**
	 * Auto-generated main method to display this
	 * org.eclipse.swt.widgets.Composite inside a new Shell.
	 */
	public static void main(String[] args) {
		showGUI();
	}

	/**
	 * Auto-generated method to display this
	 * org.eclipse.swt.widgets.Composite inside a new Shell.
	 */
	public static void showGUI() {
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		FileFilterDetails inst = new FileFilterDetails(shell, SWT.NULL, null);
		Point size = inst.getSize();
		shell.setLayout(new FillLayout());
		shell.layout();
		if (size.x == 0 && size.y == 0) {
			inst.pack();
			shell.pack();
		}
		else {
			Rectangle shellBounds = shell.computeTrim(0, 0, size.x, size.y);
			shell.setSize(shellBounds.width, shellBounds.height);
		}
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	public FileFilterDetails(Composite parent, int style, FileFilter fileFilter) {
		super(parent, style);
		this.fileFilter = fileFilter;
		initGUI();
	}

	private void initGUI() {
		try {
			GridLayout thisLayout = new GridLayout();
			thisLayout.makeColumnsEqualWidth = false;
			this.setLayout(thisLayout);
			thisLayout.numColumns = 4;
			this.setSize(459, 433);
			{
				GridData comboFilterTypeLData = new GridData();
				comboFilterTypeLData.widthHint = 60;
				comboFilterTypeLData.heightHint = 21;
				comboFilterType = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
				comboFilterType.setLayoutData(comboFilterTypeLData);
				comboFilterType.add("Include");
				comboFilterType.add("Exclude");
			}

			{
				label1 = new Label(this, SWT.NONE);
				label1.setText(" any file that matches ");
			}
			{
				comboMatchType = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
				comboMatchType.add("all");
				comboMatchType.add("any");
			}
			{
				label2 = new Label(this, SWT.NONE);
				label2.setText("of the following");
			}
			{
				GridData scrolledComposite1LData = new GridData();
				scrolledComposite1LData.horizontalSpan = 4;
				scrolledComposite1LData.heightHint = 298;
				scrolledComposite1LData.horizontalAlignment = GridData.FILL;
				scrolledComposite1LData.grabExcessVerticalSpace = true;
				scrolledComposite1LData.grabExcessHorizontalSpace = true;
				scrolledComposite1 = new ScrolledComposite(this, SWT.V_SCROLL | SWT.BORDER);
				createCompositeRuleList();
				scrolledComposite1.setLayout(null);
				scrolledComposite1.setLayoutData(scrolledComposite1LData);
				scrolledComposite1.setExpandHorizontal(true);
				scrolledComposite1.setExpandVertical(false);
				scrolledComposite1.setVisible(true);
				scrolledComposite1.setEnabled(true);
				scrolledComposite1.setBackground(whiteColor);
				scrolledComposite1.setAlwaysShowScrollBars(false);
			}
			{
				Composite buttonsComposite = new Composite(this, SWT.NULL);
				GridLayout buttonsCompositeLayout = new GridLayout();
				buttonsCompositeLayout.numColumns = 3;
				GridData buttonsCompositeLData = new GridData();
				buttonsCompositeLData.horizontalSpan = 4;
				buttonsCompositeLData.grabExcessHorizontalSpace = true;
				buttonsCompositeLData.horizontalAlignment = GridData.FILL;
				buttonsComposite.setLayoutData(buttonsCompositeLData);
				buttonsComposite.setLayout(buttonsCompositeLayout);
				{
					buttonMore = new Button(buttonsComposite, SWT.PUSH | SWT.CENTER);
					buttonMore.setText("More");
					buttonMore.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent evt) {
							addRuleRow();
							buttonFewer.setEnabled(ruleItems.size() > 1);
						}
					});
					GridData buttonMoreLData = new GridData();
					buttonMoreLData.horizontalAlignment = GridData.BEGINNING;
					buttonMore.setLayoutData(buttonMoreLData);
				}
				{
					buttonFewer = new Button(buttonsComposite, SWT.PUSH | SWT.CENTER);
					buttonFewer.setText("Fewer");
					buttonFewer.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent evt) {
							removeRuleRow();
							buttonFewer.setEnabled(ruleItems.size() > 1);
						}
					});
					GridData buttonFewerLData = new GridData();
					buttonFewerLData.horizontalAlignment = GridData.BEGINNING;
					buttonFewer.setLayoutData(buttonFewerLData);
				}
				{
					buttonAppliesToDir = new Button(buttonsComposite, SWT.CHECK | SWT.LEFT);
					buttonAppliesToDir.setText("Applies to directories");
					GridData buttonAppliesToDirLData = new GridData();
					buttonAppliesToDirLData.horizontalAlignment = GridData.END;
					buttonAppliesToDirLData.grabExcessHorizontalSpace = true;
					buttonAppliesToDir.setLayoutData(buttonAppliesToDirLData);
				}
			}

			if (fileFilter != null) {
				comboMatchType.select(fileFilter.getMatchType());
				comboFilterType.select(fileFilter.getFilterType());
				buttonAppliesToDir.setSelection(fileFilter.appliesToDirectories());
				FileFilterRule[] rules = fileFilter.getFileFiltersRules();
				for (int i = 0; i < rules.length; i++) {
					addRuleRow(rules[i].getRuleType(), rules[i].getOperator(), rules[i].getValue());
				}
			}
			else {
				comboMatchType.select(0);
				comboFilterType.select(0);
				buttonAppliesToDir.setSelection(true);
				addRuleRow();
			}
			buttonFewer.setEnabled(ruleItems.size() > 1);

			this.layout();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void addRuleRow() {
		addRuleRow(null, -1, null);
	}

	protected void addRuleRow(String ruleType, int op, OperandValue value) {
		FilterRuleListItem ruleItem = new FilterRuleListItem(this, compositeRuleList, fileFilterManager, ruleType, op, value);
		ruleItems.add(ruleItem);
		compositeRuleList.pack();
	}

	protected void removeRuleRow() {
		ruleItems.removeElementAt(ruleItems.size() - 1);
		recreateRuleList();
	}

	public void recreateRuleList() {
		compositeRuleList.dispose();
		createCompositeRuleList();
		for (int i = 0; i < ruleItems.size(); i++) {
			FilterRuleListItem ruleItem = (FilterRuleListItem) ruleItems.elementAt(i);
			ruleItem.init(compositeRuleList);
		}
		compositeRuleList.pack();
		buttonFewer.setEnabled(ruleItems.size() > 1);
	}

	public void deleteRule(FilterRuleListItem item) {
		ruleItems.remove(item);
		recreateRuleList();
	}

	protected void createCompositeRuleList() {
		compositeRuleList = new Composite(scrolledComposite1, SWT.NONE);
		scrolledComposite1.setContent(compositeRuleList);

		GridLayout compositeRuleListLayout = new GridLayout();
		compositeRuleListLayout.numColumns = 4;
		compositeRuleListLayout.makeColumnsEqualWidth = false;

		compositeRuleList.setLayout(compositeRuleListLayout);
		compositeRuleList.setBackground(whiteColor);
		// compositeRuleList.pack();
	}

	public FileFilter getFileFilter() {
		fileFilter = new FileFilter();

		fileFilter.setMatchType(comboMatchType.getSelectionIndex());
		fileFilter.setFilterType(comboFilterType.getSelectionIndex());
		fileFilter.setAppliesToDirectories(buttonAppliesToDir.getSelection());

		FileFilterRule[] rules = new FileFilterRule[ruleItems.size()];
		for (int i = 0; i < rules.length; i++) {
			FilterRuleListItem ruleItem = (FilterRuleListItem) ruleItems.get(i);
			rules[i] = fileFilterManager.createFileFilterRule(ruleItem.getRuleType(), ruleItem.getOperator(), ruleItem.getValue());
		}

		fileFilter.setFileFilterRules(rules);
		return fileFilter;
	}

}
