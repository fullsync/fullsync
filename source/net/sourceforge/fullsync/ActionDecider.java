package net.sourceforge.fullsync;

import net.sourceforge.fullsync.fs.File;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface ActionDecider
{
    
    // needed ?
    //public Action getDefaultAction( File src, File dst, StateDecider sd, BufferStateDecider bsd )  throws DataParseException;
    public Task getTask( File src, File dst, StateDecider sd, BufferStateDecider bsd )
    	throws DataParseException;
}
