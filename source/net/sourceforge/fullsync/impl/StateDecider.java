package net.sourceforge.fullsync.impl;

import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.FileComparer;
import net.sourceforge.fullsync.Location;
import net.sourceforge.fullsync.State;
import net.sourceforge.fullsync.fs.File;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class StateDecider implements net.sourceforge.fullsync.StateDecider
{
    private static final State inSyncNone = new State( State.NodeInSync, Location.None );
    private static final State orphanSrc = new State( State.Orphan, Location.Source );
    private static final State orphanDst = new State( State.Orphan, Location.Destination );
    private static final State inSyncBoth = new State( State.NodeInSync, Location.Both );
    private static final State dirFileSrc = new State( State.DirHereFileThere, Location.Source );
    private static final State dirFileDst = new State( State.DirHereFileThere, Location.Destination );
    
    protected FileComparer comparer;
    
    public StateDecider( FileComparer comparer )
    {
        this.comparer = comparer;
    }
    
    public State getState( File source, File destination )
    	throws DataParseException
    {
        if( !source.exists() )
             if( !destination.exists() )
                  return inSyncNone;
             else return orphanDst;
        else if( !destination.exists() )
             return orphanSrc;
        
        if( source.isDirectory() )
             if( destination.isDirectory() )
                  return inSyncBoth;
             else return dirFileSrc;
        else if( destination.isDirectory() )
             return dirFileDst;

        return comparer.compareFiles( source.getFileAttributes(), destination.getFileAttributes() );	
    }
}
