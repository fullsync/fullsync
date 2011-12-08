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
package net.sourceforge.fullsync.fs.filesystems;

import java.util.Arrays;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.FileSystemException;
import net.sourceforge.fullsync.fs.FileSystem;
import net.sourceforge.fullsync.fs.Site;
import net.sourceforge.fullsync.fs.connection.CommonsVfsConnection;
import net.sourceforge.fullsync.impl.SFTPLogger;
import net.sourceforge.fullsync.ui.GuiController;
import net.sourceforge.fullsync.ui.OptionsDialog;

import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

public class SFTPFileSystem implements FileSystem, UIKeyboardInteractive, UserInfo {
	private static boolean loggerSetupCompleted = false;

	private Logger logger = Logger.getLogger("FullSync");
	private ConnectionDescription desc = null;

	public SFTPFileSystem() {
		if (!loggerSetupCompleted) {
			JSch.setLogger(new SFTPLogger());
		}
	}

	@Override
	public final void authSetup(final ConnectionDescription description, final FileSystemOptions options) throws org.apache.commons.vfs2.FileSystemException {
		StaticUserAuthenticator auth = new StaticUserAuthenticator(null, description.getParameter("username"), description.getSecretParameter("password"));
		DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(options, auth);
		SftpFileSystemConfigBuilder cfg = SftpFileSystemConfigBuilder.getInstance();
		cfg.setUserInfo(options, this);
		cfg.setStrictHostKeyChecking(options, "ask");
		if ("enabled".equals(description.getParameter("publicKeyAuth"))) {
			cfg.setPreferredAuthentications(options, "publickey,password,keyboard-interactive");
		}
		else {
			cfg.setPreferredAuthentications(options, "password,keyboard-interactive");
		}
	}

	@Override
	public final Site createConnection(final ConnectionDescription description) throws FileSystemException {
		this.desc = description;
		return new CommonsVfsConnection(description, this);
	}

	@Override
	public final String getPassphrase() {
		logger.debug("SFTP UserInfo::getPassphrase");
		return desc.getSecretParameter("keyPassphrase");
	}

	@Override
	public final String getPassword() {
		logger.debug("SFTP UserInfo::getPassword");
		return desc.getSecretParameter("password");
	}

	@Override
	public final boolean promptPassword(final String message) {
		logger.debug("SFTP UserInfo::promptPassword: " + message);
		return true;
	}

	@Override
	public final boolean promptPassphrase(final String message) {
		logger.debug("SFTP UserInfo::promptPassphrase: " + message);
		return true;
	}

	@Override
	public final boolean promptYesNo(final String message) {
		final boolean[] arr = new boolean[] { false };
		if (null != desc.getParameter("interactive")) {
			GuiController.getInstance().getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					OptionsDialog od = new OptionsDialog(GuiController.getInstance().getMainShell(), SWT.ICON_QUESTION);
					od.setText("Question - FullSync"); //FIXME: translate
					od.setMessage(message); //FIXE: translate message
					od.setOptions(new String[] { "Yes", "No" }); //FIXME: translate
					if ("Yes".equals(od.open())) { //FIXME: translate
						arr[0] = true;
					}
				}
			});
		}
		else {
			logger.warn("SFTP UserInfo::promptYesNo: " + message + "; automatic decision: No");
		}
		return arr[0];
	}

	@Override
	public final void showMessage(final String message) {
		logger.warn("SFTP UserInfo::showMessage: " + message);
	}

	@Override
	public final String[] promptKeyboardInteractive(final String destination, final String name, final String instruction, final String[] prompt, final boolean[] echo) {
		logger.warn("Suppressed promptKeyboardInteractive:");
		logger.warn("Destination: " + destination);
		logger.warn("Name: " + name);
		logger.warn("Instruction: " + instruction);
		logger.warn("Prompt (#" + prompt.length + "): " + Arrays.toString(prompt));
		logger.warn("echo: (#" + echo.length + "): " + Arrays.toString(echo));
		logger.warn("rejecting prompt automatically");
		return null;
	}
}
