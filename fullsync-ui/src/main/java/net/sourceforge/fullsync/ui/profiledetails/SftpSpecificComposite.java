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
package net.sourceforge.fullsync.ui.profiledetails;

import static org.eclipse.swt.events.SelectionListener.widgetDefaultSelectedAdapter;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.ui.Messages;

class SftpSpecificComposite extends UserPasswordSpecificComposite {
	private static final int DEFAULT_SFTP_PORT = 22;

	private Button buttonKeybased;
	private Label labelKeyPassphrase;
	private Text textKeyPassphrase;
	private Button userDirIsRootCheckbox;

	@Override
	public int getDefaultPort() {
		return DEFAULT_SFTP_PORT;
	}

	@Override
	protected void onBeforePathHook(Composite parent) {
		super.onBeforePathHook(parent);
		buttonKeybased = new Button(m_parent, SWT.CHECK);
		buttonKeybased.setText(Messages.getString("ProtocolSpecificComposite.Keybased")); //$NON-NLS-1$
		GridData radioKeybasedData = new GridData();
		radioKeybasedData.horizontalSpan = 3;
		buttonKeybased.setLayoutData(radioKeybasedData);
		buttonKeybased.addSelectionListener(widgetDefaultSelectedAdapter(this::toggleKeybasedAuthentication));
		buttonKeybased.addSelectionListener(widgetSelectedAdapter(this::toggleKeybasedAuthentication));

		labelKeyPassphrase = new Label(m_parent, SWT.NONE);
		labelKeyPassphrase.setText(Messages.getString("ProtocolSpecificComposite.KeyPassphrase")); //$NON-NLS-1$
		textKeyPassphrase = new Text(m_parent, SWT.BORDER);
		textKeyPassphrase.setEchoChar('*');
		GridData textKeyPassphraseData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		textKeyPassphraseData.horizontalSpan = 2;
		textKeyPassphrase.setLayoutData(textKeyPassphraseData);

		userDirIsRootCheckbox = new Button(parent, SWT.CHECK | SWT.LEFT);
		userDirIsRootCheckbox.setText("Restrict to the default directory.");
		GridData userDirIsRootCheckboxData = new GridData();
		userDirIsRootCheckboxData.horizontalSpan = 3;
		userDirIsRootCheckbox.setLayoutData(userDirIsRootCheckboxData);
	}

	private void toggleKeybasedAuthentication(SelectionEvent e) {
		boolean enabled = buttonKeybased.getSelection();
		labelKeyPassphrase.setEnabled(enabled);
		textKeyPassphrase.setEnabled(enabled);
	}

	@Override
	public ConnectionDescription.Builder getConnectionDescription() {
		ConnectionDescription.Builder builder = super.getConnectionDescription();
		builder.setPublicKeyAuth(buttonKeybased.getSelection());
		builder.setKeyPassphrase(textKeyPassphrase.getText());
		builder.setUserDirIsRoot(userDirIsRootCheckbox.getSelection());
		return builder;
	}

	@Override
	public void setConnectionDescription(final ConnectionDescription connection) {
		super.setConnectionDescription(connection);
		boolean keybased = connection.getPublicKeyAuth().orElse(Boolean.FALSE).booleanValue();
		buttonKeybased.setSelection(keybased);
		labelKeyPassphrase.setEnabled(keybased);
		textKeyPassphrase.setEnabled(keybased);
		textKeyPassphrase.setText(connection.getKeyPassphrase().orElse("")); //$NON-NLS-1$
		userDirIsRootCheckbox.setSelection(connection.isUserDirIsRoot());
	}

	@Override
	public void reset(final String scheme) {
		super.reset(scheme);
		buttonKeybased.setSelection(false);
		textKeyPassphrase.setText(""); //$NON-NLS-1$
		textKeyPassphrase.setEnabled(false);
		labelKeyPassphrase.setEnabled(false);
		userDirIsRootCheckbox.setSelection(false);
	}
}
