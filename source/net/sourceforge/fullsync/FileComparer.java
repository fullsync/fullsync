package net.sourceforge.fullsync;

import net.sourceforge.fullsync.fs.File;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface FileComparer
{
    // TODO exception ?
    public State compareFiles( File src, File dst ) throws DataParseException;
}
