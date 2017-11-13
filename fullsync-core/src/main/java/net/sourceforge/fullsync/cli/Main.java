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
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import net.sourceforge.fullsync.FullSync;
import net.sourceforge.fullsync.Launcher;
import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.ProfileManager;
import net.sourceforge.fullsync.RuntimeConfiguration;
import net.sourceforge.fullsync.Synchronizer;
import net.sourceforge.fullsync.TaskTree;
import net.sourceforge.fullsync.Util;
import net.sourceforge.fullsync.impl.ConfigurationPreferences;

public class Main implements Launcher { // NO_UCD
	private static final String PREFERENCES_PROPERTIES = "preferences.properties"; //$NON-NLS-1$
	private static final String PROFILES_XML = "profiles.xml"; //$NON-NLS-1$
	private static final Options options = new Options();

	private static void initOptions() {
		options.addOption("h", "help", false, "this help");
		options.addOption("v", "verbose", false, "verbose output to stdout");
		options.addOption("V", "version", false, "display the version and exit");
		options.addOption("m", "minimized", false, "starts fullsync gui in system tray ");

		Option profilesFile = Option.builder("P")
			.longOpt("profiles-file")
			.desc("uses the specified file instead of profiles.xml")
			.hasArg()
			.argName("filename")
			.build();
		options.addOption(profilesFile);

		Option run = Option.builder("r")
			.longOpt("run")
			.desc("run the specified profile and then exit FullSync")
			.hasArg()
			.argName("profile")
			.build();
		options.addOption(run);

		Option daemon = Option.builder("d")
			.longOpt("daemon")
			.desc("disables the gui and runs in daemon mode with scheduler")
			.hasArg(false)
			.build();
		options.addOption(daemon);

		// REVISIT somehow i don't like -p so much, but it's the standard for specifying ports.
		// the problem is that it implies "enable remote connection" in our case what
		// i consider more important, espec because the port is optional.
		Option port = Option.builder("p")
			.longOpt("remoteport")
			.desc("accept incoming connection on the specified port or 10000")
			.optionalArg(true)
			.argName("port")
			.build();
		options.addOption(port);

		Option password = Option.builder("a")
			.longOpt("password")
			.desc("password for incoming connections")
			.optionalArg(true)
			.argName("passwd")
			.build();
		options.addOption(password);
		// + interactive mode
	}

