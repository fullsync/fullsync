package net.sourceforge.fullsync.ui;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import net.sourceforge.fullsync.Processor;
import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.ProfileManager;
import net.sourceforge.fullsync.ProfilesChangeListener;
import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.TaskGenerationListener;
import net.sourceforge.fullsync.TaskTree;
import net.sourceforge.fullsync.fs.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
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
public class MainWindow extends org.eclipse.swt.widgets.Composite implements ProfilesChangeListener, TaskGenerationListener
{
    private ProfileManager profileManager;
    private ToolItem toolItemNew;
    private TableColumn tableColumnName;
    private StatusLine statusLine;
    private ToolBar toolBar2;
    private CoolItem coolItem2;
    private TableColumn tableColumnDestination;
    private TableColumn tableColumnSource;
    private TableColumn tableColumnLastUpdate;
    private Table tableProfiles;
    private ToolItem toolItemSchedule;
    private ToolItem toolItemRun;
    private ToolItem toolItemDelete;
    private ToolItem toolItemEdit;
    private ToolBar toolBar1;
    private CoolItem coolItem1;
    private CoolBar coolBar;
    private Processor processor;
    private ArrayList images;
    private Image imageTimerRunning;
    private Image imageTimerStopped;
    
	public MainWindow(Composite parent, int style) 
	{
		super(parent, style);
		images = new ArrayList();
		initGUI();
		
		getShell().addShellListener(new ShellAdapter() {
		    public void shellClosed(ShellEvent event) {
		        // TODO add some config stuff, so we can change this behavior to dispose
		        event.doit = false;
		        minimizeToTray();
		    }
		} );
	}

