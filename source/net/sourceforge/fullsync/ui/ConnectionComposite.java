package net.sourceforge.fullsync.ui;

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.remote.RemoteManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import org.eclipse.swt.widgets.Button;
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
public class ConnectionComposite extends org.eclipse.swt.widgets.Composite {
	private Label label1;
	private Label label2;
	private Text textFieldPort;
	private Button cbDisableRemoteListener;
	private Text textPassword;
	private Label label3;
	private Text textFieldHostname;

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
		ConnectionComposite inst = new ConnectionComposite(shell, SWT.NULL);
		Point size = inst.getSize();
		shell.setLayout(new FillLayout());
		shell.layout();
		if(size.x == 0 && size.y == 0) {
			inst.pack();
			shell.pack();
		} else {
			Rectangle shellBounds = shell.computeTrim(0, 0, size.x, size.y);
			int MENU_HEIGHT = 22;
			if (shell.getMenuBar() != null)
				shellBounds.height -= MENU_HEIGHT;
			shell.setSize(shellBounds.width, shellBounds.height);
		}
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	public ConnectionComposite(org.eclipse.swt.widgets.Composite parent, int style) {
		super(parent, style);
		initGUI();
		postInitGUI();
	}

	private void initGUI() {
		try {
			GridLayout thisLayout = new GridLayout();
			this.setLayout(thisLayout);
			thisLayout.numColumns = 2;
			this.setSize(274, 139);
			{
				label1 = new Label(this, SWT.NONE);
				label1.setText(Messages.getString("ConnectionComposite.Hostname")+":"); //$NON-NLS-1$
			}
			{
				textFieldHostname = new Text(this, SWT.BORDER);
				textFieldHostname.setText("localhost");
				GridData textFieldHostnameLData = new GridData();
				textFieldHostnameLData.widthHint = 95;
				textFieldHostnameLData.heightHint = 13;
				textFieldHostnameLData.grabExcessHorizontalSpace = true;
				textFieldHostname.setLayoutData(textFieldHostnameLData);
			}
			{
				label2 = new Label(this, SWT.NONE);
				label2.setText(Messages.getString("ConnectionComposite.Port")+":"); //$NON-NLS-1$
			}
			{
				textFieldPort = new Text(this, SWT.BORDER);
				textFieldPort.setText("10000");
				GridData textFieldPortLData = new GridData();
				textFieldPortLData.widthHint = 34;
				textFieldPortLData.heightHint = 13;
				textFieldPort.setLayoutData(textFieldPortLData);
			}
			{
				label3 = new Label(this, SWT.NONE);
				label3.setText(Messages.getString("ConnectionComposite.Password")+":"); //$NON-NLS-1$
			}
			{
				textPassword = new Text(this, SWT.BORDER);
				GridData textPasswordLData = new GridData();
				textPasswordLData.widthHint = 95;
				textPasswordLData.heightHint = 13;
				textPasswordLData.grabExcessHorizontalSpace = true;
				textPassword.setLayoutData(textPasswordLData);
			}
			{
				cbDisableRemoteListener = new Button(this, SWT.CHECK | SWT.LEFT);
				cbDisableRemoteListener
					.setText(Messages.getString("ConnectionComposite.Disable_Indicator")); //$NON-NLS-1$
				GridData cbDisableRemoteListenerLData = new GridData();
				cbDisableRemoteListenerLData.horizontalSpan = 2;
				cbDisableRemoteListener.setLayoutData(cbDisableRemoteListenerLData);
			}
			this.layout();
		} catch (Exception e) {
			ExceptionHandler.reportException( e );
		}
	}

	private void postInitGUI() {
		textPassword.setEchoChar('*');
	}
	
	public void apply() {
		String hostname = textFieldHostname.getText();
		int port = 0;
		try {
			port = Integer.parseInt(textFieldPort.getText());
		} catch (NumberFormatException e) {
		}
		
		String password = textPassword.getText();
		
		boolean useRemoteListener = !cbDisableRemoteListener.getSelection();
		
		try {
		    GuiController.getInstance().getProfileManager().stopScheduler();
			RemoteManager remoteManager = new RemoteManager(hostname, port, password);
			remoteManager.setUseRemoteListener(useRemoteListener);
			GuiController.getInstance().getProfileManager().setRemoteConnection(remoteManager);
			GuiController.getInstance().getSynchronizer().setRemoteConnection(remoteManager);
		} catch (Exception e1) {
			ExceptionHandler.reportException( Messages.getString("ConnectionComposite.Unable_To_Connect"), e1 ); //$NON-NLS-1$
		}
	}
}
