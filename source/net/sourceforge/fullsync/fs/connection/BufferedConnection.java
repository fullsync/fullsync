package net.sourceforge.fullsync.fs.connection;

import java.io.IOException;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface BufferedConnection extends FileSystemConnection
{
    public void flushDirty() throws IOException;
    public boolean isMonitoringFileSystem();
	public void setMonitoringFileSystem(boolean monitor); 
}
