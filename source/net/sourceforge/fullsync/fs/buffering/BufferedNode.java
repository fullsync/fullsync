/*
 * Created on 20.07.2004
 */
package net.sourceforge.fullsync.fs.buffering;

import net.sourceforge.fullsync.fs.Node;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface BufferedNode extends Node
{
    public boolean isDirty();
    public void markDirty();
}
