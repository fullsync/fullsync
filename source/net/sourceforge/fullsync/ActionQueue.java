package net.sourceforge.fullsync;


/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface ActionQueue
{
    // we are much more a TaskExecutor
    public IoStatistics createStatistics( TaskTree tree );
	public void enqueue( TaskTree tree );
    public void enqueue( Task task );
    public void flush();
    
    public void addActionFinishedListener( TaskFinishedListener listener );
    public void removeActionFinishedListener( TaskFinishedListener listener );
}
