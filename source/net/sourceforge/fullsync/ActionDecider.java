package net.sourceforge.fullsync;

import net.sourceforge.fullsync.fs.Node;
import net.sourceforge.fullsync.impl.BufferStateDecider;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface ActionDecider
{
    
    // needed ?
    public Action getDefaultAction( State state, Node src, Node dst, BufferStateDecider bsd )  throws DataParseException;
    public Action[] getPossibleActions( State state, Node src, Node dst, BufferStateDecider bsd )  throws DataParseException;
    // TODO nodes what for ?
}
