/*
 * Created on 20.07.2004
 */
package net.sourceforge.fullsync.fs.buffering;

import java.io.IOException;

import net.sourceforge.fullsync.FileSystemException;
import net.sourceforge.fullsync.fs.Site;



/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface BufferingProvider
{
    public Site createBufferedSite( Site site ) throws FileSystemException, IOException;
}
