package net.sourceforge.fullsync;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;

import net.sourceforge.fullsync.fs.FileSystem;
import net.sourceforge.fullsync.fs.Site;
import net.sourceforge.fullsync.fs.buffering.BufferingProvider;
import net.sourceforge.fullsync.fs.buffering.syncfiles.SyncFilesBufferingProvider;
import net.sourceforge.fullsync.fs.file.LocalFileSystem;
import net.sourceforge.fullsync.fs.ftp.FtpFileSystem;
import net.sourceforge.fullsync.fs.sftp.SftpFileSystem;

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
        schemes.put( "sftp", new SftpFileSystem() );
        
        buffering = new Hashtable();
        buffering.put( "syncfiles", new SyncFilesBufferingProvider() );
    }
    
    public Site createConnection( ConnectionDescription desc )
    	throws FileSystemException, IOException, URISyntaxException
    {
        URI url = new URI( desc.getUri() );
        String scheme = url.getScheme();
        
        FileSystem fs = (FileSystem)schemes.get( scheme );
        Site s = fs.createConnection( desc );
        
        if( desc.getBufferStrategy() != null && !desc.getBufferStrategy().equals( "" ) )
            s = resolveBuffering( s, desc.getBufferStrategy() );
        
        return s;
    }
    
    public Site resolveBuffering( Site dir, String bufferStrategy )
		throws FileSystemException
    {
        BufferingProvider p = (BufferingProvider)buffering.get( bufferStrategy );
        
        if( p == null )
            throw new FileSystemException( "BufferStrategy '"+bufferStrategy+"' not found");
        
        return p.createBufferedSite( dir );
    }
}

