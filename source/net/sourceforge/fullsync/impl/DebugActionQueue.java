package net.sourceforge.fullsync.impl;

import net.sourceforge.fullsync.Action;
import net.sourceforge.fullsync.ActionQueue;
import net.sourceforge.fullsync.fs.Node;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class DebugActionQueue implements ActionQueue
{
    public DebugActionQueue()
    {
    }
    
    public void enqueue( Action action, Node source, Node destination )
    {
        System.out.println( source + ": " + action );
    }
    public void flush()
    {
        
    }
}
