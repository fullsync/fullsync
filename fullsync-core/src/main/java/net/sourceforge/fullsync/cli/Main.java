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
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Date;
import java.util.EventListener;
import java.util.List;
import java.util.Optional;
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
import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.ProfileManager;
import net.sourceforge.fullsync.RuntimeConfiguration;
import net.sourceforge.fullsync.Scheduler;
import net.sourceforge.fullsync.Synchronizer;
import net.sourceforge.fullsync.TaskTree;
import net.sourceforge.fullsync.Util;
import net.sourceforge.fullsync.event.ScheduledProfileExecution;
import net.sourceforge.fullsync.event.ShutdownEvent;
import net.sourceforge.fullsync.impl.FullSyncModule;

public class Main implements Launcher { // NO_UCD
	private static final Options options = new Options();
	@SuppressWarnings("unused")
	private static DaemonSchedulerListener daemonSchedulerListener;

	private static void initOptions() {
		options.addOption("h", "help", false, "this help"); //$NON-NLS-1$ //$NON-NLS-2$
		options.addOption("v", "verbose", false, "verbose output to stdout"); //$NON-NLS-1$ //$NON-NLS-2$
		options.addOption("V", "version", false, "display the version and exit"); //$NON-NLS-1$ //$NON-NLS-2$
		options.addOption("m", "minimized", false, "starts fullsync gui in system tray "); //$NON-NLS-1$ //$NON-NLS-2$

		Option profilesFile = Option.builder("P") //$NON-NLS-1$
			.longOpt("profiles-file") //$NON-NLS-1$
			.desc("uses the specified file instead of profiles.xml")
			.hasArg()
			.argName("filename") //$NON-NLS-1$
			.build();
		options.addOption(profilesFile);

		Option run = Option.builder("r") //$NON-NLS-1$
			.longOpt("run") //$NON-NLS-1$
			.desc("run the specified profile and then exit FullSync")
			.hasArg()
			.argName("profile") //$NON-NLS-1$
			.build();
		options.addOption(run);

		Option daemon = Option.builder("d") //$NON-NLS-1$
			.longOpt("daemon") //$NON-NLS-1$
			.desc("disables the gui and runs in daemon mode with scheduler")
			.hasArg(false)
			.build();
		options.addOption(daemon);
		// + interactive mode
	}

