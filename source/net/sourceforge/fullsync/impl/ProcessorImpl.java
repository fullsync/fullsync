package net.sourceforge.fullsync.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.sourceforge.fullsync.ActionDecider;
import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.FileSystemException;
import net.sourceforge.fullsync.IgnoreDecider;
import net.sourceforge.fullsync.Processor;
import net.sourceforge.fullsync.RuleSet;
import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.fs.File;

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
    
    protected RuleSet updateRules( File src, File dst, RuleSet rules )
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
    protected void recurse( File src, File dst, RuleSet rules, Task parent )
    	throws DataParseException, FileSystemException
    {
        if( src.isDirectory() && dst.isDirectory() ) 
		{
		    synchronizeDirectories( src, dst, rules, parent );
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
    public void synchronizeNodes( File src, File dst, RuleSet rules, Task parent )
    	throws DataParseException, FileSystemException
    {
        if( !takeIgnoreDecider.isNodeIgnored( src ) )
		{
    		Task task = actionDecider.getTask( src, dst, stateDecider, bufferStateDecider );
    		//Task task = new Task( src, dst, state, actions );
			if( rules.isUsingRecursion() )
			    recurse( src, dst, rules, task );
    		parent.addChild(task);
		} else {
		    src.setFiltered( true );
		    dst.setFiltered( true );
		    // Enqueue ignore action ?
			if( rules.isUsingRecursionOnIgnore() )
			    recurse( src, dst, rules, parent );
		}
    }
    /*
     * we could updateRules in synchronizeNodes and apply synchronizeDirectories
     * to the given src and dst if they are directories
     */
    public void synchronizeDirectories( File src, File dst, RuleSet oldrules, Task parent )
    	throws DataParseException, FileSystemException
    {
        RuleSet rules = updateRules( src, dst, oldrules );

		Collection srcFiles = src.getChildren();
		Collection dstFiles = new ArrayList( dst.getChildren() );
		
		for( Iterator i = srcFiles.iterator(); i.hasNext();  )
		{
			File sfile = (File)i.next();
			File dfile = dst.getChild( sfile.getName() );
			if( dfile == null )
			     dfile = dst.createChild( sfile.getName(), sfile.isDirectory() );
			else dstFiles.remove( dfile );
			
			synchronizeNodes( sfile, dfile, rules, parent );
		}
		
		for( Iterator i = dstFiles.iterator(); i.hasNext();  )
		{
		    File dfile = (File)i.next();
		    File sfile = src.getChild( dfile.getName() );
		    if( sfile == null )
			    sfile = src.createChild( dfile.getName(), dfile.isDirectory() );
			
			synchronizeNodes( sfile, dfile, rules, parent );
		}
		
		/* HACK OMG, that is utterly wrong !! */
		this.takeIgnoreDecider = oldrules;
        this.stateDecider = new StateDecider( oldrules );
        this.bufferStateDecider = new BufferStateDecider( oldrules );
        this.actionDecider = new PublishActionDecider();
    }
}
