package net.sourceforge.fullsync.fs;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class FileAttributes
{
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
