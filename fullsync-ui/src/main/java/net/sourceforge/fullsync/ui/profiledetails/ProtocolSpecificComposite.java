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

import java.net.URI;

import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.commons.vfs2.FileObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.ConnectionDescription.Builder;
import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.FileSystemConnection;
import net.sourceforge.fullsync.FileSystemManager;
import net.sourceforge.fullsync.ui.Messages;

abstract class ProtocolSpecificComposite {
	@Inject
	private Provider<FileObjectChooser> fileObjectChooserProvider;
	@Inject
	private Provider<FileSystemManager> fileSystemManagerProvider;
	protected Text textPath;
	private String m_scheme;
	protected Composite m_parent;
	private Button buttonBuffered;

	public void createGUI(final Composite parent) {
		m_parent = parent;
		onBeforePathHook(parent);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = SWT.CENTER;
		Label labelPath = new Label(parent, SWT.NONE);
		labelPath.setText(Messages.getString("ProtocolSpecificComposite.Path")); //$NON-NLS-1$
		textPath = new Text(parent, SWT.BORDER);
		textPath.setLayoutData(gridData);
		Button buttonBrowse = new Button(parent, SWT.NONE);
		buttonBrowse.setText(Messages.getString("ProtocolSpecificComposite.Browse")); //$NON-NLS-1$
		buttonBrowse.addListener(SWT.Selection, this::onBrowse);
		buttonBuffered = new Button(parent, SWT.CHECK | SWT.LEFT);
		GridData buttonDestinationBufferedData = new GridData();
		buttonDestinationBufferedData.horizontalSpan = 3;
		buttonBuffered.setLayoutData(buttonDestinationBufferedData);
		buttonBuffered.setText(Messages.getString("ProfileDetails.Buffered.Label")); //$NON-NLS-1$
		buttonBuffered.setVisible(false); // FIXME: [BUFFERING] remove to restore buffering
	}

	protected void onBeforePathHook(Composite parent) {
	}

	public ConnectionDescription.Builder getConnectionDescription() {
		Builder builder = new ConnectionDescription.Builder();
		builder.setScheme(m_scheme);
		builder.setPath(textPath.getText());
		return builder;
	}

	public void setConnectionDescription(final ConnectionDescription connection) {
		String path = null != connection ? connection.getPath() : ""; //$NON-NLS-1$
		textPath.setText(path);
	}

	public void reset(final String scheme) {
		m_scheme = scheme;
		textPath.setText(""); //$NON-NLS-1$
	}

	protected void setPath(final String path) {
		textPath.setText(path);
	}

	private void onBrowse(Event e) {
		onBrowse();
	}

	public void onBrowse() {
		try {
			ConnectionDescription desc = getConnectionDescription().build();
			FileSystemManager fsm = fileSystemManagerProvider.get();
			try (FileSystemConnection conn = fsm.createConnection(desc, true)) {
				FileObject base = conn.getBase();
				FileObjectChooser foc = fileObjectChooserProvider.get();
				if (foc.open(m_parent.getShell(), base)) {
					setPath(new URI(foc.getActiveFileObject().getName().getURI()).getPath());
				}
			}
		}
		catch (Exception ex) {
			ExceptionHandler.reportException(ex);
		}
	}

	/**
	 * getBuffered return the state of the buffered checkbox.
	 *
	 * @return true if the buffered checkbox is set and enabled
	 */
	public boolean getBuffered() {
		return buttonBuffered.getEnabled() && buttonBuffered.getSelection();
	}

	/**
	 * setBuffered set or clear the buffering checkbox.
	 *
	 * @param buffered
	 *            true to set the checkbox
	 */
	public void setBuffered(final boolean buffered) {
		buttonBuffered.setSelection(buffered);
	}

	/**
	 * setBufferedEnabled en/disable buffering.
	 *
	 * @param enabled
	 *            true to enable
	 */
	public void setBufferedEnabled(final boolean enabled) {
		buttonBuffered.setEnabled(enabled);
	}
}
