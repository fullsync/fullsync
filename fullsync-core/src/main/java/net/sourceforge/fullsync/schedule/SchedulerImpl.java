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

import java.util.concurrent.ScheduledExecutorService;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import net.sourceforge.fullsync.ScheduleTask;
import net.sourceforge.fullsync.ScheduleTaskSource;
import net.sourceforge.fullsync.Scheduler;
import net.sourceforge.fullsync.event.ProfileChanged;
import net.sourceforge.fullsync.event.ProfileListChanged;
import net.sourceforge.fullsync.event.SchedulerStatusChanged;

@Singleton
public class SchedulerImpl implements Scheduler, Runnable {
	private static final Logger logger = LoggerFactory.getLogger(SchedulerImpl.class);
	private final ScheduleTaskSource scheduleSource;
	private final ScheduledExecutorService scheduledExecutorService;
	private final EventBus eventBus;
	private Thread worker;
	private boolean running;
	private boolean enabled;

	@Inject
	public SchedulerImpl(ScheduleTaskSource source, ScheduledExecutorService scheduledExecutorService, EventBus eventBus) {
		scheduleSource = source;
		this.scheduledExecutorService = scheduledExecutorService;
		this.eventBus = eventBus;
	}

	private void fireSchedulerChangedEvent() {
		eventBus.post(new SchedulerStatusChanged(enabled));
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
				worker = new Thread(this, "Scheduler"); //$NON-NLS-1$
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

	private void refresh() {
		if (null != worker) {
			worker.interrupt();
		}
	}

	@Subscribe
	private void profileChanged(ProfileChanged profileChanged) {
		refresh();
	}

	@Subscribe
	private void profileListChanged(ProfileListChanged profileListChanged) {
		refresh();
	}

	@Override
	public void run() {
		running = true;
		while (enabled) {
			long now = System.currentTimeMillis();
			logger.debug("searching for next task after {}", now); //$NON-NLS-1$
			ScheduleTask task = scheduleSource.getNextScheduleTask(now);
			if (null == task) {
				logger.info("could not find a scheduled task, aborting"); //$NON-NLS-1$
				break;
			}
			logger.debug("found: {} at {}", task, task.getExecutionTime()); //$NON-NLS-1$

			long nextTime = task.getExecutionTime();
			try {
				logger.debug("waiting for {} microseconds", nextTime - now); //$NON-NLS-1$
				if (nextTime >= now) {
					Thread.sleep(nextTime - now);
				}
				logger.debug("Running task {}", task); //$NON-NLS-1$
				task.onBeforeExecution();
				scheduledExecutorService.submit(task);
			}
			catch (InterruptedException ex) {
				// expected during shutdown
			}
		}
		running = false;
		if (enabled) {
			enabled = false;
			fireSchedulerChangedEvent();
		}
	}
}
