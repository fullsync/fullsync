package net.sourceforge.fullsync;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public abstract class ExceptionHandler
{
    private static ExceptionHandler singleton
    	= new ExceptionHandler() {
	        protected void doReportException( String message, Throwable exception )
	        {
	            exception.printStackTrace();
	        }
	    };
    
    public static ExceptionHandler registerExceptionHandler( ExceptionHandler handler )
    {
        ExceptionHandler temp = singleton;
        singleton = handler;
        return temp;
    }
    
    public static void reportException( Throwable exception )
    {
        if( singleton != null )
            singleton.doReportException( "An exception occured:\n"+exception.getMessage(), exception );
    }
    public static void reportException( String message, Throwable exception )
    {
        if( singleton != null )
            singleton.doReportException( message, exception );
    }
    
    protected abstract void doReportException( String message, Throwable exception );
}
