package net.sourceforge.fullsync.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.sourceforge.fullsync.buffer.EntryDescriptor;
import net.sourceforge.fullsync.fs.Node;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class DeleteNodeEntryDescriptor implements EntryDescriptor
{
    private Node node;
    
    public DeleteNodeEntryDescriptor( Node node )
    {
        this.node = node;
    }

    public long getLength()
    {
        return 0;
    }

    public InputStream getInputStream() throws IOException
    {
        return null;
    }

    public OutputStream getOutputStream() throws IOException
    {
        return null;
    }

    public void finishStore()
    {
    }

    public void finishWrite()
    {
        node.delete();
    }

}
