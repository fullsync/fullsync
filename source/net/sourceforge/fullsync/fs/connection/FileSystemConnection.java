package net.sourceforge.fullsync.fs.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;

import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.FileAttributes;
import net.sourceforge.fullsync.fs.Site;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface FileSystemConnection extends Site
{
    public File createChild( File parent, String name, boolean directory ) throws IOException;
    //public Directory getParent( File node );
    public Hashtable getChildren( File dir ) throws IOException;
    
    // refresh file, refresh directory ?
    
    public boolean makeDirectory( File dir ) throws IOException;
    
    public boolean writeFileAttributes( File file, FileAttributes att ) throws IOException;
    public InputStream readFile( File file ) throws IOException;
    public OutputStream writeFile( File file ) throws IOException;

    public boolean delete( File node ) throws IOException;
}