	private static void printHelp() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(85, "fullsync", "", options, "", true);
	}

	public static String getConfigDir() {
		String configDir = System.getProperty("net.sourceforge.fullsync.configDir");
		if (null == configDir) {
			configDir = System.getenv("XDG_CONFIG_HOME");
		}
		if (null == configDir) {
			configDir = System.getProperty("user.home") + File.separator + ".config";
		}
		configDir = configDir + File.separator + "fullsync" + File.separator;
		File dir = new File(configDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return configDir;
	}

	public static String getLogFileName() {
		return Paths.get(getConfigDir(), "fullsync.log").toFile().getAbsolutePath();
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
			System.out.println(String.format("FullSync version %s", Util.getFullSyncVersion()));
			System.exit(0);
		}

		// Apply modifying options
		if (!line.hasOption("v")) {
			System.setErr(new PrintStream(new FileOutputStream(getLogFileName())));
		}

		if (line.hasOption("h")) {
			printHelp();
			System.exit(0);
		}

		upgradeLegacyPreferencesLocation(configDir);

		String profilesFile;
		if (line.hasOption("P")) {
			profilesFile = line.getOptionValue("P");
		}
		else {
			profilesFile = configDir + PROFILES_XML;
			upgradeLegacyProfilesXmlLocation(profilesFile);
		}
		final ConfigurationPreferences preferences = new ConfigurationPreferences(configDir + PREFERENCES_PROPERTIES);
		final ProfileManager profileManager = new ProfileManager(profilesFile);
		final Synchronizer sync = new Synchronizer();
		final RuntimeConfiguration rtConfig = new CliRuntimeConfiguration(line);
		final FullSync fullsync = new FullSync(preferences, profileManager, sync, rtConfig);

		if (rtConfig.isDaemon().orElse(false).booleanValue()) {
			finishStartup(fullsync);
			//TODO: keep process running here
		}
		else {
			launcher.launchGui(fullsync);
		}
	}

	private static void upgradeLegacyProfilesXmlLocation(String profilesFile) throws IOException {
		File newProfiles = new File(profilesFile);
		File oldProfiles = new File(PROFILES_XML);
		if (!newProfiles.exists()) {
			if (!oldProfiles.exists()) {
				// on windows FullSync 0.9.1 installs itself into %ProgramFiles%\FullSync while 0.10.0 installs itself into %ProgramFiles%\FullSync\FullSync by default
				oldProfiles = new File(".." + File.separator + PROFILES_XML);
			}
			if (oldProfiles.exists()) {
				backupFile(oldProfiles, newProfiles, "profiles_old.xml");
			}
		}
	}

	private static void upgradeLegacyPreferencesLocation(String configDir) throws IOException {
		File newPreferences = new File(configDir + PREFERENCES_PROPERTIES);
		File oldPreferences = new File(PREFERENCES_PROPERTIES);
		if (!newPreferences.exists() && oldPreferences.exists()) {
			backupFile(oldPreferences, newPreferences, "preferences_old.properties");
		}
	}

	public static void finishStartup(FullSync fullsync) {
		RuntimeConfiguration rt = fullsync.getRuntimeConfiguration();
		Optional<String> profile = rt.getProfileToRun();
		if (profile.isPresent()) {
			handleRunProfile(fullsync, profile.get());
		}
		if (rt.isDaemon().orElse(false).booleanValue()) {
			handleIsDaemon(fullsync);
		}
		if (fullsync.getPreferences().getAutostartScheduler()) {
			fullsync.getProfileManager().startScheduler();
		}
	}

	private static void handleRunProfile(FullSync fullsync, String profileName) {
		ProfileManager profileManager = fullsync.getProfileManager();
		Profile p = profileManager.getProfile(profileName);
		int errorlevel = 1;
		if (null != p) {
			Synchronizer sync = fullsync.getSynchronizer();
			TaskTree tree = sync.executeProfile(fullsync, p, false);
			errorlevel = sync.performActions(tree);
			p.setLastUpdate(new Date());
			profileManager.save();
		}
		else {
			//FIXME: this should be on STDERR really... but that is "abused" as the log output.
			System.out.println(String.format("Error: The profile with the name %s couldn't be found.", profileName));
		}
		System.exit(errorlevel);
	}

	private static void handleIsDaemon(FullSync fullsync) {
		ProfileManager profileManager = fullsync.getProfileManager();
		profileManager.addSchedulerListener(profile -> {
			Synchronizer sync = fullsync.getSynchronizer();
			TaskTree tree = sync.executeProfile(fullsync, profile, false);
			if (null == tree) {
				profile.setLastError(1, "An error occured while comparing filesystems.");
			}
			else {
				int errorLevel = sync.performActions(tree);
				if (errorLevel > 0) {
					profile.setLastError(errorLevel, "An error occured while copying files.");
				}
				else {
					profile.setLastUpdate(new Date());
				}
			}
		});
		profileManager.startScheduler();
	}

	@Override
	public void launchGui(FullSync fullsync) throws Exception {
		String arch = "x86";
		String osName = System.getProperty("os.name").toLowerCase();
		String os = "unknown";
		if (-1 != System.getProperty("os.arch").indexOf("64")) {
			arch = "x86_64";
		}
		if (-1 != osName.indexOf("linux")) {
			os = "gtk.linux";
		}
		else if (-1 != osName.indexOf("windows")) {
			os = "win32.win32";
		}
		else if (-1 != osName.indexOf("mac")) {
			os = "cocoa.macosx";
		}
		CodeSource cs = getClass().getProtectionDomain().getCodeSource();
		String libDirectory = cs.getLocation().toURI().toString().replaceAll("^(.*)/[^/]+\\.jar$", "$1/");

		List<URL> jars = new ArrayList<>();
		jars.add(new URL(libDirectory + "net.sourceforge.fullsync-fullsync-assets.jar"));
		jars.add(new URL(libDirectory + "net.sourceforge.fullsync-fullsync-ui.jar"));
		// add correct SWT implementation to the class-loader
		jars.add(new URL(libDirectory + "org.eclipse.platform-org.eclipse.swt." + os + "." + arch + ".jar"));

		// instantiate an URL class-loader with the constructed class-path and load the UI
		URLClassLoader cl = new URLClassLoader(jars.toArray(new URL[jars.size()]), Main.class.getClassLoader());
		Thread.currentThread().setContextClassLoader(cl);
		Class<?> cls = cl.loadClass("net.sourceforge.fullsync.ui.GuiController");
		Method launchUI = cls.getDeclaredMethod("launchUI", FullSync.class);
		launchUI.invoke(null, fullsync);
	}
}
