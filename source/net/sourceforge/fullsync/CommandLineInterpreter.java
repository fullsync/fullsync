package net.sourceforge.fullsync;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Date;

import net.sourceforge.fullsync.impl.ConfigurationPreferences;
import net.sourceforge.fullsync.remote.RemoteServer;
import net.sourceforge.fullsync.ui.GuiController;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class CommandLineInterpreter
{
    public static void parse( String[] args )
    {

    	// create the Options
		Options options = new Options();
		options.addOption( "h", "help", false, "this help" );
		options.addOption( "d", "daemon", false, "disables the gui and runs in daemon mode" );
		options.addOption( "v", "verbose", false, "verbose output to stdout" );
		options.addOption( "r", "run", true, "run the specified profile" );
		options.addOption( "p", "remoteport", true, "accept incomming connection on the specified port" );

		// interactive mode
		// no actions and stuff
		
		try {
		    System.setErr( new PrintStream( new FileOutputStream( "logs/stderr.log" ) ) );
//		    System.setOut( new PrintStream( new FileOutputStream( "logs/stdout.log" ) ) );

		    CommandLineParser parser = new PosixParser();
		    CommandLine line = null;
		    try {
		    	line = parser.parse( options, args );
		    }
		    catch (ParseException pe) {
		    	System.err.println(pe.getMessage());
		        HelpFormatter formatter = new HelpFormatter();
		        formatter.printHelp( "fullsync [-hvrdp]", options );
		        return;		    	
		    }
		    
		    if( line.hasOption( "h" ) )
		    {
		        HelpFormatter formatter = new HelpFormatter();
		        formatter.printHelp( "fullsync [-hvrdp]", options );
		        return;
		    }


		    DOMConfigurator.configure( "logging.xml" );
		    Preferences preferences = new ConfigurationPreferences("preferences.properties");
		    ProfileManager profileManager = new ProfileManager( "profiles.xml" );

		    final Synchronizer sync = new Synchronizer();
		    
		    if( line.hasOption( "v" ) )
		    {
		        ConsoleAppender appender = 
		            new ConsoleAppender( 
		                new PatternLayout("[%p] %c %x - %m%n"),
                        "System.out" );
		        appender.setName("Verbose");
		        
		        Logger logger = Logger.getLogger( "FullSync" );
		        logger.addAppender( appender );
		    }

		    if (line.hasOption( "r" ))
		    {
				    Profile p = profileManager.getProfile( line.getOptionValue("r") );
				    TaskTree tree = sync.executeProfile( p );
				    sync.performActions( tree );
				    p.setLastUpdate( new Date() );
				    profileManager.save();
				    return;
		    }

		    RemoteServer remoteServer = null;
	    	int port = 10000;
	    	String serverURL = null;
	    	
		    if (line.hasOption("p")) {
		    	System.out.println("Starting remote interface...");
		    	String portStr = line.getOptionValue("p");
		    	try {
					port = Integer.parseInt(portStr);
				} catch (NumberFormatException e) {
				}
				serverURL = "rmi://localhost:"+port+"/FullSync";
		    	
		    	remoteServer = new RemoteServer(profileManager, sync);

		    	LocateRegistry.createRegistry(port);	
		    	Naming.rebind(serverURL, remoteServer);
				System.out.println("Remote Interface available on URL: "+serverURL);
		    }

		    if (line.hasOption("d")) {
		        profileManager.addSchedulerListener( new ProfileSchedulerListener() {
		            public void profileExecutionScheduled( Profile profile )
                    {
                        TaskTree tree = sync.executeProfile( profile );
                        if( tree == null )
                        {
                            profile.setLastError( 1, "An error occured while comparing filesystems." );
                        } else {
                            int errorLevel = sync.performActions( tree );
                            if( errorLevel > 0 )
                                 profile.setLastError( errorLevel, "An error occured while copying files." );
                            else profile.setLastUpdate( new Date() );
                        }
                    }
		        } );
		    	profileManager.startTimer();
		    	
		    	Object mutex = new Object();
		    	synchronized (mutex) {
		    		mutex.wait();
		    	}
		    } else {
		    	try {
		    		GuiController guiController = new GuiController( preferences, profileManager, sync );
		    		guiController.startGui();
		    		guiController.run();
		    		guiController.disposeGui();
		    	} catch( Exception ex ) {
		    		ex.printStackTrace();
		    	} finally {
		    		profileManager.save();
		    	}

		    	if (remoteServer != null) {
	        		Naming.unbind(serverURL);
	            	// FIXME [Michele] For some reasons there is some thread still alive if you run the remote interface
	            	System.exit(-1);
	        	}
		    }
		    
        } catch( Exception exp ) {
		    exp.printStackTrace();
        }
    }
}
