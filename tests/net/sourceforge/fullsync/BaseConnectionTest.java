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
package net.sourceforge.fullsync;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.Hashtable;

import net.sourceforge.fullsync.impl.AdvancedRuleSetDescriptor;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

public class BaseConnectionTest {
	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();

	protected File testingDst;
	protected File testingSrc;
	protected Synchronizer synchronizer;
	protected Profile profile;

	@Before
	public void setUp() throws Exception {
		testingDst = tmpFolder.newFolder("destination");
		testingDst.mkdirs();
		testingSrc = tmpFolder.newFolder("source");
		testingSrc.mkdirs();

		synchronizer = new Synchronizer();
		profile = new Profile();
		profile.setName("TestProfile");
		ConnectionDescription src = new ConnectionDescription(testingSrc.toURI());
		src.setParameter("bufferStrategy", "");
		profile.setSource(src);

		profile.setRuleSet(new AdvancedRuleSetDescriptor("UPLOAD"));
		profile.setSynchronizationType("Publish/Update");
	}

	@After
	public void tearDown() throws Exception {
		tmpFolder.delete();
	}

	protected void createRuleFile() throws IOException {
		createNewFileWithContents(testingSrc, ".syncrules", new Date().getTime(), "START RULESET UPLOAD\n" + "	USE RULEFILES SOURCE\n"
				+ "	USE DIRECTION DESTINATION\n" + "	USE RECURSION YES\n" + "	USE RECURSIONONIGNORE YES\n" + "\n"
				+ "	APPLY IGNORERULES YES\n" + "	APPLY TAKERULES YES\n" + "	APPLY DELETION DESTINATION\n" + "\n"
				+ "	DEFINE IGNORE \"^[.].+\"\n" + "	DEFINE SYNC \"length != length\"\n" + "	DEFINE SYNC \"date != date\"\n"
				+ "END RULESET UPLOAD");
	}

	protected PrintStream createNewFile(File dir, String filename) throws IOException {
		File file = new File(dir, filename);
		file.getParentFile().mkdirs();
		file.createNewFile();
		PrintStream out = new PrintStream(new FileOutputStream(file));
		return out;
	}

	protected void createNewFileWithContents(File dir, String filename, long lm, String content) throws IOException {
		PrintStream out = createNewFile(dir, filename);
		out.print(content);
		out.close();

		new File(dir, filename).setLastModified(lm);
	}

	protected TaskTree assertPhaseOneActions(final Hashtable<String, Action> expectation) throws Exception {
		TaskGenerationListener list = new TaskGenerationListener() {
			@Override
			public void taskGenerationFinished(Task task) {
				Object ex = expectation.get(task.getSource().getName());
				assertNotNull("Unexpected generated Task for file: " + task.getSource().getName(), ex);
				assertTrue("Action was " + task.getCurrentAction() + ", expected: " + ex + " for File " + task.getSource().getName(), task
						.getCurrentAction().equalsExceptExplanation((Action) ex));
			}

			@Override
			public void taskGenerationStarted(net.sourceforge.fullsync.fs.File source, net.sourceforge.fullsync.fs.File destination) {
			}

			@Override
			public void taskTreeFinished(TaskTree tree) {
			}

			@Override
			public void taskTreeStarted(TaskTree tree) {
			}
		};

		TaskGenerator processor = synchronizer.getTaskGenerator();
		processor.addTaskGenerationListener(list);
		TaskTree tree = processor.execute(profile);
		processor.removeTaskGenerationListener(list);
		return tree;
	}
}
