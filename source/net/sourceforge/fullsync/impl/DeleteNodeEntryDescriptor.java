package net.sourceforge.fullsync.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.buffer.EntryDescriptor;
import net.sourceforge.fullsync.fs.File;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class DeleteNodeEntryDescriptor implements EntryDescriptor
{
    private Object reference;
    private File node;
    
    public DeleteNodeEntryDescriptor( Object reference, File node )
    {
        this.reference = reference;
        this.node = node;
    }
    public Object getReferenceObject()
    {
        return reference;
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
            ExceptionHandler.reportException( ioe );
        }
    }
    public String getOperationDescription()
    {
        return "Deleted File "+node.getPath();
    }

}
