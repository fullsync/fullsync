package net.sourceforge.fullsync.ui;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import net.sourceforge.fullsync.Processor;
import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.ProfileManager;
import net.sourceforge.fullsync.ProfilesChangeListener;
import net.sourceforge.fullsync.TaskTree;

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
public class MainWindow extends org.eclipse.swt.widgets.Composite implements ProfilesChangeListener 
{
	private ToolItem toolItemDelete;
	private ToolItem toolItemEdit;
	private ToolItem toolItemNew;
	private ToolItem toolItemRun;
	private ToolBar toolBar1;
	private CoolItem coolItem1;
	private CoolBar coolBar;
    private ProfileManager profileManager;
    private Processor processor;
	private TableColumn tableColumnDestination;
	private TableColumn tableColumnSource;
	private TableColumn tableColumnLastUpdate;
	private TableColumn tableColumnName;
	private Table tableProfiles;
    private ArrayList images;
    
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
	
			coolBar = new CoolBar(this,SWT.NULL);
			coolItem1 = new CoolItem(coolBar,SWT.NULL);
			toolBar1 = new ToolBar(coolBar,SWT.FLAT);
			toolItemNew = new ToolItem(toolBar1,SWT.NULL);
			toolItemEdit = new ToolItem(toolBar1,SWT.NULL);
			toolItemDelete = new ToolItem(toolBar1,SWT.NULL);
			toolItemRun = new ToolItem(toolBar1,SWT.NULL);
			tableProfiles = new Table(this,SWT.FULL_SELECTION| SWT.BORDER);
			tableColumnName = new TableColumn(tableProfiles,SWT.NULL);
			tableColumnLastUpdate = new TableColumn(tableProfiles,SWT.NULL);
			tableColumnSource = new TableColumn(tableProfiles,SWT.NULL);
			tableColumnDestination = new TableColumn(tableProfiles,SWT.NULL);
	
			this.setSize(new org.eclipse.swt.graphics.Point(635,223));
	
			GridData coolBarLData = new GridData();
			coolBarLData.verticalAlignment = GridData.CENTER;
			coolBarLData.horizontalAlignment = GridData.FILL;
			coolBarLData.widthHint = -1;
			coolBarLData.heightHint = -1;
			coolBarLData.horizontalIndent = 0;
			coolBarLData.horizontalSpan = 1;
			coolBarLData.verticalSpan = 1;
			coolBarLData.grabExcessHorizontalSpace = true;
			coolBarLData.grabExcessVerticalSpace = false;
			coolBar.setLayoutData(coolBarLData);
	
			coolItem1.setControl(toolBar1);
			coolItem1.setMinimumSize(new org.eclipse.swt.graphics.Point(31,21));
			coolItem1.setPreferredSize(new org.eclipse.swt.graphics.Point(31,21));
	
	
			toolItemNew.addSelectionListener( new SelectionAdapter() {
				public void widgetSelected(SelectionEvent evt) {
					toolItemNewWidgetSelected(evt);
				}
			});
	
			toolItemEdit.addSelectionListener( new SelectionAdapter() {
				public void widgetSelected(SelectionEvent evt) {
					toolItemEditWidgetSelected(evt);
				}
			});
	
			toolItemDelete.addSelectionListener( new SelectionAdapter() {
				public void widgetSelected(SelectionEvent evt) {
					toolItemDeleteWidgetSelected(evt);
				}
			});
	
			toolItemRun.addSelectionListener( new SelectionAdapter() {
				public void widgetSelected(SelectionEvent evt) {
					toolItemRunWidgetSelected(evt);
				}
			});
	
			GridData tableProfilesLData = new GridData();
			tableProfilesLData.verticalAlignment = GridData.FILL;
			tableProfilesLData.horizontalAlignment = GridData.FILL;
			tableProfilesLData.widthHint = -1;
			tableProfilesLData.heightHint = -1;
			tableProfilesLData.horizontalIndent = 0;
			tableProfilesLData.horizontalSpan = 1;
			tableProfilesLData.verticalSpan = 1;
			tableProfilesLData.grabExcessHorizontalSpace = true;
			tableProfilesLData.grabExcessVerticalSpace = true;
			tableProfiles.setLayoutData(tableProfilesLData);
			tableProfiles.setHeaderVisible(true);
			tableProfiles.setLinesVisible(false);
	
			tableColumnName.setText("Name");
			tableColumnName.setWidth(100);
	
			tableColumnLastUpdate.setText("Last Update");
			tableColumnLastUpdate.setWidth(100);
	
			tableColumnSource.setText("Source");
			tableColumnSource.setWidth(200);
	
			tableColumnDestination.setText("Destination");
			tableColumnDestination.setWidth(200);
			GridLayout thisLayout = new GridLayout(1, true);
			this.setLayout(thisLayout);
			thisLayout.marginWidth = 0;
			thisLayout.marginHeight = 0;
			thisLayout.numColumns = 1;
			thisLayout.makeColumnsEqualWidth = false;
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
	public Processor getProcessor()
    {
        return processor;
    }
    public void setProcessor( Processor processor )
    {
        this.processor = processor;
    }
	public void setProfileManager( ProfileManager profileManager )
	{
	    if( this.profileManager != null )
	        this.profileManager.removeChangeListener( this );
	    this.profileManager = profileManager;
	    this.profileManager.addChangeListener( this );
	    populateProfileList();
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
	    getShell().setVisible(false);

	    // TODO make sure Tray is visible here
	}

    public void profilesChanged()
    {
        populateProfileList();
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
                    t = getProcessor().execute( p );
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
}
