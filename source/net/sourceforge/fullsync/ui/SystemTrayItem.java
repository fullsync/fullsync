/*
 * Created on 16.10.2004
 */
package net.sourceforge.fullsync.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class SystemTrayItem
{
    private MainWindow mainWindow;
    private Tray tray;
    private TrayItem trayItem;
    private Menu menu;

    public SystemTrayItem( MainWindow mainWin )
    {
        this.mainWindow = mainWin;
        this.tray = mainWindow.getDisplay().getSystemTray();
        this.trayItem = new TrayItem( tray, SWT.NULL );
        
        // initialize trayItem
        trayItem.setImage( new Image( null, "images/Tray_Active_01.gif" ) );
    	trayItem.setToolTipText( "FullSync" );
    	trayItem.addListener( SWT.DefaultSelection, new Listener() {
    	    public void handleEvent(Event arg0) 
    	    {
    	        mainWindow.getShell().setVisible( true );
    	        mainWindow.getShell().setMinimized( false );
    	    }
    	} );
    	trayItem.addListener( SWT.MenuDetect, new Listener() {
    		public void handleEvent(Event evt) 
    		{
    		    menu.setVisible( true );
			}
    	} );
    	
    	// initialize popup menu
    	menu = new Menu( mainWindow.getShell(), SWT.POP_UP );
		MenuItem item;
		item = new MenuItem( menu, SWT.NULL );
		item.setText( "Open FullSync" );
		item.addListener(SWT.Selection, new Listener() {
		    public void handleEvent( Event arg0 )
            {
		    	mainWindow.getShell().setVisible( true );
		    	mainWindow.getShell().setMinimized( false );
            }
		} );
		
		item = new MenuItem( menu, SWT.NULL );
		item.setText( "Exit" );
		item.addListener( SWT.Selection, new Listener() {
		    public void handleEvent( Event event )
            {
		    	if (mainWindow.getPreferencesManager().confirmExit()) 
		    	{
		    		MessageBox mb = new MessageBox(mainWindow.getShell(), SWT.ICON_WARNING | SWT.YES | SWT.NO);
		    		mb.setText("Confirmation");
		    		mb.setMessage("Do you really want to quit FullSync? \nAny scheduled tasks can't be performed while " +
		    		"FullSync is closed.");
		    		int result = mb.open();
		    		if (result == SWT.YES) {
		    			event.doit = true;
		    			mainWindow.dispose();
		    			trayItem.dispose();					
		    		} else {
		    			event.doit = false;
		    		}
		    	}
		    	else {
	                mainWindow.dispose();
	                trayItem.dispose();							    		
		    	}
                // TODO check for running ops and ask whether he is sure
                // MICHELE Done the "ask whether he is sure"
            }
		} );
    }
    public void setVisible( boolean visible )
    {
        trayItem.setVisible( visible );
    }
    public void dispose()
    {
        trayItem.dispose();
        menu.dispose();
    }
    public boolean isDisposed()
    {
        return trayItem.isDisposed();
    }
}