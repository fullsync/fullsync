package net.sourceforge.fullsync;

import net.sourceforge.fullsync.fs.FileAttributes;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface FileComparer
{
    // TODO exception ?
    public State compareFiles( FileAttributes src, FileAttributes dst ) throws DataParseException;
}
