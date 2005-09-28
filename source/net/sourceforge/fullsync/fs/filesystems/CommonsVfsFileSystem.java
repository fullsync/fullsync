package net.sourceforge.fullsync.fs.filesystems;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.FileSystemException;
import net.sourceforge.fullsync.fs.FileSystem;
import net.sourceforge.fullsync.fs.Site;
import net.sourceforge.fullsync.fs.connection.CommonsVfsConnection;

public class CommonsVfsFileSystem implements FileSystem
{   
    public CommonsVfsFileSystem()
    {
    }
    
    public Site createConnection( ConnectionDescription desc )
        throws FileSystemException
    {
        //if( !desc.getUri().startsWith( "sftp:" ) )
        //  return null;
        
        
        return new CommonsVfsConnection( desc );
    }
}
