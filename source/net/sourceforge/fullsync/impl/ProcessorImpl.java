package net.sourceforge.fullsync.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.sourceforge.fullsync.Action;
import net.sourceforge.fullsync.ActionDecider;
import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.FileSystemException;
import net.sourceforge.fullsync.IgnoreDecider;
import net.sourceforge.fullsync.Processor;
import net.sourceforge.fullsync.RuleSet;
import net.sourceforge.fullsync.State;
import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.fs.Directory;
import net.sourceforge.fullsync.fs.Node;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class ProcessorImpl extends Processor
{
    private IgnoreDecider takeIgnoreDecider;
    private StateDecider stateDecider;
    private BufferStateDecider bufferStateDecider;
    private ActionDecider actionDecider;
    
    public ProcessorImpl()
    {
        super(); 
    }
    
    protected RuleSet updateRules( Directory src, Directory dst, RuleSet rules )
    	throws DataParseException, FileSystemException
    {
        rules = rules.createChild( src, dst );
		
		/* HACK OMG, that is utterly wrong !! */
		this.takeIgnoreDecider = rules;
        this.stateDecider = new StateDecider( rules );
        this.bufferStateDecider = new BufferStateDecider( rules );
        this.actionDecider = new PublishActionDecider();
        
		return rules;
    }
    protected void recurse( Node src, Node dst, RuleSet rules, Task parent )
    	throws DataParseException, FileSystemException
    {
        if( src.isDirectory() && dst.isDirectory() ) 
		{
		    Directory newSrc = (Directory)src;
		    Directory newDst = (Directory)dst;
		    synchronizeDirectories( newSrc, newDst, rules, parent );
		}
		// TODO [DirHereFileThere, ?] 
		//		handle case where src is dir but dst is file
    }
    
    /**
     * 
     * @param src
     * @param dst
     * @param rules
     * @return true if node is affected, false if ignored
     * @throws DataParseException
     * @throws FileSystemException
     */
    public void synchronizeNodes( Node src, Node dst, RuleSet rules, Task parent )
    	throws DataParseException, FileSystemException
    {
        if( !takeIgnoreDecider.isNodeIgnored( src ) )
		{
		    // TODO [BufferedFileChange, Source]
		    // TODO [BufferedFileChange, Destination]
		    /*
		    if( dfile.isBuffered() && rules.isCheckingBufferAlways() )
	        {
	            if( !((BufferedNode)dfile).isInSync() )
	                actionType = Action.UNEXPECTEDCHANGE;
	                //System.out.println( "CHANGED REMOTELY: "+dfile.getPath() );
	        }
		    */
		    State state = stateDecider.getState( src, dst );
    		Action[] actions = actionDecider.getPossibleActions( state, src, dst, bufferStateDecider );
    		Task task = new Task( src, dst, state, actions );
			if( rules.isUsingRecursion() )
			    recurse( src, dst, rules, task );
    		parent.addChild(task);
		} else {
		    // Enqueue ignore action ?
			if( rules.isUsingRecursionOnIgnore() )
			    recurse( src, dst, rules, parent );
		}
    }
    /*
     * we could updateRules in synchronizeNodes and apply synchronizeDirectories
     * to the given src and dst if they are directories
     */
    public void synchronizeDirectories( Directory src, Directory dst, RuleSet rules, Task parent )
    	throws DataParseException, FileSystemException
    {
        rules = updateRules( src, dst, rules );

		Collection srcFiles = src.getChildren();
		Collection dstFiles = new ArrayList( dst.getChildren() );
		
		for( Iterator i = srcFiles.iterator(); i.hasNext();  )
		{
			Node sfile = (Node)i.next();
			Node dfile = dst.getChild( sfile.getName() );
			if( dfile == null )
			     dfile = (sfile.isDirectory()?(Node)dst.createDirectory(sfile.getName()):(Node)dst.createFile(sfile.getName()));
			else dstFiles.remove( dfile );
			
			synchronizeNodes( sfile, dfile, rules, parent );
		}
		
		for( Iterator i = dstFiles.iterator(); i.hasNext();  )
		{
		    Node dfile = (Node)i.next();
		    Node sfile = src.getChild( dfile.getName() );
		    if( sfile == null )
			    sfile = (dfile.isDirectory()?(Node)src.createDirectory(dfile.getName()):(Node)src.createFile(dfile.getName()));
			
			synchronizeNodes( sfile, dfile, rules, parent );
		}
    }
}
