package net.sourceforge.fullsync.ui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import net.sourceforge.fullsync.Action;
import net.sourceforge.fullsync.ActionQueue;
import net.sourceforge.fullsync.IoStatistics;
import net.sourceforge.fullsync.Location;
import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.TaskFinishedEvent;
import net.sourceforge.fullsync.TaskFinishedListener;
import net.sourceforge.fullsync.TaskTree;
import net.sourceforge.fullsync.buffer.BlockBuffer;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.impl.FillBufferActionQueue;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

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
public class LogWindow extends org.eclipse.swt.widgets.Composite 
{
	private Label labelProgress;
	private Combo comboFilter;
	private Label labelDestination;
	private Label labelImage;
	private Label labelCaption;
	private Composite compositeBottom;
	private Label labelSeparatorBottom;
	private Label labelSeparatorTop;
	private Composite compositeTop;
	private Label labelSource;
	private Button buttonGo;
	private TableColumn tableColumn1;
	private TableColumn tableColumnExplanation;
	private TableColumn tableColumnFilename;
	private TableColumn tableColumnAction;
	private TableColumn tableColumnSourceSize;
	private Table tableLogLines;
	private int tableLogLinesFillIndex;
	private int tableLogLinesFillCount;
	
	private Hashtable actionImages;
	private Hashtable taskImages;
	private Image locationSource;
	private Image locationDestination;
	private Image locationBoth;
	private Image nodeFile;
	private Image nodeDirectory;
	private Image nodeUndefined;
	
	private GuiController guiController;
	private TaskTree taskTree;
	
	private boolean onlyChanges;
	private boolean processing;
	private int tasksFinished;
	private int tasksTotal;
	private Object mutex = new Object();
	
	
	public LogWindow(Composite parent, int style) 
	{
		super(parent, style);
		initGUI();
		initializeImages();
		onlyChanges = true;
		processing = false;
		guiController = null;
	}

