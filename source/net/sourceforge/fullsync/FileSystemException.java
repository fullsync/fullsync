package net.sourceforge.fullsync;

/**
 * Thrown when an error occured while accessing a file system.
 * 
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class FileSystemException extends NestedException
{

	/**
	 * Constructor for FileSystemException.
	 * @param text
	 */
	public FileSystemException(String text)
	{
		super(text);
	}

	/**
	 * Constructor for FileSystemException.
	 * @param ex
	 */
	public FileSystemException(Throwable ex)
	{
		super(ex);
	}

	/**
	 * Constructor for FileSystemException.
	 * @param text
	 * @param ex
	 */
	public FileSystemException(String text, Throwable ex)
	{
		super(text, ex);
	}

}
