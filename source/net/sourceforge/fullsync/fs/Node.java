/*
 * Created on 18.07.2004
 */
package net.sourceforge.fullsync.fs;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface Node
{
    public String getName();
    public String getPath();
    public boolean isDirectory();
    public boolean exists();
    
    public boolean isBuffered();
    public Node getUnbuffered();
    
    public boolean delete();
    
    public void refresh();
}
