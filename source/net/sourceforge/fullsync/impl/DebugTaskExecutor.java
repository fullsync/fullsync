package net.sourceforge.fullsync.impl;

import net.sourceforge.fullsync.TaskExecutor;
import net.sourceforge.fullsync.IoStatistics;
import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.TaskFinishedListener;
import net.sourceforge.fullsync.TaskTree;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class DebugTaskExecutor implements TaskExecutor
{
    public DebugTaskExecutor()
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
    public boolean isActive()
    {
        return true;
    }
    public void resume()
    {
    }
    public void suspend()
    {
    }
    public void cancel()
    {
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
