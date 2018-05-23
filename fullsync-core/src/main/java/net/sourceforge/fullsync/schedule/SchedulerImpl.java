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
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class SchedulerImpl implements Scheduler, Runnable {
	private static final Logger logger = LoggerFactory.getLogger(SchedulerImpl.class);
	private ScheduleTaskSource scheduleSource;
	private final ScheduledExecutorService scheduledExecutorService;
	private Thread worker;
	private boolean running;
	private boolean enabled;

	private List<SchedulerChangeListener> schedulerListeners = new ArrayList<>();

	@Inject
	public SchedulerImpl(ScheduleTaskSource source, ScheduledExecutorService scheduledExecutorService) {
		scheduleSource = source;
		this.scheduledExecutorService = scheduledExecutorService;
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

	@Override
	public void removeSchedulerChangeListener(SchedulerChangeListener listener) {
		schedulerListeners.remove(listener);
	}

	protected void fireSchedulerChangedEvent() {
		for (SchedulerChangeListener listener : schedulerListeners) {
			listener.schedulerStatusChanged(enabled);
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
		if (!enabled) {
			enabled = true;
			if ((null == worker) || !worker.isAlive()) {
				worker = new Thread(this, "Scheduler");
				worker.setDaemon(true);
				worker.start();
			}
			fireSchedulerChangedEvent();
		}
	}

	@Override
	public void stop() {
		if (enabled && (null != worker)) {
			enabled = false;
			if (running) {
				worker.interrupt();
			}
			try {
				worker.join();
			}
			catch (InterruptedException ex) {
				ex.printStackTrace();
			}
			finally {
				worker = null;
			}
			fireSchedulerChangedEvent();
		}
	}

	@Override
	public void refresh() {
		if (null != worker) {
			worker.interrupt();
		}
	}

	@Override
	public void run() {
		running = true;
		while (enabled) {
			long now = System.currentTimeMillis();
			logger.debug("searching for next task after {}", now);
			ScheduleTask task = scheduleSource.getNextScheduleTask();
			if (null == task) {
				logger.info("could not find a scheduled task, aborting");
				break;
			}
			logger.debug("found: {} at {}", task, task.getExecutionTime());

			long nextTime = task.getExecutionTime();
			try {
				logger.debug("waiting for {} microseconds", nextTime - now);
				if (nextTime >= now) {
					Thread.sleep(nextTime - now);
				}
				logger.debug("Running task {}", task);
				scheduledExecutorService.submit(task);
			}
			catch (InterruptedException ex) {
				ex.printStackTrace();
			}

		}
		running = false;
		if (enabled) {
			enabled = false;
			fireSchedulerChangedEvent();
		}
	}
}
