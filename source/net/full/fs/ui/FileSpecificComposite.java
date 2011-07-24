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

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class FileSpecificComposite extends ProtocolSpecificComposite {
	private Label labelPath = null;
	private Text textPath = null;
	private Button buttonBrowse = null;

	public FileSpecificComposite(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	public void initialize() {
		GridData gridData = new org.eclipse.swt.layout.GridData();
		gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		labelPath = new Label(this, SWT.NONE);
		labelPath.setText("Path:");
		textPath = new Text(this, SWT.BORDER);
		textPath.setLayoutData(gridData);
		buttonBrowse = new Button(this, SWT.NONE);
		buttonBrowse.setText("...");
		buttonBrowse.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				DirectoryDialog d = new DirectoryDialog(getShell());
				String dir = d.open();
				if (dir != null) {
					File f = new File(dir);
					textPath.setText(f.toURI().toString());
				}
			}
		});
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		this.setLayout(gridLayout);

	}

	@Override
	public LocationDescription getLocationDescription() throws URISyntaxException {
		return new LocationDescription(new URI(textPath.getText()));
	}

	@Override
	public void setLocationDescription(LocationDescription location) {
		textPath.setText(location.getUri().toString());
	}

	@Override
	public void reset(String scheme) {
		textPath.setText("");
	}
}
