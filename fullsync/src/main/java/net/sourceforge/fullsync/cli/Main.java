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
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.Date;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.FullSync;
import net.sourceforge.fullsync.Launcher;
import net.sourceforge.fullsync.Preferences;
import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.ProfileManager;
import net.sourceforge.fullsync.RuntimeConfiguration;
import net.sourceforge.fullsync.Synchronizer;
import net.sourceforge.fullsync.TaskTree;
import net.sourceforge.fullsync.Util;
import net.sourceforge.fullsync.impl.ConfigurationPreferences;
import net.sourceforge.fullsync.remote.RemoteController;

public class Main { // NO_UCD
	private static Options options;

	private static void initOptions() {
		options = new Options();
		options.addOption("h", "help", false, "this help");
		options.addOption("v", "verbose", false, "verbose output to stdout");
		options.addOption("V", "version", false, "display the version and exit");
		options.addOption("m", "minimized", false, "starts fullsync gui in system tray ");

		OptionBuilder.withLongOpt("profiles-file");
		OptionBuilder.withDescription("uses the specified file instead of profiles.xml");
		OptionBuilder.hasArg();
		OptionBuilder.withArgName("filename");
		options.addOption(OptionBuilder.create("P"));

		OptionBuilder.withLongOpt("run");
		OptionBuilder.withDescription("run the specified profile");
		OptionBuilder.hasArg();
		OptionBuilder.withArgName("profile");
		options.addOption(OptionBuilder.create("r"));

		options.addOption("d", "daemon", false, "disables the gui and runs in daemon mode with scheduler");

		// REVISIT somehow i don't like -p so much, but it's the standard for specifying ports.
		// the problem is that it implies "enable remote connection" in our case what
		// i consider more important, espec because the port is optional.
		OptionBuilder.withLongOpt("remoteport");
		OptionBuilder.withDescription("accept incoming connection on the specified port or 10000");
		OptionBuilder.hasOptionalArg();
		OptionBuilder.withArgName("port");
		options.addOption(OptionBuilder.create("p"));

		OptionBuilder.withLongOpt("password");
		OptionBuilder.withDescription("password for incoming connections");
		OptionBuilder.hasOptionalArg();
		OptionBuilder.withArgName("passwd");
		options.addOption(OptionBuilder.create("a"));
		// + interactive mode
	}

	private static void printHelp() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("fullsync [-hvrdp]", options);
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
			FileChannel in = fis.getChannel();
			FileChannel out = fos.getChannel();
			in.transferTo(0, in.size(), out);
			in.close();
			out.close();
			old.renameTo(new File(backupName));
		}
	}

	public static void startup(String[] args, Launcher launcher) throws Exception {
		initOptions();
		String configDir = getConfigDir();
		CommandLineParser parser = new PosixParser();
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

		// upgrade code...
		do {
			File newPreferences = new File(configDir + "preferences.properties");
			File oldPreferences = new File("preferences.properties");
			if (!newPreferences.exists() && oldPreferences.exists()) {
				backupFile(oldPreferences, newPreferences, "preferences_old.properties");
			}
		} while (false); // variable scope

		String profilesFile = "profiles.xml";
		if (line.hasOption("P")) {
			profilesFile = line.getOptionValue("P");
		}
		else {
			profilesFile = configDir + "profiles.xml";
			// upgrade code...
			File newProfiles = new File(profilesFile);
			File oldProfiles = new File("profiles.xml");
			if (!newProfiles.exists()) {
				if (!oldProfiles.exists()) {
					// on windows FullSync 0.9.1 installs itself into %ProgramFiles%\FullSync while 0.10.0 installs itself into %ProgramFiles%\FullSync\FullSync by default
					oldProfiles = new File(".." + File.separator + "profiles.xml");
				}
				if (oldProfiles.exists()) {
					backupFile(oldProfiles, newProfiles, "profiles_old.xml");
				}
			}
		}
		final ConfigurationPreferences preferences = new ConfigurationPreferences(configDir + "preferences.properties");
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

	public static void finishStartup(FullSync fullsync) {
		RuntimeConfiguration rt = fullsync.getRuntimeConfiguration();
		if (rt.getProfileToRun().isPresent()) {
			handleRunProfile(fullsync);
		}
		if (rt.isDaemon().orElse(false).booleanValue()) {
			handleIsDaemon(fullsync);
		}
		if (rt.getListenSocketAddress().isPresent() || fullsync.getPreferences().listeningForRemoteConnections()) {
			handleListening(fullsync);
		}
		if (fullsync.getPreferences().getAutostartScheduler()) {
			fullsync.getProfileManager().startScheduler();
		}
	}

	private static void handleRunProfile(FullSync fullsync) {
		ProfileManager profileManager = fullsync.getProfileManager();
		String profileName = fullsync.getRuntimeConfiguration().getProfileToRun().get();
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
			System.out.println(String.format("Error: The profile with the name {0} couldn't be found.", profileName));
		}
		System.exit(errorlevel);
	}

	private static void handleIsDaemon(FullSync fullsync) {
		ProfileManager profileManager = fullsync.getProfileManager();
		profileManager.addSchedulerListener(profile -> {
			Synchronizer sync = fullsync.getSynchronizer();
			TaskTree tree = sync.executeProfile(fullsync, profile, false);
			if (tree == null) {
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

	private static void handleListening(FullSync fullsync) {
		RuntimeConfiguration rt = fullsync.getRuntimeConfiguration();
		Preferences preferences = fullsync.getPreferences();
		int port = preferences.getRemoteConnectionsPort();
		Logger logger = LoggerFactory.getLogger("FullSync");
		InetSocketAddress listenAddress = rt.getListenSocketAddress().orElse(new InetSocketAddress(port));
		String password = rt.getRemotePassword().orElse(preferences.getRemoteConnectionsPassword());
		try {
			RemoteController.getInstance().startServer(listenAddress, password, fullsync);
			logger.info("Remote Interface available on: " + listenAddress);
		}
		catch (RemoteException e) {
			ExceptionHandler.reportException(e);
		}
	}
}
