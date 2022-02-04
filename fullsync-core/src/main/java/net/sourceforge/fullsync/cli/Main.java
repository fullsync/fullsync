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
package net.sourceforge.fullsync.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.EventListener;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Guice;
import com.google.inject.Injector;

import net.sourceforge.fullsync.FullSync;
import net.sourceforge.fullsync.Launcher;
import net.sourceforge.fullsync.Preferences;
import net.sourceforge.fullsync.ProfileManager;
import net.sourceforge.fullsync.RuntimeConfiguration;
import net.sourceforge.fullsync.Synchronizer;
import net.sourceforge.fullsync.Util;
import net.sourceforge.fullsync.event.ScheduledProfileExecution;
import net.sourceforge.fullsync.event.ShutdownEvent;
import net.sourceforge.fullsync.impl.FullSyncModule;
import net.sourceforge.fullsync.schedule.Scheduler;

public class Main implements Launcher { // NO_UCD
	private static final Options options = new Options();
	@SuppressWarnings("unused")
	private static DaemonSchedulerListener daemonSchedulerListener;

	private static void initOptions() {
		options.addOption("h", "help", false, "this help"); //$NON-NLS-1$ //$NON-NLS-2$
		options.addOption("v", "verbose", false, "verbose output to stdout"); //$NON-NLS-1$ //$NON-NLS-2$
		options.addOption("V", "version", false, "display the version and exit"); //$NON-NLS-1$ //$NON-NLS-2$
		options.addOption("m", "minimized", false, "starts fullsync gui in system tray "); //$NON-NLS-1$ //$NON-NLS-2$

		var profilesFile = Option.builder("P") //$NON-NLS-1$
			.longOpt("profiles-file") //$NON-NLS-1$
			.desc("uses the specified file instead of profiles.xml")
			.hasArg()
			.argName("filename") //$NON-NLS-1$
			.build();
		options.addOption(profilesFile);

		var run = Option.builder("r") //$NON-NLS-1$
			.longOpt("run") //$NON-NLS-1$
			.desc("run the specified profile and then exit FullSync")
			.hasArg()
			.argName("profile") //$NON-NLS-1$
			.build();
		options.addOption(run);

		var daemon = Option.builder("d") //$NON-NLS-1$
			.longOpt("daemon") //$NON-NLS-1$
			.desc("disables the gui and runs in daemon mode with scheduler")
			.hasArg(false)
			.build();
		options.addOption(daemon);
		// + interactive mode
	}

