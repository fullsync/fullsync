package net.sourceforge.fullsync.fs;

import java.io.IOException;
import java.io.Serializable;


/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface Site extends Serializable
{
    public File getRoot();
    /*
    public boolean isBuffered();
    public File getUnbuffered();
    */
    
    // open ?
    public void flush() throws IOException;
    public void close() throws IOException;
    
    public String getUri();
    
    public boolean isCaseSensitive();
    
    //public File getFile( String path );
}
