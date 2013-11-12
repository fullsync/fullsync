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
import java.nio.channels.FileChannel;

import net.sourceforge.fullsync.interfaces.Constants;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

public class Main implements BundleActivator {
	@Override
	public void start(BundleContext context) throws Exception {
		ServiceReference<ConfigurationAdmin> configAdminRef;
		ConfigurationAdmin configAdmin;

		configAdminRef = context.getServiceReference(ConfigurationAdmin.class);
		configAdmin = context.getService(configAdminRef);

		Configuration remoteServerConfig = configAdmin.getConfiguration("");
		Configuration profileManagerConfig = configAdmin.getConfiguration(Constants.PROFILE_MANAGER);

		String argsString = System.getProperty("eclipse.commands", "");
		String[] args = argsString.split("/\r?\n/");
		Options options = initOptions();
		try {
			CommandLineParser parser = new PosixParser();
			CommandLine line = null;

			try {
				line = parser.parse(options, args);
			}
			catch (ParseException pe) {
				System.err.println(pe.getMessage());
				printHelp(options);
				return;
			}

			if (line.hasOption("h")) {
				printHelp(options);
				return;
			}

			String configDir = getConfigDir();
			// Apply modifying options
			if (!line.hasOption("v")) {
				System.setErr(new PrintStream(new FileOutputStream(configDir + "fullsync.log")));
			}

			// Initialize basic facilities
			// upgrade code...
			migreateOldPreferences(configDir);
			String profilesFile = "profiles.xml";
			if (line.hasOption("P")) {
				profilesFile = line.getOptionValue("P");
			}
			else {
				profilesFile = configDir + "profiles.xml";
				// upgrade code...
				File newProfiles = new File(profilesFile);
				if (!newProfiles.exists()) {
					File oldProfiles = new File("profiles.xml");
					if (!oldProfiles.exists()) {
						// on windows FullSync 0.9.1 installs itself into %ProgramFiles%\FullSync while 0.10.0 installs itself into %ProgramFiles%\FullSync\FullSync by default
						oldProfiles = new File(".." + File.separator + "profiles.xml");
					}
					if (oldProfiles.exists()) {
						backupFile(oldProfiles, newProfiles, "profiles_old.xml");
					}
				}
			}
			profileManagerConfig.getProperties().put(Constants.PROFILE_FILENAME, profilesFile);
			/*
			ProfileManager profileManager = new ProfileManager(profilesFile);

			final Synchronizer sync = new Synchronizer();

			// Apply executing options
			if (line.hasOption("r")) {
				Profile p = profileManager.getProfile(line.getOptionValue("r"));
				TaskTree tree = sync.executeProfile(p, false);
				sync.performActions(tree);
				p.setLastUpdate(new Date());
				profileManager.save();
				return;
			}

			boolean activateRemote = false;
			int port = 10000;
			String password = "admin";
			RemoteException listenerStarupException = null;

			final ConfigurationPreferences preferences = new ConfigurationPreferences(configDir + "preferences.properties");

			if (line.hasOption("p")) {
				activateRemote = true;
				try {
					String portStr = line.getOptionValue("p");
					port = Integer.parseInt(portStr);
				}
				catch (NumberFormatException e) {
				}

				if (line.hasOption("a")) {
					password = line.getOptionValue("a");
				}
			}
			else {
				activateRemote = preferences.listeningForRemoteConnections();
				port = preferences.getRemoteConnectionsPort();
				password = preferences.getRemoteConnectionsPassword();
			}
			if (activateRemote) {
				try {
					Logger logger = LoggerFactory.getLogger("FullSync");

					RemoteController.getInstance().startServer(port, password, profileManager, sync);
					logger.info("Remote Interface available on port: " + port);
				}
				catch (RemoteException e) {
					ExceptionHandler.reportException(e);
					listenerStarupException = e;
				}
			}

			if (line.hasOption("d")) {
				profileManager.addSchedulerListener(new ProfileSchedulerListener() {
					@Override
					public void profileExecutionScheduled(Profile profile) {
						TaskTree tree = sync.executeProfile(profile, false);
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
					}
				});
				profileManager.startScheduler();
			}
			else {
				try {
					GuiController guiController = new GuiController(preferences, profileManager, sync);
					guiController.startGui(line.hasOption('m'));

					if (!line.hasOption('P') && !preferences.getHelpShown() && (null == System.getProperty("net.sourceforge.fullsync.skipHelp"))) {
						preferences.setHelpShown(true);
						preferences.save();
						File f = new File("docs/manual/manual.html");
						if (f.exists()) {
							GuiController.launchProgram(f.getAbsolutePath());
						}
					}

					if (listenerStarupException != null) {
						ExceptionHandler.reportException("Unable to start incoming connections listener.", listenerStarupException);
					}

					if (preferences.getAutostartScheduler()) {
						profileManager.startScheduler();
					}

					guiController.run();
					guiController.disposeGui();
				}
				catch (Exception ex) {
					ExceptionHandler.reportException(ex);
				}
				finally {
					profileManager.save();
				}

				// FIXME [Michele] For some reasons there is some thread still alive if you run the remote interface
				RemoteController.getInstance().stopServer();
				if (null == System.getProperty("net.sourceforge.fullsync.skipExit")) {
					System.exit(-1);
				}
			}
			*/

		}
		catch (Exception exp) {
			//ExceptionHandler.reportException(exp);
		}


	}

	@Override
	public void stop(BundleContext context) throws Exception {
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
		if (!new File(configDir).exists()) {
			new File(configDir).mkdirs();
		}
		return configDir;
	}

	private static void backupFile(final File old, final File current, final String backupName) {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(old);
			fos = new FileOutputStream(current);
			FileChannel in = fis.getChannel();
			FileChannel out = fos.getChannel();
			in.transferTo(0, in.size(), out);
			in.close();
			out.close();
			old.renameTo(new File(backupName));
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		}
		finally {
			if (null != fis) {
				try {
					fis.close();
				}
				catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
			if (null != fos) {
				try {
					fos.close();
				}
				catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
	}

	private static Options initOptions() {
		Options options = new Options();
		options.addOption("h", "help", false, "this help");
		options.addOption("v", "verbose", false, "verbose output to stdout");
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
		return options;
	}

	private static void printHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("fullsync [OPTION]", options);
	}

	private static void migreateOldPreferences(final String configDir) {
		File newPreferences = new File(configDir + "preferences.properties");
		if (!newPreferences.exists()) {
			File oldPreferences = new File("preferences.properties");
			if (oldPreferences.exists()) {
				backupFile(oldPreferences, newPreferences, "preferences_old.properties");
			}
		}
	}
}
