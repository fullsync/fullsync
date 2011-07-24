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

import org.apache.commons.vfs2.FileSystemException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class ConnectionConfiguration extends Composite {
	private Label labelProtocol = null;
	private Combo comboProtocol = null;
	private Composite compositeProtocolSpecific = null;
	private ProtocolSpecificComposite compositeSpecific;

	public ConnectionConfiguration(Composite parent, int style) {
		super(parent, style);
		initialize();
		updateComponent();
	}

	/**
	 * This method initializes this
	 * 
	 * @throws FileSystemException
	 * 
	 */
	private void initialize() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		labelProtocol = new Label(this, SWT.NONE);
		labelProtocol.setText("Protocol:");
		createComboProtocol();
		this.setLayout(gridLayout);
		createCompositeProtocolSpecific();
	}

	/**
	 * This method initializes comboProtocol
	 * 
	 * @throws FileSystemException
	 * 
	 */
	private void createComboProtocol() {
		comboProtocol = new Combo(this, SWT.READ_ONLY);
		comboProtocol.addModifyListener(new org.eclipse.swt.events.ModifyListener() {
			@Override
			public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
				if (compositeSpecific != null)
					compositeSpecific.dispose();

				compositeSpecific = FileSystemUiManager.getInstance().createProtocolSpecificComposite(compositeProtocolSpecific, SWT.NULL,
						comboProtocol.getText());

				compositeProtocolSpecific.layout();
				setSize(computeSize(getSize().x, SWT.DEFAULT));
			}
		});
	}

	/**
	 * This method initializes compositeProtocolSpecific
	 * 
	 */
	private void createCompositeProtocolSpecific() {
		GridData gridData1 = new org.eclipse.swt.layout.GridData();
		gridData1.horizontalSpan = 2;
		gridData1.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.grabExcessVerticalSpace = true;
		gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		compositeProtocolSpecific = new Composite(this, SWT.NONE);
		compositeProtocolSpecific.setLayout(new FillLayout());
		compositeProtocolSpecific.setLayoutData(gridData1);
	}

	public void updateComponent() {
		comboProtocol.removeAll();
		String[] schemes = FileSystemUiManager.getInstance().getSchemes();
		for (int i = 0; i < schemes.length; i++)
			comboProtocol.add(schemes[i]);
		comboProtocol.select(0);
	}

	public void setLocationDescription(LocationDescription location) {
		comboProtocol.setText(location.getUri().getScheme());
		compositeSpecific.setLocationDescription(location);
	}

	public LocationDescription getLocationDescription() throws URISyntaxException {
		return compositeSpecific.getLocationDescription();
	}

} // @jve:decl-index=0:visual-constraint="10,10"
