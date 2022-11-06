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
package net.sourceforge.fullsync.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.junit.jupiter.Testcontainers;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.Util;

@Testcontainers(disabledWithoutDocker = true)
public class SFTPFilesystemTest extends FilesystemTestBase {
	private final ImageFromDockerfile image = new ImageFromDockerfile().withFileFromPath(".", Paths.get("../containers/openssh-sshd"));
	private GenericContainer sftp;
	@TempDir
	public File sshConfigDir;

	@Override
	@BeforeEach
	public void setUpEach() throws Exception {
		super.setUpEach();
		var sshDir = new File(sshConfigDir, "ssh");
		assertTrue(sshDir.mkdirs(), "sshDir.mkdirs");
		System.setProperty("vfs.sftp.sshdir", sshDir.getAbsolutePath());
		sftp = new GenericContainer(image).withFileSystemBind(testingDst.getAbsolutePath(), "/home/SampleUser", BindMode.READ_WRITE)
			.withEnv("SFTP_USER_NAME", "SampleUser")
			.withEnv("SFTP_USER_PASS", "Sample")
			.withExposedPorts(22);
		sftp.start();
		try (var fis = new FileInputStream("../containers/openssh-sshd/known_hosts")) {
			var keyIds = Util.getInputStreamAsString(fis);
			var host = "[127.0.0.1]:" + sftp.getFirstMappedPort().toString();
			var knownHosts = keyIds.replaceAll("HOSTNAME_AND_PORT", host);
			createNewFileWithContents(sshDir, "known_hosts", -1, knownHosts);
		}
	}

	@Override
	@AfterEach
	public void tearDownEach() {
		System.clearProperty("vfs.sftp.sshdir");
		super.tearDownEach();
		sftp.stop();
	}

	@Override
	protected ConnectionDescription getDestinationConnectionDescription() {
		var dstBuilder = new ConnectionDescription.Builder();
		dstBuilder.setScheme("sftp");
		dstBuilder.setHost("127.0.0.1");
		dstBuilder.setPort(sftp.getFirstMappedPort());
		dstBuilder.setPath("/home/SampleUser");
		dstBuilder.setUsername("SampleUser");
		dstBuilder.setPassword("Sample");
		dstBuilder.setUserDirIsRoot(false);
		return dstBuilder.build();
	}

	@Override
	@Test
	public void testPublishUpdateNewFile() throws Exception {
		super.testPublishUpdateNewFile();
	}

	@Override
	@Test
	public void testPublishUpdateFolderStartingWithDash() throws Exception {
		super.testPublishUpdateFolderStartingWithDash();
	}

	@Override
	@Test
	public void testPublishUpdateIgnoresExistingFile() throws Exception {
		super.testPublishUpdateIgnoresExistingFile();
	}

	@Override
	@Test
	public void testPublishUpdateUpdatesModifiedFile() throws Exception {
		super.testPublishUpdateUpdatesModifiedFile();
	}
}
