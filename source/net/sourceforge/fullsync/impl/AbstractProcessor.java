package net.sourceforge.fullsync.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import net.sourceforge.fullsync.Action;
import net.sourceforge.fullsync.ActionDecider;
import net.sourceforge.fullsync.BufferUpdate;
import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.FileSystemException;
import net.sourceforge.fullsync.FileSystemManager;
import net.sourceforge.fullsync.Location;
import net.sourceforge.fullsync.Processor;
import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.RuleSet;
import net.sourceforge.fullsync.State;
import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.TaskGenerationListener;
import net.sourceforge.fullsync.TaskTree;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.Site;


/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public abstract class AbstractProcessor implements Processor
{
    protected FileSystemManager fsm;
    protected ArrayList taskGenerationListeners;
    protected boolean active; // resume/suspend
    protected boolean cancelled; // cancel
    
    private ActionDecider actionDecider;
    
    public AbstractProcessor()
    {
        this.fsm = new FileSystemManager();
        active = true;
        cancelled = false;
        taskGenerationListeners = new ArrayList();
    }
    
    public void addTaskGenerationListener( TaskGenerationListener listener )
    {
        taskGenerationListeners.add( listener );
    }
    public void removeTaskGenerationListener( TaskGenerationListener listener )
    {
        taskGenerationListeners.remove( listener );
    }
    public boolean isActive()
    {
        return active && !cancelled;
    }
    public void suspend()
    {
        active = false;
    }
    public void resume()
    {
        active = true;
    }
    public void cancel()
    {
        cancelled = true;
        active = false;
    }
    
    public ActionDecider getActionDecider()
    {
        return actionDecider;
    }
        
    public TaskTree execute( Profile profile )
    	throws FileSystemException, URISyntaxException, DataParseException, IOException
    {
        Site d1 = null, d2 = null;
	        
        RuleSet rules = profile.getRuleSet().createRuleSet();
        
        ActionDecider actionDecider;
        if( profile.getSynchronizationType().equals( "Publish/Update" ) )
            actionDecider = new PublishActionDecider();
        else if( profile.getSynchronizationType().equals( "Backup Copy" ) )
            actionDecider = new BackupActionDecider();
        else if( profile.getSynchronizationType().equals( "Exact Copy" ) )
            actionDecider = new ExactCopyActionDecider();
        else throw new IllegalArgumentException( "Profile has unknown synchronization type." );
			
        try {
	        d1 = fsm.createConnection( profile.getSource() );
	        d2 = fsm.createConnection( profile.getDestination() );
			return execute( d1, d2, actionDecider, rules );
        } catch( FileSystemException ex ) {
            if( d1 != null )
                d1.close();
            if( d2 != null )
                d2.close();
            throw ex;
        }
    }
    public TaskTree execute( Site source, Site destination, ActionDecider actionDecider, RuleSet rules )
		throws DataParseException, FileSystemException, IOException
	{
        this.actionDecider = actionDecider;  

        TaskTree tree = new TaskTree( source, destination );
        Task root = new Task( null, null, new State( State.NodeInSync, Location.None ), new Action[] { new Action( Action.Nothing, Location.None, BufferUpdate.None, "Root" ) } );
        tree.setRoot( root );
        
        for( int i = 0; i < taskGenerationListeners.size(); i++ )
            ((TaskGenerationListener)taskGenerationListeners.get(i))
            	.taskTreeStarted( tree );
        
        // TODO use syncnodes here [?]
        // TODO get traversal type and start correct traversal action
        synchronizeDirectories( source.getRoot(), destination.getRoot(), rules, root );

        for( int i = 0; i < taskGenerationListeners.size(); i++ )
            ((TaskGenerationListener)taskGenerationListeners.get(i))
            	.taskTreeFinished( tree );
        
        return tree;
	}
    public abstract void synchronizeNodes( File src, File dst, RuleSet rules, Task parent )
		throws DataParseException, IOException;
    public abstract void synchronizeDirectories( File src, File dst, RuleSet rules, Task parent )
    	throws DataParseException, IOException;
}
