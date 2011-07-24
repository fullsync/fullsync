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
package net.sourceforge.fullsync.fs.connection;

import java.io.File;

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.ui.OptionsDialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.sshtools.j2ssh.transport.AbstractKnownHostsKeyVerification;
import com.sshtools.j2ssh.transport.InvalidHostFileException;
import com.sshtools.j2ssh.transport.TransportProtocolException;
import com.sshtools.j2ssh.transport.publickey.SshPublicKey;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
// FIXME: ssh connections must not auto accept an unknown / changed host key!
public class DialogKnownHostsKeyVerification extends AbstractKnownHostsKeyVerification { // NO_UCD
	private Shell parent;
	private boolean verificationEnabled = true;

	/**
	 * Creates a new DialogKnownHostsKeyVerification object.
	 *
	 * @param parent
	 * @throws InvalidHostFileException
	 */
	public DialogKnownHostsKeyVerification(Shell parent) throws InvalidHostFileException {
		super(new File(System.getProperty("user.home"), ".ssh" + File.separator + "known_hosts").getAbsolutePath());
		this.parent = parent;
	}

	/**
	 * Creates a new DialogKnownHostsKeyVerification object.
	 *
	 * @param parent
	 * @param hostFileName
	 * @throws InvalidHostFileException
	 */
	public DialogKnownHostsKeyVerification(Shell parent, String hostFileName) throws InvalidHostFileException {
		super(hostFileName);
		this.parent = parent;
	}

	public void setVerificationEnabled(boolean enabled) {
		this.verificationEnabled = enabled;
	}

	public void onDeniedHost(final String host) throws TransportProtocolException {
		// Show a message to the user to inform them that the host
		// is denied
		if (verificationEnabled) {
			MessageBox mb = new MessageBox(parent);
			mb.setText("Remote Host Authentication");
			mb.setMessage("Access to '" + host + "' is denied.\n" + "Verify the access granted/denied in the allowed hosts file.");
			mb.open();
		}
	}

	@Override
	public void onHostKeyMismatch(final String host, final SshPublicKey recorded, final SshPublicKey actual)
			throws TransportProtocolException {
		if (verificationEnabled) {
			parent.getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					OptionsDialog od = new OptionsDialog(parent, SWT.ICON_QUESTION);
					od.setText("Remote host authentication");
					od.setMessage("The host '" + host + "' has provided a different host key.\nThe host key " + "fingerprint provided is '"
							+ actual.getFingerprint() + "'.\n" + "The allowed host key fingerprint is " + recorded.getFingerprint() + ".\n"
							+ "Do you want to allow this host?");
					od.setOptions(getOptions());
					String result = od.open();

					try {
						// Handle the reply
						if (result.equals("Always")) {
							// Always allow the host with the new fingerprint
							allowHost(host, actual, true);
						}
						else if (result.equals("Yes")) {
							// Only allow the host this once
							allowHost(host, actual, false);
						}
					}
					catch (InvalidHostFileException e) {
						// showExceptionMessage(e);
						ExceptionHandler.reportException(e);
					}
				}
			});
		}
	}

	@Override
	public void onUnknownHost(final String host, final SshPublicKey key) throws TransportProtocolException {
		// Set up the users options. Only allow always if we can
		// write to the hosts file
		try {
			if (verificationEnabled) {
				parent.getDisplay().syncExec(new Runnable() {
					@Override
					public void run() {
						OptionsDialog od = new OptionsDialog(parent, SWT.ICON_QUESTION);
						od.setText("Remote host authentication");
						od.setMessage("The host '" + host + "' is unknown. The host key " + "fingerprint is\n'" + key.getFingerprint()
								+ "'.\n" + "Do you want to allow this host?");
						od.setOptions(getOptions());
						String result = od.open();

						try {
							// Handle the reply
							if (result.equals("Always")) {
								// Always allow the host with the new fingerprint
								allowHost(host, key, true);
							}
							else if (result.equals("Yes")) {
								// Only allow the host this once
								allowHost(host, key, false);
							}
						}
						catch (InvalidHostFileException e) {
							ExceptionHandler.reportException(e);
						}
					}
				});
			}
		}
		catch (Exception ex) {
			ExceptionHandler.reportException(ex);
		}
	}

	private String[] getOptions() {
		return isHostFileWriteable() ? new String[] { "Always", "Yes", "No" } : new String[] { "Yes", "No" };
	}

}
