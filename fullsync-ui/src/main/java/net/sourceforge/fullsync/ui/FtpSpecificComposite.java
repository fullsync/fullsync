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

import java.net.URISyntaxException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import net.sourceforge.fullsync.ConnectionDescription;

public class FtpSpecificComposite extends UserPasswordSpecificComposite {
	private static final String FTP_ANONYMOUS_USERNAME = "Anonymous"; //$NON-NLS-1$
	private Combo comboAuthentication;
	private Button userDirIsRootCheckbox;

	@Override
	public int getDefaultPort() {
		return 21;
	}

	@Override
	protected void onBeforePasswordHook(Composite parent) {
		super.onBeforePasswordHook(parent);
		Label labelAuthenticationType = new Label(parent, SWT.NONE);
		labelAuthenticationType.setText(Messages.getString("ProtocolSpecificComposite.FTPAuthType")); //$NON-NLS-1$
		comboAuthentication = new Combo(parent, SWT.READ_ONLY);
		GridData comboAuthenticationData = new GridData();
		comboAuthenticationData.horizontalSpan = 2;
		comboAuthenticationData.horizontalAlignment = SWT.FILL;
		comboAuthentication.setLayoutData(comboAuthenticationData);
		comboAuthentication.add(Messages.getString("ProtocolSpecificComposite.FTPAuthTypeAnonymous")); //$NON-NLS-1$
		comboAuthentication.add(Messages.getString("ProtocolSpecificComposite.FTPAuthTypeUserPassword")); //$NON-NLS-1$
	}

	@Override
	protected void onBeforePathHook(Composite parent) {
		super.onBeforePathHook(parent);
		userDirIsRootCheckbox = new Button(parent, SWT.CHECK | SWT.LEFT);
		userDirIsRootCheckbox.setText("Restrict to the default directory.");
		GridData userDirIsRootCheckboxData = new GridData();
		userDirIsRootCheckboxData.horizontalSpan = 3;
		userDirIsRootCheckbox.setLayoutData(userDirIsRootCheckboxData);
	}

	@Override
	public void createGUI(final Composite parent) {
		super.createGUI(parent);
		setUserPasswordEnabled(false);
		comboAuthentication.select(0);
		comboAuthentication.addListener(SWT.Selection, e -> setUserPasswordEnabled(comboAuthentication.getSelectionIndex() == 1));
	}

	@Override
	public void setConnectionDescription(ConnectionDescription connection) {
		super.setConnectionDescription(connection);
		if (FTP_ANONYMOUS_USERNAME.equals(connection.getUsername().orElse(""))) {
			comboAuthentication.select(0);
		}
		else {
			comboAuthentication.select(1);
		}
		setUserPasswordEnabled(comboAuthentication.getSelectionIndex() == 1);
		userDirIsRootCheckbox.setSelection(connection.isUserDirIsRoot());
	}

	@Override
	public ConnectionDescription.Builder getConnectionDescription() throws URISyntaxException {
		ConnectionDescription.Builder builder = super.getConnectionDescription();
		if (comboAuthentication.getSelectionIndex() == 0) {
			builder.setUsername(FTP_ANONYMOUS_USERNAME);
			builder.setPassword(""); //$NON-NLS-1$
		}
		builder.setUserDirIsRoot(userDirIsRootCheckbox.getSelection());
		return builder;
	}

	@Override
	public void reset(String scheme) {
		super.reset(scheme);
		comboAuthentication.select(0);
		userDirIsRootCheckbox.setSelection(false);
	}
}
