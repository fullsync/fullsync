/*
 * Created on 18.07.2004
 */
package net.sourceforge.fullsync.fs.file;


import java.net.URI;

import net.sourceforge.fullsync.fs.Directory;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.FileSystem;
import net.sourceforge.fullsync.fs.Node;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class LocalFileSystem implements FileSystem
{
    public LocalFileSystem()
    {
        
    }
    
    public Directory resolveUri( URI uri )
    {
        if( !uri.getScheme().equals( "file" ) )
            return null;
        
        return new LocalDirectory( new java.io.File( uri ) );
    }

    public Directory getDirectory( String path )
    {
        return new LocalDirectory( new java.io.File( path ) );
    }
    public File getFile( String path )
    {
        return new LocalFile( new java.io.File( path ) );
    }
    public Node getNode( String path )
    {
        java.io.File f = new java.io.File( path );
        if( f.isDirectory() )
             return new LocalDirectory( f );
        else return new LocalFile( f );
    }
    
}
