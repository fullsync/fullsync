package net.sourceforge.fullsync.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import net.sourceforge.fullsync.Action;
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
        
    public TaskTree execute( Profile profile )
    	throws FileSystemException, URISyntaxException, DataParseException, IOException
    {
        Site d1 = fsm.createConnection( profile.getSource() );
        Site d2 = fsm.createConnection( profile.getDestination() );
        
        RuleSet rules = profile.getRuleSet().createRuleSet();        
		
		return execute( d1, d2, rules );
    }
    public TaskTree execute( Site source, Site destination, RuleSet rules )
		throws DataParseException, FileSystemException, IOException
	{
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
		throws DataParseException, FileSystemException;
    public abstract void synchronizeDirectories( File src, File dst, RuleSet rules, Task parent )
    	throws DataParseException, FileSystemException;
}
