package net.full.fullsync.sync;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class PhaseExecutionException extends Exception
{
    public PhaseExecutionException()
    {
        super();
    }

    public PhaseExecutionException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public PhaseExecutionException( String message )
    {
        super( message );
    }

    public PhaseExecutionException( Throwable cause )
    {
        super( cause );
    }
}