	private static void printHelp() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(85, "fullsync", "", options, "", true); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public static String getConfigDir() {
		String configDir = System.getProperty("net.sourceforge.fullsync.configDir"); //$NON-NLS-1$
		if (null == configDir) {
			configDir = System.getenv("XDG_CONFIG_HOME"); //$NON-NLS-1$
		}
		if (null == configDir) {
			configDir = System.getProperty("user.home") + File.separator + ".config"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		configDir = configDir + File.separator + "fullsync" + File.separator; //$NON-NLS-1$
		File dir = new File(configDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return configDir;
	}

	public static String getLogFileName() {
		return Paths.get(getConfigDir(), "fullsync.log").toFile().getAbsolutePath(); //$NON-NLS-1$
	}

	private static void backupFile(final File old, final File current, final String backupName) throws IOException {
		try (FileInputStream fis = new FileInputStream(old); FileOutputStream fos = new FileOutputStream(current)) {
			try (FileChannel in = fis.getChannel(); FileChannel out = fos.getChannel()) {
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
		String configDir = getConfigDir();
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
			System.out.println(String.format("FullSync version %s", Util.getFullSyncVersion())); //$NON-NLS-1$
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
		final String prefrencesFile = configDir + FullSync.PREFERENCES_PROPERTIES;
		final Injector injector = Guice.createInjector(new FullSyncModule(line, prefrencesFile));
		final RuntimeConfiguration rtConfig = injector.getInstance(RuntimeConfiguration.class);
		injector.getInstance(ProfileManager.class).setProfilesFileName(profilesFile);
		final ScheduledExecutorService scheduledExecutorService = injector.getInstance(ScheduledExecutorService.class);
		final EventListener deadEventListener = new EventListener() {
			private final Logger logger = LoggerFactory.getLogger("DeadEventLogger"); //$NON-NLS-1$

			@Subscribe
			private void onDeadEvent(DeadEvent deadEvent) {
				if (!(deadEvent.getEvent() instanceof ShutdownEvent)) {
					logger.warn("Dead event triggered: {}", deadEvent); //$NON-NLS-1$
				}
			}
		};
		final EventBus eventBus = injector.getInstance(EventBus.class);
		eventBus.register(deadEventListener);

		final Semaphore sem = new Semaphore(0);
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			Logger logger = LoggerFactory.getLogger(Main.class);
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
		if (rtConfig.isDaemon().orElse(false).booleanValue() || rtConfig.getProfileToRun().isPresent()) {
			finishStartup(injector);
			sem.acquireUninterruptibly();
			System.exit(0);
		}
		else {
			launcher.launchGui(injector);
			System.exit(0);
		}
	}

	private static void upgradeLegacyProfilesXmlLocation(String profilesFile) throws IOException {
		File newProfiles = new File(profilesFile);
		File oldProfiles = new File(FullSync.PROFILES_XML);
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
		File newPreferences = new File(configDir + FullSync.PREFERENCES_PROPERTIES);
		File oldPreferences = new File(FullSync.PREFERENCES_PROPERTIES);
		if (!newPreferences.exists() && oldPreferences.exists()) {
			backupFile(oldPreferences, newPreferences, "preferences_old.properties"); //$NON-NLS-1$
		}
	}

	public static void finishStartup(Injector injector) {
		RuntimeConfiguration rt = injector.getInstance(RuntimeConfiguration.class);
		Preferences preferences = injector.getInstance(Preferences.class);
		Scheduler scheduler = injector.getInstance(Scheduler.class);
		ProfileManager profileManager = injector.getInstance(ProfileManager.class);
		Synchronizer synchronizer = injector.getInstance(Synchronizer.class);
		Optional<String> profile = rt.getProfileToRun();
		profileManager.loadProfiles();
		if (profile.isPresent()) {
			handleRunProfile(synchronizer, profileManager, profile.get());
		}
		if (rt.isDaemon().orElse(false)) {
			daemonSchedulerListener = injector.getInstance(DaemonSchedulerListener.class);
			scheduler.start();
		}
		if (preferences.getAutostartScheduler()) {
			scheduler.start();
		}
	}

	private static void handleRunProfile(Synchronizer synchronizer, ProfileManager profileManager, String profileName) {
		Profile p = profileManager.getProfileByName(profileName);
		int errorlevel = 1;
		if (null != p) {
			TaskTree tree = synchronizer.executeProfile(p, false);
			errorlevel = synchronizer.performActions(tree);
			p.setLastUpdate(new Date());
			profileManager.save();
		}
		else {
			// FIXME: this should be on STDERR really... but that is "abused" as the log output.
			System.out.println(String.format("Error: The profile with the name %s couldn't be found.", profileName));
		}
		System.exit(errorlevel);
	}

	private static class DaemonSchedulerListener {
		private final Synchronizer synchronizer;

		@Inject
		public DaemonSchedulerListener(Synchronizer synchronizer) {
			this.synchronizer = synchronizer;
		}

		@Subscribe
		private void profileExecutionScheduled(ScheduledProfileExecution scheduledProfileExecution) {
			Profile profile = scheduledProfileExecution.getProfile();
			TaskTree tree = synchronizer.executeProfile(profile, false);
			if (null == tree) {
				profile.setLastError(1, "An error occured while comparing filesystems.");
			}
			else {
				int errorLevel = synchronizer.performActions(tree);
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
		String osName = System.getProperty("os.name").toLowerCase(); //$NON-NLS-1$
		String os = "unknown"; //$NON-NLS-1$
		if (-1 == System.getProperty("os.arch").indexOf("64")) { //$NON-NLS-1$ //$NON-NLS-2$
			throw new Exception("32 bit Operating Systems are not supported anymore!");
		}
		if (-1 != osName.indexOf("linux")) { //$NON-NLS-1$
			os = "gtk.linux"; //$NON-NLS-1$
		}
		else if (-1 != osName.indexOf("windows")) { //$NON-NLS-1$
			os = "win32.win32"; //$NON-NLS-1$
		}
		else if (-1 != osName.indexOf("mac")) { //$NON-NLS-1$
			os = "cocoa.macosx"; //$NON-NLS-1$
		}
		CodeSource cs = getClass().getProtectionDomain().getCodeSource();
		String libDirectory = cs.getLocation().toURI().toString().replaceAll("^(.*)/[^/]+\\.jar$", "$1/"); //$NON-NLS-1$ //$NON-NLS-2$

		List<URL> jars = new ArrayList<>();
		jars.add(new URL(libDirectory + "net.sourceforge.fullsync-fullsync-assets.jar")); //$NON-NLS-1$
		jars.add(new URL(libDirectory + "net.sourceforge.fullsync-fullsync-ui.jar")); //$NON-NLS-1$
		// add correct SWT implementation to the class-loader
		jars.add(new URL(libDirectory + String.format("org.eclipse.platform-org.eclipse.swt.%s.x86_64.jar", os))); //$NON-NLS-1$

		// instantiate an URL class-loader with the constructed class-path and load the UI
		URLClassLoader cl = new URLClassLoader(jars.toArray(new URL[jars.size()]), Main.class.getClassLoader());
		Thread.currentThread().setContextClassLoader(cl);
		Class<?> cls = cl.loadClass("net.sourceforge.fullsync.ui.GuiMain"); //$NON-NLS-1$
		Launcher guiMain = (Launcher) cls.getDeclaredConstructor(new Class<?>[] {}).newInstance();
		guiMain.launchGui(injector);
	}
}
