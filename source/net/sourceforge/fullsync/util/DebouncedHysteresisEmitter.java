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

import java.util.Timer;
import java.util.TimerTask;

public class DebouncedHysteresisEmitter {
	private final HysteresisReceiver receiver;
	private final int upDelay;
	private final int downDelay;
	private final TimerTask goUpTask;
	private final TimerTask goDownTask;
	private Timer timer;
	private TimerTask allowedTask;
	private boolean isDown;

	public DebouncedHysteresisEmitter(HysteresisReceiver _receiver, int _upDelay, int _downDelay) {
		receiver = _receiver;
		upDelay = _upDelay;
		downDelay = _downDelay;
		goUpTask = new TimerTask() {
			@Override
			public void run() {
				doGoUp();
			}
		};
		goDownTask = new TimerTask() {
			@Override
			public void run() {
				doGoDown();
			}
		};
		timer = new Timer();
		allowedTask = null;
		isDown = true;
	}

	public synchronized void up() {
		if (isDown) {
			cancelInternal();
			allowedTask = goUpTask;
			timer.schedule(goUpTask, upDelay);
		}
	}

	public synchronized void down() {
		if (!isDown) {
			cancelInternal();
			allowedTask = goDownTask;
			timer.schedule(goDownTask, downDelay);
		}
	}

	public synchronized void cancel() {
		cancelInternal();
		timer.cancel();
		timer = new Timer();
	}

	private synchronized void cancelInternal() {
		if (null != allowedTask) {
			allowedTask.cancel();
		}
		allowedTask = null;
		timer.purge();
	}

	private synchronized void doGoUp() {
		if (allowedTask == goUpTask && isDown) {
			isDown = false;
			allowedTask = null;
			receiver.up();
		}
	}

	private synchronized void doGoDown() {
		if (allowedTask == goDownTask && !isDown) {
			isDown = true;
			allowedTask = null;
			receiver.down();
		}
	}
}
