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

import net.sourceforge.fullsync.ui.Messages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class FileSpecificComposite implements ProtocolSpecificComposite {
	private Composite m_parent;
	private Label labelPath = null;
	private Text textPath = null;
	private Button buttonBrowse = null;
	private Button buttonBuffered = null;

	public FileSpecificComposite(Composite parent) {
		m_parent = parent;
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = SWT.CENTER;
		labelPath = new Label(m_parent, SWT.NONE);
		labelPath.setText("Path:");
		textPath = new Text(m_parent, SWT.BORDER);
		textPath.setLayoutData(gridData);
		buttonBrowse = new Button(m_parent, SWT.NONE);
		buttonBrowse.setText("...");
		buttonBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				DirectoryDialog d = new DirectoryDialog(m_parent.getShell());
				String dir = d.open();
				if (dir != null) {
					File f = new File(dir);
					textPath.setText(f.toURI().toString());
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

	@Override
	public void dispose() {
		labelPath.dispose();
		textPath.dispose();
		buttonBrowse.dispose();
		buttonBuffered.dispose();
	}

	@Override
	public boolean getBuffered() {
		return buttonBuffered.getSelection();
	}

	@Override
	public void setBuffered(boolean buffered) {
		buttonBuffered.setSelection(buffered);
	}

	@Override
	public void setBufferedEnabled(boolean enabled) {
		buttonBuffered.setEnabled(enabled);
	}
}
