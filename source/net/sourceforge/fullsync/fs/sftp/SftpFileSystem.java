package net.sourceforge.fullsync.fs.sftp;

import java.io.IOException;
import java.net.URISyntaxException;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.FileSystemException;
import net.sourceforge.fullsync.fs.FileSystem;
import net.sourceforge.fullsync.fs.Site;
import net.sourceforge.fullsync.fs.connection.SftpConnection;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class SftpFileSystem implements FileSystem
{

    public Site createConnection( ConnectionDescription desc ) throws FileSystemException
    {
        if( !desc.getUri().startsWith( "sftp:" ) )
            return null;
        try {
            return new SftpConnection( desc );
        } catch( IOException e ) {
            throw new FileSystemException( e );
        } catch( URISyntaxException e ) {
            throw new FileSystemException( e );
        }
        
    }

}
