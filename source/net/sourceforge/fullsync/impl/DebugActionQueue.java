package net.sourceforge.fullsync.impl;

import net.sourceforge.fullsync.Action;
import net.sourceforge.fullsync.ActionFinishedListener;
import net.sourceforge.fullsync.ActionQueue;
import net.sourceforge.fullsync.TaskTree;
import net.sourceforge.fullsync.fs.File;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class DebugActionQueue implements ActionQueue
{
    public DebugActionQueue()
    {
    }
    public void enqueue( TaskTree tree )
    {
    	
    }
    public void enqueue( Action action, File source, File destination )
    {
        System.out.println( source + ": " + action );
    }
    public void flush()
    {
        
    }
    public void addActionFinishedListener( ActionFinishedListener listener )
    {
    }
    public void removeActionFinishedListener( ActionFinishedListener listener )
    {
    }
}
