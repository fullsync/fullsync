package net.sourceforge.fullsync.ui;

import java.rmi.RemoteException;

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.Preferences;
import net.sourceforge.fullsync.remote.RemoteController;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
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
	private Button cbShowSplashScreen;
	private Text textPassword;
	private Label label3;
	private Group groupRemoteConnection;
	private Text textListeningPort;
	private Label label2;
	private Button cbListenForIncomming;
	private Label label1;
	private Combo comboProfileList;
	//private Button cbEnableSystemTray;

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
			this.setSize(500, 350);
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
                    cbConfirmExit = new Button(groupInterface, SWT.CHECK
                        | SWT.LEFT);
                    cbConfirmExit.setText("Show confirmation dialog on exit");
                    GridData askOnClosingCheckBoxLData = new GridData();
                    askOnClosingCheckBoxLData.widthHint = 246;
                    askOnClosingCheckBoxLData.heightHint = 19;
                    askOnClosingCheckBoxLData.horizontalSpan = 2;
                    cbConfirmExit.setLayoutData(askOnClosingCheckBoxLData);
                }
                {
                    cbCloseMinimizesToSystemTray = new Button(
                        groupInterface,
                        SWT.CHECK | SWT.LEFT);
                    cbCloseMinimizesToSystemTray
                        .setText("Close minimizes to System Tray");
                    GridData closeButtonMinimizesCheckBoxLData = new GridData();
                    closeButtonMinimizesCheckBoxLData.widthHint = 246;
                    closeButtonMinimizesCheckBoxLData.heightHint = 18;
                    closeButtonMinimizesCheckBoxLData.horizontalSpan = 2;
                    cbCloseMinimizesToSystemTray
                        .setLayoutData(closeButtonMinimizesCheckBoxLData);
                }
                {
                    cbMinimizeMinimizesToSystemTray = new Button(
                        groupInterface,
                        SWT.CHECK | SWT.LEFT);
                    cbMinimizeMinimizesToSystemTray
                        .setText("Minimize minimizes to System Tray");
                    GridData cbMinimizeMinimizesToSystemTrayLData = new GridData();
                    cbMinimizeMinimizesToSystemTrayLData.widthHint = 246;
                    cbMinimizeMinimizesToSystemTrayLData.heightHint = 18;
                    cbMinimizeMinimizesToSystemTrayLData.horizontalSpan = 2;
                    cbMinimizeMinimizesToSystemTray
                        .setLayoutData(cbMinimizeMinimizesToSystemTrayLData);
                }
                {
                    cbShowSplashScreen = new Button(groupInterface, SWT.CHECK
                        | SWT.LEFT);
                    cbShowSplashScreen.setText("Show Splash Screen");
                    GridData cbShowSplashScreenLData = new GridData();
                    cbShowSplashScreenLData.horizontalSpan = 2;
                    cbShowSplashScreen.setLayoutData(cbShowSplashScreenLData);
                }
                /*
                 {
                 cbEnableSystemTray = new Button(groupInterface, SWT.CHECK | SWT.LEFT);
                 cbEnableSystemTray.setText("Enable System Tray Icon");
                 GridData cbEnableSystemTrayLData = new GridData();
                 cbEnableSystemTrayLData.widthHint = 188;
                 cbEnableSystemTrayLData.heightHint = 18;
                 cbEnableSystemTrayLData.horizontalSpan = 2;
                 cbEnableSystemTray.setLayoutData(cbEnableSystemTrayLData);
                 }
                 */
                {
                    label1 = new Label(groupInterface, SWT.NONE);
                    label1.setText("Profile list style: ");
                    GridData label1LData = new GridData();
                    label1LData.heightHint = 15;
                    label1.setLayoutData(label1LData);
                }
                {
                    comboProfileList = new Combo(groupInterface, SWT.DROP_DOWN
                        | SWT.READ_ONLY);
                    GridData comboProfileListLData = new GridData();
                    comboProfileListLData.widthHint = 105;
                    comboProfileListLData.heightHint = 21;
                    comboProfileList.setLayoutData(comboProfileListLData);
                    comboProfileList.add("Table");
                    comboProfileList.add("NiceListView");
                }
            }
            {
                groupRemoteConnection = new Group(this, SWT.NONE);
                GridLayout groupRemoteConnectionLayout = new GridLayout();
                GridData groupRemoteConnectionLData = new GridData();
                groupRemoteConnectionLData.horizontalSpan = 2;
                groupRemoteConnectionLData.horizontalAlignment = GridData.FILL;
                groupRemoteConnectionLData.grabExcessVerticalSpace = true;
                groupRemoteConnectionLData.verticalAlignment = GridData.FILL;
                groupRemoteConnection.setLayoutData(groupRemoteConnectionLData);
                groupRemoteConnectionLayout.numColumns = 2;
                groupRemoteConnection.setLayout(groupRemoteConnectionLayout);
                groupRemoteConnection.setText("Remote Connection");
                {
                    cbListenForIncomming = new Button(
                        groupRemoteConnection,
                        SWT.CHECK | SWT.LEFT);
                    cbListenForIncomming.setText("Enable remote connections");
                    GridData cbListenForIncommingLData = new GridData();
                    cbListenForIncomming
                        .addSelectionListener(new SelectionAdapter() {
                            public void widgetSelected(SelectionEvent evt) {
                                updateRemoteConnectionGroup();
                                //									if (cbListenForIncomming.getSelection()) {
                                //										label2.setEnabled(true);
                                //										textListeningPort.setEnabled(true);
                                //										label3.setEnabled(true);
                                //										textPassword.setEnabled(true);
                                //										
                                //									} else {
                                //										label2.setEnabled(false);
                                //										textListeningPort.setEnabled(false);
                                //										label3.setEnabled(false);
                                //										textPassword.setEnabled(false);
                                //									}
                            }
                        });
                    cbListenForIncommingLData.horizontalSpan = 2;
                    cbListenForIncomming
                        .setLayoutData(cbListenForIncommingLData);
                }
                {
                    label2 = new Label(groupRemoteConnection, SWT.NONE);
                    label2.setText("Incoming connections port:");
                    label2.setEnabled(false);
                }
                {
                    textListeningPort = new Text(
                        groupRemoteConnection,
                        SWT.BORDER);
                    textListeningPort.setText("10000");
                    GridData textListeningPortLData = new GridData();
                    textListeningPort.setEnabled(false);
                    textListeningPortLData.widthHint = 39;
                    textListeningPortLData.heightHint = 12;
                    textListeningPort.setLayoutData(textListeningPortLData);
                }
                {
                    label3 = new Label(groupRemoteConnection, SWT.NONE);
                    label3.setText("Incoming connections password:");
                    label3.setEnabled(false);
                }
                {
                    textPassword = new Text(groupRemoteConnection, SWT.BORDER);
                    GridData textPasswordLData = new GridData();
                    textPassword.setEnabled(false);
                    textPasswordLData.widthHint = 118;
                    textPasswordLData.heightHint = 13;
                    textPasswordLData.grabExcessHorizontalSpace = true;
                    textPassword.setLayoutData(textPasswordLData);
                }
            }

			updateComponent();
			this.layout();
		} catch (Exception e) {
			ExceptionHandler.reportException( e );
		}
	}
	
	public void updateComponent() 
	{
		textPassword.setEchoChar('*');
		
		cbConfirmExit.setSelection(preferences.confirmExit());
		cbCloseMinimizesToSystemTray.setSelection(preferences.closeMinimizesToSystemTray());
		cbMinimizeMinimizesToSystemTray.setSelection(preferences.minimizeMinimizesToSystemTray());
		//cbEnableSystemTray.setSelection(preferences.systemTrayEnabled());
		comboProfileList.setText( preferences.getProfileListStyle() );
		cbListenForIncomming.setSelection(preferences.listeningForRemoteConnections());
		textListeningPort.setText(String.valueOf(preferences.getRemoteConnectionsPort()));
		textPassword.setText(preferences.getRemoteConnectionsPassword());
		cbShowSplashScreen.setSelection(preferences.showSplashScreen());
		updateRemoteConnectionGroup();
	}
	
	private void updateRemoteConnectionGroup() {
		if (cbListenForIncomming.getSelection()) {
			label2.setEnabled(true);
			textListeningPort.setEnabled(true);
			label3.setEnabled(true);
			textPassword.setEnabled(true);
		}
		else {			
			label2.setEnabled(false);
			textListeningPort.setEnabled(false);
			label3.setEnabled(false);
			textPassword.setEnabled(false);
		}

	}
	
	public void apply()
	{
		preferences.setConfirmExit(cbConfirmExit.getSelection());
		preferences.setCloseMinimizesToSystemTray(cbCloseMinimizesToSystemTray.getSelection());
		preferences.setMinimizeMinimizesToSystemTray(cbMinimizeMinimizesToSystemTray.getSelection());
		//preferences.setSystemTrayEnabled(cbEnableSystemTray.getSelection());
		boolean profileListStyleChanged = (!preferences.getProfileListStyle().equals(comboProfileList.getText()));
		preferences.setProfileListStyle(comboProfileList.getText());
		preferences.setShowSplashScreen(cbShowSplashScreen.getSelection());

		if (profileListStyleChanged) {
			GuiController.getInstance().getMainWindow().createProfileList();
		}

		boolean listenForIncoming = cbListenForIncomming.getSelection();
		preferences.setListeningForRemoteConnections(listenForIncoming);
		int port = -1;
		String password = null;
		if (listenForIncoming) {
			try {
				port = Integer.parseInt(textListeningPort.getText());
			} catch (NumberFormatException e) {
				ExceptionHandler.reportException( e );
			}
		
			preferences.setRemoteConnectionsPort(port);
			
			password = textPassword.getText();
			preferences.setRemoteConnectionsPassword(password);
		}
		
		boolean isActive = RemoteController.getInstance().isActive();
		
		// TODO [Michele] what if the port or password is changed?
		if ((isActive) && (!listenForIncoming)) {
			try {
				RemoteController.getInstance().stopServer();
			} catch (RemoteException e) {
				ExceptionHandler.reportException( e );
				MessageBox mb = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
				mb.setText("Connection Error");
				mb.setMessage("Unable to stop incomming connections listener.\n("+e.getMessage()+")");
				mb.open();
			}
		}
		if ((!isActive) && (listenForIncoming)) {
			if (port > 0) {
				try {
					RemoteController.getInstance().startServer(port, password, 
							GuiController.getInstance().getProfileManager(),
							GuiController.getInstance().getSynchronizer());
				} catch (RemoteException e) {
					ExceptionHandler.reportException( e );
					MessageBox mb = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
					mb.setText("Connection Error");
					mb.setMessage("Unable to start incomming connections listener.\n("+e.getMessage()+")");
					mb.open();

				}				
			}
		}
		
		preferences.save();
	}
}
