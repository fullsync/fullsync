package net.sourceforge.fullsync.ui;

import net.sourceforge.fullsync.Preferences;
import net.sourceforge.fullsync.ProfileManager;
import net.sourceforge.fullsync.Synchronizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
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
    protected void createMainShell()
    {
		try {
			mainShell = new Shell(display);
			mainWindow = new MainWindow(mainShell, SWT.NULL);
			mainWindow.setGuiController( this );
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
    public void setMainShellVisible( boolean visible )
    {
        mainShell.setVisible( visible );
        mainShell.setMinimized( !visible );
    }
    public Shell getMainShell()
    {
        return mainShell;
    }
    public Composite getMainWindow()
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
    public void startGui()
    {
		display = Display.getDefault();
		createMainShell();
	    systemTrayItem = new SystemTrayItem( this );
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
			mb.setText("Confirmation");
			mb.setMessage("Do you really want to quit FullSync? \n"
			        	 +"Any scheduled tasks will not be performed while " 
			        	 +"FullSync is closed.");

			// check whether the user really wants to close
			if (mb.open() != SWT.YES) 
			    return;
		}
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
					//final String BUSYID_NAME = "SWT BusyIndicator";
					//final Integer busyId = new Integer(0);

					for (int i = 0; i < shells.length; i++) 
					{
						//Integer id = (Integer) shells[i].getData(BUSYID_NAME);
						//if (id == null) {
							shells[i].setCursor(cursor);
						//	shells[i].setData(BUSYID_NAME, busyId);
						//}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
	}    
    public static GuiController getInstance()
    {
        return singleton;
    }
}
