package net.sourceforge.fullsync.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.FileSystemException;
import net.sourceforge.fullsync.IgnoreDecider;
import net.sourceforge.fullsync.RuleSet;
import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.TaskGenerationListener;
import net.sourceforge.fullsync.fs.File;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class TaskGeneratorImpl extends AbstractTaskGenerator
{
    // TODO this should be execution local so the class
    //      itself is multithreadable
    //      so maybe just put them all into a inmutable
    //		state container
    private IgnoreDecider takeIgnoreDecider;
    private StateDecider stateDecider;
    private BufferStateDecider bufferStateDecider;
    //private ActionDecider actionDecider;
    
    public TaskGeneratorImpl()
    {
        super(); 
    }
    
    protected RuleSet updateRules( File src, File dst, RuleSet rules )
    	throws DataParseException, IOException
    {
        rules = rules.createChild( src, dst );
		
		/* HACK OMG, that is utterly wrong !! */
		this.takeIgnoreDecider = rules;
        this.stateDecider = new StateDecider( rules );
        this.bufferStateDecider = new BufferStateDecider( rules );
        //this.actionDecider = new BackupActionDecider();
        
		return rules;
    }
    protected void recurse( File src, File dst, RuleSet rules, Task parent )
    	throws DataParseException, IOException
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
    	throws DataParseException, IOException
    {
        if( !takeIgnoreDecider.isNodeIgnored( src ) )
		{
            for( int i = 0; i < taskGenerationListeners.size(); i++ )
                ((TaskGenerationListener)taskGenerationListeners.get(i))
                	.taskGenerationStarted(src, dst);

            Task task = getActionDecider().getTask( src, dst, stateDecider, bufferStateDecider );

            for( int i = 0; i < taskGenerationListeners.size(); i++ )
                ((TaskGenerationListener)taskGenerationListeners.get(i))
                	.taskGenerationFinished(task);
            
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
    	throws DataParseException, IOException
    {
        // update rules to current directory
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
        //this.actionDecider = new PublishActionDecider();
    }
}
