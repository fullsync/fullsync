/*
 * Created on 18.07.2004
 */
package net.sourceforge.fullsync.fs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface File extends Node
{
    public Directory getDirectory();
    
    public long getLength();
    public long getLastModified();
    public void setLastModified( long lm );
    public InputStream getInputStream() throws IOException;
    public OutputStream getOutputStream() throws IOException;
}