	private static void printHelp() {
		var formatter = new HelpFormatter();
		formatter.printHelp(85, "fullsync", "", options, "", true); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public static String getConfigDir() {
		var configDir = System.getProperty("net.sourceforge.fullsync.configDir"); //$NON-NLS-1$
		if (null == configDir) {
			configDir = System.getenv("XDG_CONFIG_HOME"); //$NON-NLS-1$
		}
		if (null == configDir) {
			configDir = System.getProperty("user.home") + File.separator + ".config"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		configDir = configDir + File.separator + "fullsync" + File.separator; //$NON-NLS-1$
		var dir = new File(configDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return configDir;
	}

	public static String getLogFileName() {
		return Paths.get(getConfigDir(), "fullsync.log").toFile().getAbsolutePath(); //$NON-NLS-1$
	}

	private static void backupFile(final File old, final File current, final String backupName) throws IOException {
		try (var fis = new FileInputStream(old); var fos = new FileOutputStream(current)) {
			try (var in = fis.getChannel(); var out = fos.getChannel()) {
				in.transferTo(0, in.size(), out);
			}
			old.renameTo(new File(backupName));
		}
	}

	public static void main(final String[] args) throws Exception {
		// TODO: redirect stdout && stderr here!
		startup(args, new Main());
	}

	public static void startup(String[] args, Launcher launcher) throws Exception {
		initOptions();
		var configDir = getConfigDir();
		CommandLineParser parser = new DefaultParser();
		CommandLine line = null;

		try {
			line = parser.parse(options, args);
		}
		catch (ParseException ex) {
			System.err.println(ex.getMessage());
			printHelp();
			System.exit(1);
		}

		if (line.hasOption('V')) {
			System.out.printf("FullSync version %s%n", Util.getFullSyncVersion()); //$NON-NLS-1$
			System.exit(0);
		}

		// Apply modifying options
		if (!line.hasOption("v")) { //$NON-NLS-1$
			System.setErr(new PrintStream(new FileOutputStream(getLogFileName())));
		}

		if (line.hasOption("h")) { //$NON-NLS-1$
			printHelp();
			System.exit(0);
		}

		upgradeLegacyPreferencesLocation(configDir);

		String profilesFile;
		if (line.hasOption("P")) { //$NON-NLS-1$
			profilesFile = line.getOptionValue("P"); //$NON-NLS-1$
		}
		else {
			profilesFile = configDir + FullSync.PROFILES_XML;
			upgradeLegacyProfilesXmlLocation(profilesFile);
		}
		final var prefrencesFile = configDir + FullSync.PREFERENCES_PROPERTIES;
		final var injector = Guice.createInjector(new FullSyncModule(line, prefrencesFile));
		final var rtConfig = injector.getInstance(RuntimeConfiguration.class);
		injector.getInstance(ProfileManager.class).setProfilesFileName(profilesFile);
		final var scheduledExecutorService = injector.getInstance(ScheduledExecutorService.class);
		final EventListener deadEventListener = new EventListener() {
			private final Logger logger = LoggerFactory.getLogger("DeadEventLogger"); //$NON-NLS-1$

			@Subscribe
			private void onDeadEvent(DeadEvent deadEvent) {
				if (!(deadEvent.getEvent() instanceof ShutdownEvent)) {
					logger.warn("Dead event triggered: {}", deadEvent); //$NON-NLS-1$
				}
			}
		};
		final var eventBus = injector.getInstance(EventBus.class);
		eventBus.register(deadEventListener);

		final var sem = new Semaphore(0);
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			var logger = LoggerFactory.getLogger(Main.class);
			logger.debug("shutdown hook called, starting orderly shutdown"); //$NON-NLS-1$
			eventBus.post(new ShutdownEvent());
			scheduledExecutorService.shutdown();
			try {
				scheduledExecutorService.awaitTermination(5, TimeUnit.MINUTES);
			}
			catch (InterruptedException e) {
				// not relevant
			}
			logger.debug("shutdown hook finished, releasing main thread semaphore"); //$NON-NLS-1$
			sem.release();
		}));
		if (rtConfig.isDaemon().orElse(false) || rtConfig.getProfileToRun().isPresent()) {
			finishStartup(injector);
			sem.acquireUninterruptibly();
		}
		else {
			launcher.launchGui(injector);
		}
		System.exit(0);
	}

	private static void upgradeLegacyProfilesXmlLocation(String profilesFile) throws IOException {
		var newProfiles = new File(profilesFile);
		var oldProfiles = new File(FullSync.PROFILES_XML);
		if (!newProfiles.exists()) {
			if (!oldProfiles.exists()) {
				// on windows FullSync 0.9.1 installs itself into %ProgramFiles%\FullSync while 0.10.0 installs itself into
				// %ProgramFiles%\FullSync\FullSync by default
				oldProfiles = new File(".." + File.separator + FullSync.PROFILES_XML); //$NON-NLS-1$
			}
			if (oldProfiles.exists()) {
				backupFile(oldProfiles, newProfiles, "profiles_old.xml"); //$NON-NLS-1$
			}
		}
	}

	private static void upgradeLegacyPreferencesLocation(String configDir) throws IOException {
		var newPreferences = new File(configDir + FullSync.PREFERENCES_PROPERTIES);
		var oldPreferences = new File(FullSync.PREFERENCES_PROPERTIES);
		if (!newPreferences.exists() && oldPreferences.exists()) {
			backupFile(oldPreferences, newPreferences, "preferences_old.properties"); //$NON-NLS-1$
		}
	}

