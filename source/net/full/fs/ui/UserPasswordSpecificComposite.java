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

import java.net.URI;
import java.net.URISyntaxException;

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.ui.Messages;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.UserAuthenticator;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class UserPasswordSpecificComposite implements ProtocolSpecificComposite {
	private String m_scheme;

	private Composite m_parent;

	private Label labelPath = null;
	private Text textPath = null;
	private Button buttonBrowse = null;
	private Label labelHost = null;
	private Text textHost = null;
	private Label labelUsername = null;
	private Text textUsername = null;
	private Label labelPassword = null;
	private Text textPassword = null;
	private Button buttonBuffered = null;

	public UserPasswordSpecificComposite(Composite parent) {
		m_parent = parent;
		GridData gridData3 = new GridData();
		gridData3.horizontalAlignment = SWT.FILL;
		gridData3.horizontalSpan = 2;
		gridData3.verticalAlignment = SWT.CENTER;
		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = SWT.FILL;
		gridData2.horizontalSpan = 2;
		gridData2.verticalAlignment = SWT.CENTER;
		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = SWT.FILL;
		gridData1.horizontalSpan = 2;
		gridData1.verticalAlignment = SWT.CENTER;
		labelHost = new Label(m_parent, SWT.NONE);
		labelHost.setText("Host:");
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = SWT.CENTER;
		textHost = new Text(m_parent, SWT.BORDER);
		textHost.setLayoutData(gridData3);
		labelUsername = new Label(m_parent, SWT.NONE);
		labelUsername.setText("Username:");
		textUsername = new Text(m_parent, SWT.BORDER);
		textUsername.setLayoutData(gridData2);
		labelPassword = new Label(m_parent, SWT.NONE);
		labelPassword.setText("Password:");
		textPassword = new Text(m_parent, SWT.BORDER);
		textPassword.setLayoutData(gridData1);
		textPassword.setEchoChar('*');
		labelPath = new Label(m_parent, SWT.NONE);
		labelPath.setText("Path:");
		textPath = new Text(m_parent, SWT.BORDER);
		textPath.setLayoutData(gridData);
		buttonBrowse = new Button(m_parent, SWT.NONE);
		buttonBrowse.setText("...");
		buttonBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				try {
					LocationDescription desc = getLocationDescription();

					FileObject base = resolveFile(desc);
					FileObjectChooser foc = new FileObjectChooser(m_parent.getShell(), SWT.NULL);
					foc.setBaseFileObject(base);
					foc.setSelectedFileObject(base);
					if (foc.open() == 1) {
						URI uri;
						uri = new URI(foc.getActiveFileObject().getName().getURI());
						textPath.setText(uri.getPath());
					}
				}
				catch (Exception e1) {
					ExceptionHandler.reportException(e1);
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
		URI uri = new URI(m_scheme, textHost.getText(), textPath.getText(), null);
		LocationDescription loc = new LocationDescription(uri);
		loc.setProperty("username", textUsername.getText());
		loc.setProperty("password", textPassword.getText());
		return loc;
	}

	@Override
	public void setLocationDescription(LocationDescription location) {
		URI uri = location.getUri();
		textHost.setText(uri.getHost());
		textPath.setText(uri.getPath());
		textUsername.setText(location.getProperty("username"));
		textPassword.setText(location.getProperty("password"));
	}

	@Override
	public void reset(final String scheme) {
		m_scheme = scheme;
		textHost.setText("");
		textPath.setText("");
		textUsername.setText("");
		textPassword.setText("");
	}

	public FileObject resolveFile(LocationDescription location) throws FileSystemException {
		String uri = location.getUri().getScheme();

		FileSystemOptions fileSystemOptions = new FileSystemOptions();

		if (uri.startsWith("ftp") || uri.startsWith("sftp") || uri.startsWith("smb")) {
			String username = location.getProperty("username");
			String password = location.getProperty("password");
			UserAuthenticator auth = new StaticUserAuthenticator(null, username, password);
			DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(fileSystemOptions, auth);
		}
		return VFS.getManager().resolveFile(uri, fileSystemOptions);
	}

	@Override
	public void dispose() {
		labelPath.dispose();
		textPath.dispose();
		buttonBrowse.dispose();
		labelHost.dispose();
		textHost.dispose();
		labelUsername.dispose();
		textUsername.dispose();
		labelPassword.dispose();
		textPassword.dispose();
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
