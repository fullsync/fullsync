/*
 * Created on Jun 5, 2005
 */
package net.sourceforge.fullsync.ui.filterrule;

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.rules.filefilter.FileFilter;
import net.sourceforge.fullsync.rules.filefilter.values.FilterValue;
import net.sourceforge.fullsync.ui.FileFilterPage;
import net.sourceforge.fullsync.ui.WizardDialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * @author Michele Aiello
 */
public class SubfilterRuleComposite extends RuleComposite {
	
	private Color whiteColor = new Color(null, 255, 255, 255);
	
	private Text textValue;
	private Button buttonFilter;
	
	private FilterValue filterValue;
	
	public SubfilterRuleComposite(Composite parent, int style, FilterValue filterValue) {
		super(parent, style);
		this.filterValue = filterValue;
		initGUI();
	}
	
	private void initGUI() {
		GridLayout compositeLayout = new GridLayout();
		compositeLayout.numColumns = 2;
		compositeLayout.makeColumnsEqualWidth = false;
		
		this.setLayout(compositeLayout);
		this.setBackground(whiteColor);
		
		textValue = new Text(this, SWT.BORDER);
		buttonFilter = new Button(this, SWT.PUSH | SWT.CENTER);
		
		GridData text1LData = new GridData();
		text1LData.widthHint = 60;
		text1LData.heightHint = 13;
		text1LData.horizontalSpan = 1;
		text1LData.horizontalAlignment = GridData.FILL;
		text1LData.grabExcessHorizontalSpace = false;
		text1LData.horizontalAlignment = GridData.BEGINNING;
		textValue.setLayoutData(text1LData);
		
		if (filterValue != null) {
			textValue.setText(filterValue.toString());
		}
		
		textValue.setEditable(false);
		textValue.setBackground(whiteColor);
//		textValue.addModifyListener(new ModifyListener() {
//			public void modifyText(ModifyEvent arg0) {
//				((DateValue)value).fromString(textValue.getText());
//				valueChanged(new ValueChangedEvent(value));
//			}
//		});
		
		GridData buttonFilterLData = new GridData();
		buttonFilterLData.horizontalSpan = 1;
		buttonFilter.setLayoutData(buttonFilterLData);
		buttonFilter.setText("Set Filter...");
		buttonFilter.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				try {
					WizardDialog dialog = new WizardDialog(getShell(), SWT.APPLICATION_MODAL);
					FileFilterPage page = new FileFilterPage(dialog, filterValue.getValue());
					dialog.show();
					FileFilter newfilter = page.getFileFilter();
					if (newfilter != null) {
						filterValue.setValue(newfilter);
						textValue.setText(filterValue.toString());
						textValue.setToolTipText(filterValue.toString());
//						buttonFilter.setToolT ipText(filterValue.toString());
					}
				} catch (Exception e) {
					ExceptionHandler.reportException(e);
				}

			}
		});
		
	}
}
