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
	private static final long serialVersionUID = 1;
	
    protected File unbuffered;
    
    private boolean dirty;
    private FileAttributes fsAttributes;
    
    public AbstractBufferedFile( BufferedConnection bc, String name, String path, File parent, boolean directory, boolean exists )
    {
        super( bc, name, path, parent, directory, exists );
        this.dirty = false;
        this.unbuffered = null;
        children = new Hashtable();
    }
    
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

    public File getUnbuffered() throws IOException
    {
        if( unbuffered == null )
            refreshReference();
        return unbuffered;
    }
    public boolean makeDirectory() throws IOException
    {
        return getUnbuffered().makeDirectory();
    }
    public void setFsFileAttributes( FileAttributes fs )
    {
        this.fsAttributes = fs;
    }
    public FileAttributes getFsFileAttributes()
    {
        return fsAttributes;
    }
    public FileAttributes getFileAttributes()
    {
        // in case we are requesting file attributes that 
        // were not explicitly set, just take the fs attributes
        FileAttributes attrib = super.getFileAttributes();
        if( attrib == null )
             return fsAttributes;
        else return attrib;
    }
    public void clearCachedFileAttributes() throws IOException
    {
        setFileAttributes( getFsFileAttributes() );
    }
    public InputStream getInputStream() throws IOException
    {
        return getUnbuffered().getInputStream();
    }

    public OutputStream getOutputStream() throws IOException
    {
        return getUnbuffered().getOutputStream();
    }
    public void addChild( File node )
    {
        children.put( node.getName(), node );
    }
    public void removeChild( String name )
    {
        children.remove( name );
    }
    
	
    public void refresh() throws IOException
    {
        // FIXME a dir refresh must be performed on the underlaying layer pretty carefully 
        getUnbuffered().refresh();
        refreshReference();
    }
    public void refreshBuffer() throws IOException
    {
        directory = getUnbuffered().isDirectory();
        exists = getUnbuffered().exists();
        
        if( exists && !directory )
            setFsFileAttributes( getUnbuffered().getFileAttributes() );
    }
    
    public void refreshReference() throws IOException
    {
        unbuffered = getParent().getUnbuffered().getChild( getName() );
        if( unbuffered == null )
            unbuffered = getParent().getUnbuffered().createChild( getName(), directory );
    }
    
}
