package net.sourceforge.fullsync.impl;

import java.io.IOException;

import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.FileComparer;
import net.sourceforge.fullsync.Location;
import net.sourceforge.fullsync.State;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.buffering.BufferedFile;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class BufferStateDecider extends StateDecider implements net.sourceforge.fullsync.BufferStateDecider
{
    public BufferStateDecider( FileComparer comparer )
    {
        super( comparer );
    }
    public State getState( File buffered )
    	throws DataParseException, IOException
    {
        if( !buffered.isBuffered() )
            return new State( State.NodeInSync, buffered.exists()?Location.Both:Location.None );
        
        File source = buffered.getUnbuffered();
        BufferedFile destination = (BufferedFile)buffered;
        
        if( !source.exists() )
            if( !destination.exists() )
                 return new State( State.NodeInSync, Location.None );
            else return new State( State.Orphan, Location.Destination );
        else if( !destination.exists() )
            return new State( State.Orphan, Location.Source );
       
        if( source.isDirectory() )
            if( destination.isDirectory() )
                 return new State( State.NodeInSync, Location.Both );
            else return new State( State.DirHereFileThere, Location.Source );
        else if( destination.isDirectory() )
            return new State( State.DirHereFileThere, Location.Destination );

        return comparer.compareFiles( source.getFileAttributes(), destination.getFsFileAttributes() );	
    }
}
