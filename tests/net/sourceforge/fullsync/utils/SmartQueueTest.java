package net.sourceforge.fullsync.utils;

import net.sourceforge.fullsync.util.SmartQueue;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SmartQueueTest {
	private class ConsumerThread extends Thread {
		private int receivedObjects;
		@Override
		public void run() {
			receivedObjects = 0;
			Object o;
			do {
				o = queue.take();
				if (o != null) {
					receivedObjects++;
				}
			} while (o != null);
		}
		public int getReceivedObjects() {
			return receivedObjects;
		}
	}
	private SmartQueue<Object> queue;
	private ConsumerThread consumer1;
	private ConsumerThread consumer2;

	@Before
	public void setUp() {
		queue = new SmartQueue<Object>();
		consumer1 = new ConsumerThread();
		consumer2 = new ConsumerThread();
	}

	@After
	public void tearDown() throws InterruptedException {
		try {
			queue.shutdown();
		}
		catch (Exception ex) {
			/* ignore */
		}
		consumer1.join(1000);
		consumer2.join(1000);
	}

	@Test
	public void normalUsage() throws InterruptedException {
		int numObjects = 100000;
		consumer1.start();
		consumer2.start();
		for (int i = 0; i < numObjects; ++i) {
			queue.offer(this);
		}
		queue.shutdown();
		consumer1.join(6000);
		consumer2.join(6000);
		Assert.assertEquals(numObjects, consumer1.getReceivedObjects() + consumer2.getReceivedObjects());
		Assert.assertEquals(null, queue.take());
	}

	@Test
	public void testQueueLateConsumerConection() throws InterruptedException {
		int numObjects = 100000;
		for (int i = 0; i < numObjects; ++i) {
			queue.offer(this);
		}
		Assert.assertTrue(!queue.isEmpty());
		queue.shutdown();
		consumer1.start();
		consumer2.start();
		consumer1.join(6000);
		consumer2.join(6000);
		Assert.assertTrue(queue.isEmpty());
		Assert.assertEquals(numObjects, consumer1.getReceivedObjects() + consumer2.getReceivedObjects());
		Assert.assertEquals(null, queue.take());
	}

	@Test
	public void testQueueTakeBlocks() throws InterruptedException {
		int numObjects = 10;
		for (int i = 0; i < numObjects; ++i) {
			queue.offer(this);
		}
		consumer1.start();
		consumer1.join(500);
		Assert.assertTrue(consumer1.isAlive());
		Assert.assertTrue(!consumer2.isAlive());
		queue.shutdown();
		consumer1.join(500);
		Assert.assertTrue(!consumer1.isAlive());
		Assert.assertTrue(!consumer2.isAlive());
	}

	@Test
	public void testShutdownThrows() {
		IllegalStateException illegalState = null;
		queue.shutdown();
		try {
			queue.shutdown();
		}
		catch (IllegalStateException ex) {
			illegalState = ex;
		}
		Assert.assertNotEquals(null, illegalState);
	}

	@Test
	public void testOfferAfterShutdownThrows() {
		IllegalStateException illegalState = null;
		queue.shutdown();
		try {
			queue.offer(this);
		}
		catch (IllegalStateException ex) {
			illegalState = ex;
		}
		Assert.assertNotEquals(null, illegalState);
	}

	@Test
	public void testPauseQueue() throws InterruptedException {
		int numObjects = 10;
		consumer1.start();
		for (int i = 0; i < numObjects; ++i) {
			queue.offer(this);
		}
		consumer1.join(50);
		Assert.assertEquals(numObjects, consumer1.getReceivedObjects());
		queue.pause();
		for (int i = 0; i < numObjects; ++i) {
			queue.offer(this);
		}
		consumer1.join(50);
		Assert.assertEquals(numObjects, consumer1.getReceivedObjects());
		queue.resume();
		consumer1.join(50);
		Assert.assertEquals(numObjects * 2, consumer1.getReceivedObjects());
	}

	@Test
	public void testPausedQueueCancel() throws InterruptedException {
		int numObjects = 10;
		consumer1.start();
		for (int i = 0; i < numObjects; ++i) {
			queue.offer(this);
		}
		consumer1.join(50);
		Assert.assertEquals(numObjects, consumer1.getReceivedObjects());
		queue.pause();
		for (int i = 0; i < numObjects; ++i) {
			queue.offer(this);
		}
		consumer1.join(50);
		Assert.assertEquals(numObjects, consumer1.getReceivedObjects());
		queue.cancel();
		consumer1.join(50);
		Assert.assertEquals(numObjects, consumer1.getReceivedObjects());
	}

	@Test
	public void testOfferNullThrows() {
		IllegalArgumentException illegalArgument = null;
		try {
			queue.offer(null);
		}
		catch (IllegalArgumentException ex) {
			illegalArgument = ex;
		}
		Assert.assertNotEquals(null, illegalArgument);
	}
}