	/**
	* Initializes the GUI.
	* Auto-generated code - any changes you make will disappear.
	*/
	public void initGUI(){
		try {
			preInitGUI();
	
			Color white = getDisplay().getSystemColor( SWT.COLOR_WHITE );
            {
                compositeTop = new Composite(this, SWT.NONE);
                compositeTop.setBackground( white );
                FormLayout compositeTopLayout = new FormLayout();
                GridData compositeTopLData = new GridData();
                compositeTopLData.grabExcessHorizontalSpace = true;
                compositeTopLData.horizontalSpan = 3;
                compositeTopLData.horizontalAlignment = GridData.FILL;
                compositeTopLData.heightHint = 64;
                compositeTop.setLayoutData(compositeTopLData);
                compositeTop.setLayout(compositeTopLayout);
                {
                    labelCaption = new Label(compositeTop, SWT.NONE);
                    labelCaption.setBackground(white);
                    labelCaption.setText("Choose the actions that should be performed.");
                    labelCaption.setFont( new Font( getDisplay(), "Tohama", 9, SWT.BOLD ) );
                    FormData labelCaptionLData = new FormData();
                    labelCaptionLData.width = 330;
                    labelCaptionLData.height = 16;
                    labelCaptionLData.left =  new FormAttachment(0, 1000, 12);
                    labelCaptionLData.right =  new FormAttachment(627, 1000, 0);
                    labelCaptionLData.top =  new FormAttachment(164, 1000, 0);
                    labelCaptionLData.bottom =  new FormAttachment(414, 1000, 0);
                    labelCaption.setLayoutData(labelCaptionLData);
                    // TODO dispose font !!
                }
                {
                    labelSource = new Label(compositeTop, SWT.NONE);
                    labelSource.setBackground(white);
                    FormData labelSourceLData = new FormData();
                    labelSource.setText("Source: ");
                    labelSourceLData.width = 289;
                    labelSourceLData.height = 14;
                    labelSourceLData.left =  new FormAttachment(65, 1000, 0);
                    labelSourceLData.right =  new FormAttachment(594, 1000, 0);
                    labelSourceLData.top =  new FormAttachment(429, 1000, 0);
                    labelSourceLData.bottom =  new FormAttachment(648, 1000, 0);
                    labelSource.setLayoutData(labelSourceLData);
                }
                {
                    labelDestination = new Label(compositeTop, SWT.NONE);
                    labelDestination.setBackground(white);
                    FormData labelDestinationLData = new FormData();
                    labelDestination.setText("Destination:");
                    labelDestinationLData.width = 381;
                    labelDestinationLData.height = 17;
                    labelDestinationLData.left =  new FormAttachment(65, 1000, 0);
                    labelDestinationLData.right =  new FormAttachment(762, 1000, 0);
                    labelDestinationLData.top =  new FormAttachment(695, 1000, 0);
                    labelDestinationLData.bottom =  new FormAttachment(960, 1000, 0);
                    labelDestination.setLayoutData(labelDestinationLData);
                }
                {
                    labelImage = new Label(compositeTop, SWT.NONE);
                    labelImage.setImage( new Image( getDisplay(), "images/Tasklist_Wizard.png") );
                    FormData labelImageLData = new FormData();
                    labelImageLData.width = 64;
                    labelImageLData.height = 64;
                    labelImageLData.left =  new FormAttachment(883, 1000, 0);
                    labelImageLData.right =  new FormAttachment(1000, 1000, 0);
                    labelImageLData.top =  new FormAttachment(7, 1000, 0);
                    labelImageLData.bottom =  new FormAttachment(1007, 1000, 0);
                    labelImage.setLayoutData(labelImageLData);
                }
            }
            {
                labelSeparatorTop = new Label(this, SWT.SEPARATOR
                    | SWT.HORIZONTAL);
                GridData labelSeparatorTopLData = new GridData();
                labelSeparatorTopLData.grabExcessHorizontalSpace = true;
                labelSeparatorTopLData.horizontalSpan = 3;
                labelSeparatorTopLData.horizontalAlignment = GridData.FILL;
                labelSeparatorTop.setLayoutData(labelSeparatorTopLData);
            }
			tableLogLines = new Table(this,SWT.FULL_SELECTION | SWT.MULTI);

			this.setSize(546, 395);
	
			GridData tableLogLinesLData = new GridData();
			tableLogLinesLData.verticalAlignment = GridData.FILL;
			tableLogLinesLData.horizontalAlignment = GridData.FILL;
			tableLogLinesLData.widthHint = -1;
			tableLogLinesLData.heightHint = -1;
			tableLogLinesLData.horizontalIndent = 0;
			tableLogLinesLData.horizontalSpan = 3;
			tableLogLinesLData.verticalSpan = 1;
			tableLogLinesLData.grabExcessHorizontalSpace = true;
			tableLogLinesLData.grabExcessVerticalSpace = true;
			tableLogLines.setLayoutData(tableLogLinesLData);
			tableLogLines.setHeaderVisible(true);
			tableLogLines.setLinesVisible(true);
			tableLogLines.setSize(new org.eclipse.swt.graphics.Point(690,179));
            {
                labelSeparatorBottom = new Label(this, SWT.SEPARATOR
                    | SWT.HORIZONTAL);
                GridData labelSeparatorBottomLData = new GridData();
                labelSeparatorBottomLData.grabExcessHorizontalSpace = true;
                labelSeparatorBottomLData.horizontalAlignment = GridData.FILL;
                labelSeparatorBottom.setLayoutData(labelSeparatorBottomLData);
            }
            {
                tableColumn1 = new TableColumn(tableLogLines, SWT.NONE);
                tableColumn1.setResizable(false);
                tableColumn1.setText("tableColumn1");
            }
            {
                tableColumnFilename = new TableColumn(tableLogLines, SWT.NONE);
                tableColumnFilename.setText("Filename");
                tableColumnFilename.setWidth(238);
            }
            {
                tableColumnSourceSize = new TableColumn(tableLogLines, SWT.NONE);
                tableColumnSourceSize.setText("Size");
                tableColumnSourceSize.setWidth(80);
            }
            {
                tableColumnAction = new TableColumn(tableLogLines, SWT.NONE);
                tableColumnAction.setResizable(false);
                tableColumnAction.setText("Action");
                tableColumnAction.setWidth(50);
            }
            {
                tableColumnExplanation = new TableColumn(
                    tableLogLines,
                    SWT.NONE);
                tableColumnExplanation.setText("Explanation");
                tableColumnExplanation.setWidth(154);
            }
			tableLogLines.addMouseListener( new MouseAdapter() {
				public void mouseUp(MouseEvent evt) {
					tableLogLinesMouseUp(evt);
				}
			});

            {
                compositeBottom = new Composite(this, SWT.NONE);
                GridLayout compositeBottomLayout = new GridLayout();
                GridData compositeBottomLData = new GridData();
                compositeBottomLData.grabExcessHorizontalSpace = true;
                compositeBottomLData.horizontalAlignment = GridData.FILL;
                compositeBottom.setLayoutData(compositeBottomLData);
                compositeBottomLayout.numColumns = 3;
                compositeBottom.setLayout(compositeBottomLayout);
                {
                    comboFilter = new Combo(compositeBottom, SWT.DROP_DOWN
                        | SWT.READ_ONLY);
                    GridData comboFilterLData = new GridData();
                    comboFilterLData.widthHint = 68;
                    comboFilterLData.heightHint = 21;
                    comboFilter.setLayoutData(comboFilterLData);
                    comboFilter.addModifyListener(new ModifyListener() {
                        public void modifyText(ModifyEvent evt) {
                            comboFilterModifyText(evt);
                        }
                    });
                }
                {
                    labelProgress = new Label(compositeBottom, SWT.NONE);
                    GridData labelProgressLData = new GridData();
                    labelProgressLData.horizontalAlignment = GridData.FILL;
                    labelProgressLData.heightHint = 13;
                    labelProgressLData.horizontalIndent = 5;
                    labelProgressLData.grabExcessHorizontalSpace = true;
                    labelProgress.setLayoutData(labelProgressLData);
                    labelProgress.setSize(new org.eclipse.swt.graphics.Point( 42, 13));
                }
                {
                    buttonGo = new Button(compositeBottom, SWT.PUSH
                        | SWT.CENTER);
                    GridData buttonGoLData = new GridData();
                    buttonGoLData.horizontalAlignment = GridData.END;
                    buttonGoLData.widthHint = 25;
                    buttonGoLData.heightHint = 23;
                    buttonGo.setLayoutData(buttonGoLData);
                    buttonGo.setText("Go");
                    buttonGo.addSelectionListener(new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent evt) {
                            buttonGoWidgetSelected(evt);
                        }
                    });
                }
            }
			GridLayout thisLayout = new GridLayout();
			this.setLayout(thisLayout);
			thisLayout.marginWidth = 0;
			thisLayout.marginHeight = 0;
			thisLayout.horizontalSpacing = 0;
			thisLayout.verticalSpacing = 0;
			this.layout();
	
