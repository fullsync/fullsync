package net.sourceforge.fullsync.ui;

import java.util.Arrays;

import net.sourceforge.fullsync.ExceptionHandler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
/**
* This code was generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* *************************************
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED
* for this machine, so Jigloo or this code cannot be used legally
* for any corporate or commercial purpose.
* *************************************
*/
public class NiceListView extends Composite implements Listener
{
	private NiceListViewItem selected;
	
	
	public NiceListView(org.eclipse.swt.widgets.Composite parent, int style) 
	{
		super(parent, style );
		addFocusListener( new FocusAdapter() {
		    public void focusGained( FocusEvent e )
            {
                
            }
		});
		addKeyListener( new KeyAdapter() {
		    public void keyPressed( KeyEvent e )
            {
		        try {
			        Control[] children = getChildren();
			        int index = Arrays.asList(children).indexOf( selected );
	                if( e.keyCode == SWT.ARROW_UP )
	                    setSelected( (NiceListViewItem)children[index-1] );
	                else if( e.keyCode == SWT.ARROW_DOWN )
	                    setSelected( (NiceListViewItem)children[index+1] );
		        } catch( ClassCastException ex ) {
		            ExceptionHandler.reportException( ex );
		        }
            }
		});
		initGUI();
	}
	
	public void handleEvent( Event event )
    {
	    switch( event.type )
	    {
	    case SWT.KeyDown:
	    	switch( event.keyCode )
	    	{
	    	case SWT.ARROW_UP:
	    	case SWT.ARROW_DOWN:
		        Control[] children = getChildren();
		    	int index = Arrays.asList(children).indexOf( selected );
		    	if( event.keyCode == SWT.ARROW_UP && index > 0 )
		    	    setSelected( (NiceListViewItem)children[index-1] );
		    	else if( event.keyCode == SWT.ARROW_DOWN && index+1 < children.length )
		    	    setSelected( (NiceListViewItem)children[index+1] );
		    	break;
	    	}
	    }
    }

	private void initGUI() {
		try {
			GridLayout thisLayout = new GridLayout();
			thisLayout.horizontalSpacing = 2;
			thisLayout.verticalSpacing = 0;
			this.setLayout(thisLayout);
			this.setBackground( new Color( getDisplay(), 255, 255, 255 ) );
			this.layout();
		} catch (Exception e) {
			ExceptionHandler.reportException( e );
		}
	}
	public Composite getSelectedContent()
	{
	    if( selected == null )
	        return null;
	    return selected.getContent(); 
	}

	public void setSelected( NiceListViewItem item )
	{
	    if( item == selected )
	    {
	        selected.forceFocus();
	        return;
	    }
	    Control[] children = this.getChildren();
	    for( int i = 0; i < children.length; i++ )
	    {
	        NiceListViewItem a = (NiceListViewItem)children[i];
	        a.setSelected( false );
	    }
	    item.setSelected( true );
	    selected = item;
	    this.setSize( this.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
	    this.layout();
	}
	
	public boolean setFocus() 
	{
	    if( selected != null )
	    {
	        selected.forceFocus();
	        return true;
	    }
		Control[] cs= getChildren();
		/*
		for (int i= 0; i < cs.length; i++) {
			NiceListViewItem ji= (NiceListViewItem) cs[i];
			if( ji.isSelected() ) {
				ji.forceFocus();
				return;
			}
		}*/
		if (cs.length > 0)
		{
			cs[0].forceFocus();
			return true;
		} else {
		    return false;
		}
    }
	
	public void clear()
	{
	    Control[] children = this.getChildren();
	    for( int i = 0; i < children.length; i++ )
	    {
	        children[i].dispose();
	    }
	}
}
