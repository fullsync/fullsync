/*
 * Created on 18.07.2004
 */
package net.sourceforge.fullsync.buffer;

import java.io.IOException;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface Buffer
{
    public void load();
    public void unload();
    public void flush() throws IOException;
    
    public void storeEntry( EntryDescriptor descriptor ) throws IOException;
    //public void writeEntryTo( Entry entry, OutputStream out ) throws IOException;
    
    public void addEntryFinishedListener( EntryFinishedListener listener );
    public void removeEntryFinishedListener( EntryFinishedListener listener );
}