			postInitGUI();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/** Add your pre-init code in here 	*/
	public void preInitGUI(){
	}

	/** Add your post-init code in here 	*/
	public void postInitGUI(){
	    comboFilter.add( "Everything" );
	    comboFilter.add( "Changes only" );
	    comboFilter.select(1);
	    
		getShell().addShellListener(new ShellAdapter() {
		    public void shellClosed(ShellEvent event) {
		    	if (processing) {
					MessageBox mb = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
					mb.setText("Error");
					mb.setMessage("The Synchronization Window can't be closed during Synchronization.");
					mb.open();

					event.doit = false;
		    	}
		    }

		} );

	}

	public static void show( final GuiController guiController, final TaskTree task )
	{
		final Display display = Display.getDefault();
		display.syncExec( new Runnable() {
		    public void run()
            {
		        try {
					Shell shell = new Shell(display);
					LogWindow inst = new LogWindow(shell, SWT.NULL);
					inst.setGuiController( guiController );
					inst.setTaskTree( task );
					inst.rebuildActionList();
					shell.setLayout(new org.eclipse.swt.layout.FillLayout());
					Rectangle shellBounds = shell.computeTrim(0,0,inst.getSize().x,inst.getSize().y);
					shell.setSize(shellBounds.width, shellBounds.height);
					shell.setText( "Synchronization Actions" );
					shell.setImage( new Image( null, "images/Tasklist_Icon.gif" ) );
					shell.open();
		        } catch( Exception ex ) {
		            ex.printStackTrace();
		        }
            }
		});
	}
	public void setGuiController( GuiController guiController )
	{
	    this.guiController = guiController;
	}
	public GuiController getGuiController()
    {
        return guiController;
    }
	public void setTaskTree( TaskTree task )
	{
	    this.taskTree = task;
	    
	    labelSource.setText( "Source: "+task.getSource().getUri() );
	    labelSource.pack();
	    labelDestination.setText( "Destination: "+task.getDestination().getUri() );
	    labelDestination.pack();
	}
	public static Image loadImage( String filename )
	{
	    try {
	        return new Image( null, new FileInputStream( "images/"+filename) );
        } catch( FileNotFoundException e ) {
            e.printStackTrace();
        }
        return null;
	}
	public void initializeImages()
	{
	    nodeFile = loadImage( "Node_File.gif" );
	    nodeDirectory = loadImage( "Node_Directory.gif" );
	    nodeUndefined = loadImage( "Node_Undefined.gif" );
	    locationSource = loadImage( "Location_Source.gif" );
	    locationDestination = loadImage( "Location_Destination.gif" );
	    locationBoth = loadImage( "Location_Both.gif" );
	    	    
	    actionImages = new Hashtable();
	    for( int i = 0; i < Action.names.length; i++ )
	    {
	        actionImages.put( new Integer( i ), loadImage( "Action_"+Action.names[i]+".gif" ) );
	    }
	    for( int i = 0; i < Action.errorNames.length; i++ )
	    {
	        actionImages.put( new Integer( i+10 ), loadImage( "Action_"+Action.errorNames[i]+".gif" ) );
	    }
	    
	    taskImages = new Hashtable();
	}
	public void dispose()
	{
	    nodeFile.dispose();
	    nodeDirectory.dispose();
	    nodeUndefined.dispose();
	    locationSource.dispose();
	    locationDestination.dispose();
	    locationBoth.dispose();
	    
	    Enumeration e;
	    e = actionImages.elements();
	    while( e.hasMoreElements() )
	    {
	        ((Image)e.nextElement()).dispose();
	    }
	    e = taskImages.elements();
	    while( e.hasMoreElements() )
	    {
	        ((Image)e.nextElement()).dispose();
	    }
	    
	    super.dispose();
	}
	protected void drawSide( GC g, Task t, Action a, int location )
	{
	    File n;
	    if( t == null ) {
	        n = null;
	    } else if( location==Location.Source ) { 
	        n = t.getSource();
	    } else { 
	        n = t.getDestination();
	    }
	    
	    int  x = location==Location.Source?2:2*16+2;

	    if( n == null )
	    {
	        g.drawImage( nodeUndefined, x, 0 );
	    } else if( n.exists() ) {
	        if( n.isDirectory() )
	             g.drawImage( nodeDirectory, x, 0 );
	        else g.drawImage( nodeFile, x, 0 );
	    }
	    // TODO draw some not-existing image ?
	    
	    if( (a.getLocation() & location) > 0 )
	    {
	        Image actionImage = (Image)actionImages.get( new Integer( a.getType() ) );
	        if( actionImage != null )
	             g.drawImage( actionImage, x, 0 );
	        if( location == Location.Source )
	             g.drawImage( locationSource, x+16, 0 );
	        else g.drawImage( locationDestination, x-16, 0 );
	    }
	}
	protected void drawLocation( GC g, Action a )
	{
	    switch( a.getLocation() )
	    {
	    case Location.Source:
	        g.drawImage( locationSource, 16+2, 0 );
	    	break;
	    case Location.Destination:
	        g.drawImage( locationDestination, 16+2, 0 );
	    	break;
	    case Location.Both:
	        g.drawImage( locationBoth, 16+2, 0 );
	    	break;
	    }
	}
	protected Object calcTaskImageHash( Task t, Action a )
	{
	    int hash = 0;
	    
	    // using 5 bits for files
	    if( t == null )
	    {
	        hash |= 1;
	    } else {
	        File src = t.getSource();
	        File dst = t.getDestination();
	        if( src.exists() )
	        {
	            hash |= 2;
	            if( src.isDirectory() )
	                hash |= 4;
	        }
	        if( dst.exists() )
	        {
	            hash |= 8;
	            if( dst.isDirectory() )
	                hash |= 16;
	        }
	    }
	    
	    // using 2+ bits for action
	    hash |= (a.getLocation() << 6);
	    hash |= (a.getType() << 8);
	    
	    return new Integer( hash );
	}
	protected Image buildTaskImage( Task t, Action a )
	{
	    ImageData data = new ImageData( 16*3+2, 16, 8, new PaletteData( 255, 255, 255 ) );
	    data.transparentPixel = data.palette.getPixel( new RGB( 0, 0, 0 ) );
	    
	    Image image = new Image( null, data );
	    GC g = new GC(image);
        drawSide( g, t, a, Location.Source );
        drawSide( g, t, a, Location.Destination );
        drawLocation( g, a );
        g.dispose();
        return image;
	}
	protected Image getTaskImage( Task t, Action a )
	{
	    Image image;
	    Object key = calcTaskImageHash( t, a );
	    Object value = taskImages.get( key );
	    if( value == null )
	    {
	        image = buildTaskImage( t, a );
	        taskImages.put( key, image );
	    } else {
	        image = (Image)value;
	    }
	    return image;
	}
	protected Image getTaskImage( Action a )
	{
	    return getTaskImage( null, a );
	}
	protected Image getTaskImage( Task t )
	{
	    return getTaskImage( t, t.getCurrentAction() );
	}
	protected void addTaskChildren( Task t )
	{
	    for( Enumeration e = t.getChildren(); e.hasMoreElements(); )
	        addTask( (Task)e.nextElement() );
	}
    protected void addTask( Task t )
    {
        if( !onlyChanges || t.getCurrentAction().getType() != Action.Nothing )
        {
	        Image image = getTaskImage( t );
	        
	        TableItem item;
	        if( tableLogLinesFillIndex < tableLogLinesFillCount ) {
	            item = tableLogLines.getItem( tableLogLinesFillIndex );
	            tableLogLinesFillIndex++;
	        } else {
	            item = new TableItem( tableLogLines, SWT.NULL );
	            tableLogLinesFillIndex++;
	            tableLogLinesFillCount++;
	        }
	        item.setImage( 3, image );
	        item.setText( new String[] {
	            "",
	            t.getSource().getPath(),
	            Long.toString(t.getSource().getFileAttributes()!=null?t.getSource().getFileAttributes().getLength():0L)+" bytes",
	            "",
	            t.getCurrentAction().getExplanation() 
	        } );
	        item.setData( t );
	        
	        // putting the tableitem in the data slot of the task.
	        t.setData(item);
        }
        addTaskChildren( t );
    }
    protected void updateTask( TableItem item )
    {
        Task t = (Task)item.getData();
        Image image = getTaskImage( t );
        item.setImage( 3, image );
        item.setText( 4, t.getCurrentAction().getExplanation() ); 
    }
    public void rebuildActionList()
    {
        //tableLogLines.clearAll();
        //tableLogLines.setItemCount(0);
        
        tableLogLinesFillIndex = 0;
        tableLogLinesFillCount = tableLogLines.getItemCount();
        addTaskChildren( taskTree.getRoot() );
        
        // index is always pointing at the next free slot
        if( tableLogLinesFillIndex < tableLogLinesFillCount )
        {
            tableLogLines.setItemCount( tableLogLinesFillIndex );
            tableLogLinesFillCount = tableLogLines.getItemCount();
        }
    }

