package net.sourceforge.fullsync;


/**
 * not used yet
 * 
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class SynchronizationException extends NestedException
{

	/**
	 * Constructor for SynchronizationException.
	 * @param text
	 */
	public SynchronizationException(String text)
	{
		super(text);
	}

	/**
	 * Constructor for SynchronizationException.
	 * @param ex
	 */
	public SynchronizationException(Throwable ex)
	{
		super(ex);
	}

	/**
	 * Constructor for SynchronizationException.
	 * @param text
	 * @param ex
	 */
	public SynchronizationException(String text, Throwable ex)
	{
		super(text, ex);
	}

}
