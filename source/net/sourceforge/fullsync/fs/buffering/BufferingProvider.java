/*
 * Created on 20.07.2004
 */
package net.sourceforge.fullsync.fs.buffering;

import net.sourceforge.fullsync.fs.Directory;


/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface BufferingProvider
{
    public BufferedDirectory createBufferedDirectory( Directory dir );
}
