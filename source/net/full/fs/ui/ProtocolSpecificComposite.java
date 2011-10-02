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
import net.sourceforge.fullsync.ui.Messages;

import org.apache.commons.vfs2.FileObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ProtocolSpecificComposite {

	private Label labelPath = null;
	private Text textPath = null;
	private Button buttonBrowse = null;
	private Button buttonBuffered = null;
	protected String m_scheme = null;
	protected Composite m_parent = null;

	public void createGUI(final Composite parent) {
		m_parent = parent;
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = SWT.CENTER;
		labelPath = new Label(parent, SWT.NONE);
		labelPath.setText("Path:");
		textPath = new Text(parent, SWT.BORDER);
		textPath.setLayoutData(gridData);
		buttonBrowse = new Button(parent, SWT.NONE);
		buttonBrowse.setText("...");
		buttonBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				onBrowse();
			}
		});
		buttonBuffered = new Button(parent, SWT.CHECK | SWT.LEFT);
		GridData buttonDestinationBufferedData = new GridData();
		buttonDestinationBufferedData.horizontalSpan = 3;
		buttonBuffered.setLayoutData(buttonDestinationBufferedData);
		buttonBuffered.setText(Messages.getString("ProfileDetails.Buffered.Label")); //$NON-NLS-1$
	}


	public ConnectionDescription getConnectionDescription() throws URISyntaxException {
		return new ConnectionDescription(new URI(m_scheme, null, textPath.getText(), null));
	}

	public void setConnectionDescription(final ConnectionDescription connection) {
		textPath.setText(connection.getUri().getPath());
	}

	public void reset(final String scheme) {
		m_scheme = scheme;
		textPath.setText("");
	}

	protected void setPath(final String path) {
		textPath.setText(path);
	}

	/**
	 * onBrowse
	 * open a browse dialog and let the user choose a path.
	 */
	public void onBrowse() {
		Site conn = null;
		try {
			ConnectionDescription desc = getConnectionDescription();
			FileSystemManager fsm = new FileSystemManager();
			conn = fsm.createConnection(desc);

			FileObject base = conn.getBase();
			FileObjectChooser foc = new FileObjectChooser(m_parent.getShell(), SWT.NULL);
			foc.setBaseFileObject(base);
			foc.setSelectedFileObject(base);
			if (foc.open()) {
				URI uri;
				uri = new URI(foc.getActiveFileObject().getName().getURI());
				setPath(uri.getPath());
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

	/**
	 * getBuffered
	 * return the state of the buffered checkbox.
	 * @return true if the buffered checkbox is set and enabled
	 */
	public boolean getBuffered() {
		return buttonBuffered.getEnabled() && buttonBuffered.getSelection();
	}

	/**
	 * setBuffered
	 * set or clear the buffering checkbox.
	 * @param buffered true to set the checkbox
	 */
	public void setBuffered(final boolean buffered) {
		buttonBuffered.setSelection(buffered);
	}

	/**
	 * setBufferedEnabled
	 * en/disable buffering.
	 * @param enabled true to enable
	 */
	public void setBufferedEnabled(final boolean enabled) {
		buttonBuffered.setEnabled(enabled);
	}

	/**
	 * dispose
	 * dispose all created controls.
	 */
	public void dispose() {
		labelPath.dispose();
		textPath.dispose();
		buttonBrowse.dispose();
		buttonBuffered.dispose();
	}
}
