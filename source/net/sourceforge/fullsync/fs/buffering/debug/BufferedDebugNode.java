package net.sourceforge.fullsync.fs.buffering.debug;

import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.FileAttributes;
import net.sourceforge.fullsync.fs.buffering.BufferedFile;
import net.sourceforge.fullsync.fs.debug.DebugNode;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class BufferedDebugNode extends DebugNode implements BufferedFile
{
    private DebugNode unbuff;
    
    public BufferedDebugNode( boolean exists, boolean directory, long length, long lm )
    {
        super( exists, directory, length, lm );
        this.unbuff = null;
    }

    public void flushDirty()
    {
    }

    public void addChild( File node )
    {
    }
    public void removeChild( String name )
    {
    }

    public File createChild( String name )
    {
        // TODO Auto-generated method stub
        return null;
    }
    public boolean isFile()
    {
        // TODO Auto-generated method stub
        return false;
    }
    public boolean isFiltered()
    {
        // TODO Auto-generated method stub
        return false;
    }
    public boolean isDirty()
    {
        return false;
    }

    public void markDirty()
    {
    }

    public FileAttributes getFsFileAttributes()
    {
        // TODO Auto-generated method stub
        return null;
    }
    public File createChild( String name, boolean directory )
    {
        // TODO Auto-generated method stub
        return null;
    }
    public FileAttributes getFileAttributes()
    {
        // TODO Auto-generated method stub
        return null;
    }
    public void setFileAttributes( FileAttributes att )
    {
        // TODO Auto-generated method stub

    }
    public boolean isBuffered()
    {
        return true;
    }
    public long getFileSystemLength() 
    {
        return getLength();
    }
    public long getFileSystemLastModified()
    {
        return getLastModified();
    }
    public File getUnbuffered()
    {
        return unbuff;
    }
    public void setUnbuffered( DebugNode unbuff )
    {
        this.unbuff = unbuff;
    }
    public String toString()
    {
        return super.toString()+" [FS: "+unbuff.toString()+"]";
    }
    
    public void refreshReference()
    {
        // TODO Auto-generated method stub

    }
}
