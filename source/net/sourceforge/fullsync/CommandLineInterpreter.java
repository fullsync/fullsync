package net.sourceforge.fullsync;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Date;

import net.sourceforge.fullsync.impl.ConfigurationPreferences;
import net.sourceforge.fullsync.remoteinterface.RemoteInterfaceServer;
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
		options.addOption( "c", "console", false, "disables the gui and runs pure console mode" );
		options.addOption( "v", "verbose", false, "verbose output to stdout" );
		options.addOption( "r", "run", false, "run the specified profile (-p)" );
		options.addOption( "s", "remoteport", true, "run the server for remote interface on the specified port" );
		options.addOption( "i", "connect", true, "connect to a remote server on the specified port" );

		// interactive mode
		// no actions and stuff
		
		OptionBuilder.withLongOpt( "profile" );
		OptionBuilder.withDescription( "specifies a profile" );
		OptionBuilder.withValueSeparator( '=' );
		OptionBuilder.hasArg();
		OptionBuilder.withArgName("PROFILE");
		options.addOption( OptionBuilder.create("p") ); 
		        
		
		try {
		    CommandLineParser parser = new PosixParser();
		    CommandLine line = null;
		    try {
		    	line = parser.parse( options, args );
		    }
		    catch (ParseException pe) {
		    	System.err.println(pe.getMessage());
		        HelpFormatter formatter = new HelpFormatter();
		        formatter.printHelp( "fullsync [-cvhrpsi]", options );
		        return;		    	
		    }
		    
		    if( line.hasOption( "h" ) )
		    {
		        HelpFormatter formatter = new HelpFormatter();
		        formatter.printHelp( "fullsync [-cvhrpsi]", options );
		        return;
		    }

		    DOMConfigurator.configure( "logging.xml" );
		    Preferences preferences = new ConfigurationPreferences("preferences.properties");
		    ProfileManager profileManager = new ProfileManager( "profiles.xml" );
		    if( line.hasOption( "i" ) ) {
		    	String remotePortStr = line.getOptionValue("i");
		    	int remotePort = 10000;
		    	try {
		    		remotePort = Integer.parseInt(remotePortStr);
				} catch (NumberFormatException e) {
				}
				// TODO [Michele] add the host on the command line
			    profileManager.setRemoteConnection("localhost", remotePort);
		    }

		    Synchronizer sync = new Synchronizer();
		    
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
	    	RemoteInterfaceServer remoteServer = null;
	    	int port = 10000;
	    	String serverURL = null;
	    	
		    if (line.hasOption("s")) {
		    	String portStr = line.getOptionValue("s");
		    	try {
					port = Integer.parseInt(portStr);
				} catch (NumberFormatException e) {
				}
				serverURL = "rmi://localhost:"+port+"/FullSync";
		    	
		    	remoteServer = new RemoteInterfaceServer(profileManager);

		    	LocateRegistry.createRegistry(port);	
		    	Naming.rebind(serverURL, remoteServer);
				System.out.println("Remote Interface available on URL: "+serverURL);
		    }
		    if( line.hasOption( "c" ) && line.hasOption( "r" ) )
		    {
				if( line.hasOption( "p" ) ) 
				{
				    Profile p = profileManager.getProfile( line.getOptionValue("p") );
				    TaskTree tree = sync.executeProfile( p );
				    sync.performActions( tree );
				    p.setLastUpdate( new Date() );
				    profileManager.save();
				    return;
				}
		    }

		    System.setErr( new PrintStream( new FileOutputStream( "logs/stderr.log" ) ) );
		    //System.setOut( new PrintStream( new FileOutputStream( "logs/stdout.log" ) ) );

		    GuiController guiController = new GuiController( preferences, profileManager, sync );
            guiController.startGui();
        	guiController.run();
        	guiController.disposeGui();
        	if (remoteServer != null) {
        		Naming.unbind(serverURL);
        	}
        	// FIXME [Michele] For some reasons there is some thread still alive if you run the remote interface
        	// I've to work on this problem.
        	System.exit(-1);
        } catch( Exception exp ) {
		    exp.printStackTrace();
        }
    }
}
