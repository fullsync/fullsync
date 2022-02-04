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
package net.sourceforge.fullsync.impl;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.apache.commons.cli.CommandLine;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;

import net.sourceforge.fullsync.FullSync;
import net.sourceforge.fullsync.Preferences;
import net.sourceforge.fullsync.ProfileManager;
import net.sourceforge.fullsync.RuntimeConfiguration;
import net.sourceforge.fullsync.TaskGenerator;
import net.sourceforge.fullsync.cli.CliRuntimeConfiguration;
import net.sourceforge.fullsync.schedule.Scheduler;
import net.sourceforge.fullsync.schedule.SchedulerImpl;

public class FullSyncModule extends AbstractModule {
	// 10 threads per core for a Quad Core + HT CPU?
	private static final int MAX_THREAD_POOL_SIZE = 80;
	private final CommandLine line;
	private final String prefrencesFile;
	private final ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(MAX_THREAD_POOL_SIZE);
	private final EventBus eventBus = new AsyncEventBus(scheduledExecutorService);

	public FullSyncModule(CommandLine line, String prefrencesFile) {
		this.line = line;
		this.prefrencesFile = prefrencesFile;
	}

	@Override
	protected void configure() {
		bind(String.class).annotatedWith(Names.named(FullSync.PREFERENCES_PROPERTIES)).toInstance(prefrencesFile);
		bind(TaskGenerator.class).to(TaskGeneratorImpl.class);
		bind(Preferences.class).to(ConfigurationPreferences.class);
		bind(RuntimeConfiguration.class).toInstance(new CliRuntimeConfiguration(line));
		bind(ProfileManager.class).to(XmlBackedProfileManager.class);
		bind(Scheduler.class).to(SchedulerImpl.class);
		bind(ScheduledExecutorService.class).toInstance(scheduledExecutorService);
		bind(EventBus.class).toInstance(eventBus);
		bindListener(Matchers.any(), this::hear);
	}

	private <I> void hear(TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {
		typeEncounter.register((InjectionListener<I>) eventBus::register);
	}
}
