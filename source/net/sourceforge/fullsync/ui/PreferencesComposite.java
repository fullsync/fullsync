package net.sourceforge.fullsync.ui;

import net.sourceforge.fullsync.Preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;



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
public class PreferencesComposite extends org.eclipse.swt.widgets.Composite {

	private Group groupInterface;
	private Button cbConfirmExit;
	private Button cbCloseMinimizesToSystemTray;
	private Button cbMinimizeMinimizesToSystemTray;
	private Label label1;
	private Combo comboProfileList;
	private Button cbEnableSystemTray;

	private Preferences preferences;

	public PreferencesComposite(Composite parent, int style, Preferences preferences) 
	{
		super(parent, style);
		this.preferences = preferences;
		initGui();
	}

	public void initGui() {
		try {
			GridLayout thisLayout = new GridLayout();
			thisLayout.numColumns = 2;
			thisLayout.horizontalSpacing = 20;
			thisLayout.makeColumnsEqualWidth = true;
			this.setLayout(thisLayout);
			this.setSize(401, 294);
			{
				groupInterface = new Group(this, SWT.NONE);
				GridLayout GeneralPreferencesGroupLayout = new GridLayout();
				GeneralPreferencesGroupLayout.numColumns = 2;
				GridData GeneralPreferencesGroupLData = new GridData();
				GeneralPreferencesGroupLData.horizontalSpan = 2;
				GeneralPreferencesGroupLData.grabExcessHorizontalSpace = true;
				GeneralPreferencesGroupLData.grabExcessVerticalSpace = true;
				GeneralPreferencesGroupLData.horizontalAlignment = GridData.FILL;
				GeneralPreferencesGroupLData.verticalAlignment = GridData.FILL;
				groupInterface.setLayoutData(GeneralPreferencesGroupLData);
				groupInterface.setLayout(GeneralPreferencesGroupLayout);
				groupInterface.setText("Interface");
				{
					cbConfirmExit = new Button(groupInterface, SWT.CHECK | SWT.LEFT);
					cbConfirmExit.setText("Show confirmation dialog on exit");
					GridData askOnClosingCheckBoxLData = new GridData();
					askOnClosingCheckBoxLData.widthHint = 246;
					askOnClosingCheckBoxLData.heightHint = 19;
					askOnClosingCheckBoxLData.horizontalSpan = 2;
					cbConfirmExit.setLayoutData(askOnClosingCheckBoxLData);
				}
				{
					cbCloseMinimizesToSystemTray = new Button(groupInterface, SWT.CHECK | SWT.LEFT);
					cbCloseMinimizesToSystemTray.setText("Close minimizes to System Tray");
					GridData closeButtonMinimizesCheckBoxLData = new GridData();
					closeButtonMinimizesCheckBoxLData.widthHint = 246;
					closeButtonMinimizesCheckBoxLData.heightHint = 18;
					closeButtonMinimizesCheckBoxLData.horizontalSpan = 2;
					cbCloseMinimizesToSystemTray.setLayoutData(closeButtonMinimizesCheckBoxLData);
				}
                {
                    cbMinimizeMinimizesToSystemTray = new Button(groupInterface, SWT.CHECK | SWT.LEFT);
                    cbMinimizeMinimizesToSystemTray
                        .setText("Minimize minimizes to System Tray");
                    GridData cbMinimizeMinimizesToSystemTrayLData = new GridData();
                    cbMinimizeMinimizesToSystemTrayLData.widthHint = 246;
                    cbMinimizeMinimizesToSystemTrayLData.heightHint = 18;
                    cbMinimizeMinimizesToSystemTrayLData.horizontalSpan = 2;
                    cbMinimizeMinimizesToSystemTray.setLayoutData(cbMinimizeMinimizesToSystemTrayLData);
                }
                {
                    cbEnableSystemTray = new Button(groupInterface, SWT.CHECK | SWT.LEFT);
                    cbEnableSystemTray.setText("Enable System Tray Icon");
                    GridData cbEnableSystemTrayLData = new GridData();
                    cbEnableSystemTrayLData.widthHint = 188;
                    cbEnableSystemTrayLData.heightHint = 18;
                    cbEnableSystemTrayLData.horizontalSpan = 2;
                    cbEnableSystemTray.setLayoutData(cbEnableSystemTrayLData);
                }
                {
                    label1 = new Label(groupInterface, SWT.NONE);
                    label1.setText("profile list style: ");
                    GridData label1LData = new GridData();
                    label1LData.heightHint = 15;
                    label1.setLayoutData(label1LData);
                }
                {
                    comboProfileList = new Combo(groupInterface, SWT.DROP_DOWN | SWT.READ_ONLY);
                    GridData comboProfileListLData = new GridData();
                    comboProfileListLData.widthHint = 105;
                    comboProfileListLData.heightHint = 21;
                    comboProfileList.setLayoutData(comboProfileListLData);
                    comboProfileList.add( "Table" );
                    comboProfileList.add( "NiceListView" );
                }
			}
			
			updateComponent();
			this.layout();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updateComponent() 
	{
		cbConfirmExit.setSelection(preferences.confirmExit());
		cbCloseMinimizesToSystemTray.setSelection(preferences.closeMinimizesToSystemTray());
		cbMinimizeMinimizesToSystemTray.setSelection(preferences.minimizeMinimizesToSystemTray());
		cbEnableSystemTray.setSelection(preferences.systemTrayEnabled());
		comboProfileList.setText( preferences.getProfileListStyle() );
	}
	public void apply()
	{
		preferences.setConfirmExit(cbConfirmExit.getSelection());
		preferences.setCloseMinimizesToSystemTray(cbCloseMinimizesToSystemTray.getSelection());
		preferences.setMinimizeMinimizesToSystemTray(cbMinimizeMinimizesToSystemTray.getSelection());
		preferences.setSystemTrayEnabled(cbEnableSystemTray.getSelection());
		preferences.setProfileListStyle(comboProfileList.getText());
		preferences.save();
	}
}
