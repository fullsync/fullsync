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
	private static final long serialVersionUID = 1;
	
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
        return null;
    }
    public boolean isFile()
    {
        return false;
    }
    public boolean isFiltered()
    {
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
        return null;
    }
    public File createChild( String name, boolean directory )
    {
        return null;
    }
    public FileAttributes getFileAttributes()
    {
        return null;
    }
    public void setFileAttributes( FileAttributes att )
    {
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
    }
}
