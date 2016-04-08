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
import java.util.HashMap;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.ExceptionHandler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class ConnectionConfiguration {
	private Composite m_parent; // the tabs content
	private static String[] schemes = new String[] { "file", "ftp", "sftp", "smb" };
	private static HashMap<String, Class<? extends ProtocolSpecificComposite>> composites;
	private Label labelProtocol;
	private Combo comboProtocol;
	private Composite compositeProtocolSpecific;
	private ProtocolSpecificComposite compositeSpecific;
	private String selectedScheme;
	private boolean bufferedEnabled = true;
	private boolean bufferedActive = false;

	static {
		composites = new HashMap<String, Class<? extends ProtocolSpecificComposite>>();
		composites.put("file", FileSpecificComposite.class);
		composites.put("ftp", FTPSpecificComposite.class);
		composites.put("sftp", SFTPSpecificComposite.class);
		composites.put("smb", SMBSpecificComposite.class);
	}

	public ConnectionConfiguration(Composite parent, ConnectionDescription desc) {
		m_parent = parent;
		if (null != desc) {
			URI uri = desc.getUri();
			if (null != uri) {
				selectedScheme = uri.getScheme();
			}
		}
		initialize();
	}

	private void initialize() {
		compositeProtocolSpecific = new Composite(m_parent, SWT.NONE);
		compositeProtocolSpecific.setLayout(new GridLayout(3, false));
		compositeProtocolSpecific.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// protcol combo box
		labelProtocol = new Label(compositeProtocolSpecific, SWT.NONE);
		labelProtocol.setText("Protocol:"); //FIXME: externalize
		comboProtocol = new Combo(compositeProtocolSpecific, SWT.READ_ONLY);
		GridData protocolData = new GridData();
		protocolData.horizontalSpan = 2;
		protocolData.horizontalAlignment = SWT.FILL;
		comboProtocol.setLayoutData(protocolData);
		comboProtocol.removeAll();
		int selectedIndex = 0, i = 0;
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
			m_parent.getDisplay().asyncExec(() -> {
				for (Control c : m_parent.getChildren()) {
					if (!c.isDisposed()) {
						c.dispose();
					}
				}

				initialize();

				m_parent.layout(true);
			});
		});

		selectedScheme = comboProtocol.getText();
		createProtocolSpecificComposite();
	}
	public void setConnectionDescription(ConnectionDescription location) {
		compositeSpecific.setConnectionDescription(location);
	}

	public ConnectionDescription getConnectionDescription() throws URISyntaxException {
		return compositeSpecific.getConnectionDescription();
	}

	private void createProtocolSpecificComposite() {
		Class<? extends ProtocolSpecificComposite> com = composites.get(selectedScheme);
		try {
			compositeSpecific = com.newInstance();
			compositeSpecific.createGUI(compositeProtocolSpecific);
			compositeSpecific.reset(selectedScheme);
			compositeSpecific.setBufferedEnabled(bufferedEnabled);
			compositeSpecific.setBuffered(bufferedActive);
		}
		catch (InstantiationException e) {
			ExceptionHandler.reportException(e);
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			ExceptionHandler.reportException(e);
			e.printStackTrace();
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
