package net.sourceforge.fullsync.ui;

import java.util.Arrays;

import net.sourceforge.fullsync.ExceptionHandler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
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
public class NiceListView extends Composite 
{

	/**
	* Auto-generated main method to display this 
	* org.eclipse.swt.widgets.Composite inside a new Shell.
	*/
	public static void main(String[] args) {
		showGUI();
	}
		
	/**
	* Auto-generated method to display this 
	* org.eclipse.swt.widgets.Composite inside a new Shell.
	*/
	public static void showGUI() {
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		ScrolledComposite com = new ScrolledComposite( shell, SWT.BORDER | SWT.V_SCROLL );
		NiceListView inst = new NiceListView(com, SWT.NULL);
		com.setExpandHorizontal( true );
		com.setExpandVertical( false );
		com.setBackground( display.getSystemColor( SWT.COLOR_WHITE ) );
		com.setAlwaysShowScrollBars( true );
		com.setContent( inst );
		
		NiceListViewItem item = new NiceListViewItem( inst, SWT.NULL );
		item.setImage( new Image( display, "images/showlog.gif" ) );
		item.setText( "Sample Profile One" );
		item.setStatusText( "" );
		GridData layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.horizontalAlignment = GridData.FILL;
		item.setLayoutData( layoutData );
		
		item = new NiceListViewItem( inst, SWT.NULL );
		item.setImage( new Image( display, "images/showlog.gif" ) );
		item.setText( "Sample Profile Two" );
		item.setStatusText( "" );
		layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.horizontalAlignment = GridData.FILL;
		item.setLayoutData( layoutData );
		
		Composite content = item.getContent();//new Composite( item.getContentComposite(), SWT.NULL );
		GridLayout layout = new GridLayout( 3, false );
		layout.marginHeight = 1;
		layout.marginWidth = 1;
		layout.verticalSpacing = 2;
		layout.horizontalSpacing = 20;
		content.setLayout( layout );
		Label l = new Label( content, SWT.NULL );
		l.setText( "Source: somewhere" );
		l.setLayoutData( new GridData( GridData.FILL, GridData.CENTER, false, false ) );
		l.setSize( 300, 16 );
		l = new Label( content, SWT.NULL );
		l.setText( "Last Update: somewhere" );
		l.setLayoutData( new GridData( GridData.FILL, GridData.CENTER, false, false ) );
		l.setSize( 300, 16 );
		Button b = new Button( content, SWT.NULL );
		b.setText( "Run" );
		GridData d = new GridData( GridData.END, GridData.CENTER, true, false );
		d.verticalSpan = 2;
		b.setLayoutData( d );
		l = new Label( content, SWT.NULL );
		l.setText( "Destination: somewhere" );
		l.setLayoutData( new GridData( GridData.FILL, GridData.CENTER, false, false ) );
		l.setSize( 200, 16 );
		l = new Label( content, SWT.NULL );
		l.setText( "Next Update: not scheduled" );
		l.setLayoutData( new GridData( GridData.FILL, GridData.CENTER, false, false ) );
		l.setSize( 200, 16 );
		content.layout();
		item.layout();
		
		item = new NiceListViewItem( inst, SWT.NULL );
		item.setImage( new Image( display, "images/showlog.gif" ) );
		item.setText( "Sample Profile Three" );
		item.setStatusText( "" );
		layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.horizontalAlignment = GridData.FILL;
		item.setLayoutData( layoutData );

		item = new NiceListViewItem( inst, SWT.NULL );
		item.setImage( new Image( display, "images/showlog.gif" ) );
		item.setText( "Sample Profile Four" );
		item.setStatusText( "" );
		layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.horizontalAlignment = GridData.FILL;
		item.setLayoutData( layoutData );

		Point size = inst.getSize();
		shell.setLayout(new FillLayout());
		shell.layout();
		if(size.x == 0 && size.y == 0) {
			inst.pack();
			shell.pack();
		} else {
			Rectangle shellBounds = shell.computeTrim(0, 0, size.x, size.y);
			int MENU_HEIGHT = 22;
			if (shell.getMenuBar() != null)
				shellBounds.height -= MENU_HEIGHT;
			shell.setSize(shellBounds.width, shellBounds.height);
		}
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	private NiceListViewItem selected;
	
	public boolean isFocusControl()
    {
        return true;
    }
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
	
	public void clear()
	{
	    Control[] children = this.getChildren();
	    for( int i = 0; i < children.length; i++ )
	    {
	        children[i].dispose();
	    }
	}
}
