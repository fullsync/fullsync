package net.sourceforge.fullsync.debug;

import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.FileComparer;
import net.sourceforge.fullsync.Location;
import net.sourceforge.fullsync.State;
import net.sourceforge.fullsync.fs.FileAttributes;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class DebugFileComparer implements FileComparer
{
    public State compareFiles( FileAttributes src, FileAttributes dst ) throws DataParseException
    {
        if( src.getLastModified() > dst.getLastModified() )
             return new State( State.FileChange, Location.Source );
        else if( src.getLastModified() < dst.getLastModified() )
             return new State( State.FileChange, Location.Destination );
        else if( src.getLength() != dst.getLength() )
             return new State( State.FileChange, Location.None );
        else return new State( State.NodeInSync, Location.Both );
    }
}
