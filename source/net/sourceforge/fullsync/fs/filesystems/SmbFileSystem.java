package net.sourceforge.fullsync.fs.filesystems;

import java.io.IOException;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.FileSystemException;
import net.sourceforge.fullsync.fs.FileSystem;
import net.sourceforge.fullsync.fs.Site;
import net.sourceforge.fullsync.fs.connection.SmbConnection;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class SmbFileSystem implements FileSystem
{

    public Site createConnection( ConnectionDescription desc ) throws FileSystemException
    {
        if( !desc.getUri().startsWith( "smb:" ) )
            return null;
        
        try {
            return new SmbConnection( desc );
        } catch( IOException e ) {
            throw new FileSystemException( e );
        }
        
    }
    
}