package net.sourceforge.fullsync;

import java.io.*;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class NestedException extends Exception
{
    private Throwable ex;

    public NestedException( String text ) { super( text ); ex = null; }
    public NestedException( Throwable ex ) { super( "an exception occured: " ); this.ex = ex; }
    public NestedException( String text, Throwable ex ) { super( text ); this.ex = ex; }

    public void printStackTrace( PrintStream stream )
    {
        printStackTrace( new PrintWriter( stream ) );
    }
    public void printStackTrace( PrintWriter out )
    {
        if( ex != null ) {
	        out.println( this.toString() );
    	    out.println( "nested exception: " );
        	ex.printStackTrace( out );
        }
		super.printStackTrace( out );
		out.flush();
    }
}
