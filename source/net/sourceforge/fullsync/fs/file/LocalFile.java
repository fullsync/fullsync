/*
 * Created on 18.07.2004
 */
package net.sourceforge.fullsync.fs.file;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.sourceforge.fullsync.fs.Directory;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.Node;


/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class LocalFile implements File
{
    //private LocalDirectory parent;
    private java.io.File file;
    
    public LocalFile( java.io.File file )
    {
        //this.parent = null;
        this.file = file;
    }/*
    public LocalFile( java.io.File file, LocalDirectory parent )
    {
        this.parent = parent;
        this.file = file;
    }*/
    
    public Directory getDirectory()
    {
        return new LocalDirectory( file.getParentFile() );
    }
    public InputStream getInputStream()
    	throws IOException
    {
        return new FileInputStream( file );
    }
    public long getLength()
    {
        return file.length();
    }
    public long getLastModified()
    {
        return file.lastModified();
    }
    public void setLastModified( long lm )
    {
        file.setLastModified( lm );
    }
    public OutputStream getOutputStream()
    	throws IOException
    {
        return new FileOutputStream( file );
    }
    public String getName()
    {
        return file.getName();
    }
    public String getPath()
    {
        return file.getPath();
    }
    public Node getUnbuffered()
    {
        return this;
    }
    public boolean isBuffered()
    {
        return false;
    }
    public boolean isDirectory()
    {
        return false;
    }
    public void refresh()
    {
    }
    public boolean delete()
    {
        return file.delete();
    }
    public boolean exists()
    {
        return file.exists();
    }
    public String toString()
    {
        return file.toString();
    }
}
