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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.FileSystemManager;
import net.sourceforge.fullsync.fs.Site;
import net.sourceforge.fullsync.fs.connection.CommonsVfsConnection;
import net.sourceforge.fullsync.ui.Messages;

import org.apache.commons.vfs2.FileObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class SFTPSpecificComposite implements ProtocolSpecificComposite {
	private String m_scheme;

	private Composite m_parent;

	private Label labelPath = null;
	private Text textPath = null;
	private Button buttonBrowse = null;
	private Label labelHost = null;
	private Text textHost = null;
	private Label labelUsername = null;
	private Text textUsername = null;
	private Label labelPassword = null;
	private Text textPassword = null;
	private Button buttonBuffered = null;
	private Button buttonKeybased = null;
	private Label labelKeyPassphrase = null;
	private Text textKeyPassphrase = null;

	public SFTPSpecificComposite(Composite parent) {
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

		labelPath = new Label(m_parent, SWT.NONE);
		labelPath.setText(Messages.getString("ProtocolSpecificComposite.Path"));
		textPath = new Text(m_parent, SWT.BORDER);
		textPath.setLayoutData(gridData);
		buttonBrowse = new Button(m_parent, SWT.NONE);
		buttonBrowse.setText("...");
		buttonBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				ConnectionDescription desc = null;
				Site conn = null;
				try {
					desc = getConnectionDescription();
					FileSystemManager fsm = new FileSystemManager();
					conn = fsm.createConnection(desc);

					FileObject base = ((CommonsVfsConnection) conn).getBase();
					FileObjectChooser foc = new FileObjectChooser(m_parent.getShell(), SWT.NULL);
					foc.setBaseFileObject(base);
					foc.setSelectedFileObject(base);
					if (foc.open()) {
						URI uri;
						uri = new URI(foc.getActiveFileObject().getName().getURI());
						textPath.setText(uri.getPath());
					}
				}
				catch (Exception e1) {
					ExceptionHandler.reportException(e1);
				}
				finally {
					if (null != conn) {
						try {
							conn.close();
						}
						catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});
		buttonBuffered = new Button(m_parent, SWT.CHECK | SWT.LEFT);
		GridData buttonDestinationBufferedData = new GridData();
		buttonDestinationBufferedData.horizontalSpan = 3;
		buttonBuffered.setLayoutData(buttonDestinationBufferedData);
		buttonBuffered.setText(Messages.getString("ProfileDetails.Buffered.Label")); //$NON-NLS-1$
	}

	@Override
	public ConnectionDescription getConnectionDescription() throws URISyntaxException {
		ConnectionDescription loc = new ConnectionDescription(new URI(m_scheme, textHost.getText(), textPath.getText(), null));
		loc.setParameter("username", textUsername.getText());
		loc.setSecretParameter("password", textPassword.getText());
		loc.setParameter("publicKeyAuth", buttonKeybased.getSelection() ? "enabled" : "disabled");
		loc.setSecretParameter("keyPassphrase", textKeyPassphrase.getText());
		return loc;
	}

	@Override
	public void setConnectionDescription(final ConnectionDescription connection) {
		URI uri = connection.getUri();
		textHost.setText(uri.getHost());
		textPath.setText(uri.getPath());
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
		m_scheme = scheme;
		textHost.setText("");
		textPath.setText("");
		textUsername.setText("");
		textPassword.setText("");
		buttonKeybased.setSelection(false);
		textKeyPassphrase.setText("");
		textKeyPassphrase.setEnabled(false);
		labelKeyPassphrase.setEnabled(false);
	}

	@Override
	public void dispose() {
		labelPath.dispose();
		textPath.dispose();
		buttonBrowse.dispose();
		labelHost.dispose();
		textHost.dispose();
		labelUsername.dispose();
		textUsername.dispose();
		labelPassword.dispose();
		textPassword.dispose();
		buttonBuffered.dispose();
	}

	@Override
	public boolean getBuffered() {
		return buttonBuffered.getEnabled() && buttonBuffered.getSelection();
	}

	@Override
	public void setBuffered(final boolean buffered) {
		buttonBuffered.setSelection(buffered);
	}

	@Override
	public void setBufferedEnabled(final boolean enabled) {
		buttonBuffered.setEnabled(enabled);
	}

}
