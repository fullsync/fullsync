/*
 * Created on 20.07.2004
 */
package net.sourceforge.fullsync.fs.ftp;

import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;

import net.sourceforge.fullsync.fs.Directory;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.Node;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;


/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class FtpDirectory implements Directory
{
    private FtpDirectory parent;
    
    private FTPClient client;
    private String path;
    private String name;
    
    private Hashtable children;
    
    public FtpDirectory( FTPClient client, String path )
    {
        this.parent = null;
        this.client = client;
        this.children = null;
        this.path = path;
        try {
            int i = path.lastIndexOf('/');
	        this.name = path.substring( i );
        } catch( StringIndexOutOfBoundsException e ) {
            this.name = path;
        }
    }
    public FtpDirectory( FTPClient client, String name, FtpDirectory parent )
    {
        this.parent = parent;
        this.client = client;
        this.path = parent.getPath()+"/"+name;
        this.name = name;
        this.children = null;
    }
    
    public void initialize()
    {
        try {
            children = new Hashtable();
            if( !client.changeWorkingDirectory( path ) )
                return;

            FTPFile[] files = client.listFiles();
            for( int c = 0; c < files.length; c++ )
            {
                if( files[c].isDirectory() )
                     children.put( files[c].getName(), new FtpDirectory( client, files[c].getName(), this ) );
                else children.put( files[c].getName(), new FtpFile( client, files[c], this ) );
            }
            
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }

    public Directory getParent()
    {
        return parent;
    }

    public Collection getChildren()
    {
        if( children == null )
            initialize();
        
        return children.values();
    }

    public Node getChild( String name )
    {
        if( children == null )
            initialize();
        return (Node)children.get( name );
    }

    public Directory createDirectory( String name )
    {
        // TODO check existing
        FtpDirectory dir = new FtpDirectory( client, name, this );
        children.put( name, dir );
        return dir;
    }

    public File createFile( String name )
    {
        FtpFile file = new FtpFile( client, name, this );
        children.put( name, file );
        return file;
    }

    public void makeDirectory()
    {
        try {
            //client.changeWorkingDirectory( parent.getPath() );
            client.makeDirectory( path );
        } catch( IOException e ) {
            e.printStackTrace();
        }
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
        return true;
    }

    public boolean exists()
    {
        try {
            return client.changeWorkingDirectory( path );
        } catch( IOException e ) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isBuffered()
    {
        return false;
    }

    public Node getUnbuffered()
    {
        return this;
    }

    public boolean delete()
    {
        if( children == null )
            initialize();
        if( children.isEmpty() )
        {
            try {
                return client.removeDirectory( getPath() );
            } catch( IOException ioe ) {
                return false;
            }
        } else {
            return false;
        }
    }

    public void refresh()
    {
        if( children == null )
            initialize();
        
        for( Enumeration e = children.elements(); e.hasMoreElements(); )
        {
            Node n = (Node)e.nextElement();
            if( n.isDirectory() == false )
                n.refresh();
        }
    }

}
