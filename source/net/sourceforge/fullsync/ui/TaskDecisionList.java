/**
 *	@license
 *	This program is free software; you can redistribute it and/or
 *	modify it under the terms of the GNU General Public License
 *	as published by the Free Software Foundation; either version 2
 *	of the License, or (at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program; if not, write to the Free Software
 *	Foundation, Inc., 51 Franklin Street, Fifth Floor,
 *	Boston, MA  02110-1301, USA.
 *
 *	---
 *	@copyright Copyright (C) 2005, Jan Kopcsek <codewright@gmx.net>
 *	@copyright Copyright (C) 2011, Obexer Christoph <cobexer@gmail.com>
 */
package net.sourceforge.fullsync.ui;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;

import net.sourceforge.fullsync.Action;
import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.Location;
import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.TaskTree;
import net.sourceforge.fullsync.fs.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
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
public class TaskDecisionList extends org.eclipse.swt.widgets.Composite
{
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

	//private GuiController guiController;
	private TaskTree taskTree;
	private final HashMap taskItemMap;

	private boolean onlyChanges;
	private boolean changeAllowed;
	private final Object mutex = new Object();


	public TaskDecisionList(Composite parent, int style)
	{
		super(parent, style);

		this.taskItemMap = new HashMap();

		initGUI();
		initializeImages();
		onlyChanges = true;
		changeAllowed = true;
	}

	/**
	* Initializes the GUI.
	* Auto-generated code - any changes you make will disappear.
	*/
	public void initGUI(){
		try {
			Color white = getDisplay().getSystemColor( SWT.COLOR_WHITE );

			this.setSize(546, 395);

			tableLogLines = new Table(this,SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER);
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
                tableColumn1 = new TableColumn(tableLogLines, SWT.NONE);
                tableColumn1.setResizable(false);
                tableColumn1.setText("tableColumn1"); //$NON-NLS-1$
            }
            {
                tableColumnFilename = new TableColumn(tableLogLines, SWT.NONE);
                tableColumnFilename.setText(Messages.getString("TaskDecisionList.Filename")); //$NON-NLS-1$
                tableColumnFilename.setWidth(238);
            }
            {
                tableColumnSourceSize = new TableColumn(tableLogLines, SWT.NONE);
                tableColumnSourceSize.setText(Messages.getString("TaskDecisionList.Size")); //$NON-NLS-1$
                tableColumnSourceSize.setWidth(80);
            }
            {
                tableColumnAction = new TableColumn(tableLogLines, SWT.NONE);
                tableColumnAction.setResizable(false);
                tableColumnAction.setText(Messages.getString("TaskDecisionList.Action")); //$NON-NLS-1$
                tableColumnAction.setWidth(50);
            }
            {
                tableColumnExplanation = new TableColumn(
                    tableLogLines,
                    SWT.NONE);
                tableColumnExplanation.setText(Messages.getString("TaskDecisionList.Explanation")); //$NON-NLS-1$
                tableColumnExplanation.setWidth(154);
            }
			tableLogLines.addMouseListener( new MouseAdapter() {
				@Override
				public void mouseUp(MouseEvent evt) {
					tableLogLinesMouseUp(evt);
				}
			});

			GridLayout thisLayout = new GridLayout();
			this.setLayout(thisLayout);
			thisLayout.marginWidth = 5;
			thisLayout.marginHeight = 5;
			thisLayout.horizontalSpacing = 0;
			thisLayout.verticalSpacing = 0;
			this.layout();
		} catch (Exception e) {
			ExceptionHandler.reportException( e );
		}
	}
	public static void show( final GuiController guiController, final Profile profile, final TaskTree task, final boolean interactive )
	{
		final Display display = Display.getDefault();
		display.asyncExec( new Runnable() {
		    @Override
			public void run() {
		        try {
		            WizardDialog dialog = new WizardDialog( guiController.getMainShell(), SWT.RESIZE );
				    final TaskDecisionPage page = new TaskDecisionPage( dialog, guiController, profile, task );
				    if (!interactive) {
				    	dialog.addWizardDialogListener(new WizardDialogAdapter() {
				    		@Override
							public void dialogOpened(WizardDialog dialog) {
				    			page.performActions();
				    		}
				    	});
				    }
				    dialog.show();
		        } catch( Exception ex ) {
		            ExceptionHandler.reportException( ex );
		        }
            }
		});
	}
	public void setTaskTree( TaskTree task )
	{
	    this.taskTree = task;
	}
	public void initializeImages()
	{
	    GuiController gui = GuiController.getInstance();
	    nodeFile = gui.getImage( "Node_File.png" ); //$NON-NLS-1$
	    nodeDirectory = gui.getImage( "Node_Directory.png" ); //$NON-NLS-1$
	    nodeUndefined = gui.getImage( "Node_Undefined.png" ); //$NON-NLS-1$
	    locationSource = gui.getImage( "Location_Source.png" ); //$NON-NLS-1$
	    locationDestination = gui.getImage( "Location_Destination.png" ); //$NON-NLS-1$
	    locationBoth = gui.getImage( "Location_Both.png" ); //$NON-NLS-1$

	    actionImages = new Hashtable();
	    for( int i = 0; i < Action.names.length; i++ )
	    {
	        actionImages.put( new Integer( i ), gui.getImage( "Action_"+Action.names[i]+".png" ) ); //$NON-NLS-1$ //$NON-NLS-2$
	    }
	    for( int i = 0; i < Action.errorNames.length; i++ )
	    {
	        actionImages.put( new Integer( i+10 ), gui.getImage( "Action_"+Action.errorNames[i]+".png" ) ); //$NON-NLS-1$ //$NON-NLS-2$
	    }

	    taskImages = new Hashtable();
	}
	@Override
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
	            "", //$NON-NLS-1$
	            t.getSource().getPath(),
	            Long.toString(t.getSource().getFileAttributes()!=null?t.getSource().getFileAttributes().getLength():0L)+" bytes", //$NON-NLS-1$
	            "", //$NON-NLS-1$
	            t.getCurrentAction().getExplanation()
	        } );
	        item.setData( t );

	        // putting the tableitem in the data slot of the task.
//	        t.setData(item);
	        taskItemMap.put(t, item);
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

        setRedraw(false);
        addTaskChildren( taskTree.getRoot() );
        setRedraw(true);

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
            @Override
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
        Action[] possibleActions = taskList[0].getActions().clone();

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
	        mi.setText( Action.toString( action.getType() )+" - "+action.getExplanation() ); //$NON-NLS-1$
	        mi.setData( action );
	        mi.addSelectionListener( selListener );
        }

        m.setLocation( tableLogLines.toDisplay( x, y ) );
        m.setVisible( true );
    }

    public void setOnlyChanges( boolean onlyChanges )
    {
        this.onlyChanges = onlyChanges;
    }
    public boolean isChangeAllowed()
    {
        return changeAllowed;
    }
    public void setChangeAllowed( boolean changeAllowed )
    {
        this.changeAllowed = changeAllowed;
    }
    public void showItem( TableItem item )
    {
        tableLogLines.showItem( item );
    }

	/** Auto-generated event handler method */
	protected void tableLogLinesMouseUp(MouseEvent evt)
	{
		if( changeAllowed && evt.button == 3 )
		{
		    showPopup( evt.x, evt.y );
		}
	}
	public TableItem getTableItemForTask(Task task) {
		return (TableItem) taskItemMap.get(task);
	}
}
