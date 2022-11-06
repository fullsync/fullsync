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
package net.sourceforge.fullsync.filesystems;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.FullSync;
import net.sourceforge.fullsync.impl.SFTPLogger;

class SFTPAuthProvider implements FileSystemAuthProvider, UIKeyboardInteractive, UserInfo {
	private static final Logger logger = LoggerFactory.getLogger(SFTPAuthProvider.class.getSimpleName());

	static {
		JSch.setLogger(new SFTPLogger());
		var sshDirPath = System.getProperty("vfs.sftp.sshdir"); //$NON-NLS-1$
		if (null == sshDirPath) {
			sshDirPath = System.getProperty("user.home") + File.separator + ".ssh"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		var sshDir = new File(sshDirPath);
		if (!sshDir.exists() && !sshDir.mkdirs()) {
			logger.warn("failed to create the .ssh directory, remembering SSH keys likely won't work... tried: {}", sshDir.getAbsolutePath()); //$NON-NLS-1$
			sshDir = null;
		}
		if (null != sshDir) {
			System.setProperty("vfs.sftp.sshdir", sshDir.getAbsolutePath()); //$NON-NLS-1$
		}
	}
	private final FullSync fullsync;
	private final ConnectionDescription connectionDescription;
	private final boolean interactive;

	SFTPAuthProvider(final FullSync fullsync, final ConnectionDescription connectionDescription, boolean interactive) {
		this.fullsync = fullsync;
		this.connectionDescription = connectionDescription;
		this.interactive = interactive;
	}

	@Override
	public final void authSetup(final ConnectionDescription description, final FileSystemOptions options) throws FileSystemException {
		var username = description.getUsername().orElse(""); //$NON-NLS-1$
		var password = description.getPassword().orElse(""); //$NON-NLS-1$
		var auth = new StaticUserAuthenticator(null, username, password);
		DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(options, auth);
		var cfg = SftpFileSystemConfigBuilder.getInstance();
		cfg.setUserDirIsRoot(options, connectionDescription.isUserDirIsRoot());
		logger.debug("using knownHosts: {0}", cfg.getKnownHosts(options)); //$NON-NLS-1$
		cfg.setUserInfo(options, this);
		cfg.setStrictHostKeyChecking(options, "ask"); //$NON-NLS-1$
		if (description.getPublicKeyAuth().orElse(false)) {
			cfg.setPreferredAuthentications(options, "publickey,password,keyboard-interactive"); //$NON-NLS-1$
		}
		else {
			cfg.setPreferredAuthentications(options, "password,keyboard-interactive"); //$NON-NLS-1$
		}
	}

	@Override
	public final String getPassphrase() {
		logger.debug("UserInfo::getPassphrase"); //$NON-NLS-1$
		return connectionDescription.getKeyPassphrase().orElse(""); //$NON-NLS-1$
	}

	@Override
	public final String getPassword() {
		logger.debug("UserInfo::getPassword"); //$NON-NLS-1$
		return connectionDescription.getPassword().orElse(""); //$NON-NLS-1$
	}

	@Override
	public final boolean promptPassword(final String message) {
		logger.debug("UserInfo::promptPassword: {}", message); //$NON-NLS-1$
		return true;
	}

	@Override
	public final boolean promptPassphrase(final String message) {
		logger.debug("UserInfo::promptPassphrase: {}", message); //$NON-NLS-1$
		return true;
	}

	@Override
	public final boolean promptYesNo(final String message) {
		boolean result = false;
		if (interactive) {
			try {
				result = fullsync.getQuestionHandler().promptYesNo(message).get();
				logger.warn("UserInfo::promptYesNo: {}; decision: {}", message, result ? "Yes" : "No"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			catch (InterruptedException | ExecutionException ex) {
				logger.error("UserInfo::promptYesNo failed, user may not have received a prompt", ex); //$NON-NLS-1$
			}
		}
		else {
			logger.warn("UserInfo::promptYesNo: {}; automatic decision: No", message); //$NON-NLS-1$
		}
		return result;
	}

	@Override
	public final void showMessage(final String message) {
		logger.warn("UserInfo::showMessage: {}", message); //$NON-NLS-1$
	}

	@Override
	public final String[] promptKeyboardInteractive(final String destination, final String name, final String instruction,
		final String[] prompt, final boolean[] echo) {
		if (logger.isWarnEnabled()) {
			logger.warn("Suppressed promptKeyboardInteractive:"); //$NON-NLS-1$
			logger.warn("Destination: {}", destination); //$NON-NLS-1$
			logger.warn("Name: {}", name); //$NON-NLS-1$
			logger.warn("Instruction: {}", instruction); //$NON-NLS-1$
			logger.warn("Prompt (#{}): {}", prompt.length, Arrays.toString(prompt)); //$NON-NLS-1$
			logger.warn("echo: (#{}): {}", echo.length, Arrays.toString(echo)); //$NON-NLS-1$
			logger.warn("rejecting prompt automatically"); //$NON-NLS-1$
		}
		return null;
	}
}
