package net.sourceforge.fullsync;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.Hashtable;

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
		System.setProperty("vfs.sftp.sshdir", new File("./sshd-config/").getAbsolutePath());
	}

	@Test
	public void testSingleInSync() throws Exception {
		createRuleFile();
		Date d = new Date();
		long lm = d.getTime();

		createNewFileWithContents(testingSrc, "sourceFile1.txt", lm, "this is a test\ncontent1");
		createNewFileWithContents(testingSrc, "sourceFile2.txt", lm, "this is a test\ncontent2");
		createNewFileWithContents(testingDst, "sourceFile1.txt", lm, "this is a test\ncontent1");

		Hashtable<String, Action> expectation = new Hashtable<String, Action>();
		expectation.put("sourceFile1.txt", new Action(Action.UnexpectedChangeError, Location.Destination, BufferUpdate.None, ""));
		expectation.put("sourceFile2.txt", new Action(Action.Add, Location.Destination, BufferUpdate.Destination, ""));
		// Phase One:
		TaskTree tree = assertPhaseOneActions(expectation);
		// Phase Three:
		synchronizer.performActions(tree); // TODO assert task finished events ?
	}

	@Test
	public void testSingleSpaceMinus() throws Exception {
		createRuleFile();
		long lm = new Date().getTime();

		new File(testingSrc, "sub - folder/sub2 - folder").mkdirs();
		createNewFileWithContents(testingSrc, "sub - folder/sub2 - folder/sourceFile1.txt", lm, "this is a test\ncontent1");
		createNewFileWithContents(testingSrc, "sub - folder/sourceFile2.txt", lm, "this is a test\ncontent2");

		Hashtable<String, Action> expectation = new Hashtable<String, Action>();
		expectation.put("sub - folder", new Action(Action.Add, Location.Destination, BufferUpdate.Destination, ""));
		expectation.put("sub2 - folder", new Action(Action.Add, Location.Destination, BufferUpdate.Destination, ""));
		expectation.put("sourceFile1.txt", new Action(Action.Add, Location.Destination, BufferUpdate.Destination, ""));
		expectation.put("sourceFile2.txt", new Action(Action.Add, Location.Destination, BufferUpdate.Destination, ""));
		// Phase One:
		TaskTree tree = assertPhaseOneActions(expectation);
		// Phase Three:
		synchronizer.performActions(tree); // TODO assert task finished events ?
	}
}
