package net.sourceforge.fullsync;

import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

import net.sourceforge.fullsync.fs.File;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class Task implements Serializable
{
	private static final long serialVersionUID = 1;
	
    private File source;
    private File destination;
    private State state;
    private Action[] actions;
    private int currentAction;
    
    // REVISIT if we dont need this data here we can also create a map in the 
    //         LogWindow class to keep track of Task-TableItem association.
    private Object data;
    
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
    public int getTaskCount()
    {
    	int count = 0;
    	Enumeration e = children.elements();
    	while( e.hasMoreElements() )
    	{
    		count += ((Task)e.nextElement()).getTaskCount();
    	}
    	return count+1;
    }
    
    public Object getData() {
    	return data;
    }
    
    public void setData(Object data) {
    	this.data = data;
    }
    
    private void writeObject(java.io.ObjectOutputStream out)
    	throws IOException
	{
    	out.writeObject(source);
    	out.writeObject(destination);
    	out.writeObject(state);
    	out.writeObject(actions);
    	out.writeObject(children);
	}
    
    private void readObject(java.io.ObjectInputStream in)
    	throws IOException, ClassNotFoundException
	{
        this.source = (File) in.readObject();
        this.destination = (File) in.readObject();
        this.state = (State) in.readObject();
        this.actions = (Action[]) in.readObject();
        this.children = (Vector) in.readObject();
        this.currentAction = 0;
	}
    
}
