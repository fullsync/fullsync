/*
 * Created on 18.07.2004
 */
package net.sourceforge.fullsync.fs;

import java.net.URI;

import net.sourceforge.fullsync.FileSystemException;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface FileSystem
{
    public Directory getDirectory( String path );
    public File getFile( String path );
    public Node getNode( String path );
    
    public Directory resolveUri( URI uri ) throws FileSystemException;
}
