/*
 * Created on 18.07.2004
 */
package net.sourceforge.fullsync.fs.filesystems;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.FileSystemException;
import net.sourceforge.fullsync.fs.FileSystem;
import net.sourceforge.fullsync.fs.Site;
import net.sourceforge.fullsync.fs.connection.LocalConnection;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class LocalFileSystem implements FileSystem
{
    public LocalFileSystem()
    {
        
    }
    
    public Site createConnection( ConnectionDescription desc )
    	throws FileSystemException, IOException
    {
        if( !desc.getUri().startsWith( "file" ) )
            return null;// TODO throw exception here !
        
        //return new LocalDirectory( new java.io.File( uri ) );
        LocalConnection conn;
        try {
            conn = new LocalConnection( new java.io.File( new URI( desc.getUri() ).getPath() ) );
            return conn;
        } catch( URISyntaxException e ) {
            throw new FileSystemException( e );
        }
        
    }

}
