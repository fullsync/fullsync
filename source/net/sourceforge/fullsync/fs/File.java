/*
 * Created on 18.07.2004
 */
package net.sourceforge.fullsync.fs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface File
{
    public String getName();
    public String getPath();
    public File getParent();
    public boolean isDirectory();
    public boolean isFile();
    public boolean exists();
    public boolean isFiltered();
    public void setFiltered( boolean filtered );
    
    public boolean isBuffered();
    public File getUnbuffered();
    public void refreshBuffer();
    
    public void setFileAttributes( FileAttributes att );
    public FileAttributes getFileAttributes();

    public Collection getChildren();
    public File getChild( String name );
    
    // TODO currently, 'create' isnt the right word
    //		they do not exist before and may not exists after sync
    public File createChild( String name, boolean directory );// throws FileSystemException;
    
    public void refresh();
    
    public boolean makeDirectory()  throws IOException;
    
    public InputStream getInputStream() throws IOException;
    public OutputStream getOutputStream() throws IOException;
    
    public boolean delete() throws IOException;
}
