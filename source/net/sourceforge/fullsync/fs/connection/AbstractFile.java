package net.sourceforge.fullsync.fs.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;

import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.FileAttributes;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class AbstractFile implements File
{
    protected FileSystemConnection fs;
    protected String name;
    protected String path;
    protected File parent;
    protected boolean exists;
    protected boolean filtered;
    protected boolean directory;
    protected FileAttributes attributes;
    protected Hashtable children;
    
    public AbstractFile( FileSystemConnection fs, String name, String path, File parent, boolean directory, boolean exists )
    {
        this.fs = fs;
        this.name = name;
        this.path = path;
        this.parent = parent;
        this.exists = exists;
        this.filtered = false;
        this.directory = directory;
        this.children = null;
    }
    
    public FileSystemConnection getConnection()
    {
        return fs;
    }

    public String getName()
    {
        return name;
    }

    public String getPath()
    {
        return path;
    }

    public File getParent()
    {
        return parent;
    }
    
    public boolean exists()
    {
        return exists;
    }

    public boolean isFiltered()
    {
        return filtered;
    }
    
    public void setFiltered( boolean filtered )
    {
        this.filtered = filtered;
    }

    public boolean isDirectory()
    {
        return directory;
    }

    public boolean isFile()
    {
        return !directory;
    }

    public boolean isBuffered()
    {
        return false;
    }

    public File getUnbuffered()
    {
        return this;
    }
    
    public void setFileAttributes( FileAttributes att )
    {
        this.attributes = att;
        getConnection().setFileAttributes( this, att );
    }
    public FileAttributes getFileAttributes()
    {
        return attributes;
    }
    
    public File createChild( String name, boolean directory )
    {
        File f = getConnection().createChild( this, name, directory );
        children.put( name, f );
        return f;
    }
    public File getChild( String name )
    {
        if( children == null )
            refresh();
        return (File)children.get( name );
    }
    public Collection getChildren()
    {
        if( children == null )
            refresh();
        return children.values();
    }

    public boolean makeDirectory() throws IOException
    {
        if( isDirectory() )
        {
            if( getConnection().makeDirectory( this ) )
            {
                exists = true;
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    public InputStream getInputStream() throws IOException
    {
        if( isFile() )
             return getConnection().readFile( this );
        else return null;
    }

    public OutputStream getOutputStream() throws IOException
    {
        if( isFile() )
        {
            OutputStream out = getConnection().writeFile( this );
            if( out != null )
                this.exists = true;
            return out;
        } else {
            return null;
        }
    }

    public boolean delete() throws IOException
    {
        if( fs.delete( this ) )
        {
            this.exists = false;
            return true;
        } else {
            return false;
        }
    }
    public void refresh()
    {
        if( isDirectory() )
        {
            // FIXME be aware of deleting entries that may be referenced by overlaying buffer
            Hashtable newChildren = getConnection().getChildren( this );
            if( children != null )
            for( Enumeration e = children.elements(); e.hasMoreElements(); )
            {
                File n = (File)e.nextElement();
                if( !newChildren.containsKey( n.getName() ) )
                {
                    if( n.exists() )
                         newChildren.put( n.getName(), new AbstractFile( getConnection(), n.getName(), n.getPath(), n.getParent(), n.isDirectory(), false ) );
                    else newChildren.put( n.getName(), n );
                }
            }
            children = newChildren;
        } else {
            // TODO update file attribute data / existing / is dir and stuff
            // HACK wtf !?
            parent.refresh();
        }
    }
    public void refreshBuffer()
    {
        
    }
}
