package net.sourceforge.fullsync;

import java.io.IOException;
import java.net.URISyntaxException;

import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.Site;
import net.sourceforge.fullsync.impl.SyncRules;


/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public abstract class Processor
{
    protected FileSystemManager fsm;
    
    public Processor()
    {
        this.fsm = new FileSystemManager();
    }
    
    public TaskTree execute( Profile profile )
    	throws FileSystemException, URISyntaxException, DataParseException, IOException
    {
        Site d1 = fsm.createConnection( profile.getSource() );
        Site d2 = fsm.createConnection( profile.getDestination() );
        
        SyncRules rules = new SyncRules(profile.getRuleSet());
		rules.setJustLogging( false );
		
		return execute( d1, d2, rules );
    }
    public TaskTree execute( Site source, Site destination, RuleSet rules )
		throws DataParseException, FileSystemException, IOException
	{
        TaskTree tree = new TaskTree( source, destination );
        Task root = new Task( null, null, new State( State.NodeInSync, Location.None ), new Action[] { new Action( Action.Nothing, Location.None, BufferUpdate.None, "Root" ) } );
        
        // TODO use syncnodes here
        synchronizeDirectories( source.getRoot(), destination.getRoot(), rules, root );
        tree.setRoot( root );
        return tree;
	}
    public abstract void synchronizeNodes( File src, File dst, RuleSet rules, Task parent )
		throws DataParseException, FileSystemException;
    public abstract void synchronizeDirectories( File src, File dst, RuleSet rules, Task parent )
    	throws DataParseException, FileSystemException;
}
