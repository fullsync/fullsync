package net.sourceforge.fullsync.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.sourceforge.fullsync.BufferUpdate;
import net.sourceforge.fullsync.buffer.EntryDescriptor;
import net.sourceforge.fullsync.fs.File;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class BufferUpdateEntryDescriptor implements EntryDescriptor
{
    private int bufferUpdate;
    private File src;
    private File dst;
    
    public BufferUpdateEntryDescriptor( File src, File dst, int bufferUpdate )
    {
        this.bufferUpdate = bufferUpdate;
        this.src = src;
        this.dst = dst;
    }
    public Object getReferenceObject()
    {
        return null;
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
	        if( (bufferUpdate & BufferUpdate.Source) > 0 )
	            src.refreshBuffer();
	        if( (bufferUpdate & BufferUpdate.Destination) > 0 )
	            dst.refreshBuffer();
        } catch( IOException ioe ) {
            ioe.printStackTrace();
        }
    }
    
    public String getOperationDescription()
    {
        return null;
    }

}
