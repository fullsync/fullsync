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
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class SystemTrayItem
{
    private GuiController guiController;
    private Tray tray;
    private TrayItem trayItem;
    private Menu menu;

    public SystemTrayItem( GuiController gui )
    {
        this.guiController = gui;
        this.tray = guiController.getDisplay().getSystemTray();
        this.trayItem = new TrayItem( tray, SWT.NULL );
        
        // initialize trayItem
        trayItem.setImage( new Image( null, "images/Tray_Active_01.gif" ) );
    	trayItem.setToolTipText( "FullSync" );
    	trayItem.addListener( SWT.DefaultSelection, new Listener() {
    	    public void handleEvent(Event arg0) 
    	    {
    	        guiController.setMainShellVisible( true );
    	    }
    	} );
    	trayItem.addListener( SWT.MenuDetect, new Listener() {
    		public void handleEvent(Event evt) 
    		{
    		    menu.setVisible( true );
			}
    	} );
    	
    	// initialize popup menu
    	menu = new Menu( guiController.getMainShell(), SWT.POP_UP );
		MenuItem item;
		item = new MenuItem( menu, SWT.NULL );
		item.setText( "Open FullSync" );
		item.addListener(SWT.Selection, new Listener() {
		    public void handleEvent( Event arg0 )
            {
		    	guiController.setMainShellVisible( true );
            }
		} );
		
		item = new MenuItem( menu, SWT.NULL );
		item.setText( "Exit" );
		item.addListener( SWT.Selection, new Listener() {
		    public void handleEvent( Event event )
            {
		        guiController.closeGui();
            }
		} );
    }
    public void setVisible( boolean visible )
    {
        trayItem.setVisible( visible );
    }
    public boolean isDisposed()
    {
        return trayItem.isDisposed();
    }
    public void dispose()
    {
        trayItem.dispose();
        menu.dispose();
    }
}