package net.sourceforge.fullsync;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;

import net.sourceforge.fullsync.fs.Directory;
import net.sourceforge.fullsync.fs.FileSystem;
import net.sourceforge.fullsync.fs.buffering.BufferedDirectory;
import net.sourceforge.fullsync.fs.buffering.BufferingProvider;
import net.sourceforge.fullsync.fs.buffering.syncfiles.SyncFilesBufferingProvider;
import net.sourceforge.fullsync.fs.file.LocalFileSystem;
import net.sourceforge.fullsync.fs.ftp.FtpFileSystem;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class FileSystemManager
{
    private Hashtable schemes;
    private Hashtable buffering;
    
    public FileSystemManager()
    {
        schemes = new Hashtable();
        schemes.put( "file", new LocalFileSystem() );
        schemes.put( "ftp", new FtpFileSystem() );
        
        buffering = new Hashtable();
        buffering.put( "syncfiles", new SyncFilesBufferingProvider() );
    }
    
    public Directory resolveUri( URI uri )
    	throws FileSystemException, URISyntaxException
    {
        String buffStrat = null;
        String scheme = uri.getScheme();
        if( scheme.equals( "buffered" ) )
        {
            URI buff = new URI( uri.getRawSchemeSpecificPart( ) );
            buffStrat = buff.getScheme();
            uri = new URI( buff.getRawSchemeSpecificPart() );
            scheme = uri.getScheme();
        }
        FileSystem fs = (FileSystem)schemes.get( scheme );
        Directory d = fs.resolveUri( uri );
        if( buffStrat != null )
            d = resolveBuffering( d, buffStrat );
        
        return d;
    }
    
    public BufferedDirectory resolveBuffering( Directory dir, String bufferingStrategy )
		throws FileSystemException
    {
        BufferingProvider p = (BufferingProvider)buffering.get( bufferingStrategy );
        
        if( p == null )
            return null; // FIXME throw exception buffering strategy not found
        
        return p.createBufferedDirectory( dir );
    }
}

