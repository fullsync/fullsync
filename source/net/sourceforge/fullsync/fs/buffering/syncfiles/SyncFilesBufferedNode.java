/*
 * Created on 18.07.2004
 */
package net.sourceforge.fullsync.fs.buffering.syncfiles;

import net.sourceforge.fullsync.fs.Node;
import net.sourceforge.fullsync.fs.buffering.BufferedDirectory;
import net.sourceforge.fullsync.fs.buffering.BufferedNode;


/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public abstract class SyncFilesBufferedNode implements BufferedNode
{
    protected BufferedDirectory parent;
    protected Node unbuff;
    protected boolean dirty;
    protected String name;

    //private String name;
    //private String path;
    //private boolean directory;
    
    public SyncFilesBufferedNode( String name, Node unbuff )
    {
        this.parent = null;
        this.unbuff = unbuff;
        this.name = name;
    }
    public SyncFilesBufferedNode( String name, Node unbuff, BufferedDirectory parent )
    {
        this.parent = parent;
        this.unbuff = unbuff;
        this.name = name;
    }
    
    public String getName()
    {
        return name;
    }

    public String getPath()
    {
        if( unbuff == null )
             return parent.getPath()+"/"+name;
        else return unbuff.getPath();
    }

    public boolean isBuffered()
    {
        return true;
    }
    
    public boolean isDirty()
    {
        return dirty;
    }

    public Node getUnbuffered()
    {
        return unbuff;
    }
    
    public void markDirty()
    {
        dirty = true;
    }
    
    public String toString()
    {
        return "Buffered: "+unbuff.toString();
    }
    
    public boolean exists()
    {
        return unbuff==null?false:unbuff.exists();
    }
    
    public abstract boolean isInSync();
    public abstract String toBufferLine();
}
