package net.sourceforge.fullsync.impl;

import net.sourceforge.fullsync.IoStatistics;
import net.sourceforge.fullsync.TaskFinishedListener;
import net.sourceforge.fullsync.ActionQueue;
import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.TaskTree;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class DebugActionQueue implements ActionQueue
{
    public DebugActionQueue()
    {
    }
    public IoStatistics createStatistics( TaskTree tree )
    {
        // TODO Auto-generated method stub
        return null;
    }
    public void enqueue( TaskTree tree )
    {
    	
    }
    public void enqueue( Task task )
    {
        System.out.println( task.getSource() + ": " + task.getCurrentAction() );
    }
    public void flush()
    {
        
    }
    public void addTaskFinishedListener( TaskFinishedListener listener )
    {
    }
    public void removeTaskFinishedListener( TaskFinishedListener listener )
    {
    }
}
