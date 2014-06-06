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
package net.sourceforge.fullsync.schedule;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchedulerImpl implements Scheduler, Runnable {
	private Logger logger = LoggerFactory.getLogger(Scheduler.class);
	private ScheduleTaskSource scheduleSource;
	private Thread worker;
	private boolean running;
	private boolean enabled;

	private ArrayList<SchedulerChangeListener> schedulerListeners;

	public SchedulerImpl() {
		this(null);
	}

	public SchedulerImpl(ScheduleTaskSource source) {
		scheduleSource = source;
		schedulerListeners = new ArrayList<SchedulerChangeListener>();
	}

	@Override
	public void setSource(ScheduleTaskSource source) {
		scheduleSource = source;
	}

	@Override
	public ScheduleTaskSource getSource() {
		return scheduleSource;
	}

	@Override
	public void addSchedulerChangeListener(SchedulerChangeListener listener) {
		schedulerListeners.add(listener);
	}

	protected void fireSchedulerChangedEvent() {
		for (int i = 0; i < schedulerListeners.size(); i++) {
			(schedulerListeners.get(i)).schedulerStatusChanged(enabled);
		}
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void start() {
		if (enabled) {
			return;
		}

		enabled = true;
		if ((worker == null) || !worker.isAlive()) {
			worker = new Thread(this, "Scheduler");
			worker.setDaemon(true);
			worker.start();
		}
		fireSchedulerChangedEvent();
	}

	@Override
	public void stop() {
		if (!enabled || (worker == null)) {
			return;
		}

		enabled = false;
		if (running) {
			worker.interrupt();
		}
		try {
			worker.join();
		}
		catch (InterruptedException e) {
		}
		finally {
			worker = null;
		}
		fireSchedulerChangedEvent();
	}

	@Override
	public void refresh() {
		if (worker != null) {
			worker.interrupt();
		}
	}

	@Override
	public void run() {
		running = true;
		while (enabled) {
			long now = System.currentTimeMillis();
			if (logger.isDebugEnabled()) {
				logger.debug("searching for next task after " + now);
			}
			ScheduleTask task = scheduleSource.getNextScheduleTask();
			if (task == null) {
				logger.info("could not find a scheduled task, aborting");
				break;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("found: " + task.toString() + " at " + task.getExecutionTime());
			}

			long nextTime = task.getExecutionTime();
			try {
				if (logger.isDebugEnabled()) {
					logger.debug("waiting for " + (nextTime - now) + " mseconds");
				}
				if (nextTime >= now) {
					Thread.sleep(nextTime - now);
				}
				if (logger.isDebugEnabled()) {
					logger.debug("Running task " + task);
				}
				task.run();
			}
			catch (InterruptedException ie) {
			}

		}
		running = false;
		if (enabled) {
			enabled = false;
			fireSchedulerChangedEvent();
		}
	}
}
