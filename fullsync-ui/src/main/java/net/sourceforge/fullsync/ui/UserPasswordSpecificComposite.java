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

import java.net.URI;
import java.net.URISyntaxException;

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
		labelHost.setText("Host:");
		GridData gridData = getGridData();
		gridData.grabExcessHorizontalSpace = true;
		textHost = new Text(parent, SWT.BORDER);
		textHost.setLayoutData(getGridData());

		int port = getDefaultPort();
		if (-1 != port) {
			Label labelPort = new Label(parent, SWT.NONE);
			labelPort.setText("Port:");
			spinnerPort = new Spinner(parent, SWT.BORDER);
			spinnerPort.setMinimum(1);
			spinnerPort.setMaximum(0xFFFF);
			spinnerPort.setSelection(port);
			spinnerPort.setLayoutData(getGridData());
		}

		onBeforePasswordHook(parent);
		labelUsername = new Label(parent, SWT.NONE);
		labelUsername.setText("Username:");
		textUsername = new Text(parent, SWT.BORDER);
		textUsername.setLayoutData(getGridData());
		labelPassword = new Label(parent, SWT.NONE);
		labelPassword.setText("Password:");
		textPassword = new Text(parent, SWT.BORDER);
		textPassword.setLayoutData(getGridData());
		textPassword.setEchoChar('*');
		super.createGUI(parent);
	}

	protected void onBeforePasswordHook(final Composite parent) {
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
	public ConnectionDescription.Builder getConnectionDescription() throws URISyntaxException {
		ConnectionDescription.Builder builder = super.getConnectionDescription();
		String path = textPath.getText();
		if ((null == path) || path.isEmpty()) {
			path = "/";
		}

		URI uri = null;
		if (null != spinnerPort) {
			uri = new URI(m_scheme, null, textHost.getText(), spinnerPort.getSelection(), path, null, null);
		}
		else {
			uri = new URI(m_scheme, textHost.getText(), path, null);
		}
		builder.setUri(uri);
		builder.setUsername(textUsername.getText());
		builder.setPassword(textPassword.getText());
		return builder;
	}

	@Override
	public void setConnectionDescription(final ConnectionDescription connection) {
		super.setConnectionDescription(connection);
		URI uri = connection.getUri();
		textHost.setText(uri.getHost());
		int port = uri.getPort();
		if (-1 == port) {
			port = getDefaultPort();
		}
		if (null != spinnerPort) {
			spinnerPort.setSelection(port);
		}
		textUsername.setText(connection.getUsername());
		textPassword.setText(connection.getPassword());
	}

	@Override
	public void reset(final String scheme) {
		super.reset(scheme);
		textHost.setText("");
		if (null != spinnerPort) {
			spinnerPort.setSelection(getDefaultPort());
		}
		textUsername.setText("");
		textPassword.setText("");
	}

	public abstract int getDefaultPort();
}
