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
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.rmi.RemoteException;
import java.util.Date;

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.Preferences;
import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.ProfileManager;
import net.sourceforge.fullsync.ProfileSchedulerListener;
import net.sourceforge.fullsync.Synchronizer;
import net.sourceforge.fullsync.TaskTree;
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
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.xml.DOMConfigurator;
import org.eclipse.swt.program.Program;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class CommandLineInterpreter {
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
	}

	private static void printHelp() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("fullsync [-hvrdp]", options);
	}

	public static void parse(String[] args) {
		initOptions();

		try {
			if (null == System.getProperty("net.sourceforge.fullsync.noredirectstderr")) {
				System.setErr(new PrintStream(new FileOutputStream("logs/stderr.log")));
				// System.setOut( new PrintStream( new FileOutputStream( "logs/stdout.log" ) ) );
			}

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

			if (line.hasOption("h")) {
				printHelp();
				return;
			}

			// Initialize basic facilities
			DOMConfigurator.configure("logging.xml");
			Preferences preferences = new ConfigurationPreferences("preferences.properties");

			String profilesFile = "profiles.xml";
			if (line.hasOption("P")) {
				profilesFile = line.getOptionValue("P");
			}
			ProfileManager profileManager = new ProfileManager(profilesFile);

			final Synchronizer sync = new Synchronizer();

			// Apply modifying options
			if (line.hasOption("v")) {
				ConsoleAppender appender = new ConsoleAppender(new PatternLayout("[%p] %c %x - %m%n"), "System.out");
				appender.setName("Verbose");

				Logger logger = Logger.getLogger("FullSync");
				logger.addAppender(appender);
			}

			// Apply executing options
			if (line.hasOption("r")) {
				Profile p = profileManager.getProfile(line.getOptionValue("r"));
				TaskTree tree = sync.executeProfile(p);
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
					Logger logger = Logger.getLogger("FullSync");

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
						TaskTree tree = sync.executeProfile(profile);
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
					if (!line.hasOption('P') && !(new File(profilesFile)).exists()) {
						try {
							File f = new File("docs/manual/Getting_Started.html");
							if (f.exists()) {
								Program.launch(f.getAbsolutePath());
							}
						}
						catch (Error ex) {
						}
					}
					GuiController guiController = new GuiController(preferences, profileManager, sync);
					guiController.startGui(line.hasOption('m'));

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
				System.exit(-1);
			}

		}
		catch (Exception exp) {
			ExceptionHandler.reportException(exp);
		}
	}
}
