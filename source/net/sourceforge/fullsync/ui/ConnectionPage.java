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

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.remote.RemoteManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

class ConnectionPage extends WizardDialog {
	public ConnectionPage(Shell parent) {
		super(parent);
	}

	private Text textFieldHostname;
	private Text textFieldPort;
	private Text textPassword;
	private Button cbDisableRemoteListener;

	@Override
	public String getTitle() {
		return Messages.getString("ConnectionPage.Connection"); //$NON-NLS-1$
	}

	@Override
	public String getCaption() {
		return Messages.getString("ConnectionPage.ConnectToARemoteServer"); //$NON-NLS-1$
	}

	@Override
	public String getDescription() {
		return Messages.getString("ConnectionPage.ChooseTarget"); //$NON-NLS-1$
	}

	@Override
	public Image getIcon() {
		return GuiController.getInstance().getImage("Remote_Connect.png"); //$NON-NLS-1$
	}

	@Override
	public Image getImage() {
		return GuiController.getInstance().getImage("Remote_Wizard.png"); //$NON-NLS-1$
	}

	@Override
	public void createContent(Composite content) {
		GridLayout thisLayout = new GridLayout(2, false);
		content.setLayout(thisLayout);
		GridData gdata = new GridData();
		gdata.grabExcessHorizontalSpace = true;
		gdata.horizontalAlignment = SWT.FILL;
		content.setLayoutData(gdata);

		// hostname
		Label labelHostname = new Label(content, SWT.NONE);
		labelHostname.setText(Messages.getString("ConnectionComposite.Hostname") + ":"); //$NON-NLS-1$ //$NON-NLS-2$
		textFieldHostname = new Text(content, SWT.BORDER);
		textFieldHostname.setText("localhost"); //$NON-NLS-1$
		GridData textFieldHostnameLData = new GridData();
		textFieldHostnameLData.horizontalAlignment = SWT.FILL;
		textFieldHostnameLData.grabExcessHorizontalSpace = true;
		textFieldHostname.setLayoutData(textFieldHostnameLData);

		// port
		Label labelPort = new Label(content, SWT.NONE);
		labelPort.setText(Messages.getString("ConnectionComposite.Port") + ":"); //$NON-NLS-1$ //$NON-NLS-2$
		textFieldPort = new Text(content, SWT.BORDER);
		textFieldPort.setText("10000"); //$NON-NLS-1$
		GridData textFieldPortLData = new GridData();
		textFieldPortLData.horizontalAlignment = SWT.FILL;
		textFieldPortLData.grabExcessHorizontalSpace = true;
		textFieldPort.setLayoutData(textFieldPortLData);

		// password
		Label labelPassword = new Label(content, SWT.NONE);
		labelPassword.setText(Messages.getString("ConnectionComposite.Password") + ":"); //$NON-NLS-1$ //$NON-NLS-2$
		textPassword = new Text(content, SWT.BORDER);
		GridData textPasswordLData = new GridData();
		textPasswordLData.horizontalAlignment = SWT.FILL;
		textPasswordLData.grabExcessHorizontalSpace = true;
		textPassword.setLayoutData(textPasswordLData);
		textPassword.setEchoChar('*');

		// disable remote listener
		cbDisableRemoteListener = new Button(content, SWT.CHECK | SWT.LEFT);
		cbDisableRemoteListener.setText(Messages.getString("ConnectionComposite.Disable_Indicator")); //$NON-NLS-1$
		GridData cbDisableRemoteListenerLData = new GridData();
		cbDisableRemoteListenerLData.horizontalSpan = 2;
		cbDisableRemoteListener.setLayoutData(cbDisableRemoteListenerLData);

		content.layout();
	}

	@Override
	public boolean apply() {
		String hostname = textFieldHostname.getText();
		int port = 0;
		try {
			port = Integer.parseInt(textFieldPort.getText());
		}
		catch (NumberFormatException e) {
			//FIXME: reject dialog and tell the user about
		}

		String password = textPassword.getText();

		boolean useRemoteListener = !cbDisableRemoteListener.getSelection();

		try {
			GuiController.getInstance().getProfileManager().setRemoteConnected(true);
			GuiController.getInstance().getProfileManager().stopScheduler();
			RemoteManager remoteManager = new RemoteManager(hostname, port, password);
			if (!remoteManager.isConnectedToRemoteInstance()) {
				remoteManager.setUseRemoteListener(useRemoteListener);
				GuiController.getInstance().getProfileManager().setRemoteConnection(remoteManager);
				GuiController.getInstance().getSynchronizer().setRemoteConnection(remoteManager);
			}
			else {
				throw new Exception("The FullSync instance you tried to connect to is already connected to another FullSync instance");
			}
		}
		catch (Exception ex) {
			GuiController.getInstance().getProfileManager().setRemoteConnected(false);
			ExceptionHandler.reportException(Messages.getString("ConnectionComposite.Unable_To_Connect"), ex); //$NON-NLS-1$
		}
		return true; //FIXME: return false if failed
	}

	@Override
	public boolean cancel() {
		return true;
	}

}
