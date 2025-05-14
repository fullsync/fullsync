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

import jakarta.inject.Inject;
import jakarta.inject.Provider;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.ui.Messages;

public class ConnectionConfiguration {
	private static final String[] schemes = {
		"file", //$NON-NLS-1$
		"ftp", //$NON-NLS-1$
		"sftp", //$NON-NLS-1$
		"smb" //$NON-NLS-1$
	};
	private final Provider<FileSpecificComposite> fileSpecificCompositeProvider;
	private final Provider<FtpSpecificComposite> ftpSpecificCompositeProvider;
	private final Provider<SftpSpecificComposite> sftpSpecificCompositeProvider;
	private final Provider<SmbSpecificComposite> smbSpecificCompositeProvider;
	private Composite parent; // the tabs content
	private Composite compositeProtocolSpecific;
	private ProtocolSpecificComposite compositeSpecific;
	private String selectedScheme;
	private boolean bufferedEnabled = true;
	private boolean bufferedActive = false;

	@Inject
	public ConnectionConfiguration(Provider<FileSpecificComposite> fileSpecificCompositeProvider,
		Provider<FtpSpecificComposite> ftpSpecificCompositeProvider, Provider<SftpSpecificComposite> sftpSpecificCompositeProvider,
		Provider<SmbSpecificComposite> smbSpecificCompositeProvider) {
		this.fileSpecificCompositeProvider = fileSpecificCompositeProvider;
		this.ftpSpecificCompositeProvider = ftpSpecificCompositeProvider;
		this.sftpSpecificCompositeProvider = sftpSpecificCompositeProvider;
		this.smbSpecificCompositeProvider = smbSpecificCompositeProvider;
	}

	void render(Composite parent, ConnectionDescription desc) {
		this.parent = parent;
		if (null != desc) {
			selectedScheme = desc.getScheme();
		}
		initialize();
	}

	private void initialize() {
		compositeProtocolSpecific = new Composite(parent, SWT.NONE);
		compositeProtocolSpecific.setLayout(new GridLayout(3, false));
		compositeProtocolSpecific.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// protocol combo box
		var labelProtocol = new Label(compositeProtocolSpecific, SWT.NONE);
		labelProtocol.setText(Messages.getString("ConnectionConfiguration.Protocol")); //$NON-NLS-1$
		var comboProtocol = new Combo(compositeProtocolSpecific, SWT.READ_ONLY);
		var protocolData = new GridData();
		protocolData.horizontalSpan = 2;
		protocolData.horizontalAlignment = SWT.FILL;
		comboProtocol.setLayoutData(protocolData);
		comboProtocol.removeAll();
		var selectedIndex = 0;
		var i = 0;
		for (String scheme : schemes) {
			comboProtocol.add(scheme);
			if (scheme.equals(selectedScheme)) {
				selectedIndex = i;
			}
			++i;
		}
		comboProtocol.select(selectedIndex);
		comboProtocol.addModifyListener(e -> {
			selectedScheme = comboProtocol.getText();
			parent.getDisplay().asyncExec(() -> {
				for (Control c : parent.getChildren()) {
					if (!c.isDisposed()) {
						c.dispose();
					}
				}
				initialize();
				parent.layout(true);
			});
		});

		selectedScheme = comboProtocol.getText();
		createProtocolSpecificComposite();
	}

	public void setConnectionDescription(ConnectionDescription location) {
		compositeSpecific.setConnectionDescription(location);
	}

	public ConnectionDescription.Builder getConnectionDescription() {
		return compositeSpecific.getConnectionDescription();
	}

	private void createProtocolSpecificComposite() {
		switch (selectedScheme) {
			case "file" -> compositeSpecific = fileSpecificCompositeProvider.get(); //$NON-NLS-1$
			case "ftp" -> compositeSpecific = ftpSpecificCompositeProvider.get(); // $NON-NLS-1$
			case "sftp" -> compositeSpecific = sftpSpecificCompositeProvider.get(); // $NON-NLS-1$
			case "smb" -> compositeSpecific = smbSpecificCompositeProvider.get(); // $NON-NLS-1$
			default -> compositeSpecific = null;
		}
		if (null != compositeSpecific) {
			compositeSpecific.createGUI(compositeProtocolSpecific);
			compositeSpecific.reset(selectedScheme);
			compositeSpecific.setBufferedEnabled(bufferedEnabled);
			compositeSpecific.setBuffered(bufferedActive);
		}
	}

	public boolean getBuffered() {
		return compositeSpecific.getBuffered();
	}

	public void setBuffered(final boolean buffered) {
		bufferedActive = buffered;
		compositeSpecific.setBuffered(buffered);
	}

	public void setBufferedEnabled(final boolean enabled) {
		bufferedEnabled = enabled;
		compositeSpecific.setBufferedEnabled(enabled);
	}
}
