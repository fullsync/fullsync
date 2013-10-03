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
package net.sourceforge.fullsync.util;

import java.util.LinkedList;

/**
 * A simple blocking queue that can be shutdown.
 *
 * @param <E> the type of the elements held in this queue.
 */
public class SmartQueue<E> {
	private LinkedList<E> queue;
	private volatile boolean shutdown;
	private volatile boolean paused;

	/**
	 * constructs a new SmartQueue.
	 */
	public SmartQueue() {
		queue = new LinkedList<E>();
		shutdown = false;
		paused = false;
	}

	/**
	 * marks this Queue as depleted:
	 * * no new items can be added
	 * * all existing items can still be retrieved
	 * * all waiting threads will be notified.
	 * Throws IllegalStateException if shutdown() has already been called.
	 */
	public synchronized void shutdown() {
		if (!shutdown) {
			shutdown = true;
			notifyAll();
		}
		else {
			throw new IllegalStateException();
		}
	}

	/**
	 * Pause this queue, callers to take() will block until the queue becomes resumed.
	 */
	public synchronized void pause() {
		paused = true;
	}

	/**
	 * Resume the queue, wakes up all sleeping consumers.
	 */
	public synchronized void resume() {
		paused = false;
		notifyAll();
	}

	/**
	 * Add an item to the end of the queue.
	 * @param item to add
	 * Throws IllegalStateException if the queue has been shutdown.
	 */
	public synchronized void offer(final E item) {
		if (!shutdown) {
			queue.offer(item);
			if (!paused) {
				notify();
			}
		}
		else {
			throw new IllegalStateException();
		}
	}

	/**
	 * Retrieve an item from the queue, or null if the queue has been depleted.
	 * This method will block until an item is available or the queue is shutdown.
	 * @return item or null (null = End Of Queue)
	 */
	public synchronized E take() {
		E item = null;
		while (!queue.isEmpty() || !shutdown) {
			if (paused) {
				doWait();
			}
			else {
				item = queue.poll();
				if (null == item) {
					doWait();
				}
				else {
					break;
				}
			}
		}
		return item;
	}

	private void doWait() {
		try {
			wait();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public synchronized boolean isEmpty() {
		return queue.isEmpty();
	}

	/**
	 * Similar to Shutdown, but it also clears the queue and removes any remaining items.
	 */
	public synchronized void cancel() {
		shutdown = true;
		queue.clear();
		notifyAll();
	}
}
