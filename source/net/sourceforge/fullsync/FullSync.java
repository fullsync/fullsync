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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import net.sourceforge.fullsync.impl.ConfigurationPreferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adamtaft.eb.EventBus;
import com.adamtaft.eb.EventBusService;
import com.adamtaft.eb.EventHandler;

public class FullSync {
	private static FullSync instance;

	public static FullSync inst() {
		if (!instance.initialized) {
			synchronized (instance) {
				if (!instance.initialized) {
					try {
						instance.wait();
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return instance;
	}

	private Preferences preferences;
	private EventBus eventBus;
	private FileSystemManager fileSystemManager;
	private ExecutorService executorService;
	private volatile boolean initialized;
	private FullSyncEventTracer tracer;

	private FullSync(ConfigurationPreferences _preferences) {
		instance = this;
		preferences = _preferences;
		initialized = false;
		Thread fullsyncInitializer = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					eventBus = EventBusService.getInstance();
					fileSystemManager = new FileSystemManager();
					executorService = Executors.newCachedThreadPool(new ThreadFactory() {
						private final ThreadFactory delegate = Executors.defaultThreadFactory();
						@Override
						public Thread newThread(Runnable r) {
							Thread t = delegate.newThread(r);
							t.setDaemon(true);
							return t;
						}
					});
				}
				finally {
					synchronized (instance) {
						initialized = true;
						instance.notifyAll();
					}
					tracer = new FullSyncEventTracer();
					FullSync.subscribe(tracer);
				}
			}
		});
		fullsyncInitializer.setDaemon(true);
		fullsyncInitializer.start();
	}

	public static void init(ConfigurationPreferences _preferences) {
		instance = new FullSync(_preferences);
	}

	public static Preferences prefs() {
		return inst().preferences;
	}

	public static void subscribe(Object subscriber) {
		inst().eventBus.subscribe(subscriber);
	}

	public static void unsubscribe(Object subscriber) {
		inst().eventBus.unsubscribe(subscriber);
	}

	public static void publish(Object event) {
		inst().eventBus.publish(event);
	}

	public static boolean hasPendingEvents() {
		return inst().eventBus.hasPendingEvents();
	}

	public static FileSystemManager getFileSystemManager() {
		return inst().fileSystemManager;
	}

	public static void submit(final Runnable r) {
		inst().executorService.submit(r);
	}
}

class FullSyncEventTracer {
	private Logger logger;
	public FullSyncEventTracer() {
		logger = LoggerFactory.getLogger("FullSync");
	}
	@EventHandler
	public void onEvent(final Object obj) {
		logger.debug("Event: {} {}", obj.getClass(), obj);
	}
}
