package net.sourceforge.fullsync.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class NiceListViewItem extends Canvas 
{
    private Label labelIcon;
    private Label labelCaption;
    private Label labelStatus;
    private Composite compositeContent;
    
    private Color colorDefault;
    private Color colorHover;
    private Color colorSelectedDefault;
    private Color colorSelectedHover;
    
    private boolean selected;

	public NiceListViewItem(NiceListView parent, int style) 
	{
		super(parent, style);
		
		colorDefault = getDisplay().getSystemColor( SWT.COLOR_WHITE );
		colorHover = new Color( getDisplay(), 248, 252, 255 );
		colorSelectedDefault = new Color( getDisplay(), 230, 240, 255 );
		colorSelectedHover = new Color( getDisplay(), 230, 240, 255 );
		
		initGUI();
		selected = false;
	}

	private void initGUI() 
	{
		try {
			MouseTrackListener mouseTrackListener = new MouseTrackListener() {
		    	public void mouseExit(MouseEvent evt) {
		    	    setBackground( selected?colorSelectedDefault:colorDefault );
		    	    
		        }
		        public void mouseEnter(MouseEvent evt) {
		            setBackground( selected?colorSelectedHover:colorHover );
		        }
		        public void mouseHover( MouseEvent e )
		        {
		        }
			};

			MouseListener mouseListener = new MouseAdapter() {
			    public void mouseUp( MouseEvent e )
                {
			        if( !selected )
			            ((NiceListView)getParent()).setSelected( NiceListViewItem.this );
                }
			};
			
			GridData layoutData = new GridData();
			layoutData.grabExcessHorizontalSpace = true;
			layoutData.horizontalAlignment = GridData.FILL;
			this.setLayoutData( layoutData );
			
			GridLayout thisLayout = new GridLayout();
            this.addMouseTrackListener( mouseTrackListener );
            this.addMouseListener( mouseListener );
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
			    labelIcon.addMouseTrackListener( mouseTrackListener );
			    labelIcon.addMouseListener( mouseListener );
			}
            {
                labelCaption = new Label(this, SWT.NULL);
                labelCaption.setText("Caption");
                labelCaption.setFont(new Font( getDisplay(), "Tahoma", 9, 1 ));
                labelCaption.setSize(200, 14);
                GridData labelCaptionLData = new GridData();
                labelCaptionLData.widthHint = -1;
                labelCaptionLData.heightHint = 14;
                labelCaption.setLayoutData(labelCaptionLData);
                labelCaption.addMouseTrackListener( mouseTrackListener );
                labelCaption.addMouseListener( mouseListener );
            }
            {
                labelStatus = new Label(this, SWT.NONE);
                labelStatus.setText("Status");
                GridData labelStatusLData = new GridData();
                labelStatusLData.grabExcessHorizontalSpace = true;
                labelStatusLData.horizontalAlignment = GridData.FILL;
                labelStatusLData.horizontalIndent = 10;
                labelStatus.setLayoutData(labelStatusLData);
                labelStatus.addMouseTrackListener( mouseTrackListener );
                labelStatus.addMouseListener( mouseListener );
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
			e.printStackTrace();
		}
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
	         labelStatus.setText( "" );
	    else labelStatus.setText( "("+status+")" );
	    labelStatus.pack();
	}
	
	public void setSelected( boolean selected )
	{
	    if( selected )
	    {
	        setBackground( colorSelectedHover );
	        compositeContent.setVisible( true );
	        Point size = compositeContent.computeSize( SWT.DEFAULT, SWT.DEFAULT );
	        ((GridData)compositeContent.getLayoutData()).widthHint = size.x;
	        ((GridData)compositeContent.getLayoutData()).heightHint = size.y;
	        //labelCaption.setFocus();
	        this.setFocus();
	    } else {
	        setBackground( colorDefault );
	        compositeContent.setVisible( false );
	        ((GridData)compositeContent.getLayoutData()).widthHint = 0;
	        ((GridData)compositeContent.getLayoutData()).heightHint = 0;
	    }
	    this.selected = selected;
	    this.layout();
	}
	public Composite getContent()
	{
	    return compositeContent;
	}
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
	}
	public boolean isFocusControl()
    {
        return true;
    }
}
