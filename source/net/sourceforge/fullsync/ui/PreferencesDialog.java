package net.sourceforge.fullsync.ui;

import net.sourceforge.fullsync.Preferences;
import net.sourceforge.fullsync.impl.ConfigurationPreferences;

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
	
	private Button okButton;
	private Button cancelButton;

	private Group groupInterface;
	private Button cbConfirmExit;
	private Button cbCloseMinimizesToSystemTray;
	private Button cbMinimizeMinimizesToSystemTray;
	private Button cbEnableSystemTray;

	private GuiController guiController;

	/**
	* Auto-generated main method to display this 
	* org.eclipse.swt.widgets.Dialog inside a new Shell.
	*/
	public static void main(String[] args) {
		try {
			Display display = Display.getDefault();
			Shell shell = new Shell(display);
			Preferences pref = new ConfigurationPreferences( "preferences.xml" );
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
			dialogShellLayout.horizontalSpacing = 20;
			dialogShellLayout.makeColumnsEqualWidth = true;
			dialogShell.setLayout(dialogShellLayout);
			dialogShell.layout();
			dialogShell.pack();
			dialogShell.setSize(261, 231);
			{
				groupInterface = new Group(dialogShell, SWT.NONE);
				GridLayout GeneralPreferencesGroupLayout = new GridLayout();
				GridData GeneralPreferencesGroupLData = new GridData();
				GeneralPreferencesGroupLData.horizontalSpan = 2;
				GeneralPreferencesGroupLData.grabExcessHorizontalSpace = true;
				GeneralPreferencesGroupLData.grabExcessVerticalSpace = true;
				GeneralPreferencesGroupLData.horizontalAlignment = GridData.FILL;
				GeneralPreferencesGroupLData.verticalAlignment = GridData.FILL;
				groupInterface.setLayoutData(GeneralPreferencesGroupLData);
				GeneralPreferencesGroupLayout.makeColumnsEqualWidth = true;
				GeneralPreferencesGroupLayout.marginHeight = 8;
				GeneralPreferencesGroupLayout.marginWidth = 8;
				groupInterface.setLayout(GeneralPreferencesGroupLayout);
				groupInterface.setText("Interface");
				{
					cbConfirmExit = new Button(	groupInterface, SWT.CHECK | SWT.LEFT);
					cbConfirmExit.setText("Show confirmation dialog on exit");
					GridData askOnClosingCheckBoxLData = new GridData();
					askOnClosingCheckBoxLData.horizontalAlignment = GridData.FILL;
					askOnClosingCheckBoxLData.heightHint = 16;
					cbConfirmExit.setLayoutData(askOnClosingCheckBoxLData);
				}
				{
					cbCloseMinimizesToSystemTray = new Button(
						groupInterface,
						SWT.CHECK | SWT.LEFT);
					cbCloseMinimizesToSystemTray.setText("Close minimizes to System Tray");
					GridData closeButtonMinimizesCheckBoxLData = new GridData();
					closeButtonMinimizesCheckBoxLData.horizontalAlignment = GridData.FILL;
					closeButtonMinimizesCheckBoxLData.heightHint = 16;
					cbCloseMinimizesToSystemTray.setLayoutData(closeButtonMinimizesCheckBoxLData);
				}
                {
                    cbMinimizeMinimizesToSystemTray = new Button(
                        groupInterface,
                        SWT.CHECK | SWT.LEFT);
                    cbMinimizeMinimizesToSystemTray
                        .setText("Minimize minimizes to System Tray");
                    GridData cbMinimizeMinimizesToSystemTrayLData = new GridData();
                    cbMinimizeMinimizesToSystemTrayLData.horizontalAlignment = GridData.FILL;
                    cbMinimizeMinimizesToSystemTray.setLayoutData(cbMinimizeMinimizesToSystemTrayLData);
                }
                {
                    cbEnableSystemTray = new Button(
                        groupInterface,
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
					    Preferences preferences = guiController.getPreferences(); 
						preferences.setConfirmExit(cbConfirmExit.getSelection());
						preferences.setCloseMinimizesToSystemTray(cbCloseMinimizesToSystemTray.getSelection());
						preferences.setMinimizeMinimizesToSystemTray(cbMinimizeMinimizesToSystemTray.getSelection());
						preferences.setSystemTrayEnabled(cbEnableSystemTray.getSelection());
						preferences.save();
						dialogShell.dispose();
					}
				});
				okButtonLData.horizontalAlignment = GridData.FILL;
				okButtonLData.grabExcessHorizontalSpace = true;
				okButtonLData.heightHint = 23;
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
	
	public void setGuiController(GuiController guiController) {
		this.guiController = guiController;
	}
	
	private void initialize() 
	{
	    Preferences preferences = guiController.getPreferences();
		cbConfirmExit.setSelection(preferences.confirmExit());
		cbCloseMinimizesToSystemTray.setSelection(preferences.closeMinimizesToSystemTray());
		cbMinimizeMinimizesToSystemTray.setSelection(preferences.minimizeMinimizesToSystemTray());
		cbEnableSystemTray.setSelection(preferences.systemTrayEnabled());
	}
}
