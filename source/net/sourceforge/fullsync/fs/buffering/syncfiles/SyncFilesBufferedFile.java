/*
 * Created on 18.07.2004
 */
package net.sourceforge.fullsync.fs.buffering.syncfiles;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Date;

import net.sourceforge.fullsync.fs.Directory;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.buffering.BufferedDirectory;
import net.sourceforge.fullsync.fs.buffering.BufferedFile;


/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class SyncFilesBufferedFile extends SyncFilesBufferedNode implements BufferedFile
{
    private long length;
    private long lastModified;
    private long lengthUnbuff;
    private long lastModifiedUnbuff;
    
    public SyncFilesBufferedFile( String name, File f )
    {
        super( name, f );
    }
    public SyncFilesBufferedFile( String name, File f, BufferedDirectory parent )
    {
        super( name, f, parent );
    }
    public SyncFilesBufferedFile( String name, File f, BufferedDirectory parent, String[] bufferParts )
    {
        super( name, f, parent );
        
        length = Long.parseLong( bufferParts[2] );
        lengthUnbuff = Long.parseLong( bufferParts[3] );
        lastModified = Long.parseLong( bufferParts[4] );
        lastModifiedUnbuff = Long.parseLong( bufferParts[5] );
    }
    
    
    public Directory getDirectory()
    {
        return parent;
    }
    public InputStream getInputStream() throws IOException
    {
        return ((File)unbuff).getInputStream();
    }
    public OutputStream getOutputStream() throws IOException
    {
        markDirty();
        return ((File)unbuff).getOutputStream();
    }
    public void setLastModified( long lm )
    {
        lastModified = lm;
        ((File)unbuff).setLastModified( lm );
    }
    public long getLastModified()
    {
        return lastModified;
    }
    public long getLength()
    {
        return length;
    }
    public void setLength( long length )
    {
        this.length = length;
    }
    public boolean delete()
    {
        markDirty();
        String name = unbuff.getName();
        if( unbuff.delete() )
        {
            parent.removeChild( name );
            return true;
        } else {
            return false;
        }
    }
    public boolean isInSync()
    {
        File f = (File)unbuff;
        if( f == null )
            return false;
        System.out.println( "isInSync "+getPath() );
        DateFormat.getDateTimeInstance().format( new Date( lastModifiedUnbuff ) );
        System.out.println( "UnBuff: "+DateFormat.getDateTimeInstance().format( new Date( f.getLastModified() ) )+"  Buff: "+DateFormat.getDateTimeInstance().format( new Date( lastModifiedUnbuff ) ) );
        System.out.println( "Diff: "+Math.abs( f.getLastModified()-lastModifiedUnbuff ) );
        if( f.getLength() == lengthUnbuff && 
            Math.abs( f.getLastModified()-lastModifiedUnbuff ) < 60000 )
        {
            return true;
        } else {
            return false;
        }
    }
    public void refresh()
    {
        unbuff.refresh();
        System.out.println( "SetNew: "+DateFormat.getDateTimeInstance().format( new Date( ((File)unbuff).getLastModified() ) ) );
        lastModifiedUnbuff = ((File)unbuff).getLastModified();
        lengthUnbuff = ((File)unbuff).getLength();
    }
    public void markDirty()
    {
        parent.markDirty();
    }
    public String toBufferLine()
    {
        return "F\t"+getName()+"\t"+length+"\t"+lengthUnbuff+
                "\t"+lastModified+"\t"+lastModifiedUnbuff;
    }
    public boolean isDirectory()
    {
        return false;
    }
    
}
