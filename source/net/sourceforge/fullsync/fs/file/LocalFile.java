/*
 * Created on 18.07.2004
 */
package net.sourceforge.fullsync.fs.file;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;

import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.FileAttributes;


/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class LocalFile implements File
{
    private java.io.File file;
    private boolean directory;
    private boolean filtered;
    private Hashtable children;
    
    public LocalFile( java.io.File file )
    {
        this( file, file.isDirectory() );
    }
    public LocalFile( java.io.File file, boolean directory )
    {
        this.file = file;
        this.directory = directory;
        this.children = new Hashtable();
    }
    
    public File getParent()
    {
        return new LocalFile( file.getParentFile() );
    }
    public InputStream getInputStream()
    	throws IOException
    {
        return new FileInputStream( file );
    }
    public OutputStream getOutputStream()
    	throws IOException
    {
        return new FileOutputStream( file );
    }
    public Collection getChildren()
    {
        return children.values();
    }
    public File getChild( String name )
    {
        Object obj = children.get( name );
        if( obj == null )
             return null;
        else return (File)obj;
    }
    protected void addChild( File node )
    {
        children.put( node.getName(), node );
    }
    public File createChild( String name, boolean directory )
    {
        java.io.File f = new java.io.File( file.getPath()+java.io.File.separator+name );
        File n = new LocalFile( f, directory );
        addChild( n );
        return n;
    }
    public boolean makeDirectory()
    {
        return file.mkdir();
    }
    public String getName()
    {
        return file.getName();
    }
    public String getPath()
    {
        return file.getPath();
    }
    public File getUnbuffered()
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
        children.clear();
        if( !file.exists() )
            return;
        
        String[] files = file.list();
        Arrays.sort( files );
        
        for( int c = 0; c < files.length; c++ )
        {
            if( files[c] != "." && files[c] != ".." )
            {
                java.io.File file = new java.io.File(this.file.getPath()+java.io.File.separator+files[c]);
                children.put( files[c], new LocalFile( file ) );
            }
        }
    }
    public void refreshBuffer()
    {
        
    }
    
    public FileAttributes getFileAttributes()
    {
        return new FileAttributes( file.length(), file.lastModified() );
    }
    public boolean isFile()
    {
        return !directory;
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
        file.setLastModified( att.getLastModified() );
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
