package net.sourceforge.fullsync;

import java.io.IOException;
import java.net.URISyntaxException;

import net.sourceforge.fullsync.impl.ProcessorImpl;
import net.sourceforge.fullsync.ui.MainWindow;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class Main
{
    public static void showGUI( ProfileManager pm )
    {
		try {
			Display display = Display.getDefault();
			Shell shell = new Shell(display);
			MainWindow inst = new MainWindow(shell, SWT.NULL);
			inst.setProfileManager( pm );
			inst.setProcessor( new ProcessorImpl() );
			shell.setLayout(new org.eclipse.swt.layout.FillLayout());
			Rectangle shellBounds = shell.computeTrim(0,0,635,223);
			shell.setSize(shellBounds.width, shellBounds.height);
			shell.setText( "AllSync Profiles" );
			shell.setImage( new Image( null, "images/Location_Both.gif" ) );
			shell.open();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    public static void main( String[] args )
    	throws DataParseException, FileSystemException, IOException, URISyntaxException
    {
        /* */
        ProfileManager pm = new ProfileManager( "profiles.xml" );
        showGUI( pm );
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