	/**
	* Initializes the GUI.
	* Auto-generated code - any changes you make will disappear.
	*/
	public void initGUI(){
		try {
			preInitGUI();

			this.setSize(629, 199);

			GridLayout thisLayout = new GridLayout();
			thisLayout.horizontalSpacing = 0;
			thisLayout.marginHeight = 0;
			thisLayout.marginWidth = 0;
			thisLayout.verticalSpacing = 0;
			this.setLayout(thisLayout);
            {
                coolBar = new CoolBar(this, SWT.NONE);
                coolBar.setLocked(false);
                {
                    coolItem1 = new CoolItem(coolBar, SWT.NONE);
                    {
                        toolBar1 = new ToolBar(coolBar, SWT.FLAT);
                        {
                            toolItemNew = new ToolItem(toolBar1, SWT.PUSH);
                            toolItemNew
                                .addSelectionListener(new SelectionAdapter() {
                                    public void widgetSelected(
                                        SelectionEvent evt) {
                                        toolItemNewWidgetSelected(evt);
                                    }
                                });
                        }
                        {
                            toolItemEdit = new ToolItem(toolBar1, SWT.PUSH);
                            toolItemEdit
                                .addSelectionListener(new SelectionAdapter() {
                                    public void widgetSelected(
                                        SelectionEvent evt) {
                                        toolItemEditWidgetSelected(evt);
                                    }
                                });
                        }
                        {
                            toolItemDelete = new ToolItem(toolBar1, SWT.PUSH);
                            toolItemDelete
                                .addSelectionListener(new SelectionAdapter() {
                                    public void widgetSelected(
                                        SelectionEvent evt) {
                                        toolItemDeleteWidgetSelected(evt);
                                    }
                                });
                        }
                        {
                            toolItemRun = new ToolItem(toolBar1, SWT.PUSH);
                            toolItemRun
                                .addSelectionListener(new SelectionAdapter() {
                                    public void widgetSelected(
                                        SelectionEvent evt) {
                                        toolItemRunWidgetSelected(evt);
                                    }
                                });
                        }
                        toolBar1.pack();
                    }
                    coolItem1.setControl(toolBar1);
                    //coolItem1.setMinimumSize(new org.eclipse.swt.graphics.Point(128, 22));
                    coolItem1.setPreferredSize(new org.eclipse.swt.graphics.Point(128, 22));
                }
                {
                    coolItem2 = new CoolItem(coolBar, SWT.NONE);
                    coolItem2.setSize(494, 22);
                    coolItem2
                        .setMinimumSize(new org.eclipse.swt.graphics.Point(
                            24,
                            22));
                    coolItem2
                        .setPreferredSize(new org.eclipse.swt.graphics.Point(
                            24,
                            22));
                    coolItem2.setText("coolItem2");
                    {
                        toolBar2 = new ToolBar(coolBar, SWT.FLAT);
                        coolItem2.setControl(toolBar2);
                        {
                            toolItemSchedule = new ToolItem(toolBar2, SWT.PUSH);
                            toolItemSchedule
                                .addSelectionListener(new SelectionAdapter() {
                                    public void widgetSelected(
                                        SelectionEvent evt) {
                                        toolItemScheduleWidgedSelected(evt);
                                    }
                                });
                        }
                    }
                }
                GridData coolBarLData = new GridData();
                coolBarLData.grabExcessHorizontalSpace = true;
                coolBarLData.horizontalAlignment = GridData.FILL;
                coolBarLData.verticalAlignment = GridData.FILL;
                coolBar.setLayoutData(coolBarLData);
            }
            {
                tableProfiles = new Table(this, SWT.FULL_SELECTION | SWT.BORDER);
                {
                    tableColumnName = new TableColumn(tableProfiles, SWT.NONE);
                    tableColumnName.setText("Name");
                    tableColumnName.setWidth(100);
                }
                {
                    tableColumnLastUpdate = new TableColumn(
                        tableProfiles,
                        SWT.NONE);
                    tableColumnLastUpdate.setText("Last Update");
                    tableColumnLastUpdate.setWidth(100);
                }
                {
                    tableColumnSource = new TableColumn(tableProfiles, SWT.NONE);
                    tableColumnSource.setText("Source");
                    tableColumnSource.setWidth(200);
                }
                {
                    tableColumnDestination = new TableColumn(
                        tableProfiles,
                        SWT.NONE);
                    tableColumnDestination.setText("Destination");
                    tableColumnDestination.setWidth(200);
                }
                tableProfiles.setHeaderVisible(true);
                tableProfiles.setLinesVisible(false);

                GridData tableProfilesLData = new GridData();
                tableProfilesLData.grabExcessHorizontalSpace = true;
                tableProfilesLData.grabExcessVerticalSpace = true;
                tableProfilesLData.horizontalAlignment = GridData.FILL;
                tableProfilesLData.verticalAlignment = GridData.FILL;
                tableProfiles.setLayoutData(tableProfilesLData);
            }
            {
                statusLine = new StatusLine(this, SWT.NONE);
                GridData statusLineLData = new GridData();
                statusLineLData.grabExcessHorizontalSpace = true;
                statusLineLData.horizontalAlignment = GridData.FILL;
                statusLine.setLayoutData(statusLineLData);
            }
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
	public void postInitGUI()
	{
		Image i;
		i = LogWindow.loadImage( "Button_New.gif" );
		toolItemNew.setImage( i );
		images.add( i );
		i = LogWindow.loadImage( "Button_Edit.gif" );
		toolItemEdit.setImage( i );
		images.add( i );
		i = LogWindow.loadImage( "Button_Delete.gif" );
		toolItemDelete.setImage( i );
		images.add( i );
		i = LogWindow.loadImage( "Button_Run.gif" );
		toolItemRun.setImage( i );
		images.add( i );
		i = LogWindow.loadImage( "Timer_Running.gif" );
		imageTimerRunning = i;
		images.add( i );
		i = LogWindow.loadImage( "Timer_Stopped.gif" );
		imageTimerStopped = i;
		images.add( i );
		//toolBar1.layout();
	}
	public void dispose()
    {
        for( Iterator i = images.iterator(); i.hasNext(); )
        {
            Image image = (Image)i.next();
            image.dispose();
        }
        super.dispose();
    }
	public StatusLine getStatusLine()
	{
	    return statusLine;
	}
	public Processor getProcessor()
    {
        return processor;
    }
    public void setProcessor( Processor processor )
    {
        if( this.processor != null )
            this.processor.removeTaskGenerationListener( this );
        this.processor = processor;
        if( this.processor != null )
            this.processor.addTaskGenerationListener( this );
    }
	public void setProfileManager( ProfileManager profileManager )
	{
	    if( this.profileManager != null )
	        this.profileManager.removeChangeListener( this );
	    this.profileManager = profileManager;
	    this.profileManager.addChangeListener( this );
	    populateProfileList();
	    updateTimerEnabled();
	}
	public ProfileManager getProfileManager()
	{
	    return profileManager;
	}
	
	public void populateProfileList()
	{
	    if( profileManager != null )
	    {
	        tableProfiles.clearAll();
	        tableProfiles.setItemCount(0);
	        Enumeration e = profileManager.getProfiles();
	        while( e.hasMoreElements() )
	        {
	            Profile p = (Profile)e.nextElement();
	            TableItem item = new TableItem( tableProfiles, SWT.NULL );
	            item.setText( new String[] { 
	                    p.getName(),
	                    p.getLastUpdate().toString(),
	                    p.getSource().toString(),
	                    p.getDestination().toString() } );
	        }
	        tableColumnName.pack();
	        tableColumnLastUpdate.pack();
	        tableColumnSource.pack();
	        tableColumnDestination.pack();
	    }
	    
	}

	protected void minimizeToTray() 
	{
	    // on OSX use this: 
	    //    mainWindow.setMinimized(true);
	    getShell().setMinimized(true);
	    getShell().setVisible(false);
	    // TODO make sure Tray is visible here
	}
	
	// TODO this should be an event caught from ProfileManager
	protected void updateTimerEnabled()
	{
	    ProfileManager pm = getProfileManager();
	    if( pm == null ) 
	        return;
	    
	    if( pm.isTimerEnabled() )
	    {
	        toolItemSchedule.setImage( imageTimerRunning );
	        toolItemSchedule.setToolTipText( "Suspend Timer" );
	    } else {
	        toolItemSchedule.setImage( imageTimerStopped );
	        toolItemSchedule.setToolTipText( "Start Timer" );
	    }
	}
	
    public void taskTreeStarted( TaskTree tree )
    {
    }
    public void taskGenerationStarted( final File source, final File destination )
    {
        statusLine.setMessage( "checking "+source.getPath() );
    }
    public void taskGenerationFinished( Task task )
    {
        
    }
    public void taskTreeFinished( TaskTree tree )
    {
        statusLine.setMessage( "synchronization finished");
    }
    public void profilesChanged()
    {
        getDisplay().asyncExec( new Runnable() {
            public void run()
            {
                populateProfileList();
            }
        } );
    }
	protected void toolItemRunWidgetSelected(SelectionEvent evt)
	{
		TableItem[] items = tableProfiles.getSelection();
		if( items.length == 0 )
		    return;
		    
		TableItem i = items[0];
		final Profile p = getProfileManager().getProfile( i.getText( 0 ) );
		if( p == null )
		    return;
		
		
	    Thread worker = new Thread( new Runnable() {
	        public void run()
            {
				TaskTree t;
                try {
                    statusLine.setMessage( "Starting profile "+p.getName()+"..." );
                    t = getProcessor().execute( p );
                    statusLine.setMessage( "Finished profile "+p.getName() );
                    LogWindow.show( t );
                } catch( Exception e ) {
                    e.printStackTrace();
                }
            }
	    });
	    worker.start();
	}

	protected void toolItemNewWidgetSelected(SelectionEvent evt)
	{
		ProfileDetails.showProfile( getProfileManager(), null );
	}

	protected void toolItemEditWidgetSelected(SelectionEvent evt)
	{
		TableItem[] items = tableProfiles.getSelection();
		if( items.length == 0 )
		    return;
		    
		TableItem i = items[0];
		Profile p = getProfileManager().getProfile( i.getText( 0 ) );
		if( p == null )
		    return;

		ProfileDetails.showProfile( getProfileManager(), p.getName() );
	}

	protected void toolItemDeleteWidgetSelected(SelectionEvent evt)
	{
		TableItem[] items = tableProfiles.getSelection();
		if( items.length == 0 )
		    return;
		    
		TableItem i = items[0];
		Profile p = getProfileManager().getProfile( i.getText( 0 ) );
		if( p == null )
		    return;

		MessageBox mb = new MessageBox( getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO );
		mb.setText( "Confirmation" );
	    mb.setMessage( "Do you really want to delete profile "+p.getName()+" ?");
	    if( mb.open() == SWT.YES )
	    {
	        profileManager.removeProfile( p.getName() );
	        profileManager.save();
	    }
	}
    protected void toolItemScheduleWidgedSelected(SelectionEvent evt)
    {
        if( getProfileManager().isTimerEnabled() )
        {
            getProfileManager().stopTimer();
        } else {
            getProfileManager().startTimer();
        }
        updateTimerEnabled();
    }
}
