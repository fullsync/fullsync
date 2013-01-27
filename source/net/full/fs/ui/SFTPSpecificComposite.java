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
import net.sourceforge.fullsync.ui.Messages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

class SFTPSpecificComposite extends ProtocolSpecificComposite {
	private static final int DEFAULT_SFTP_PORT = 22;

	private Label labelHost = null;
	private Text textHost = null;
	private Label labelPort = null;
	private Spinner spinnerPort = null;
	private Label labelUsername = null;
	private Text textUsername = null;
	private Label labelPassword = null;
	private Text textPassword = null;
	private Button buttonKeybased = null;
	private Label labelKeyPassphrase = null;
	private Text textKeyPassphrase = null;

	@Override
	public void createGUI(final Composite parent) {
		m_parent = parent;

		GridData gridData1 = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData1.horizontalSpan = 2;

		// hostname
		labelHost = new Label(m_parent, SWT.NONE);
		labelHost.setText(Messages.getString("ProtocolSpecificComposite.Host"));
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = SWT.CENTER;
		textHost = new Text(m_parent, SWT.BORDER);
		GridData gridData3 = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData3.horizontalSpan = 2;
		textHost.setLayoutData(gridData3);

		labelPort = new Label(parent, SWT.NONE);
		labelPort.setText("Port:");
		spinnerPort = new Spinner(parent, SWT.BORDER);
		spinnerPort.setMinimum(1);
		spinnerPort.setMaximum(0xFFFF);
		spinnerPort.setSelection(DEFAULT_SFTP_PORT);
		GridData gridData4 = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData4.horizontalSpan = 2;
		spinnerPort.setLayoutData(gridData4);

		labelUsername = new Label(m_parent, SWT.NONE);
		labelUsername.setText(Messages.getString("ProtocolSpecificComposite.Username"));
		textUsername = new Text(m_parent, SWT.BORDER);
		GridData gridData2 = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData2.horizontalSpan = 2;
		textUsername.setLayoutData(gridData2);
		labelPassword = new Label(m_parent, SWT.NONE);
		labelPassword.setText(Messages.getString("ProtocolSpecificComposite.Password"));
		textPassword = new Text(m_parent, SWT.BORDER);
		textPassword.setLayoutData(gridData1);
		textPassword.setEchoChar('*');

		buttonKeybased = new Button(m_parent, SWT.CHECK);
		buttonKeybased.setText(Messages.getString("ProtocolSpecificComposite.Keybased"));
		GridData radioKeybasedData = new GridData();
		radioKeybasedData.horizontalSpan = 3;
		buttonKeybased.setLayoutData(radioKeybasedData);
		buttonKeybased.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				boolean enabled = buttonKeybased.getSelection();
				labelKeyPassphrase.setEnabled(enabled);
				textKeyPassphrase.setEnabled(enabled);
			}
			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				boolean enabled = buttonKeybased.getSelection();
				labelKeyPassphrase.setEnabled(enabled);
				textKeyPassphrase.setEnabled(enabled);
			}
		});

		labelKeyPassphrase = new Label(m_parent, SWT.NONE);
		labelKeyPassphrase.setText(Messages.getString("ProtocolSpecificComposite.KeyPassphrase"));
		textKeyPassphrase = new Text(m_parent, SWT.BORDER);
		textKeyPassphrase.setEchoChar('*');
		GridData textKeyPassphraseData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		textKeyPassphraseData.horizontalSpan = 2;
		textKeyPassphrase.setLayoutData(textKeyPassphraseData);
		super.createGUI(parent);
	}

	@Override
	public ConnectionDescription getConnectionDescription() throws URISyntaxException {
		ConnectionDescription loc = super.getConnectionDescription();
		loc.setUri(new URI(m_scheme, null, textHost.getText(), spinnerPort.getSelection(), loc.getUri().getPath(), null, null));
		loc.setParameter("username", textUsername.getText());
		loc.setSecretParameter("password", textPassword.getText());
		loc.setParameter("publicKeyAuth", buttonKeybased.getSelection() ? "enabled" : "disabled");
		loc.setSecretParameter("keyPassphrase", textKeyPassphrase.getText());
		return loc;
	}

	@Override
	public void setConnectionDescription(final ConnectionDescription connection) {
		super.setConnectionDescription(connection);
		URI uri = connection.getUri();
		textHost.setText(uri.getHost());
		int port = uri.getPort();
		if (-1 == port) {
			port = DEFAULT_SFTP_PORT;
		}
		spinnerPort.setSelection(port);
		textUsername.setText(connection.getParameter("username"));
		textPassword.setText(connection.getSecretParameter("password"));
		buttonKeybased.setSelection("enabled".equals(connection.getParameter("publicKeyAuth")));
		labelKeyPassphrase.setEnabled(buttonKeybased.getSelection());
		textKeyPassphrase.setEnabled(buttonKeybased.getSelection());
		String keyPassphrase = connection.getSecretParameter("keyPassphrase");
		if (null != keyPassphrase) {
			textKeyPassphrase.setText(keyPassphrase);
		}
	}

	@Override
	public void reset(final String scheme) {
		super.reset(scheme);
		textHost.setText("");
		spinnerPort.setSelection(DEFAULT_SFTP_PORT);
		textUsername.setText("");
		textPassword.setText("");
		buttonKeybased.setSelection(false);
		textKeyPassphrase.setText("");
		textKeyPassphrase.setEnabled(false);
		labelKeyPassphrase.setEnabled(false);
	}

	@Override
	public void dispose() {
		super.dispose();
		labelHost.dispose();
		textHost.dispose();
		labelPort.dispose();
		spinnerPort.dispose();
		labelUsername.dispose();
		textUsername.dispose();
		labelPassword.dispose();
		textPassword.dispose();
	}
}
