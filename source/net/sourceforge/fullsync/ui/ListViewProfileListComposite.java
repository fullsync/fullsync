package net.sourceforge.fullsync.ui;

import java.util.Date;
import java.util.Enumeration;

import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.ProfileListChangeListener;
import net.sourceforge.fullsync.ProfileManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
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
/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class ListViewProfileListComposite extends ProfileListComposite implements ProfileListChangeListener
{
    private Table tableProfiles;
    private TableColumn tableColumnName;
    private TableColumn tableColumnLastUpdate;
    private TableColumn tableColumnNextUpdate;
    private TableColumn tableColumnSource;
    private TableColumn tableColumnDestination;
    
    private ProfileManager profileManager;
    private ProfileListControlHandler handler;

    public ListViewProfileListComposite( Composite parent, int style )
    {
        super( parent, style );
        initGui();
    }
    
    public void initGui()
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
            tableColumnNextUpdate = new TableColumn(
                tableProfiles,
                SWT.NONE);
            tableColumnNextUpdate.setText("Next Update");
            tableColumnNextUpdate.setWidth(100);
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

        tableProfiles.setMenu( this.getMenu() );
        
        this.setLayout( new FillLayout() );
        this.layout();
    }
    public void populateProfileList()
	{
        long now = new Date().getTime();
	    if( getProfileManager() != null )
	    {
	        tableProfiles.clearAll();
	        tableProfiles.setItemCount(0);
	        Enumeration e = getProfileManager().getProfiles();
	        while( e.hasMoreElements() )
	        {
	            Profile p = (Profile)e.nextElement();
	            TableItem item = new TableItem( tableProfiles, SWT.NULL );
	            item.setText( new String[] { 
	                    p.getName(),
	                    p.getLastUpdate().toString(),
	                    p.getNextUpdate(),
	                    p.getSource().toString(),
	                    p.getDestination().toString() } );
	            item.setData( p );
	        }
	        tableColumnName.pack();
	        tableColumnLastUpdate.pack();
	        tableColumnNextUpdate.pack();
	        tableColumnSource.pack();
	        tableColumnDestination.pack();
	    }
	}
    public void dispose()
    {
        profileManager.removeProfilesChangeListener( this );
        super.dispose();
    }
    public Profile getSelectedProfile()
    {
        return (Profile)(tableProfiles.getSelection()[0].getData());
    }
    public void setProfileManager( ProfileManager profileManager )
    {
	    if( this.profileManager != null )
	    {
	        profileManager.removeProfilesChangeListener( this );
	        
	    }
        this.profileManager = profileManager;
        if( this.profileManager != null )
        {
            profileManager.addProfilesChangeListener( this );
        }
        populateProfileList();
    }
    public ProfileManager getProfileManager()
    {
        return profileManager;
    }
    public ProfileListControlHandler getHandler()
    {
        return handler;
    }
    public void setHandler( ProfileListControlHandler handler )
    {
        this.handler = handler;
    }
    public void profileChanged( Profile p )
    {
        profileListChanged();
    }
    public void profileListChanged()
    {
        getDisplay().asyncExec( new Runnable() {
            public void run()
            {
                populateProfileList();
            }
        });
    }
}
