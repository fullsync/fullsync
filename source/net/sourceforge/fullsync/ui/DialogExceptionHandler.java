package net.sourceforge.fullsync.ui;

import net.sourceforge.fullsync.ExceptionHandler;

import org.eclipse.swt.widgets.Shell;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class DialogExceptionHandler extends ExceptionHandler
{
    private Shell parent;

    public DialogExceptionHandler( Shell parent )
    {
        this.parent = parent;
    }
    
    protected void doReportException( String message, Throwable exception )
    {
        exception.printStackTrace();
        
        ExceptionDialog ed = new ExceptionDialog( parent, message, exception );
        ed.open();
    }
}
