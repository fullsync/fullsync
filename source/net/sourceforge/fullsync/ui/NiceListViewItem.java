package net.sourceforge.fullsync.ui;

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.Profile;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

public class NiceListViewItem extends Canvas implements Listener
{
    private NiceListView parent;
    
    private Label labelIcon;
    private Label labelCaption;
    private Label labelStatus;
    private Composite compositeContent;
    
    private Color colorDefault;
    private Color colorHover;
    private Color colorSelectedDefault;
    private Color colorSelectedFocus;
    
    private ProfileListControlHandler handler;
    private Profile profile;
    
    private boolean mouseOver;
    private boolean hasFocus;
    private boolean selected;

	public NiceListViewItem(NiceListView parent, int style) 
	{
		super(parent, style);
		this.parent = parent;
		
		colorDefault = getDisplay().getSystemColor( SWT.COLOR_WHITE );
		colorHover = new Color( getDisplay(), 248, 252, 255 );
		colorSelectedDefault = new Color( getDisplay(), 236, 233, 216 );
		colorSelectedFocus = new Color( getDisplay(), 230, 240, 255 );
		
		initGUI();
	}

	private void initGUI() 
	{
		try {
			GridData layoutData = new GridData();
			layoutData.grabExcessHorizontalSpace = true;
			layoutData.horizontalAlignment = GridData.FILL;
			this.setLayoutData( layoutData );
			
			GridLayout thisLayout = new GridLayout();
            this.addListener( SWT.MouseEnter, this );
            this.addListener( SWT.MouseExit, this );
            this.addListener( SWT.MouseUp, this );
            this.addListener( SWT.MouseDown, this );
            this.addListener( SWT.MouseDoubleClick, this );
            this.addListener( SWT.KeyDown, this );
            this.addListener( SWT.FocusIn, this );
            this.addListener( SWT.FocusOut, this );
			this.setLayout(thisLayout);
			thisLayout.numColumns = 3;
			thisLayout.marginHeight = 3;
			thisLayout.marginWidth = 3;
			
			{
			    labelIcon = new Label( this, SWT.NULL );
			    labelIcon.setSize( 16, 16 );
			    GridData labelIconLData = new GridData();
			    labelIconLData.grabExcessVerticalSpace = true;
			    labelIconLData.verticalAlignment = GridData.BEGINNING;
			    labelIconLData.verticalSpan = 2;
			    labelIconLData.widthHint = 16;
			    labelIconLData.heightHint = 16;
			    labelIcon.setLayoutData(labelIconLData);
	            labelIcon.addListener( SWT.MouseEnter, this );
	            labelIcon.addListener( SWT.MouseExit, this );
	            labelIcon.addListener( SWT.MouseUp, this );
	            labelIcon.addListener( SWT.MouseDown, this );
	            labelIcon.addListener( SWT.MouseDoubleClick, this );
			}
            {
                labelCaption = new Label(this, SWT.NULL);
                labelCaption.setFont(new Font( getDisplay(), "Tahoma", 9, 1 )); //$NON-NLS-1$
                labelCaption.setSize(200, 14);
                GridData labelCaptionLData = new GridData();
                labelCaptionLData.widthHint = -1;
                labelCaptionLData.heightHint = 14;
                labelCaption.setLayoutData(labelCaptionLData);
	            labelCaption.addListener( SWT.MouseEnter, this );
	            labelCaption.addListener( SWT.MouseExit, this );
	            labelCaption.addListener( SWT.MouseUp, this );
	            labelCaption.addListener( SWT.MouseDown, this );
	            labelCaption.addListener( SWT.MouseDoubleClick, this );
            }
            {
                labelStatus = new Label(this, SWT.NONE);
                GridData labelStatusLData = new GridData();
                labelStatusLData.grabExcessHorizontalSpace = true;
                labelStatusLData.horizontalAlignment = GridData.FILL;
                labelStatusLData.horizontalIndent = 10;
                labelStatus.setLayoutData(labelStatusLData);
                labelStatus.addListener( SWT.MouseEnter, this );
                labelStatus.addListener( SWT.MouseExit, this );
                labelStatus.addListener( SWT.MouseUp, this );
                labelStatus.addListener( SWT.MouseDown, this );
	            labelStatus.addListener( SWT.MouseDoubleClick, this );
            }
            /*
            {
                compositeContent = new Composite( this, SWT.NULL );
                compositeContent.setVisible( false );
                compositeContent.setSize( 200, 40 );
                GridData compositeContentLData = new GridData();
                compositeContentLData.horizontalSpan = 2;
                compositeContentLData.grabExcessHorizontalSpace = true;
                compositeContentLData.horizontalAlignment = GridData.FILL;
                compositeContentLData.heightHint = 1;
                compositeContentLData.widthHint = 1;
                compositeContent.setLayoutData(compositeContentLData);
                compositeContent.addMouseTrackListener( mouseTrackListener );
                compositeContent.addMouseListener( mouseListener );
            }
            */

			this.setBackground( colorDefault );
			this.layout();
		} catch (Exception e) {
			ExceptionHandler.reportException( e );
		}
	}
	public void handleEvent( Event event )
    {
	    switch( event.type )
	    {
	    case SWT.MouseEnter:
	        mouseOver = true;
	    	updateBackground();
	    	break;
	    case SWT.MouseExit:
	        mouseOver = false;
	    	updateBackground();
	    	break;
	    case SWT.MouseDown:
	    	parent.setSelected( NiceListViewItem.this );
	    	break;
	    case SWT.MouseUp:
	        if( event.button == 3 )
	            getMenu().setVisible( true );
	        break;
	    case SWT.MouseDoubleClick:
	        handler.editProfile(profile);
	    	break;
	    case SWT.KeyDown:
	        parent.handleEvent( event );
	    	break;
	    case SWT.FocusIn:
	        hasFocus = true;
	    	updateBackground();
	    	break;
	    case SWT.FocusOut:
	        hasFocus = false;
	    	updateBackground();
	    	break;
		};

    }
	
