package net.sourceforge.fullsync.fs;

import java.io.Serializable;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class FileAttributes implements Serializable
{
	private static final long serialVersionUID = 1;
	
    private long length;
    private long lastModified;
    
    public FileAttributes( long length, long lastModified )
    {
        this.length = length;
        this.lastModified = lastModified;
    }
    public long getLength() 
    { 
        return length;
    }
    public long getLastModified() 
    { 
        return lastModified;
    }
}
