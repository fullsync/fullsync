package net.sourceforge.fullsync;

import net.sourceforge.fullsync.fs.Node;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface ActionQueue
{
    public void enqueue( Action action, Node source, Node destination );
    public void flush();
}