	public void setBackground( Color color )
	{
		Control[] children;
		super.setBackground( color );
		if( labelIcon.getImage() != null )
		    labelIcon.getImage().setBackground( color );

		children = this.getChildren();
		for( int i = 0; i < children.length; i++ )
		    children[i].setBackground( color );
		
		if( compositeContent != null )
		{
			children = compositeContent.getChildren();
			for( int i = 0; i < children.length; i++ )
			    children[i].setBackground( color );
		}
	}
	public void updateBackground()
	{
	    if( selected )
	    {
	        if( hasFocus )
	             setBackground( colorSelectedFocus );
	        else setBackground( colorSelectedDefault );
	    } else if( mouseOver ) {
	        setBackground( colorHover );
	    } else {
	        setBackground( colorDefault );
	    }
	}

	public void setImage( Image image )
	{
	    labelIcon.setImage( image );
	    image.setBackground( labelIcon.getBackground() );
	}
	public Image getImage()
	{
	    return labelIcon.getImage();
	}
	
	public void setText( String text )
	{
	    labelCaption.setText( text );
	    labelCaption.pack();
	    layout();
	}
	public void setStatusText( String status )
	{
	    if( status == null || status.length() == 0 )
	         labelStatus.setText( "" ); //$NON-NLS-1$
	    else labelStatus.setText( "("+status+")" ); //$NON-NLS-1$ //$NON-NLS-2$
	    labelStatus.pack();
	}
	
	public void setSelected( boolean selected )
	{
	    if( selected )
	    {
	        forceFocus();
	        compositeContent.setVisible( true );
	        Point size = compositeContent.computeSize( SWT.DEFAULT, SWT.DEFAULT );
	        ((GridData)compositeContent.getLayoutData()).widthHint = size.x;
	        ((GridData)compositeContent.getLayoutData()).heightHint = size.y;
	        //labelCaption.setFocus();
	        //this.setFocus();
	    } else {
	        compositeContent.setVisible( false );
	        ((GridData)compositeContent.getLayoutData()).widthHint = 0;
	        ((GridData)compositeContent.getLayoutData()).heightHint = 0;
	    }
	    this.selected = selected;
        updateBackground();
	    this.layout();
	}
	public Composite getContent()
	{
	    return compositeContent;
	}
	/**
	 * @param content The composite that will be shown when the item is
	 * 				  selectes. ATTENTION: this composite should not have
	 * 				  any more composites, as the background color and 
	 * 				  mouselisteners are set only on all direct children 
	 * 				  of this composite 
	 */
	public void setContent( Composite content )
	{
	    this.compositeContent = content;
        GridData compositeContentLData = new GridData();
        compositeContentLData.horizontalSpan = 2;
        compositeContentLData.grabExcessHorizontalSpace = true;
        compositeContentLData.horizontalAlignment = GridData.FILL;
        compositeContentLData.heightHint = 0;
        compositeContentLData.widthHint = 0;
        compositeContent.setLayoutData(compositeContentLData);
        compositeContent.setVisible( false );
        compositeContent.addListener( SWT.MouseDoubleClick, this );
        compositeContent.addListener( SWT.MouseUp, this );
        compositeContent.addListener( SWT.MouseDown, this );
		Control[] children = compositeContent.getChildren();
		for( int i = 0; i < children.length; i++ )
		{
		    children[i].addListener( SWT.MouseDoubleClick, this );
		    children[i].addListener( SWT.MouseUp, this );
		    children[i].addListener( SWT.MouseDown, this );
		}
	}
	/*
	public boolean isFocusControl()
    {
        return true;
    }*/
    public ProfileListControlHandler getHandler()
    {
        return handler;
    }
    public void setHandler( ProfileListControlHandler handler )
    {
        this.handler = handler;
    }
    public Profile getProfile() {
    	return profile;
    }
    public void setProfile(Profile profile) {
    	this.profile = profile;
    }

}
