package net.sourceforge.fullsync;

import java.util.Enumeration;
import java.util.Vector;

import net.sourceforge.fullsync.fs.Node;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class Task
{
    private Node source;
    private Node destination;
    private State state;
    private Action[] actions;
    private int currentAction;
    
    private Vector children;
    
    public Task( Node source, Node destination, State state, Action[] actions )
    {
        this.source = source;
        this.destination = destination;
        this.state = state;
        this.actions = actions;
        this.currentAction = 0;
        this.children = new Vector();
    }
    
    public Node getDestination()
    {
        return destination;
    }
    public void setDestination( Node destination )
    {
        this.destination = destination;
    }
    public Node getSource()
    {
        return source;
    }
    public void setSource( Node source )
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
}
