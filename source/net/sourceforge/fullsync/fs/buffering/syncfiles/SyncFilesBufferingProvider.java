package net.sourceforge.fullsync.fs.buffering.syncfiles;

import net.sourceforge.fullsync.fs.Directory;
import net.sourceforge.fullsync.fs.buffering.BufferedDirectory;
import net.sourceforge.fullsync.fs.buffering.BufferingProvider;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class SyncFilesBufferingProvider implements BufferingProvider
{

    public BufferedDirectory createBufferedDirectory( Directory dir )
    {
        return new SyncFilesBufferedDirectory( dir.getName(), dir, ".syncfiles" );
        // FIXME explicit mentioning of filename
    }

}
