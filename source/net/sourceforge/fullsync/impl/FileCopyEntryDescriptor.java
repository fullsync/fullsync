package net.sourceforge.fullsync.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.sourceforge.fullsync.buffer.EntryDescriptor;
import net.sourceforge.fullsync.fs.File;


/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class FileCopyEntryDescriptor implements EntryDescriptor
{
    private Object reference;
    private File src;
    private File dst;
    private InputStream inputStream;
    private OutputStream outputStream;
    
    public FileCopyEntryDescriptor( Object reference, File src, File dst )
    {
        this.reference = reference;
        this.src = src;
        this.dst = dst;
    }
    public Object getReferenceObject()
    {
        return reference;
    }
    public long getLength()
    {
        return src.getFileAttributes().getLength();
    }
    public InputStream getInputStream()
    	throws IOException
    {
        if( inputStream == null )
            inputStream = src.getInputStream();
        return inputStream;
    }
    public OutputStream getOutputStream()
    	throws IOException
    {
        if( outputStream == null )
            outputStream = dst.getOutputStream();
        return outputStream;
    }
    public void finishWrite()
    {
        try {
	        if( outputStream != null )
	            outputStream.close();
	        dst.setFileAttributes( src.getFileAttributes() );
	        dst.writeFileAttributes();
	        dst.refresh();
        } catch( IOException ex ) {
            ex.printStackTrace();
        }
    }
    public void finishStore()
    {
        try {
	        if( inputStream != null )
	            inputStream.close();
    	} catch( IOException ex ) {}
    }
    public String getOperationDescription()
    {
        return "Copied "+src.getPath()+" to "+dst.getPath(); 
    }
}
