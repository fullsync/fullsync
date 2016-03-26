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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class DebouncedHysteresisEmitter {
	private static int DELAY_END = -1;

	class EmitterThread extends Thread {
		private final DebouncedHysteresisEmitter target;

		EmitterThread(DebouncedHysteresisEmitter _target) {
			target = _target;
		}

		@Override
		public void run() {
			long delay = Integer.MAX_VALUE;
			for (;;) {
				try {
					Integer nextDelay = waitQueue.poll(delay, TimeUnit.MILLISECONDS);
					if (null != nextDelay) {
						delay = nextDelay.intValue();
						if (DELAY_END == delay) {
							return;
						}
					}
					else {
						synchronized (target) {
							if (isDown) {
								doGoUp();
							}
							else {
								doGoDown();
							}
						}
						delay = Integer.MAX_VALUE;
					}
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private final HysteresisReceiver receiver;
	private final Integer upDelay;
	private final Integer downDelay;
	private boolean isDown;
	private boolean upScheduled;
	private boolean downScheduled;
	private BlockingQueue<Integer> waitQueue;
	private final EmitterThread emitterThread;

	public DebouncedHysteresisEmitter(HysteresisReceiver _receiver, int _upDelayMs, int _downDelayMs) {
		receiver = _receiver;
		upDelay = Integer.valueOf(_upDelayMs);
		downDelay = Integer.valueOf(_downDelayMs);
		isDown = true;
		upScheduled = false;
		downScheduled = false;
		waitQueue = new LinkedBlockingQueue<Integer>();
		emitterThread = new EmitterThread(this);
		emitterThread.setDaemon(true);
		emitterThread.start();
	}

	public synchronized void up() {
		if (isDown && !upScheduled) {
			cancelInternal();
			upScheduled = true;
			waitQueue.add(upDelay);
		}
	}

	public synchronized void down() {
		if (!isDown && !downScheduled) {
			cancelInternal();
			downScheduled = true;
			waitQueue.add(downDelay);
		}
	}

	public synchronized void cancel() {
		cancelInternal();
		waitQueue.add(Integer.valueOf(DELAY_END));
	}

	private synchronized void cancelInternal() {
		upScheduled = false;
		downScheduled = false;
	}

	private synchronized void doGoUp() {
		if (isDown) {
			isDown = false;
			upScheduled = false;
			receiver.up();
		}
	}

	private synchronized void doGoDown() {
		if (!isDown) {
			isDown = true;
			downScheduled = false;
			receiver.down();
		}
	}
}
