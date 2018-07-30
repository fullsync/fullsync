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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import net.sourceforge.fullsync.ConnectionDescription;

abstract class UserPasswordSpecificComposite extends ProtocolSpecificComposite {
	private Text textHost;
	private Spinner spinnerPort;
	private Label labelUsername;
	private Text textUsername;
	private Label labelPassword;
	private Text textPassword;

	@Override
	public void createGUI(final Composite parent) {
		Label labelHost = new Label(parent, SWT.NONE);
		labelHost.setText(Messages.getString("UserPasswordSpecificComposite.Host")); //$NON-NLS-1$
		GridData gridData = getGridData();
		gridData.grabExcessHorizontalSpace = true;
		textHost = new Text(parent, SWT.BORDER);
		textHost.setLayoutData(getGridData());

		int port = getDefaultPort();
		if (-1 != port) {
			Label labelPort = new Label(parent, SWT.NONE);
			labelPort.setText(Messages.getString("UserPasswordSpecificComposite.Port")); //$NON-NLS-1$
			spinnerPort = new Spinner(parent, SWT.BORDER);
			spinnerPort.setMinimum(1);
			spinnerPort.setMaximum(0xFFFF);
			spinnerPort.setSelection(port);
			spinnerPort.setLayoutData(getGridData());
		}

		onBeforeUsernameHook(parent);
		labelUsername = new Label(parent, SWT.NONE);
		labelUsername.setText(Messages.getString("UserPasswordSpecificComposite.Username")); //$NON-NLS-1$
		textUsername = new Text(parent, SWT.BORDER);
		textUsername.setLayoutData(getGridData());
		labelPassword = new Label(parent, SWT.NONE);
		labelPassword.setText(Messages.getString("UserPasswordSpecificComposite.Password")); //$NON-NLS-1$
		textPassword = new Text(parent, SWT.BORDER);
		textPassword.setLayoutData(getGridData());
		textPassword.setEchoChar('*');
		onAfterPasswordHook(parent);
		super.createGUI(parent);
	}

	protected void onBeforeUsernameHook(final Composite parent) {
	}

	protected void onAfterPasswordHook(final Composite parent) {
	}

	protected void setUserPasswordEnabled(boolean enabled) {
		labelUsername.setEnabled(enabled);
		textUsername.setEnabled(enabled);
		labelPassword.setEnabled(enabled);
		textPassword.setEnabled(enabled);
	}

	private GridData getGridData() {
		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = SWT.FILL;
		gridData1.horizontalSpan = 2;
		gridData1.verticalAlignment = SWT.CENTER;
		return gridData1;
	}

	@Override
	public ConnectionDescription.Builder getConnectionDescription() {
		ConnectionDescription.Builder builder = super.getConnectionDescription();
		builder.setHost(textHost.getText());
		if (null != spinnerPort) {
			builder.setPort(spinnerPort.getSelection());
		}
		builder.setUsername(textUsername.getText());
		builder.setPassword(textPassword.getText());
		return builder;
	}

	@Override
	public void setConnectionDescription(final ConnectionDescription connection) {
		super.setConnectionDescription(connection);
		textHost.setText(connection.getHost().orElse("")); //$NON-NLS-1$
		if (null != spinnerPort) {
			int port = connection.getPort().orElse(-1).intValue();
			if (-1 == port) {
				port = getDefaultPort();
			}
			spinnerPort.setSelection(port);
		}
		textUsername.setText(connection.getUsername().orElse("")); //$NON-NLS-1$
		textPassword.setText(connection.getPassword().orElse("")); //$NON-NLS-1$
	}

	@Override
	public void reset(final String scheme) {
		super.reset(scheme);
		textHost.setText(""); //$NON-NLS-1$
		if (null != spinnerPort) {
			spinnerPort.setSelection(getDefaultPort());
		}
		textUsername.setText(""); //$NON-NLS-1$
		textPassword.setText(""); //$NON-NLS-1$
	}

	public abstract int getDefaultPort();
}
