package net.sourceforge.fullsync;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface IoStatistics
{
	public int getFilesCopied();
	public int getDirsCreated();
	public int getDeletions();
	public int getBytesTransferred();
	public int getCountActions();
}
