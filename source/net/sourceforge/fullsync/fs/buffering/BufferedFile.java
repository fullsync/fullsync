/*
 * Created on 20.07.2004
 */
package net.sourceforge.fullsync.fs.buffering;

import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.FileAttributes;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface BufferedFile extends File
{
    //public boolean isDirty();
    //public void markDirty();
    
    public FileAttributes getFsFileAttributes();
    
    public void addChild( File node );
    public void removeChild( String name );
    
    public void refreshReference();
}
