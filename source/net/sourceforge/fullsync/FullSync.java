package net.sourceforge.fullsync;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import net.sourceforge.fullsync.buffer.BlockBuffer;
import net.sourceforge.fullsync.impl.FillBufferActionQueue;
import net.sourceforge.fullsync.impl.AbstractProcessor;
import net.sourceforge.fullsync.impl.ProcessorImpl;
import net.sourceforge.fullsync.ui.LogWindow;
import net.sourceforge.fullsync.ui.MainWindow;
import net.sourceforge.fullsync.ui.SystemTrayItem;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class FullSync
{
    private static FullSync singleton;
    
    private ProfileManager profileManager;
    private AbstractProcessor processor;
    
    private PreferencesManager preferencesManager;

    private boolean guiEnabled;
    private Display display;
    private MainWindow mainWindow;
    private SystemTrayItem trayItem;
    
    public FullSync()
    	throws SAXException, IOException, ParserConfigurationException, FactoryConfigurationError
    {
        DOMConfigurator.configure( "logging.xml" );
        
        guiEnabled = false;
        profileManager = new ProfileManager( "profiles.xml" );
        preferencesManager = new PreferencesManager("preferences.xml");
        processor = new ProcessorImpl();

        singleton = this;
    }
    
    public void start()
    {
    	//profileManager.startTimer();
        if( guiEnabled )
        {
            startGui();
        	trayItem = new SystemTrayItem( mainWindow );

        	//MICHELE Added
			mainWindow.setSystemTrayItem(trayItem);

        	run();
        }
    }
    public void run()
    {
    	while (!trayItem.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
        stop();
    }
    public void stop()
    {
    	profileManager.stopTimer();
    	trayItem.dispose();
    	// stop gui
    }
    
    public AbstractProcessor getProcessor() 
    {
		return processor;
	}
    public ProfileManager getProfileManager()
    {
    	return profileManager;
    }
    
    public void executeProfile( Profile profile, boolean interactive )
    {
		TaskTree t;
        try {
            t = getProcessor().execute( profile );
            if( interactive ) {
            	LogWindow.show( t );
            } else {
            	performActions( t );
            }
            profile.setLastUpdate( new Date() );
            profileManager.fireChange();
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }
    /**
     * TODO if we add some listener/feedback receiver here we could
     * easily use this for visual action performing as well.
     */
    public void performActions( TaskTree taskTree )
    {
        try {
            //tasksTotal = taskTree.getTaskCount();
		    //tasksFinished = 0;
		    
			// Logger logger = Logger.getRootLogger();
            // logger.addAppender( new FileAppender( new PatternLayout( "%d{ISO8601} [%p] %c %x - %m%n" ), "log/log.txt" ) );
            Logger logger = Logger.getLogger( "FullSync" );
	        logger.info( "Synchronization started" );
	        logger.info( "  source:      "+taskTree.getSource().getUri().toString() );
	        logger.info( "  destination: "+taskTree.getDestination().getUri().toString() );
	        
	        BlockBuffer buffer = new BlockBuffer( logger );
	        ActionQueue queue = new FillBufferActionQueue(buffer);
	        // TODO add some visualisation of finished tasks 
	        // final Color colorFinished = new Color( null, 150, 255, 150 );
	        // item.setBackground()
	        //   -- this should still be possible if logwindow is using this method
	        
	        buffer.load();
	        queue.enqueue( taskTree );
	        queue.flush();
	        buffer.unload();
	        
	        taskTree.getSource().flush();
	        taskTree.getDestination().flush();
	        taskTree.getSource().close();
	        taskTree.getDestination().close();
	        logger.info( "finished synchronization" );
	    } catch( IOException e ) {
	        e.printStackTrace();
	    }
    }
    
    public void setGuiEnabled( boolean guiEnabled )
    {
        this.guiEnabled = guiEnabled;
    }
    
    public boolean isGuiEnabled()
    {
        return guiEnabled;
    }
    public MainWindow getMainWindow()
    {
        return mainWindow;
    }
    
    protected void startGui()
    {
		try {
			display = Display.getDefault();
			Shell mainShell = new Shell(display);
			mainWindow = new MainWindow(mainShell, SWT.NULL);
			mainWindow.setProfileManager( profileManager );
			mainWindow.setPreferencesManager( preferencesManager );
			mainWindow.setProcessor( processor );
			mainShell.setLayout(new org.eclipse.swt.layout.FillLayout());
			Rectangle shellBounds = mainShell.computeTrim(0,0,635,223);
			mainShell.setSize(shellBounds.width, shellBounds.height);
			mainShell.setText( "FullSync 0.7.1" );
			mainShell.setImage( new Image( null, "images/FullSync.gif" ) );
			mainShell.setVisible( true );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    public static FullSync getInstance()
    {
        return singleton;
    }

    public static void main( String[] args )
    	throws DataParseException, FileSystemException, IOException, URISyntaxException, Exception
    {
        /* /
        ActionDeciderDebugger debug = new ActionDeciderDebugger();
        debug.debugActionDecider( new PublishActionDecider(), false, true );
        
        /* /
        Provider[] p = Security.getProviders();
        for( int i = 0; i < p.length; i++ )
            System.out.println( p[i].getName() );
        
        System.out.println( Security.getAlgorithms("KeyAgreement") );//, "DH" ) );
        KeyPairGenerator dhKeyPairGen = KeyPairGenerator.getInstance("DH");
        KeyAgreement dhKeyAgreement = KeyAgreement.getInstance("DH");
        /* /
    	
    	FullSync fs = new FullSync(true);
    	fs.start();
    	
    	/* */
        
        CommandLineInterpreter.parse( args );
        
        /* /
        FileSystemManager fsm = new FileSystemManager();
        Directory d1 = fsm.resolveUri( new URI( "file:/E:/Java/WebsiteSynchronizer/_testing/Source" ) );
        Directory d2 = fsm.resolveUri( new URI( "buffered:syncfiles:file:/C:/Temp/test2" ) );
        
        SyncRules rules = new SyncRules("UPLOAD");
		rules.setJustLogging( false );

		Task task = new Task();
		task.setSource( d1 );
		task.setDestination( d2 );
		task.setRules( rules );
		
		Buffer buffer = new BlockBuffer();
		buffer.load();
        ProcessorImpl c = new ProcessorImpl(  );
        c.setActionQueue( new FillBufferActionQueue( buffer ) );
        c.execute( task );
        buffer.unload();
        /* */ 
    }
}
