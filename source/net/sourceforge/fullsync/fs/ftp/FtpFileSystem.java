/*
 * Created on 20.07.2004
 */
package net.sourceforge.fullsync.fs.ftp;

import java.io.IOException;
import java.net.URI;

import net.sourceforge.fullsync.FileSystemException;
import net.sourceforge.fullsync.fs.Directory;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.FileSystem;
import net.sourceforge.fullsync.fs.Node;

import org.apache.commons.net.ftp.FTPClient;


/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class FtpFileSystem implements FileSystem
{
    public FtpFileSystem()
    {
    }
    public Directory resolveUri( URI uri )
    	throws FileSystemException
    {
        if( !uri.getScheme().equals( "ftp" ) )
            return null;
        
        try {
	        FTPClient client = new FTPClient();
	        client.connect( uri.getHost(), uri.getPort()==-1?21:uri.getPort() );
	        client.login( uri.getUserInfo(), "test" ); // FIXME args, where to get the password ?!?
	        
	        if( !client.changeWorkingDirectory( uri.getPath() ) )
	        {
	            // we cannot change to given dir, maybe its a file
	            /*
	            int i = url.getPath().lastIndexOf('/');
	            String path = url.getPath().substring( 0, i-1 );
	            if( client.changeWorkingDirectory( path ) )
	            {
	                Directory d = new FtpDirectory( client, path, null );
	                d.getChild( url.getPath() )
	            }*/
	            
	            // nah, just print an error
	            client.quit();
	            throw new FileSystemException( "Could not set working dir" );
	        }
	        
	        return new FtpDirectory(client, client.printWorkingDirectory() );
        } catch( IOException ioe ) {
            throw new FileSystemException(ioe);
        }
    }
    
    public Directory getDirectory( String path )
    {
        // TODO look for .. s
        /*
        FtpDirectory dir = new FtpDirectory( client, path );
        cache.put( path, dir );*/
        return null;
        
    }

    public File getFile( String path )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Node getNode( String path )
    {
        // TODO Auto-generated method stub
        return null;
    }

}
