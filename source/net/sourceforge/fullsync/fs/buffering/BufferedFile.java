/*
 * Created on 20.07.2004
 */
package net.sourceforge.fullsync.fs.buffering;

import net.sourceforge.fullsync.fs.File;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface BufferedFile extends BufferedNode, File
{
    public void setLength( long length );
}
