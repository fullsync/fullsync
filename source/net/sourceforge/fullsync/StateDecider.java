package net.sourceforge.fullsync;

import net.sourceforge.fullsync.fs.File;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface StateDecider
{
    public State getState( File src, File dst ) throws DataParseException;
}
