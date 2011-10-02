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
package net.full.fs.ui;

import java.net.URI;
import java.net.URISyntaxException;

import net.sourceforge.fullsync.ConnectionDescription;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class UserPasswordSpecificComposite extends ProtocolSpecificComposite {
	private Label labelHost = null;
	private Text textHost = null;
	private Label labelUsername = null;
	private Text textUsername = null;
	private Label labelPassword = null;
	private Text textPassword = null;

	@Override
	public void createGUI(final Composite parent) {
		GridData gridData3 = new GridData();
		gridData3.horizontalAlignment = SWT.FILL;
		gridData3.horizontalSpan = 2;
		gridData3.verticalAlignment = SWT.CENTER;
		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = SWT.FILL;
		gridData2.horizontalSpan = 2;
		gridData2.verticalAlignment = SWT.CENTER;
		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = SWT.FILL;
		gridData1.horizontalSpan = 2;
		gridData1.verticalAlignment = SWT.CENTER;
		labelHost = new Label(parent, SWT.NONE);
		labelHost.setText("Host:");
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = SWT.CENTER;
		textHost = new Text(parent, SWT.BORDER);
		textHost.setLayoutData(gridData3);
		labelUsername = new Label(parent, SWT.NONE);
		labelUsername.setText("Username:");
		textUsername = new Text(parent, SWT.BORDER);
		textUsername.setLayoutData(gridData2);
		labelPassword = new Label(parent, SWT.NONE);
		labelPassword.setText("Password:");
		textPassword = new Text(parent, SWT.BORDER);
		textPassword.setLayoutData(gridData1);
		textPassword.setEchoChar('*');
		super.createGUI(parent);
	}

	@Override
	public ConnectionDescription getConnectionDescription() throws URISyntaxException {
		ConnectionDescription desc = super.getConnectionDescription();
		desc.setUri(new URI(m_scheme, textHost.getText(), desc.getUri().getPath(), null));
		desc.setParameter("username", textUsername.getText());
		desc.setSecretParameter("password", textPassword.getText());
		return desc;
	}

	@Override
	public void setConnectionDescription(final ConnectionDescription connection) {
		super.setConnectionDescription(connection);
		URI uri = connection.getUri();
		textHost.setText(uri.getHost());
		textUsername.setText(connection.getParameter("username"));
		textPassword.setText(connection.getSecretParameter("password"));
	}

	@Override
	public void reset(final String scheme) {
		super.reset(scheme);
		textHost.setText("");
		textUsername.setText("");
		textPassword.setText("");
	}

	@Override
	public void dispose() {
		super.dispose();
		labelHost.dispose();
		textHost.dispose();
		labelUsername.dispose();
		textUsername.dispose();
		labelPassword.dispose();
		textPassword.dispose();
	}

}