    protected void showPopup( int x, int y )
    {
        // TODO investigate whether there is a way to change the list selection
        //      while the context menu is open
        //      -> it would allow strange action changes
        
        // TODO impl some kind of ActionList supporting "containsAction"
        //		and "indexOfAction" using own comparison rules
        
        SelectionListener selListener = new SelectionAdapter() {
            public void widgetSelected( SelectionEvent e )
            {
                TableItem[] tableItemList = tableLogLines.getSelection();
                if( tableItemList.length == 0 )
                    return;
                
                Action targetAction = (Action)e.widget.getData();
                
                for( int iTask = 0; iTask < tableItemList.length; iTask++ )
                {
                    TableItem item = tableItemList[iTask];
                    Task task = (Task)item.getData();
                    Action[] actions = task.getActions();

    	            for( int iAction = 0; iAction < actions.length; iAction++ )
    	            {
    	                Action a = actions[iAction];
    	                if( a.getType() == targetAction.getType() 
    	                        && a.getLocation() == targetAction.getLocation()
    	                        && a.getExplanation().equals( targetAction.getExplanation() ) )
    	                {
    	                    task.setCurrentAction( iAction );
    	                    break;
    	                }
    	            }

                    updateTask( item );
                }
            }
        };
        
        TableItem[] items = tableLogLines.getSelection();
        if( items.length == 0 )
            return;
        
        Task[] taskList = new Task[items.length];
        for( int i = 0; i < items.length; i++ )
            taskList[i] = (Task)items[i].getData();
        
        Menu m = new Menu( this );
        MenuItem mi;

        // load initial actions of first task
        Action[] possibleActions = taskList[0].getActions();
        
        for( int iTask = 1; iTask < taskList.length; iTask++ )
        {
            // invalidate all possible actions we dont find in this actionlist
	        Action[] actions = taskList[iTask].getActions();
	        
	        for( int iPosAction = 0; iPosAction < possibleActions.length; iPosAction++ )
	        {
	            Action action = possibleActions[iPosAction];
	            boolean found = false;
	            
	            if( action == null )
	                continue;
	            
	            // check whether action is also supported by this task
	            for( int iAction = 0; iAction < actions.length; iAction++ )
	            {
	                Action a = actions[iAction];
	                if( a.getType() == action.getType() 
	                        && a.getLocation() == action.getLocation()
	                        && a.getExplanation().equals( action.getExplanation() ) )
	                {
	                    // the action exists
	                    found = true;
	                    break;
	                }
	            }
	            
	            if( !found )
	            {
	                // invalidate action that is not supported by all selected tasks
	                possibleActions[iPosAction] = null;
	            }
	        }
        }
        
        Task referenceTask = taskList.length==1?taskList[0]:null;
        for( int i = 0; i < possibleActions.length; i++ )
        {
            Action action = possibleActions[i];
            
            if( action == null )
                continue;
            
		    Image image = getTaskImage( referenceTask, action );
	        mi = new MenuItem( m, SWT.NULL );
	        mi.setImage( image );
	        mi.setText( Action.toString( action.getType() )+" - "+action.getExplanation() );
	        mi.setData( action );
	        mi.addSelectionListener( selListener );
        }

        m.setLocation( tableLogLines.toDisplay( x, y ) );
        m.setVisible( true );
    }
    protected void performActions()
    {
        Thread worker = new Thread( new Runnable() {
			public void run() {
	            guiController.showBusyCursor( true );
				try {
				    final Display display = getDisplay(); 
		            processing = true;
		            
					// Logger logger = Logger.getRootLogger();
		            // logger.addAppender( new FileAppender( new PatternLayout( "%d{ISO8601} [%p] %c %x - %m%n" ), "log/log.txt" ) );
		            Logger logger = Logger.getLogger( "FullSync" );
			        logger.info( "Synchronization started" );
			        logger.info( "  source:      "+taskTree.getSource().getUri().toString() );
			        logger.info( "  destination: "+taskTree.getDestination().getUri().toString() );
			        
			        // TODO we should use the Synchronizer here ! maybe (TaskTree, ActionFinishedListener) ?
			        BlockBuffer buffer = new BlockBuffer( logger );
			        ActionQueue queue = new FillBufferActionQueue(buffer);
			        final Color colorFinishedSuccessful = new Color( null, 150, 255, 150 );
			        final Color colorFinishedUnsuccessful = new Color( null, 255, 150, 150 );

			        IoStatistics stats = queue.createStatistics( taskTree );
		            tasksTotal = stats.getCountActions();
				    tasksFinished = 0;

			        queue.addTaskFinishedListener( new TaskFinishedListener() {
			        	public void taskFinished( final TaskFinishedEvent event ) 
			        	{
				            display.asyncExec( new Runnable() {
				            	public void run() {
						            tasksFinished++;
						            labelProgress.setText( tasksFinished+" of "+tasksTotal+" tasks finished" );
						            Object taskData = event.getTask().getData();
						            if ((taskData != null) && (taskData instanceof TableItem)) {
						            	TableItem item = (TableItem) taskData;
						            	if( event.isSuccessful() )
						            	     item.setBackground(colorFinishedSuccessful);
						            	else item.setBackground(colorFinishedUnsuccessful);
						            	tableLogLines.showItem(item);
						            }
								}
				            } );
			        	} 
			        } );

			        buffer.load();
			        queue.enqueue( taskTree );
			        queue.flush();
			        buffer.unload();
			        
			        taskTree.getSource().flush();
			        taskTree.getDestination().flush();
			        taskTree.getSource().close();
			        taskTree.getDestination().close();
			        logger.info( "finished synchronization" );
			        
			        getDisplay().asyncExec( new Runnable() {
						public void run() {
			            	// Notification Window before disposal.
							MessageBox mb = new MessageBox( getShell(), SWT.ICON_INFORMATION | SWT.OK );
							mb.setText( "Finished" );
						    mb.setMessage( "Profile execution finished");
						    mb.open();
						    
						    getShell().dispose();
						}
			        } );
					
			        processing = false;
			    } catch( IOException e ) {
			        e.printStackTrace();
			    } catch( Exception e ) {
			        e.printStackTrace();
			    } finally {
			        guiController.showBusyCursor( false );
			    }
			}
        }, "Action Performer" );
        worker.start();
    }
    
	/** Auto-generated event handler method */
	protected void tableLogLinesMouseUp(MouseEvent evt)
	{
		if( !processing && evt.button == 3 )
		{
		    showPopup( evt.x, evt.y );
		}
	}

	/** Auto-generated event handler method */
	protected void buttonGoWidgetSelected(SelectionEvent evt)
	{
	    if( !processing )
	    {
			performActions();
		}
	}

	/** Auto-generated event handler method */
	protected void comboFilterModifyText(ModifyEvent evt)
	{
	    if( !processing )
	    {
	        onlyChanges = comboFilter.getSelectionIndex() == 1;
	        if( taskTree != null )
	            rebuildActionList();
	    }
	}
}
