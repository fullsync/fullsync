package net.sourceforge.fullsync.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.sourceforge.fullsync.buffer.EntryDescriptor;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.buffering.BufferedFile;


/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class FileCopyEntryDescriptor implements EntryDescriptor
{
    private File src;
    private File dst;
    
    public FileCopyEntryDescriptor( File src, File dst )
    {
        this.src = src;
        this.dst = dst;
    }
    
    public long getLength()
    {
        return src.getLength();
    }
    public InputStream getInputStream()
    	throws IOException
    {
        return src.getInputStream();
    }
    public OutputStream getOutputStream()
    	throws IOException
    {
        return dst.getOutputStream();
    }
    public void finishWrite()
    {
        if( dst.isBuffered() )
            ((BufferedFile)dst).setLength( src.getLength() );
        dst.setLastModified( src.getLastModified() );
        // TODO set attributes
        dst.refresh();
    }
    public void finishStore()
    {
        
    }
}
