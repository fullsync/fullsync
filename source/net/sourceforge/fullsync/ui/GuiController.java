package net.sourceforge.fullsync.ui;

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.ImageRepository;
import net.sourceforge.fullsync.Preferences;
import net.sourceforge.fullsync.ProfileManager;
import net.sourceforge.fullsync.Synchronizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 * 
 * TODO this class should also handle images
 */
public class GuiController implements Runnable
{
    private static GuiController singleton;
    
    private Preferences preferences;
    private ProfileManager profileManager;
    private Synchronizer synchronizer;
    
    private Display display;
    private ImageRepository imageRepository;
    private Shell mainShell;
    private MainWindow mainWindow;
    private SystemTrayItem systemTrayItem;
    
    private boolean active;
    
    public GuiController( Preferences preferences, ProfileManager profileManager, Synchronizer synchronizer )
    {
        this.preferences = preferences;
        this.profileManager = profileManager;
        this.synchronizer = synchronizer;
        
        singleton = this;
    }
    protected void createMainShell( boolean minimized )
    {
		try {
			mainShell = new Shell(display);
			mainWindow = new MainWindow(mainShell, SWT.NULL, this);
			mainShell.setLayout(new org.eclipse.swt.layout.FillLayout());
			Rectangle shellBounds = mainShell.computeTrim(0,0,mainWindow.getSize().x,mainWindow.getSize().y);
			mainShell.setSize(shellBounds.width, shellBounds.height);
			mainShell.setText( "FullSync 0.8.0" );  //$NON-NLS-1$
			mainShell.setImage( getImage( "FullSync.png" ) ); //$NON-NLS-1$
			if( !minimized )
			    mainShell.setVisible( true );
		} catch (Exception e) {
			ExceptionHandler.reportException( e );
		}
	}
    public void setMainShellVisible( boolean visible )
    {
        mainShell.setVisible( visible );
        mainShell.setMinimized( !visible );
    }
    public Shell getMainShell()
    {
        return mainShell;
    }
    public MainWindow getMainWindow()
    {
        return mainWindow;
    }
    public SystemTrayItem getSystemTrayItem()
    {
        return systemTrayItem;
    }
    public Preferences getPreferences()
    {
        return preferences;
    }
    public ProfileManager getProfileManager()
    {
        return profileManager;
    }
    public Synchronizer getSynchronizer()
    {
        return synchronizer;
    }
    public Display getDisplay()
    {
        return display;
    }
    public Image getImage( String imageName )
    {
        return imageRepository.getImage( imageName );
    }
    public void removeImage( String imageName )
    {
        imageRepository.removeImage( imageName );
    }
    public void startGui( boolean minimized )
    {
		display = Display.getDefault();
		imageRepository = new ImageRepository( display );
		createMainShell( minimized );
	    systemTrayItem = new SystemTrayItem( this );
		ExceptionHandler.registerExceptionHandler( 
		        new ExceptionHandler() {
		            protected void doReportException( final String message, final Throwable exception )
		            {
		                exception.printStackTrace();
		                
		                display.syncExec( new Runnable() {
		                    public void run()
                            {
				                ExceptionDialog ed = new ExceptionDialog( mainShell, message, exception );
				                ed.open();
                            }
		                } );
		            }
		        });
		active = true;
    }
    public void run()
    {
		while( active ) {
			if (!display.readAndDispatch())
				display.sleep();
		}
    }
    public void closeGui()
    {
        // TODO before closing anything we need to find out whether there are operations
        //      currently running / windows open that should/may not be closed
        
	    // Close the application, but give him a chance to 
	    // confirm his action first
		if (preferences.confirmExit()) 
		{
			MessageBox mb = new MessageBox(mainShell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
			mb.setText(Messages.getString("GuiController.Confirmation")); //$NON-NLS-1$
			mb.setMessage(Messages.getString("GuiController.Do_You_Want_To_Quit") + "\n" //$NON-NLS-1$ //$NON-NLS-2$
			        	 +Messages.getString("GuiController.Schedule_is_stopped")); //$NON-NLS-1$

			// check whether the user really wants to close
			if (mb.open() != SWT.YES) 
			    return;
		}
		
    	GuiController.getInstance().getProfileManager().disconnectRemote();		            	
    	GuiController.getInstance().getSynchronizer().disconnectRemote();
		
		disposeGui();
		active = false;
    }
    public void disposeGui()
    {
        if( mainShell != null ) {
            mainShell.dispose();
        }
		if (systemTrayItem != null) {
			systemTrayItem.dispose();
		}
		if( imageRepository != null ) {
		    imageRepository.dispose();
		}
    }
    // TODO the busy cursor should be applied only to the window that is busy
    //      difficulty: getShell() can only be accessed by the display thread :-/
    public void showBusyCursor( final boolean show )
	{
		display.asyncExec(new Runnable() {
			public void run() {
				try {
				    Cursor cursor = show?display.getSystemCursor(SWT.CURSOR_WAIT):null;
					Shell[] shells = display.getShells();

					for (int i = 0; i < shells.length; i++) 
					{
						shells[i].setCursor(cursor);
					}
				} catch (Exception ex) {
					ExceptionHandler.reportException( ex );
				}
			}
		});
	}    
    public static GuiController getInstance()
    {
        return singleton;
    }
}
