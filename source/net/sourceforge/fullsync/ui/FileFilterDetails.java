package net.sourceforge.fullsync.ui;

import java.util.Vector;

import net.sourceforge.fullsync.rules.filefilter.FileFilter;
import net.sourceforge.fullsync.rules.filefilter.FileFilterManager;
import net.sourceforge.fullsync.rules.filefilter.FileFilterRule;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.SWT;


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
		
	private Label label1;
	private Combo comboMatchType;
	private Label label2;
	private ScrolledComposite scrolledComposite1;
	private Composite compositeRuleList;
	private Button buttonFewer;
	private Button buttonMore;
	
	private Color whiteColor = new Color(null, 255, 255, 255);
	
	private FileFilterManager fileFilterManager = new FileFilterManager();
	private FileFilter fileFilter;
	
	private static class RuleRow {
		
		private String ruleType;
		private int op;
		private String value;
		private FileFilterDetails root;
		
		private FileFilterManager fileFilterManager;
				
		public RuleRow(FileFilterDetails root, Composite composite, 
				FileFilterManager fileFilterManager, String ruleType, 
				int op, String value) 
		{
			this.fileFilterManager = fileFilterManager;
			this.ruleType = ruleType;
			this.op = op;
			this.value = value;
			this.root = root;
			
			init(composite);
		}
		
		public String getRuleType() {
			return ruleType;
		}
		
		public int getOperator() {
			return op;
		}
		
		public String getValue() {
			return value;
		}
		
		public void init(final Composite composite) {
			final Combo comboRuleTypes = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
			final Combo comboOperators = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
			
			comboRuleTypes.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent evt) {
					ruleType = comboRuleTypes.getText();
					root.recreateRuleList();
				}
			});

			comboOperators.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent evt) {
					op = comboOperators.getSelectionIndex();
					comboOperators.getParent().layout();
				}
			});
			
			String[] ruleTypes = fileFilterManager.getAllRuleTypes();
			comboRuleTypes.removeAll();
			for (int i = 0; i < ruleTypes.length; i++) {
				comboRuleTypes.add(ruleTypes[i]);
			}
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
			
			String[] allOperands = fileFilterManager.getOperandsForRuleType(comboRuleTypes.getText());
			if ((allOperands == null) || (allOperands.length == 0)) {
				final Text textValue = new Text(composite, SWT.BORDER);
				textValue.addModifyListener(new ModifyListener() {
					public void modifyText(ModifyEvent arg0) {
						value = textValue.getText();
					}
				});
				GridData text1LData = new GridData();
				text1LData.widthHint = 112;
				text1LData.heightHint = 13;
				textValue.setLayoutData(text1LData);
				if (value != null) {
					textValue.setText(value);
				}
			}
			else {
				final Combo comboOperands = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
				for (int i = 0; i < allOperands.length; i++) {
					comboOperands.add(allOperands[i]);
				}
				if (value != null) {
					comboOperands.setText(value);
				}
				if (comboOperands.getSelectionIndex() < 0) {	
					comboOperands.select(0);
				}
				comboOperands.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent evt) {
						value = comboOperands.getText();
					}
				});

			}
//            composite.pack();
		}
	}
	
	private Vector ruleRows = new Vector();
	
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
		if(size.x == 0 && size.y == 0) {
			inst.pack();
			shell.pack();
		} else {
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
			this.setLayout(thisLayout);
			thisLayout.numColumns = 3;
			this.setSize(459, 433);
			{
				label1 = new Label(this, SWT.NONE);
				label1.setText("Match");
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
				scrolledComposite1LData.horizontalSpan = 3;
				scrolledComposite1LData.heightHint = 298;
				scrolledComposite1LData.horizontalAlignment = GridData.FILL;
				scrolledComposite1LData.grabExcessVerticalSpace = true;
				scrolledComposite1LData.grabExcessHorizontalSpace = true;
				scrolledComposite1 = new ScrolledComposite(this, SWT.V_SCROLL
						| SWT.BORDER);
                createCompositeRuleList();
				scrolledComposite1.setLayout(null);
				scrolledComposite1.setLayoutData(scrolledComposite1LData);
				scrolledComposite1.setExpandHorizontal(true);
				scrolledComposite1.setExpandVertical(false);
				scrolledComposite1.setVisible(true);
				scrolledComposite1.setEnabled(true);
				scrolledComposite1.setBackground(whiteColor);
				scrolledComposite1.setAlwaysShowScrollBars(true);
			}
			{
				buttonMore = new Button(this, SWT.PUSH | SWT.CENTER);
				buttonMore.setText("More");
				buttonMore.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent evt) {
						addRuleRow();
						buttonFewer.setEnabled(ruleRows.size() > 1);
					}
				});
			}
			{
				buttonFewer = new Button(this, SWT.PUSH | SWT.CENTER);
				GridData buttonFewerLData = new GridData();
				buttonFewerLData.horizontalSpan = 2;
				buttonFewer.setLayoutData(buttonFewerLData);
				buttonFewer.setText("Fewer");
				buttonFewer.setEnabled(false);
				buttonFewer.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent evt) {
						removeRuleRow();
						buttonFewer.setEnabled(ruleRows.size() > 1);
					}
				});
			}
			
			if (fileFilter != null) {
				comboMatchType.select(fileFilter.getMatchType());
				FileFilterRule[] rules = fileFilter.getFileFiltersRules();
				for (int i = 0; i < rules.length; i++) {
					addRuleRow(rules[i].getRuleType(), rules[i].getOperator(), rules[i].getValue().toString());
				}
			}
			else {
				comboMatchType.select(0);
				addRuleRow();
			}
			
			
			this.layout();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void addRuleRow() {
		addRuleRow(null, -1, null);
	}

	protected void addRuleRow(String ruleType, int op, String value) {
		RuleRow ruleRow = new RuleRow(this, compositeRuleList, fileFilterManager, ruleType, op, value);
		ruleRows.add(ruleRow);
		compositeRuleList.pack();
	}

	protected void removeRuleRow() {
		ruleRows.removeElementAt(ruleRows.size()-1);
		recreateRuleList();
	}
	
	protected void recreateRuleList() {
		compositeRuleList.dispose();
		createCompositeRuleList();
		for (int i = 0; i < ruleRows.size(); i++) {
			RuleRow ruleRow = (RuleRow)ruleRows.elementAt(i);
			ruleRow.init(compositeRuleList);
		}
		compositeRuleList.pack();		
	}
	
	protected void createCompositeRuleList() {
		compositeRuleList = new Composite(scrolledComposite1, SWT.NONE);
		scrolledComposite1.setContent(compositeRuleList);
        
		GridLayout compositeRuleListLayout2 = new GridLayout();
		compositeRuleListLayout2.numColumns = 3;
		compositeRuleListLayout2.makeColumnsEqualWidth = true;
		compositeRuleList.setLayout(compositeRuleListLayout2);
		//compositeRuleList.setBounds(0, 0, 428, 298);
		compositeRuleList.setBackground(whiteColor);
        compositeRuleList.pack();
	}
	
	public FileFilter getFileFilter() {
		fileFilter = new FileFilter();
		fileFilter.setMatchType(comboMatchType.getSelectionIndex());
		
		FileFilterRule[] rules = new FileFilterRule[ruleRows.size()];
		for (int i = 0; i < rules.length; i++) {
			RuleRow rule = (RuleRow)ruleRows.get(i);
			rules[i] = fileFilterManager.createFileFilterRule(rule.getRuleType(), rule.getOperator(), rule.getValue());
		}
		
		fileFilter.setFileFilterRules(rules);
		return fileFilter;
	}
}
