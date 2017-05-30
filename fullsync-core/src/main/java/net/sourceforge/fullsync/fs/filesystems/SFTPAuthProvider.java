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

import java.io.File;
import java.util.Arrays;

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
import net.sourceforge.fullsync.fs.FileSystemAuthProvider;
import net.sourceforge.fullsync.impl.SFTPLogger;

class SFTPAuthProvider implements FileSystemAuthProvider, UIKeyboardInteractive, UserInfo {
	private static final String sshDirName;
	private static final Logger logger = LoggerFactory.getLogger("FullSync");

	static {
		JSch.setLogger(new SFTPLogger());
		String sshDirPath = System.getProperty("vfs.sftp.sshdir");
		if (null == sshDirPath) {
			sshDirPath = System.getProperty("user.home") + File.separator + ".ssh";
		}
		File sshDir = new File(sshDirPath);
		if (!sshDir.exists() && !sshDir.mkdirs()) {
			String path = sshDir.getAbsolutePath();
			logger.warn("failed to create the .ssh directory, remembering SSH keys likely won't work... tried: " + path);
			sshDir = null;
			sshDirName = null;
		}
		else {
			sshDirName = sshDirPath;
		}
		if (null != sshDir) {
			System.setProperty("vfs.sftp.sshdir", sshDir.getAbsolutePath());
		}
	}

	private final FullSync fullsync;
	private final ConnectionDescription desc;

	SFTPAuthProvider(final FullSync _fullsync, final ConnectionDescription _desc) {
		fullsync = _fullsync;
		desc = _desc;
	}

	@Override
	public final void authSetup(final ConnectionDescription description, final FileSystemOptions options) throws FileSystemException {
		String username = description.getParameter(ConnectionDescription.PARAMETER_USERNAME);
		String password = description.getSecretParameter(ConnectionDescription.PARAMETER_PASSWORD);
		StaticUserAuthenticator auth = new StaticUserAuthenticator(null, username, password);
		DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(options, auth);
		SftpFileSystemConfigBuilder cfg = SftpFileSystemConfigBuilder.getInstance();
		//TODO: add cfg.setUserDirIsRoot(opts, false); and handle profile updates
		if (null != sshDirName) {
			cfg.setKnownHosts(options, new File(sshDirName, "known_hosts"));
		}
		logger.debug("SFTP using knownHosts: ", cfg.getKnownHosts(options));
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
	public final String getPassphrase() {
		logger.debug("SFTP UserInfo::getPassphrase");
		return desc.getSecretParameter("keyPassphrase");
	}

	@Override
	public final String getPassword() {
		logger.debug("SFTP UserInfo::getPassword");
		return desc.getSecretParameter(ConnectionDescription.PARAMETER_PASSWORD);
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
		if (null != desc.getParameter(ConnectionDescription.PARAMETER_INTERACTIVE)) {
			return fullsync.getQuestionHandler().promptYesNo(message);
		}
		else {
			logger.warn("SFTP UserInfo::promptYesNo: " + message + "; automatic decision: No");
		}
		return false;
	}

	@Override
	public final void showMessage(final String message) {
		logger.warn("SFTP UserInfo::showMessage: " + message);
	}

	@Override
	public final String[] promptKeyboardInteractive(final String destination, final String name, final String instruction,
		final String[] prompt, final boolean[] echo) {
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
