/*
 * Created on 20.07.2004
 */
package net.sourceforge.fullsync.buffer;

import java.io.IOException;
import java.util.Vector;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class DebugBuffer implements Buffer
{
    private Vector entries = null;
    
    public DebugBuffer()
    {
    }

    public void flush() throws IOException
    {
        entries.clear();
    }
    public void load()
    {
        entries = new Vector();
    }
    public void unload()
    {
        entries = null;
    }
    public void storeEntry( EntryDescriptor descriptor ) throws IOException
    {
        entries.add( descriptor );
    }
}
