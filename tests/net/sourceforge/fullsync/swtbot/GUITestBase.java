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

package net.sourceforge.fullsync.swtbot;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.junit.After;
import org.junit.jupiter.api.BeforeAll;
import org.junit.rules.TemporaryFolder;

import net.sourceforge.fullsync.cli.Main;

public abstract class GUITestBase {
	private static final long GUI_STARTUP_TIMEOUT = 7000;

	protected TemporaryFolder tempConfigDir;
	protected SWTBot bot;

	private Thread applicationThread;

	@BeforeEach
	public void setUpBefore() throws Exception {
		tempConfigDir = new TemporaryFolder();
		tempConfigDir.create();
		System.setProperty("net.sourceforge.fullsync.configDir", tempConfigDir.getRoot().toString());
		System.setProperty("net.sourceforge.fullsync.skipExit", "true");
		System.setProperty("net.sourceforge.fullsync.skipHelp", "true");
		System.setProperty("net.sourceforge.fullsync.skipWelcomeScreen", "true");

		applicationThread = new Thread(() -> Main.main(new String[] { "-v" }));
		applicationThread.setName("FullSync GUI");
		applicationThread.start();
		assertTrue("GUI startup failed, Display not found", null != waitForDisplayToAppear(GUI_STARTUP_TIMEOUT));
		bot = new SWTBot();
	}

	@After
	public void tearDownAfter() throws Exception {
		final Display d = Display.findDisplay(applicationThread);
		if ((null != d) && !d.isDisposed()) {
			d.syncExec(() -> d.close());
		}
		applicationThread.join();

		try {
			verifyAfterGUIStopped();
		}
		finally {
			tempConfigDir.delete();
		}
	}

	protected abstract void verifyAfterGUIStopped() throws Exception;

	private Display waitForDisplayToAppear(final long timeOut) {
		long endTime = System.currentTimeMillis() + timeOut;
		Display display;
		Thread[] threads = new Thread[Thread.activeCount()];
		Thread.enumerate(threads);
		while (System.currentTimeMillis() < endTime) {
			for (Thread thread : threads) {
				display = Display.findDisplay(thread);
				if (null != display) {
					return display;
				}
			}
		}
		return null;
	}

	protected void assertConfigFileExists(final String name) {
		File configFolder = new File(tempConfigDir.getRoot(), "fullsync");
		assertTrue(configFolder + File.separator + name + " missing", new File(configFolder, name).exists());
	}
}
