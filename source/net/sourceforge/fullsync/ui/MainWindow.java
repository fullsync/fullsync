package net.sourceforge.fullsync.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;

import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.ProfileManager;
import net.sourceforge.fullsync.ProfileSchedulerListener;
import net.sourceforge.fullsync.ProfilesChangeListener;
import net.sourceforge.fullsync.Synchronizer;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
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
public class MainWindow extends org.eclipse.swt.widgets.Composite 
	implements ProfilesChangeListener, ProfileSchedulerListener, TaskGenerationListener
{
    private ToolItem toolItemNew;
    private Menu menuBarMainWindow;
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
    private ArrayList images;
    private Image imageTimerRunning;
    private Image imageTimerStopped;
    
    private GuiController guiController;
	
	public MainWindow(Composite parent, int style) 
	{
		super(parent, style);
		images = new ArrayList();
		initGUI();

		getShell().addShellListener(new ShellAdapter() {
		    public void shellClosed(ShellEvent event) {
				event.doit = false;
		    	if (guiController.getPreferences().closeMinimizesToSystemTray()) 
		    	{
					minimizeToTray();
		    	} else {
		    	    guiController.closeGui();
		    	}
		    }

		    public void shellIconified(ShellEvent event) {
		        if (guiController.getPreferences().minimizeMinimizesToSystemTray())
		        {
		            event.doit = false;
		            minimizeToTray();
		        } else {
		            event.doit = true;
		        }
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
                                    public void widgetSelected(SelectionEvent evt) {
                                        createNewProfile();
                                    }
                                });
                        }
                        {
                            toolItemEdit = new ToolItem(toolBar1, SWT.PUSH);
                            toolItemEdit
                                .addSelectionListener(new SelectionAdapter() {
                                    public void widgetSelected(SelectionEvent evt) {
                                        editCurrentProfile();
                                    }
                                });
                        }
                        {
                            toolItemDelete = new ToolItem(toolBar1, SWT.PUSH);
                            toolItemDelete
                                .addSelectionListener(new SelectionAdapter() {
                                    public void widgetSelected(SelectionEvent evt) {
                                        deleteCurrentProfile();
                                    }
                                });
                        }
                        {
                            toolItemRun = new ToolItem(toolBar1, SWT.PUSH);
                            toolItemRun
                                .addSelectionListener(new SelectionAdapter() {
                                    public void widgetSelected(SelectionEvent evt) {
                                        runCurrentProfile();
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
				menuBarMainWindow = new Menu(getShell(), SWT.BAR);
				getShell().setMenuBar(menuBarMainWindow);
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
		toolItemNew.setToolTipText("New Profile");
		images.add( i );
		i = LogWindow.loadImage( "Button_Edit.gif" );
		toolItemEdit.setImage( i );
		toolItemEdit.setToolTipText("Edit Profile");
		images.add( i );
		i = LogWindow.loadImage( "Button_Delete.gif" );
		toolItemDelete.setImage( i );
		toolItemDelete.setToolTipText("Delete Profile");
		images.add( i );
		Image buttonRun = LogWindow.loadImage( "Button_Run.gif" );
		toolItemRun.setImage( buttonRun );
		toolItemRun.setToolTipText("Run Profile");
		images.add( buttonRun );
		i = LogWindow.loadImage( "Timer_Running.gif" );
		imageTimerRunning = i;
		images.add( i );
		i = LogWindow.loadImage( "Timer_Stopped.gif" );
		imageTimerStopped = i;
		images.add( i );
		//toolBar1.layout();
		
		// PopUp Menu for the Profile list.
		Menu profilesPopupMenu = new Menu(getShell(), SWT.POP_UP);
		
		MenuItem addItem = new MenuItem(profilesPopupMenu, SWT.PUSH);
		addItem.setText("New Profile...");
		addItem.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {
					createNewProfile();
				}
			}
		);

		MenuItem separatorItem1 = new MenuItem(profilesPopupMenu, SWT.SEPARATOR);		
		
		MenuItem editItem = new MenuItem(profilesPopupMenu, SWT.PUSH);
		editItem.setText("Edit Profile...");
		editItem.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {
					editCurrentProfile();
				}
			}
		);

		MenuItem runItem = new MenuItem(profilesPopupMenu, SWT.PUSH);
		runItem.setText("Run Profile...");
		runItem.setImage(buttonRun);
		runItem.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {
					runCurrentProfile();
				}
			}
		);

		MenuItem separatorItem2 = new MenuItem(profilesPopupMenu, SWT.SEPARATOR);

		MenuItem deleteItem = new MenuItem(profilesPopupMenu, SWT.PUSH);
		deleteItem.setText("Delete Profile...");
		deleteItem.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {
					deleteCurrentProfile();
				}
			}
		);

		tableProfiles.setMenu(profilesPopupMenu);

		// Menu Bar
		MenuItem menuItemFile = new MenuItem(menuBarMainWindow, SWT.CASCADE);
		menuItemFile.setText("&File");
		
		Menu menuFile = new Menu(menuItemFile);
		menuItemFile.setMenu(menuFile);
		
		MenuItem menuItemNewProfile = new MenuItem(menuFile, SWT.PUSH);
		menuItemNewProfile.setText("&New Profile...");
		menuItemNewProfile.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {
					createNewProfile();
				}
			}
		);

		MenuItem separatorItem3 = new MenuItem(menuFile, SWT.SEPARATOR);

		MenuItem menuItemEditProfile = new MenuItem(menuFile, SWT.PUSH);
		menuItemEditProfile.setText("&Edit Profile...");
		menuItemEditProfile.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {
					editCurrentProfile();
				}
			}
		);

		MenuItem menuItemRunProfile = new MenuItem(menuFile, SWT.PUSH);
		menuItemRunProfile.setText("&Run Profile...");
		menuItemRunProfile.setImage(buttonRun);
		menuItemRunProfile.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {
					runCurrentProfile();
				}
			}
		);
		
		MenuItem separatorItem4 = new MenuItem(menuFile, SWT.SEPARATOR);

		MenuItem menuItemDeleteProfile = new MenuItem(menuFile, SWT.PUSH);
		menuItemDeleteProfile.setText("&Delete Profile...");
		menuItemDeleteProfile.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {
					deleteCurrentProfile();
				}
			}
		);

		MenuItem separatorItem5 = new MenuItem(menuFile, SWT.SEPARATOR);

		MenuItem menuItemExitProfile = new MenuItem(menuFile, SWT.PUSH);
		menuItemExitProfile.setText("Exit");
		menuItemExitProfile.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {
		    	    guiController.closeGui();
				}
			}
		);
		
		
		MenuItem menuItemEdit = new MenuItem(menuBarMainWindow, SWT.CASCADE);
		menuItemEdit.setText("&Edit");
		
		Menu menuEdit = new Menu(menuItemEdit);
		menuItemEdit.setMenu(menuEdit);

		MenuItem preferencesItem = new MenuItem(menuEdit, SWT.PUSH);
		preferencesItem.setText("&Preferences...\tCtrl+Shift+P");
		preferencesItem.setAccelerator(SWT.CTRL|SWT.SHIFT+'P');
		preferencesItem.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {
					// show the Preferences Dialog.
					PreferencesDialog prefDialog = new PreferencesDialog(getShell(), SWT.NULL);
					prefDialog.setGuiController(guiController);
					prefDialog.open();
				}
			}
		);
		
		MenuItem menuItemHelp = new MenuItem(menuBarMainWindow, SWT.CASCADE);
		menuItemHelp.setText("&Help");
		
		Menu menuHelp = new Menu(menuItemHelp);
		menuItemHelp.setMenu(menuHelp);

		MenuItem menuItemHelpContent = new MenuItem(menuHelp, SWT.PUSH);
		menuItemHelpContent.setText("Help\tF1");
		menuItemHelpContent.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {
					//TODO help contents
					//The first version can be an HTML/TXT file to open
					//with the default browser.
				}
			}
		);

		MenuItem separatorItem6 = new MenuItem(menuHelp, SWT.SEPARATOR);

		MenuItem menuItemAbout = new MenuItem(menuHelp, SWT.PUSH);
		menuItemAbout.setAccelerator(SWT.CTRL+'A');
		menuItemAbout.setText("&About FullSync\tCtrl+A");
		menuItemAbout.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {
					AboutDialog aboutDialog = new AboutDialog(getShell(), SWT.NULL);
					aboutDialog.open();
				}
			}
		);
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
	
	public void setGuiController( GuiController guiController )
    {
	    if( guiController != null )
	    {
	        guiController.getProfileManager().removeProfilesChangeListener( this );
	        guiController.getProfileManager().removeSchedulerListener( this );
	        guiController.getSynchronizer().getProcessor().removeTaskGenerationListener(this);
	    }
        this.guiController = guiController;
        guiController.getProfileManager().addProfilesChangeListener( this );
        guiController.getProfileManager().addSchedulerListener( this );
        guiController.getSynchronizer().getProcessor().addTaskGenerationListener(this);
        populateProfileList();
        updateTimerEnabled();
    }
	public GuiController getGuiController()
    {
        return guiController;
    }
	
	public void populateProfileList()
	{
	    if( guiController != null )
	    {
	        tableProfiles.clearAll();
	        tableProfiles.setItemCount(0);
	        Enumeration e = guiController.getProfileManager().getProfiles();
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
	    if( guiController == null )
	        return;

	    ProfileManager pm = guiController.getProfileManager();
	    
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
    public void profileExecutionScheduled( Profile profile )
    {
        Synchronizer sync = guiController.getSynchronizer();
        sync.performActions( sync.executeProfile( profile ) );
    }
	protected void runCurrentProfile()
	{
		TableItem[] items = tableProfiles.getSelection();
		if( items.length == 0 )
		    return;
		    
		TableItem i = items[0];
		final Profile p = guiController.getProfileManager().getProfile( i.getText( 0 ) );
		if( p == null )
		    return;
		
		
	    Thread worker = new Thread( new Runnable() {
	        public void run()
            {
				TaskTree t = null;
                try {
                    guiController.showBusyCursor( true );
					try {
						statusLine.setMessage( "Starting profile "+p.getName()+"..." );
						t = guiController.getSynchronizer().executeProfile( p );
						statusLine.setMessage( "Finished profile "+p.getName() );
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
	                	guiController.showBusyCursor( false );
					}
                    LogWindow.show( guiController, t );
                    
                    // HACK this really doesn't belong here, because the user
                    //      can abort the real execution, but logwindow does not
                    //      know the initial profile. maybe tasktree should get
                    //      a reference to the profile ?
                    p.setLastUpdate( new Date() );
                    guiController.getProfileManager().fireProfilesChangeEvent();
                    guiController.getProfileManager().save();
                } catch( Exception e ) {
                    e.printStackTrace();
                }
            }
	    });
	    worker.start();
	}

	
	public void createNewProfile()
	{
		ProfileDetails.showProfile( guiController.getProfileManager(), null );
	}

	public void editCurrentProfile()
	{
		TableItem[] items = tableProfiles.getSelection();
		if( items.length == 0 )
		    return;
		    
		TableItem i = items[0];
		Profile p = guiController.getProfileManager().getProfile( i.getText( 0 ) );
		if( p == null )
		    return;

		ProfileDetails.showProfile( guiController.getProfileManager(), p.getName() );
	}

	public void deleteCurrentProfile()
	{
	    ProfileManager profileManager = guiController.getProfileManager();
	    
		TableItem[] items = tableProfiles.getSelection();
		if( items.length == 0 )
		    return;
		    
		TableItem i = items[0];
		Profile p = profileManager.getProfile( i.getText( 0 ) );
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
	    ProfileManager profileManager = guiController.getProfileManager();
	    if( profileManager.isTimerEnabled() )
        {
            profileManager.stopTimer();
        } else {
            profileManager.startTimer();
        }
        updateTimerEnabled();
    }
    
}
