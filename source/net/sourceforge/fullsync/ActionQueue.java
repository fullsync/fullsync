package net.sourceforge.fullsync;

import net.sourceforge.fullsync.fs.File;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface ActionQueue
{
	public void enqueue( TaskTree tree );
    public void enqueue( Action action, File source, File destination );
    public void flush();
    public void addActionFinishedListener( ActionFinishedListener listener );
    public void removeActionFinishedListener( ActionFinishedListener listener );
}
