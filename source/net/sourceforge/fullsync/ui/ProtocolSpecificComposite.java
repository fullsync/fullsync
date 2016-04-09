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

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.FileSystemManager;
import net.sourceforge.fullsync.fs.Site;

import org.apache.commons.vfs2.FileObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

class ProtocolSpecificComposite {

	private Label labelPath;
	protected Text textPath = null;
	private Button buttonBrowse;
	private Button buttonBuffered;
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
		buttonBrowse.addListener(SWT.Selection, e -> onBrowse());
		buttonBuffered = new Button(parent, SWT.CHECK | SWT.LEFT);
		GridData buttonDestinationBufferedData = new GridData();
		buttonDestinationBufferedData.horizontalSpan = 3;
		buttonBuffered.setLayoutData(buttonDestinationBufferedData);
		buttonBuffered.setText(Messages.getString("ProfileDetails.Buffered.Label")); //$NON-NLS-1$
		buttonBuffered.setVisible(false); //FIXME: [BUFFERING] remove to restore buffering
	}

	public ConnectionDescription getConnectionDescription() throws URISyntaxException {
		String path = textPath.getText();
		if ((null == path) || (0 == path.length())) {
			path = "/";
		}
		return new ConnectionDescription(new URI(m_scheme, null, path, null));
	}

	public void setConnectionDescription(final ConnectionDescription connection) {
		if ((null != connection) && (null != connection.getUri())) {
			textPath.setText(connection.getUri().getPath());
		}
		else {
			textPath.setText("");
		}
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
		try {
			ConnectionDescription desc = getConnectionDescription();
			FileSystemManager fsm = new FileSystemManager();
			desc.setParameter(ConnectionDescription.PARAMETER_INTERACTIVE, "true");
			try (Site conn = fsm.createConnection(desc)) {
				FileObject base = conn.getBase();
				FileObjectChooser foc = new FileObjectChooser(m_parent.getShell(), SWT.NULL);
				foc.setBaseFileObject(base);
				foc.setSelectedFileObject(base);
				if (foc.open()) {
					setPath(new URI(foc.getActiveFileObject().getName().getURI()).getPath());
				}
			}
		}
		catch (Exception ex) {
			ExceptionHandler.reportException(ex);
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
}
