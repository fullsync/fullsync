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

import java.net.URISyntaxException;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.ui.Messages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class WebDavSpecificComposite extends UserPasswordSpecificComposite {
	private static final String ANONYMOUS_USERNAME = "Anonymous";
	Combo comboAuthentication;
	@Override
	public int getDefaultPort() {
		return 21;
	}

	@Override
	protected void onBeforePasswordHook(Composite parent) {
		super.onBeforePasswordHook(parent);
		Label labelAuthenticationType = new Label(parent, SWT.NONE);
		labelAuthenticationType.setText(Messages.getString("ProtocolSpecificComposite.WebDavAuthType"));
		comboAuthentication = new Combo(parent, SWT.READ_ONLY);
		GridData comboAuthenticationData = new GridData();
		comboAuthenticationData.horizontalSpan = 2;
		comboAuthenticationData.horizontalAlignment = SWT.FILL;
		comboAuthentication.setLayoutData(comboAuthenticationData);
		comboAuthentication.add(Messages.getString("ProtocolSpecificComposite.WebDavAuthTypeAnonymous"));
		comboAuthentication.add(Messages.getString("ProtocolSpecificComposite.WebDavAuthTypeUserPassword"));
		comboAuthentication.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setUserPasswordEnabled(comboAuthentication.getSelectionIndex() == 1);
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		comboAuthentication.select(0);
	}

	@Override
	public void setConnectionDescription(ConnectionDescription connection) {
		super.setConnectionDescription(connection);
		if (ANONYMOUS_USERNAME.equals(connection.getParameter(ConnectionDescription.PARAMETER_USERNAME))) {
			comboAuthentication.select(0);
		}
		else {
			comboAuthentication.select(1);
		}
		setUserPasswordEnabled(comboAuthentication.getSelectionIndex() == 1);
	}

	@Override
	public ConnectionDescription getConnectionDescription() throws URISyntaxException {
		ConnectionDescription connection = super.getConnectionDescription();
		if (comboAuthentication.getSelectionIndex() == 0) {
			connection.setParameter(ConnectionDescription.PARAMETER_USERNAME, ANONYMOUS_USERNAME);
			connection.setSecretParameter(ConnectionDescription.PARAMETER_PASSWORD, "");
		}
		return connection;
	}
}
