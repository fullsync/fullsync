package net.sourceforge.fullsync.impl;

import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.FileComparer;
import net.sourceforge.fullsync.Location;
import net.sourceforge.fullsync.State;
import net.sourceforge.fullsync.fs.Node;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class BufferStateDecider extends StateDecider
{
    public BufferStateDecider( FileComparer comparer )
    {
        super( comparer );
    }
    public State getState( Node buffered )
    	throws DataParseException
    {
        if( !buffered.isBuffered() )
            return new State( State.NodeInSync, buffered.exists()?Location.Both:Location.None );
        
        return getState( buffered, buffered.getUnbuffered() );
    }
}
