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
import java.util.HashMap;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.ExceptionHandler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class ConnectionConfiguration implements ModifyListener {
	private Composite m_parent; // the tabs content
	private static String[] schemes = new String[] { "file", "ftp", "sftp", "smb" };
	private static HashMap<String, Class<? extends ProtocolSpecificComposite>> composites;
	private Label labelProtocol = null;
	private Combo comboProtocol = null;
	private Composite compositeProtocolSpecific = null;
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

	public ConnectionConfiguration(Composite parent) {
		m_parent = parent;
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
		comboProtocol.addModifyListener(this);

		selectedScheme = comboProtocol.getText();
		compositeSpecific = createProtocolSpecificComposite();
	}

	@Override
	public void modifyText(final ModifyEvent e) {
		if (compositeSpecific != null) {
			compositeSpecific.dispose();
		}

		selectedScheme = comboProtocol.getText();
		compositeProtocolSpecific.dispose();
		initialize();

		m_parent.layout(true);
	}


	public void setConnectionDescription(ConnectionDescription location) {
		comboProtocol.setText(location.getUri().getScheme());
		compositeSpecific.setConnectionDescription(location);
	}

	public ConnectionDescription getConnectionDescription() throws URISyntaxException {
		return compositeSpecific.getConnectionDescription();
	}

	private ProtocolSpecificComposite createProtocolSpecificComposite() {
		ProtocolSpecificComposite composite = null;
		Class<? extends ProtocolSpecificComposite> com = composites.get(selectedScheme);
		try {
			composite = com.newInstance();
			composite.createGUI(compositeProtocolSpecific);
			composite.reset(selectedScheme);
			composite.setBufferedEnabled(bufferedEnabled);
			composite.setBuffered(bufferedActive);
		}
		catch (InstantiationException e) {
			ExceptionHandler.reportException(e);
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			ExceptionHandler.reportException(e);
			e.printStackTrace();
		}
		return composite;
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
