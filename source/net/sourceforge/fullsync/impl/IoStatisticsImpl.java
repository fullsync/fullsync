package net.sourceforge.fullsync.impl;

import net.sourceforge.fullsync.IoStatistics;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class IoStatisticsImpl implements IoStatistics
{
	private static final long serialVersionUID = 1;
	
	public int filesCopied;
	public int dirsCreated;
	public int deletions;
	public int bytesTransferred;
	
	public int getFilesCopied()
    {
        return filesCopied;
    }
	public int getDirsCreated()
    {
        return dirsCreated;
    }
	public int getDeletions()
    {
        return deletions;
    }
	public int getBytesTransferred()
    {
        return bytesTransferred;
    }
	public int getCountActions()
	{
	    return filesCopied + dirsCreated + deletions;
	}
}
