package net.sourceforge.fullsync.fs.connection;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface BufferedConnection extends FileSystemConnection
{
    public void flushDirty();
    public boolean isMonitoringFileSystem();
	public void setMonitoringFileSystem(boolean monitor); 
}
