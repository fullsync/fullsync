package net.sourceforge.fullsync;

import java.util.Enumeration;
import java.util.Vector;

import net.sourceforge.fullsync.fs.File;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class Task
{
    private File source;
    private File destination;
    private State state;
    private Action[] actions;
    private int currentAction;
    
    private Vector children;
    
    public Task( File source, File destination, State state, Action[] actions )
    {
        this.source = source;
        this.destination = destination;
        this.state = state;
        this.actions = actions;
        this.currentAction = 0;
        this.children = new Vector();
    }
    
    public File getDestination()
    {
        return destination;
    }
    public void setDestination( File destination )
    {
        this.destination = destination;
    }
    public File getSource()
    {
        return source;
    }
    public void setSource( File source )
    {
        this.source = source;
    }
    
    public Action getCurrentAction()
    {
        return actions[currentAction];
    }
    public int getCurrentActionIndex()
    {
        return currentAction;
    }
    public void setCurrentAction( int i )
    {
        this.currentAction = i;
    }
    public State getState()
    {
        return state;
    }
    public void setState( State state )
    {
        this.state = state;
    }
    public Action[] getActions()
    {
        return actions;
    }
    public void setActions( Action[] actions )
    {
        this.actions = actions;
    }
    public void addChild( Task child )
    {
        this.children.add( child );
    }
    public Enumeration getChildren()
    {
        return children.elements();
    }
    public String toString()
    {
        return getCurrentAction().toString();
    }
}
