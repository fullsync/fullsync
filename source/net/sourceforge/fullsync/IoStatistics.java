package net.sourceforge.fullsync;

import java.io.Serializable;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface IoStatistics extends Serializable
{
	public int getFilesCopied();
	public int getDirsCreated();
	public int getDeletions();
	public int getBytesTransferred();
	public int getCountActions();
}
