package net.sourceforge.fullsync;

import java.io.IOException;


/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface TaskExecutor extends Phase
{
    // we are much more a TaskExecutor
    public IoStatistics createStatistics( TaskTree tree );
	public void enqueue( TaskTree tree );
    public void enqueue( Task task );
    public void flush() throws IOException;
    
    // listeners
    public void addTaskFinishedListener( TaskFinishedListener listener );
    public void removeTaskFinishedListener( TaskFinishedListener listener );
}
