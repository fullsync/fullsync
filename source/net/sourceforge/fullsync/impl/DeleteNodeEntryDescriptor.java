package net.sourceforge.fullsync.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.sourceforge.fullsync.buffer.EntryDescriptor;
import net.sourceforge.fullsync.fs.File;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class DeleteNodeEntryDescriptor implements EntryDescriptor
{
    private File node;
    
    public DeleteNodeEntryDescriptor( File node )
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
        try {
            node.delete();
            
        } catch( IOException ioe ) {
            ioe.printStackTrace();
        }
    }
    public String getOperationDescription()
    {
        return "Deleted File "+node.getPath();
    }

}
