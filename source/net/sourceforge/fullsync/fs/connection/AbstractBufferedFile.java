package net.sourceforge.fullsync.fs.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;

import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.FileAttributes;
import net.sourceforge.fullsync.fs.buffering.BufferedFile;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class AbstractBufferedFile extends AbstractFile implements BufferedFile
{
    protected File unbuffered;
    
    private boolean dirty;
    private FileAttributes fsAttributes;
    
    
    public AbstractBufferedFile( BufferedConnection bc, File unbuffered, File parent, boolean directory, boolean exists )
    {
        super( bc, unbuffered.getName(), unbuffered.getPath(), parent, directory, exists );
        this.dirty = false;
        this.unbuffered = unbuffered;
        children = new Hashtable();
    }

    public boolean isDirty()
    {
        return dirty;
    }

    public void markDirty()
    {
        dirty = true;
    }
    public boolean isBuffered()
    {
        return true;
    }

    public File getUnbuffered()
    {
        return unbuffered;
    }
    public boolean makeDirectory() throws IOException
    {
        return unbuffered.makeDirectory();
    }
    public void setFsFileAttributes( FileAttributes fs )
    {
        this.fsAttributes = fs;
    }
    public FileAttributes getFsFileAttributes()
    {
        return fsAttributes;
    }
    public InputStream getInputStream() throws IOException
    {
        return unbuffered.getInputStream();
    }

    public OutputStream getOutputStream() throws IOException
    {
        return unbuffered.getOutputStream();
    }
    public void addChild( File node )
    {
        children.put( node.getName(), node );
    }
    public void removeChild( String name )
    {
        children.remove( name );
    }
    
    public void refresh()
    {
        // FIXME a dir refresh must be performed on the underlaying layer pretty carefully 
        unbuffered.refresh();
        refreshReference();
    }
    public void refreshBuffer()
    {
        directory = unbuffered.isDirectory();
        exists = unbuffered.exists();
        
        if( exists && !directory )
            setFsFileAttributes( unbuffered.getFileAttributes() );
    }
    
    public void refreshReference()
    {
        unbuffered = getParent().getUnbuffered().getChild( getName() );
    }
}
