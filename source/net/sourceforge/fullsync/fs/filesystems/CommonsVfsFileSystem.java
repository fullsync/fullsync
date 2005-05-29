package net.sourceforge.fullsync.fs.filesystems;

import java.io.File;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.FileSystemException;
import net.sourceforge.fullsync.fs.FileSystem;
import net.sourceforge.fullsync.fs.Site;
import net.sourceforge.fullsync.fs.connection.CommonsVfsConnection;

import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.provider.sftp.SftpFileSystemConfigBuilder;
import org.apache.commons.vfs.provider.sftp.TrustEveryoneUserInfo;

import com.jcraft.jsch.UserInfo;

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
