package net.sourceforge.fullsync.fs.debug;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.FileAttributes;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class DebugNode implements File
{
    private String name;
    private String path;
    private boolean directory;
    private boolean exists;
    private boolean filtered;
    
    private long length;
    private long lastModified;
    
    public DebugNode( boolean exists, boolean directory, long length, long lm )
    {
        this.name = "debug";
        this.path = "debug";
        this.exists = exists;
        this.directory = directory;
        this.length = length;
        this.lastModified = lm;
    }

    public File getDirectory()
    {
        return null;
    }

    public long getLength()
    {
        return length;
    }

    public long getLastModified()
    {
        return lastModified;
    }

    public void setLastModified( long lm )
    {
        this.lastModified = lm;
    }

    public InputStream getInputStream() throws IOException
    {
        return null;
    }

    public OutputStream getOutputStream() throws IOException
    {
        return null;
    }

    public File getParent()
    {
        return null;
    }

    public Collection getChildren()
    {
        return null;
    }

    public File getChild( String name )
    {
        return null;
    }

    public File createDirectory( String name )
    {
        return null;
    }

    public File createFile( String name )
    {
        return null;
    }

    public boolean makeDirectory()
    {
        return true;
    }

    public String getName()
    {
        return name;
    }

    public String getPath()
    {
        return path;
    }

    public boolean isDirectory()
    {
        return directory;
    }

    public boolean exists()
    {
        return exists;
    }

    public boolean isBuffered()
    {
        return false;
    }

    public File getUnbuffered()
    {
        return null;
    }

    public boolean delete()
    {
        return false;
    }

    public void refresh()
    {

    }

    public void refreshBuffer()
    {
    }
    public File createChild( String name, boolean directory )
    {
        return null;
    }
    public FileAttributes getFileAttributes()
    {
        return null;
    }
    public boolean isFile()
    {
        return false;
    }
    public boolean isFiltered()
    {
        return filtered;
    }
    public void setFiltered( boolean filtered )
    {
        this.filtered = filtered;
    }
    public void setFileAttributes( FileAttributes att )
    {
    }
    public void writeFileAttributes() throws IOException
    {

    }
    public void setDirectory( boolean directory )
    {
        this.directory = directory;
    }
    public void setExists( boolean exists )
    {
        this.exists = exists;
    }
    public void setLength( long length )
    {
        this.length = length;
    }
    public void setName( String name )
    {
        this.name = name;
    }
    public void setPath( String path )
    {
        this.path = path;
    }
    public String toString()
    {
        if( !exists )
            return "not exists";
        else if( directory )
            return "Directory";
        else
            return "File ("+length+","+lastModified+")";
    }
}
