package net.sourceforge.fullsync;

import java.util.Date;

import net.sourceforge.fullsync.impl.ConfigurationPreferences;
import net.sourceforge.fullsync.ui.GuiController;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
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
		    CommandLine line = parser.parse( options, args );
		    
		    if( line.hasOption( "h" ) )
		    {
		        HelpFormatter formatter = new HelpFormatter();
		        formatter.printHelp( "fullsync [-cvhrp]", options );
		        return;
		    }

		    DOMConfigurator.configure( "logging.xml" );
		    Preferences preferences = new ConfigurationPreferences("preferences.properties");
		    ProfileManager profileManager = new ProfileManager( "profiles.xml" );
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
		    
		    GuiController guiController = new GuiController( preferences, profileManager, sync );
            guiController.startGui();
        	guiController.run();
        	guiController.disposeGui();
        } catch( Exception exp ) {
		    exp.printStackTrace();
        }
    }
}
