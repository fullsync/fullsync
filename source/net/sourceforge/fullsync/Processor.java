package net.sourceforge.fullsync;

import java.io.IOException;
import java.net.URISyntaxException;

import net.sourceforge.fullsync.fs.Directory;
import net.sourceforge.fullsync.fs.Node;
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
    
    public Task execute( Profile profile )
    	throws FileSystemException, URISyntaxException, DataParseException, IOException
    {
        Directory d1 = fsm.resolveUri( profile.getSource() );
        Directory d2 = fsm.resolveUri( profile.getDestination() );
        
        SyncRules rules = new SyncRules(profile.getRuleSet());
		rules.setJustLogging( false );
		
		return execute( d1, d2, rules );
    }
    public Task execute( Directory source, Directory destination, RuleSet rules )
		throws DataParseException, FileSystemException, IOException
	{
        Task root = new Task( null, null, new State( State.NodeInSync, Location.None ), new Action[] { new Action( Action.Nothing, Location.None, "Root" ) } );
        
        // TODO use syncnodes here
        synchronizeDirectories( source, destination, rules, root );
        return root;
	}
    public abstract void synchronizeNodes( Node src, Node dst, RuleSet rules, Task parent )
		throws DataParseException, FileSystemException;
    public abstract void synchronizeDirectories( Directory src, Directory dst, RuleSet rules, Task parent )
    	throws DataParseException, FileSystemException;
}
