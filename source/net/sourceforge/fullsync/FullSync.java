package net.sourceforge.fullsync;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import net.sourceforge.fullsync.impl.ProcessorImpl;
import net.sourceforge.fullsync.ui.MainWindow;

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
    
    private boolean guiEnabled;
    private ProfileManager pm;
    private Shell mainShell;
    
    public FullSync( boolean guiEnabled )
    	throws SAXException, IOException, ParserConfigurationException, FactoryConfigurationError
    {
        DOMConfigurator.configure( "logging.xml" );
        
        singleton = this;
        this.guiEnabled = guiEnabled;
        
        pm = new ProfileManager( "profiles.xml" );
    }
    
    public void start()
    {
        if( guiEnabled )
        {
            startGui();
        }
    }
    
    public boolean isGuiEnabled()
    {
        return guiEnabled;
    }
    public Shell getMainShell()
    {
        return mainShell;
    }
    
    protected void startGui()
    {
		try {
			Display display = Display.getDefault();
			mainShell = new Shell(display);
			MainWindow inst = new MainWindow(mainShell, SWT.NULL);
			inst.setProfileManager( pm );
			inst.setProcessor( new ProcessorImpl() );
			mainShell.setLayout(new org.eclipse.swt.layout.FillLayout());
			Rectangle shellBounds = mainShell.computeTrim(0,0,635,223);
			mainShell.setSize(shellBounds.width, shellBounds.height);
			mainShell.setText( "FullSync 0.7 Preview" );
			mainShell.setImage( new Image( null, "images/Location_Both.gif" ) );
			mainShell.open();
	        while (!mainShell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
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
        /* */
        
        FullSync fs = new FullSync( true );
        fs.start();
        
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
