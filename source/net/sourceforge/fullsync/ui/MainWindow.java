package net.sourceforge.fullsync.ui;

import java.util.Enumeration;

import net.sourceforge.fullsync.Processor;
import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.ProfileManager;
import net.sourceforge.fullsync.Task;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
* This code was generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a
* for-profit company or business) then you should purchase
* a license - please visit www.cloudgarden.com for details.
*/
public class MainWindow extends org.eclipse.swt.widgets.Composite {

	private ToolItem toolItem1;
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
    
	public MainWindow(Composite parent, int style) {
		super(parent, style);
		initGUI();
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
			toolItem1 = new ToolItem(toolBar1,SWT.NULL);
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
			coolItem1.setMinimumSize(new org.eclipse.swt.graphics.Point(23,22));
			coolItem1.setPreferredSize(new org.eclipse.swt.graphics.Point(23,22));
	
	
			final org.eclipse.swt.graphics.Image toolItem1image = new org.eclipse.swt.graphics.Image(Display.getDefault(), getClass().getClassLoader().getResourceAsStream("Runbutton.gif"));
			toolItem1.setImage(toolItem1image);
			toolItem1.addSelectionListener( new SelectionAdapter() {
				public void widgetSelected(SelectionEvent evt) {
					toolItem1WidgetSelected(evt);
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
			addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent e) {
					toolItem1image.dispose();
				}
			});
	
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
	    this.profileManager = profileManager;
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

	

	/** Auto-generated event handler method */
	protected void toolItem1WidgetSelected(SelectionEvent evt)
	{
		TableItem[] items = tableProfiles.getSelection();
		if( items.length == 0 )
		    return;
		    
		TableItem i = items[0];
		Profile p = getProfileManager().getProfile( i.getText( 0 ) );
		if( p == null )
		    return;
		
		
		// TODO fork
		try {
			Task t = getProcessor().execute( p );
		    LogWindow.show( t );
        } catch( Exception e ) {
            e.printStackTrace();
        }
	}
}