	public static void finishStartup(Injector injector) {
		var rt = injector.getInstance(RuntimeConfiguration.class);
		var preferences = injector.getInstance(Preferences.class);
		var scheduler = injector.getInstance(Scheduler.class);
		var profileManager = injector.getInstance(ProfileManager.class);
		var synchronizer = injector.getInstance(Synchronizer.class);
		var profile = rt.getProfileToRun();
		profileManager.loadProfiles();

		profile.ifPresent(s -> handleRunProfile(synchronizer, profileManager, s));

		if (rt.isDaemon().orElse(false)) {
			daemonSchedulerListener = injector.getInstance(DaemonSchedulerListener.class);
			scheduler.start();
		}
		if (preferences.getAutostartScheduler()) {
			scheduler.start();
		}
	}

	private static void handleRunProfile(Synchronizer synchronizer, ProfileManager profileManager, String profileName) {
		var p = profileManager.getProfileByName(profileName);
		var errorlevel = 1;
		if (null != p) {
			var tree = synchronizer.executeProfile(p, false);
			errorlevel = synchronizer.performActions(tree);
			p.setLastUpdate(new Date());
			profileManager.save();
		}
		else {
			// FIXME: this should be on STDERR really... but that is "abused" as the log output.
			System.out.printf("Error: The profile with the name %s couldn't be found.%n", profileName);
		}
		System.exit(errorlevel);
	}

	private record DaemonSchedulerListener(Synchronizer synchronizer) {
		@Inject
		private DaemonSchedulerListener {
		}

		@Subscribe
		private void profileExecutionScheduled(ScheduledProfileExecution scheduledProfileExecution) {
			var profile = scheduledProfileExecution.profile();
			var tree = synchronizer.executeProfile(profile, false);
			if (null == tree) {
				profile.setLastError(1, "An error occured while comparing filesystems.");
			}
			else {
				var errorLevel = synchronizer.performActions(tree);
				if (errorLevel > 0) {
					profile.setLastError(errorLevel, "An error occured while copying files.");
				}
				else {
					profile.setLastUpdate(new Date());
				}
			}
		}
	}

	@Override
	public void launchGui(Injector injector) throws Exception {
		var osName = System.getProperty("os.name").toLowerCase(); //$NON-NLS-1$
		var os = "unknown"; //$NON-NLS-1$
		if (!System.getProperty("os.arch").contains("64")) { //$NON-NLS-1$ //$NON-NLS-2$
			throw new Exception("32 bit Operating Systems are not supported anymore!");
		}
		if (osName.contains("linux")) { //$NON-NLS-1$
			os = "gtk.linux"; //$NON-NLS-1$
		}
		else if (osName.contains("windows")) { //$NON-NLS-1$
			os = "win32.win32"; //$NON-NLS-1$
		}
		else if (osName.contains("mac")) { //$NON-NLS-1$
			os = "cocoa.macosx"; //$NON-NLS-1$
		}
		var cs = getClass().getProtectionDomain().getCodeSource();
		var libDirectory = cs.getLocation().toURI().toString().replaceAll("^(.*)/[^/]+\\.jar$", "$1/"); //$NON-NLS-1$ //$NON-NLS-2$

		List<URL> jars = new ArrayList<>();
		jars.add(new URL(libDirectory + "net.sourceforge.fullsync-fullsync-assets.jar")); //$NON-NLS-1$
		jars.add(new URL(libDirectory + "net.sourceforge.fullsync-fullsync-ui.jar")); //$NON-NLS-1$
		// add correct SWT implementation to the class-loader
		jars.add(new URL(libDirectory + String.format("org.eclipse.platform-org.eclipse.swt.%s.x86_64.jar", os))); //$NON-NLS-1$

		// instantiate an URL class-loader with the constructed class-path and load the UI
		var cl = new URLClassLoader(jars.toArray(new URL[0]), Main.class.getClassLoader());
		Thread.currentThread().setContextClassLoader(cl);
		Class<?> cls = cl.loadClass("net.sourceforge.fullsync.ui.GuiMain"); //$NON-NLS-1$
		var guiMain = (Launcher) cls.getDeclaredConstructor().newInstance();
		guiMain.launchGui(injector);
	}
}
