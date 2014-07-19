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
package net.sourceforge.fullsync.pipeline;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import net.sourceforge.fullsync.ActionType;
import net.sourceforge.fullsync.Location;
import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.rules.filefilter.TestNode;
import net.sourceforge.fullsync.util.SmartQueue;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SyncActionGeneratorTest {
	File root = TestNode.createRoot(true, true, 0, 0);

	Thread sourceThread;
	Thread destinationThread;
	SmartQueue<File> sourceQueue;
	SmartQueue<File> destinationQueue;
	SyncActionGenerator generator;
	long now;

	protected long getLastModified() {
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date d = cal.getTime();
		return d.getTime();
	}

	@Before
	public void prepare() {
		sourceQueue = new SmartQueue<File>();
		destinationQueue = new SmartQueue<File>();
		TestNotificationTarget workNotificationTarget = new TestNotificationTarget();
		generator = new SyncActionGenerator(workNotificationTarget, sourceQueue, destinationQueue);
		sourceThread = new Thread(generator.getSourceTask());
		sourceThread.start();
		destinationThread = new Thread(generator.getDestinationTask());
		destinationThread.start();
		now = getLastModified();
	}

	@After
	public void teardown() throws InterruptedException {
		sourceQueue.cancel();
		destinationQueue.cancel();
		sourceThread.join();
		destinationThread.join();
	}

	protected void verifyDecisions(String[] srcFiles, String[] dstFiles, String[] tasks) {

	}

	@Test(timeout = 10000)
	public void testGeneratesTasks() {
		sourceQueue.offer(new TestNode("a", root, true, false, 5, now));
		sourceQueue.shutdown();
		destinationQueue.shutdown();
		Task t = generator.getOutput().take();
		Assert.assertNotNull(t);
		Assert.assertFalse(t.getCurrentAction().isError());
		Assert.assertTrue(t.getCurrentAction().getType() == ActionType.Add);
		Assert.assertTrue(t.getCurrentAction().getLocation() == Location.Destination);
		Assert.assertEquals("/a", t.getSource().getPath());
	}
}

class TestNotificationTarget implements TaskletWorkNotificationTarget {
	@Override
	public void startWork() {
	}

	@Override
	public void endWork() {
	}

	@Override
	public void syncEnded() {
	}
}