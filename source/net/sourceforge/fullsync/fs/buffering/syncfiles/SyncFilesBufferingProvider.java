package net.sourceforge.fullsync.fs.buffering.syncfiles;

import java.io.IOException;

import net.sourceforge.fullsync.fs.Site;
import net.sourceforge.fullsync.fs.buffering.BufferingProvider;
import net.sourceforge.fullsync.fs.connection.SyncFileBufferedConnection;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class SyncFilesBufferingProvider implements BufferingProvider
{

    public Site createBufferedSite( Site dir )
    	throws IOException
    {
        SyncFileBufferedConnection conn = 
            new SyncFileBufferedConnection( dir );
        return conn;
    }

}
