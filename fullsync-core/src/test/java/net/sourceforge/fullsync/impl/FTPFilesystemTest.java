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

import java.nio.file.Paths;
import java.util.TimeZone;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.junit.jupiter.Testcontainers;

import net.sourceforge.fullsync.ConnectionDescription;

@Testcontainers(disabledWithoutDocker = true)
public class FTPFilesystemTest extends FilesystemTestBase {
	private static ImageFromDockerfile image;
	private GenericContainer ftp;

	@BeforeAll
	public static void setUpAll() {
		image = new ImageFromDockerfile().withFileFromPath(".", Paths.get("../containers/pure-ftpd"))
			.withBuildArg("TIMEZONE", TimeZone.getDefault().getID());
	}

	@Override
	@BeforeEach
	public void setUpEach() throws Exception {
		super.setUpEach();
		ftp = new GenericContainer(image).withFileSystemBind(testingDst.getAbsolutePath(), "/home/SampleUser", BindMode.READ_WRITE)
			.withEnv("FTP_USER_NAME", "SampleUser")
			.withEnv("FTP_USER_PASS", "Sample")
			.withEnv("FTP_USER_HOME", "/home/SampleUser")
			// .withEnv("PUBLICHOST", InetAddress.getLocalHost().getHostName())
			.withNetworkMode("host");
		ftp.start();
	}

	@Override
	@AfterEach
	public void tearDownEach() {
		super.tearDownEach();
		ftp.stop();
	}

	@Override
	protected ConnectionDescription getDestinationConnectionDescription() {
		ConnectionDescription.Builder dstBuilder = new ConnectionDescription.Builder();
		dstBuilder.setScheme("ftp");
		dstBuilder.setBufferStrategy("");
		dstBuilder.setHost("127.0.0.1");
		dstBuilder.setPort(21);
		dstBuilder.setPath("/");
		dstBuilder.setUsername("SampleUser");
		dstBuilder.setPassword("Sample");
		return dstBuilder.build();
	}

	@Override
	@Test
	public void testPublishUpdateNewFile() throws Exception {
		super.testPublishUpdateNewFile();
	}

	@Override
	@Test
	@Disabled("FTP in VFS2 won't play nice with 'LIST -something' and there is no way currently to get around this...")
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
