package net.sourceforge.fullsync.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.ProfileManager;
import net.sourceforge.fullsync.ProfileSchedulerListener;
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
	implements ProfileSchedulerListener, ProfileListControlHandler, TaskGenerationListener
{
    private ToolItem toolItemNew;
    private Menu menuBarMainWindow;
    private StatusLine statusLine;
    private ToolBar toolBar2;
    private CoolItem coolItem2;
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
    
    private ProfileListComposite profileList;
    private GuiController guiController;
    
    private String statusDelayString;
    private Timer statusDelayTimer;
    
	public MainWindow(Composite parent, int style, GuiController initGuiController) 
	{
		super(parent, style);
		this.guiController = initGuiController;
		this.images = new ArrayList();
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
		
        profileList.setProfileManager( guiController.getProfileManager() );
        guiController.getProfileManager().addSchedulerListener( this );
        guiController.getSynchronizer().getProcessor().addTaskGenerationListener(this);
        updateTimerEnabled();
	}

	/**
	* Initializes the GUI.
	* Auto-generated code - any changes you make will disappear.
	*/
	public void initGUI(){
		try {
			preInitGUI();

			this.setSize(600, 300);

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
                                        editProfile( profileList.getSelectedProfile() );
                                    }
                                });
                        }
                        {
                            toolItemDelete = new ToolItem(toolBar1, SWT.PUSH);
                            toolItemDelete
                                .addSelectionListener(new SelectionAdapter() {
                                    public void widgetSelected(SelectionEvent evt) {
                                        deleteProfile( profileList.getSelectedProfile() );
                                    }
                                });
                        }
                        {
                            toolItemRun = new ToolItem(toolBar1, SWT.PUSH);
                            toolItemRun
                                .addSelectionListener(new SelectionAdapter() {
                                    public void widgetSelected(SelectionEvent evt) {
                                        runProfile( profileList.getSelectedProfile() );
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
			    if( guiController.getPreferences().getProfileListStyle().equals( "NiceListView" ) )
			         profileList = new NiceListViewProfileListComposite( this, SWT.NULL );
			    else profileList = new ListViewProfileListComposite( this, SWT.NULL );
			    GridData profileListLData = new GridData();
			    profileListLData.grabExcessHorizontalSpace = true;
		        profileListLData.grabExcessVerticalSpace = true;
		        profileListLData.horizontalAlignment = GridData.FILL;
		        profileListLData.verticalAlignment = GridData.FILL;
		        profileList.setLayoutData(profileListLData);
		        profileList.setHandler( this );
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
		i = TaskDecisionList.loadImage( "Button_New.gif" );
		toolItemNew.setImage( i );
		toolItemNew.setToolTipText("New Profile");
		images.add( i );
		i = TaskDecisionList.loadImage( "Button_Edit.gif" );
		toolItemEdit.setImage( i );
		toolItemEdit.setToolTipText("Edit Profile");
		images.add( i );
		i = TaskDecisionList.loadImage( "Button_Delete.gif" );
		toolItemDelete.setImage( i );
		toolItemDelete.setToolTipText("Delete Profile");
		images.add( i );
		Image buttonRun = TaskDecisionList.loadImage( "Button_Run.gif" );
		toolItemRun.setImage( buttonRun );
		toolItemRun.setToolTipText("Run Profile");
		images.add( buttonRun );
		i = TaskDecisionList.loadImage( "Timer_Running.gif" );
		imageTimerRunning = i;
		images.add( i );
		i = TaskDecisionList.loadImage( "Timer_Stopped.gif" );
		imageTimerStopped = i;
		images.add( i );
		//toolBar1.layout();
		
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
					editProfile( profileList.getSelectedProfile() );
				}
			}
		);

		MenuItem menuItemRunProfile = new MenuItem(menuFile, SWT.PUSH);
		menuItemRunProfile.setText("&Run Profile...");
		menuItemRunProfile.setImage(buttonRun);
		menuItemRunProfile.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {
					runProfile( profileList.getSelectedProfile() );
				}
			}
		);
		
		MenuItem separatorItem4 = new MenuItem(menuFile, SWT.SEPARATOR);

		MenuItem menuItemDeleteProfile = new MenuItem(menuFile, SWT.PUSH);
		menuItemDeleteProfile.setText("&Delete Profile...");
		menuItemDeleteProfile.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {
					deleteProfile( profileList.getSelectedProfile() );
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
					WizardDialog dialog = new WizardDialog( getShell(), SWT.APPLICATION_MODAL );
					WizardPage page = new PreferencesPage( dialog, guiController.getPreferences() );
					dialog.show();
				}
			}
		);
		
		MenuItem menuItemRemoteConnection = new MenuItem(menuBarMainWindow, SWT.CASCADE);
		menuItemRemoteConnection.setText("&Remote Connection");
		
		Menu menuRemoteConnection = new Menu(menuItemRemoteConnection);
		menuItemRemoteConnection.setMenu(menuRemoteConnection);
		
		final MenuItem connectItem = new MenuItem(menuRemoteConnection, SWT.PUSH);
		connectItem.setText("&Connect to a remote server...\tCtrl+Shift+C");
		connectItem.setAccelerator(SWT.CTRL|SWT.SHIFT+'C');
		connectItem.setEnabled(true);

		final MenuItem disconnectItem = new MenuItem(menuRemoteConnection, SWT.PUSH);
		disconnectItem.setText("&Disconnect\tCtrl+Shift+D");
		disconnectItem.setAccelerator(SWT.CTRL|SWT.SHIFT+'D');
		disconnectItem.setEnabled(false);

		connectItem.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {
					WizardDialog dialog = new WizardDialog( getShell(), SWT.APPLICATION_MODAL );
					ConnectionPage page = new ConnectionPage(dialog);
					dialog.show();
					if (GuiController.getInstance().getProfileManager().isConnected()) {
						connectItem.setEnabled(false);
						disconnectItem.setEnabled(true);
						GuiController.getInstance().getMainShell().setImage(new Image( null, "images/Remote_Connect.gif" ));
					}
				}
			}
		);
		
		disconnectItem.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {
					MessageBox mb = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.YES | SWT.NO);
					mb.setText("Confirmation");
					mb.setMessage("Do you really want to Disconnect the Remote Server? \n");

					if (mb.open() == SWT.YES) {
		            	GuiController.getInstance().getProfileManager().disconnectRemote();		            	
		            	GuiController.getInstance().getSynchronizer().disconnectRemote();
		            	
						connectItem.setEnabled(true);
						disconnectItem.setEnabled(false);
						GuiController.getInstance().getMainShell().setImage(new Image( null, "images/FullSync.gif" ));					
					}
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
	
	public GuiController getGuiController()
    {
        return guiController;
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
        //statusLine.setMessage( "checking "+source.getPath() );
        statusDelayString = "checking "+source.getPath();
    }
    public void taskGenerationFinished( Task task )
    {
        
    }
    public void taskTreeFinished( TaskTree tree )
    {
        statusLine.setMessage( "synchronization finished");
    }
    public void profileExecutionScheduled( Profile profile )
    {
        Synchronizer sync = guiController.getSynchronizer();
        TaskTree tree = sync.executeProfile( profile );
        if( tree == null )
        {
            profile.setLastError( 1, "An error occured while comparing filesystems." );
        } else {
            int errorLevel = sync.performActions( tree );
            if( errorLevel > 0 ) {
                profile.setLastError( errorLevel, "An error occured while copying files." );
            } else { 
                profile.setLastError( 0, null );
                profile.setLastUpdate( new Date() );
            }
        }
    }

	
	public void createNewProfile()
	{
		ProfileDetails.showProfile( getShell(), guiController.getProfileManager(), null );
	}

	public void runProfile( final Profile p )
	{
		if( p == null )
			return;
		
		Thread worker = new Thread( new Runnable() {
			public void run()
			{
				_doRunProfile(p);
			}
		});
		worker.start();
	}
	private synchronized void _doRunProfile( Profile p )
	{
	    TaskTree t = null;
        try {
            guiController.showBusyCursor( true );
			try {
			    // REVISIT wow, a timer here is pretty much overhead / specific for
			    //         this generell problem
			    statusDelayTimer = new Timer( true );
			    statusDelayTimer.schedule( new TimerTask() {
			        public void run()
			        {
			            statusLine.setMessage( statusDelayString );
			        }
			    }, 10, 100 );
			    statusDelayString = "Starting profile "+p.getName()+"...";
				statusLine.setMessage( statusDelayString );
				t = guiController.getSynchronizer().executeProfile( p );
				if( t == null )
		        {
		            p.setLastError( 1, "An error occured while comparing filesystems." );
		            statusLine.setMessage( "An error occured while processing profile "+p.getName()+". Please see the logs for more information." );
		        } else {
		            statusLine.setMessage( "Finished profile "+p.getName() );
		        }
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			    statusDelayTimer.cancel();
            	guiController.showBusyCursor( false );
			}
			if( t != null )
			    TaskDecisionList.show( guiController, p, t );
            
        } catch( Exception e ) {
            e.printStackTrace();
        }
	}

	public void editProfile( final Profile p )
	{
		if( p == null )
		    return;

		ProfileDetails.showProfile( getShell(), guiController.getProfileManager(), p.getName() );
	}

	public void deleteProfile( final Profile p )
	{
		if( p == null )
		    return;

		ProfileManager profileManager = guiController.getProfileManager();

		MessageBox mb = new MessageBox( getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO );
		mb.setText( "Confirmation" );
	    mb.setMessage( "Do you really want to delete profile "+p.getName()+" ?");
	    if( mb.open() == SWT.YES )
	    {
	        profileManager.removeProfile( p );
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
    
    public void updateProfileList() {
    	profileList.dispose();
    	statusLine.dispose();
		{
		    if( guiController.getPreferences().getProfileListStyle().equals( "NiceListView" ) )
		         profileList = new NiceListViewProfileListComposite( this, SWT.NULL );
		    else profileList = new ListViewProfileListComposite( this, SWT.NULL );
		    GridData profileListLData = new GridData();
		    profileListLData.grabExcessHorizontalSpace = true;
	        profileListLData.grabExcessVerticalSpace = true;
	        profileListLData.horizontalAlignment = GridData.FILL;
	        profileListLData.verticalAlignment = GridData.FILL;
	        profileList.setLayoutData(profileListLData);
	        profileList.setHandler( this );
		}
        {
            statusLine = new StatusLine(this, SWT.NONE);
            GridData statusLineLData = new GridData();
            statusLineLData.grabExcessHorizontalSpace = true;
            statusLineLData.horizontalAlignment = GridData.FILL;
            statusLine.setLayoutData(statusLineLData);
        }
    	profileList.setProfileManager( guiController.getProfileManager() );
	    this.layout();
    }
}
