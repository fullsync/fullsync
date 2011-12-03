package net.sourceforge.fullsync;

import java.io.File;
import java.net.URI;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.mindtree.techworks.infix.pluginscommon.test.ssh.SSHServerResource;

public class SFTPConnectionTest extends BaseConnectionTest {
	@Rule
	public SSHServerResource sshServer = new SSHServerResource("SampleUser", 2222, "127.0.0.1");

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		ConnectionDescription dst = new ConnectionDescription(new URI("sftp://127.0.0.1:2222/"));
		dst.setParameter("bufferStrategy", "syncfiles");
		dst.setParameter("username", "SampleUser");
		dst.setSecretParameter("password", "SampleUser");
		profile.setDestination(dst);

		testingDst.delete();
		testingDst = sshServer.getUserHome();
		System.setProperty("vfs.sftp.sshdir", new File("./tests/sshd-config/").getAbsolutePath());
	}

	@Override
	@Test
	public void testSingleInSync() throws Exception {
		super.testSingleInSync();
	}

	@Override
	@Test
	public void testSingleSpaceMinus() throws Exception {
		super.testSingleSpaceMinus();
	}
}
