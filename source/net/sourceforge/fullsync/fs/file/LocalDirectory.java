/*
 * Created on 18.07.2004
 */
package net.sourceforge.fullsync.fs.file;


import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;

import net.sourceforge.fullsync.fs.Directory;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.Node;


/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class LocalDirectory implements Directory
{
    private java.io.File file;
    private Hashtable children;
    
    public LocalDirectory( java.io.File file )
    {
        this.file = file;
        this.children = new Hashtable();
        refresh();
    }
    
    public String getName()
    {
        return file.getName();
    }
    public String getPath()
    {
        return file.getPath();
    }
    public boolean isDirectory()
    {
        return true;
    }
    public Directory getParent()
    {
        return new LocalDirectory( file.getParentFile() );
    }
    public Collection getChildren()
    {
        return children.values();
    }
    public Node getChild( String name )
    {
        Object obj = children.get( name );
        if( obj == null )
             return null;
        else return (Node)obj;
    }
    protected void addChild( Node node )
    {
        children.put( node.getName(), node );
    }
    public Directory createDirectory( String name )
    {
        java.io.File f = new java.io.File( file.getPath()+java.io.File.separator+name );
        //f.mkdir();
        Directory n = new LocalDirectory( f );
        addChild( n );
        return n;
    }
    public File createFile( String name )
    {
        java.io.File f = new java.io.File( file.getPath()+java.io.File.separator+name );
        /*
        try {
            f.createNewFile();
        } catch( IOException e ) {
            throw new FileSystemException( e );
        }*/
        File n = new LocalFile( f );
        addChild( n );
        return n;
    }
    public void makeDirectory()
    {
        file.mkdir();
        System.out.println( "mkdir: "+file );
    }
    public Node getUnbuffered()
    {
        return this;
    }
    public boolean isBuffered()
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
                if( file.isDirectory() )
                     children.put( files[c], new LocalDirectory( file ) );
                else children.put( files[c], new LocalFile( file ) );
            }
        }
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
