package net.sourceforge.fullsync;

import java.io.Serializable;

import net.sourceforge.fullsync.fs.Site;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class TaskTree implements Serializable
{
	private static final long serialVersionUID = 1;
	
    private Site source;
    private Site destination;
    private Task root;
    
    public TaskTree( Site source, Site destination )
    {
        this.source = source;
        this.destination = destination;
    }
    
    
    public Task getRoot()
    {
        return root;
    }
    public void setRoot( Task root )
    {
        this.root = root;
    }
    public Site getDestination()
    {
        return destination;
    }
    public Site getSource()
    {
        return source;
    }
    public int getTaskCount()
    {
    	return root.getTaskCount();
    }
}
