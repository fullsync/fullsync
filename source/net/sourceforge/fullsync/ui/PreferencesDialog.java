package net.sourceforge.fullsync.ui;

import net.sourceforge.fullsync.PreferencesManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
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
public class PreferencesDialog extends org.eclipse.swt.widgets.Dialog {

	private Shell dialogShell;
	private Button cbConfirmExit;
	private Group GeneralPreferencesGroup;
	private Button cancelButton;
	private Button okButton;
	private Button cbCloseMinimizesToSystemTray;

	private PreferencesManager preferencesManager;
	private Button cbMinimizeMinimizesToSystemTray;
	private Button cbEnableSystemTray;

	/**
	* Auto-generated main method to display this 
	* org.eclipse.swt.widgets.Dialog inside a new Shell.
	*/
	public static void main(String[] args) {
		try {
			Display display = Display.getDefault();
			Shell shell = new Shell(display);
			PreferencesManager pref = new PreferencesManager( "preferences.xml" );
			PreferencesDialog inst = new PreferencesDialog(shell, SWT.NULL);
			inst.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public PreferencesDialog(Shell parent, int style) {
		super(parent, style);
	}

	public void open() {
		try {
			Shell parent = getParent();
			dialogShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

			GridLayout dialogShellLayout = new GridLayout();
			dialogShell.setText("Preferences...");
			dialogShellLayout.numColumns = 2;
			dialogShellLayout.horizontalSpacing = 25;
			dialogShell.setLayout(dialogShellLayout);
			dialogShell.layout();
			dialogShell.pack();
			dialogShell.setSize(261, 231);
			{
				GeneralPreferencesGroup = new Group(dialogShell, SWT.NONE);
				GridLayout GeneralPreferencesGroupLayout = new GridLayout();
				GridData GeneralPreferencesGroupLData = new GridData();
				GeneralPreferencesGroupLData.horizontalSpan = 2;
				GeneralPreferencesGroupLData.grabExcessHorizontalSpace = true;
				GeneralPreferencesGroupLData.grabExcessVerticalSpace = true;
				GeneralPreferencesGroupLData.horizontalAlignment = GridData.FILL;
				GeneralPreferencesGroupLData.verticalAlignment = GridData.FILL;
				GeneralPreferencesGroup.setLayoutData(GeneralPreferencesGroupLData);
				GeneralPreferencesGroupLayout.makeColumnsEqualWidth = true;
				GeneralPreferencesGroupLayout.marginHeight = 8;
				GeneralPreferencesGroupLayout.marginWidth = 8;
				GeneralPreferencesGroup.setLayout(GeneralPreferencesGroupLayout);
				GeneralPreferencesGroup.setText("Interface");
				{
					cbConfirmExit = new Button(	GeneralPreferencesGroup, SWT.CHECK | SWT.LEFT);
					cbConfirmExit.setText("Show confirmation dialog on exit");
					GridData askOnClosingCheckBoxLData = new GridData();
					askOnClosingCheckBoxLData.horizontalAlignment = GridData.FILL;
					askOnClosingCheckBoxLData.heightHint = 16;
					cbConfirmExit.setLayoutData(askOnClosingCheckBoxLData);
				}
				{
					cbCloseMinimizesToSystemTray = new Button(
						GeneralPreferencesGroup,
						SWT.CHECK | SWT.LEFT);
					cbCloseMinimizesToSystemTray.setText("Close minimizes to System Tray");
					GridData closeButtonMinimizesCheckBoxLData = new GridData();
					closeButtonMinimizesCheckBoxLData.horizontalAlignment = GridData.FILL;
					closeButtonMinimizesCheckBoxLData.heightHint = 16;
					cbCloseMinimizesToSystemTray.setLayoutData(closeButtonMinimizesCheckBoxLData);
				}
                {
                    cbMinimizeMinimizesToSystemTray = new Button(
                        GeneralPreferencesGroup,
                        SWT.CHECK | SWT.LEFT);
                    cbMinimizeMinimizesToSystemTray
                        .setText("Minimize minimizes to System Tray");
                    GridData cbMinimizeMinimizesToSystemTrayLData = new GridData();
                    cbMinimizeMinimizesToSystemTrayLData.horizontalAlignment = GridData.FILL;
                    cbMinimizeMinimizesToSystemTray.setLayoutData(cbMinimizeMinimizesToSystemTrayLData);
                }
                {
                    cbEnableSystemTray = new Button(
                        GeneralPreferencesGroup,
                        SWT.CHECK | SWT.LEFT);
                    cbEnableSystemTray.setText("Enable System Tray Icon");
                }
			}
			{
				okButton = new Button(dialogShell, SWT.PUSH | SWT.CENTER);
				okButton.setText("OK");
				GridData okButtonLData = new GridData();
				okButton.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent evt) {
						preferencesManager.setConfirmExit(cbConfirmExit.getSelection());
						preferencesManager.setCloseMinimizesToSystemTray(cbCloseMinimizesToSystemTray.getSelection());
						preferencesManager.setMinimizeMinimizesToSystemTray(cbMinimizeMinimizesToSystemTray.getSelection());
						preferencesManager.setSystemTrayEnabled(cbEnableSystemTray.getSelection());
						preferencesManager.save();
						dialogShell.dispose();
					}
				});
				okButtonLData.horizontalAlignment = GridData.FILL;
				okButtonLData.grabExcessHorizontalSpace = true;
				okButton.setLayoutData(okButtonLData);
			}
			{
				cancelButton = new Button(dialogShell, SWT.PUSH | SWT.CENTER);
				cancelButton.setText("Cancel");
				GridData cancelButtonLData = new GridData();
				cancelButton.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent evt) {
						dialogShell.dispose();
					}
				});
				cancelButtonLData.horizontalAlignment = GridData.FILL;
				cancelButtonLData.grabExcessHorizontalSpace = true;
				cancelButton.setLayoutData(cancelButtonLData);
			}
			dialogShell.open();
			dialogShell.setDefaultButton(cancelButton);
			
			initialize();
			Display display = dialogShell.getDisplay();
			while (!dialogShell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setPreferencesManager(PreferencesManager preferencesManager) {
		this.preferencesManager = preferencesManager;
	}
	
	private void initialize() {
		cbConfirmExit.setSelection(preferencesManager.confirmExit());
		cbCloseMinimizesToSystemTray.setSelection(preferencesManager.closeMinimizesToSystemTray());
	}
}
