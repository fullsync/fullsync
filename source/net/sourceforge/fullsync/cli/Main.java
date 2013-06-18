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
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.TreeSet;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.FileSystemException;
import net.sourceforge.fullsync.FileSystemManager;
import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.ProfileManager;
import net.sourceforge.fullsync.ProfileSchedulerListener;
import net.sourceforge.fullsync.Synchronizer;
import net.sourceforge.fullsync.TaskTree;
import net.sourceforge.fullsync.Util;
import net.sourceforge.fullsync.fs.Site;
import net.sourceforge.fullsync.impl.ConfigurationPreferences;
import net.sourceforge.fullsync.remote.RemoteController;
import net.sourceforge.fullsync.ui.GuiController;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class Main{ // NO_UCD
	private static Options options;

	private static void initOptions() {
		options = new Options();
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

		OptionBuilder.withLongOpt("list");
		OptionBuilder.withDescription("list all files and directories at the given location");
		OptionBuilder.hasArg();
		OptionBuilder.withArgName("URL");
		options.addOption(OptionBuilder.create("ls"));

		OptionBuilder.withLongOpt("timezone");
		OptionBuilder.withDescription("timezone of the server, needed to convert the server time to your local time");
		OptionBuilder.hasArg();
		OptionBuilder.withArgName("timezone name");
		options.addOption(OptionBuilder.create("tz"));
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
		if (!new File(configDir).exists()) {
			new File(configDir).mkdirs();
		}
		return configDir;
	}

	private static void backupFile(final File old, final File current, final String backupName) throws IOException {
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
		finally {
			if (null != fis) {
				fis.close();
			}
			if (null != fos) {
				fos.close();
			}
		}
	}

	public static void main(String[] args) {
		initOptions();
		String configDir = getConfigDir();
		try {
			CommandLineParser parser = new PosixParser();
			CommandLine line = null;

			try {
				line = parser.parse(options, args);
			}
			catch (ParseException pe) {
				System.err.println(pe.getMessage());
				printHelp();
				return;
			}

			// Apply modifying options
			if (!line.hasOption("v")) {
				System.setErr(new PrintStream(new FileOutputStream(configDir + "fullsync.log")));
			}

			if (line.hasOption("h")) {
				printHelp();
				return;
			}
			
			if (line.hasOption("ls")) {
				doList(line);
			}

			// Initialize basic facilities
			
			// upgrade code...
			do {
				File newPreferences = new File(configDir + "preferences.properties");
				File oldPreferences = new File("preferences.properties");
				if (!newPreferences.exists() && oldPreferences.exists()) {
					backupFile(oldPreferences, newPreferences, "preferences_old.properties");
				}
			}
			while (false); // variable scope
			final ConfigurationPreferences preferences = new ConfigurationPreferences(configDir + "preferences.properties");
			
			String profilesFile;
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
				/*
				 * Object mutex = new Object();
				 * synchronized (mutex) {
				 * mutex.wait();
				 * }
				 */
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

		}
		catch (Exception exp) {
			ExceptionHandler.reportException(exp);
		}
	}

	private static void doList(CommandLine line) {
		String suri = line.getOptionValue("ls");
		URI uri = null;
		try {
			uri = new URI(suri);
		}
		catch (URISyntaxException ex) {
			ExceptionHandler.reportException(ex);
			System.err.println("Unable to parse URI. " + ex.getMessage());
			//TODO: try native path  to uri
		}
		if (null != uri) {
			ConnectionDescription desc = new ConnectionDescription(uri);
			if (line.hasOption("timezone")) {
				desc.setParameter(ConnectionDescription.PARAMETER_TIMEZONE, line.getOptionValue("timezone"));
			}
			FileSystemManager fsm = new FileSystemManager();
			try {
				Site site = fsm.createConnection(desc);
				DateTimeZone local = DateTimeZone.getDefault();

				String dateFormat = DateTimeFormat.patternForStyle("MM", Locale.getDefault());
				DateTimeFormatter fmt = DateTimeFormat.forPattern(dateFormat);
				int timelength = fmt.print(DateTime.now()).length() + 5; // for missing leading zeros
				// <type> <last modified> <size> <name>
				String lineFormat = "%4s %" + timelength + "s %10s %s";
				Collection<net.sourceforge.fullsync.fs.File> children = site.getRoot().getChildren();
				TreeSet<net.sourceforge.fullsync.fs.File> sortedChildren = new TreeSet<net.sourceforge.fullsync.fs.File>(children);
				for (net.sourceforge.fullsync.fs.File child : sortedChildren) {
					String size = "";
					String type = "dir";
					if (child.isFile()) {
						size = Util.formatSize(child.getSize());
						type = "file";
					}
					System.out.println(String.format(lineFormat, type, fmt.print(local.convertUTCToLocal(child.getLastModified())), size, child.getName()));
				}
				System.exit(0);
			} catch (FileSystemException ex) {
				ExceptionHandler.reportException(ex);
			} catch (IOException ex) {
				ExceptionHandler.reportException(ex);
			} catch (URISyntaxException ex) {
				ExceptionHandler.reportException(ex);
			}
		}
		System.exit(1);
	}
}
