/*
 * Created on 18.07.2004
 */
package net.sourceforge.fullsync.fs;

import java.io.IOException;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.FileSystemException;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface FileSystem
{
    public Site createConnection( ConnectionDescription desc ) throws FileSystemException, IOException;
}
