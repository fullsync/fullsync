package net.full.fullsync.sync.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.full.fullsync.sync.Element;
import net.full.fullsync.sync.ElementProcessedListener;
import net.full.fullsync.sync.PhaseExecutionException;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class BasePhaseLogic implements PhaseLogic
{
    private List elementProcessedListeners;
    
    public BasePhaseLogic()
    {
        this.elementProcessedListeners = new ArrayList();
    }
    
    public boolean initialize()
    {
        return true;
    }
    public boolean prepareStart()
    {
        for( Iterator i = elementProcessedListeners.iterator(); i.hasNext(); )
            ((ElementProcessedListener)i.next()).elementProcessingStarted();
        return true;
    }
    public boolean process( Element element ) 
        throws PhaseExecutionException
    {
        return true;
    }
    public boolean pause()
    {
        return true;
    }
    public boolean resume()
    {
        return true;
    }
    public boolean finish()
    {
        for( Iterator i = elementProcessedListeners.iterator(); i.hasNext(); )
            ((ElementProcessedListener)i.next()).elementProcessingFinished();
        return true;
    }
    public boolean cancel()
    {
        for( Iterator i = elementProcessedListeners.iterator(); i.hasNext(); )
            ((ElementProcessedListener)i.next()).elementProcessingFinished();
        return true;
    }
    
    
    public void addElementProcessedListener( ElementProcessedListener listener )
    {
        elementProcessedListeners.add( listener );
    }
    public void removeElementProcessedListener( ElementProcessedListener listener )
    {
        elementProcessedListeners.remove( listener );
    }
    protected void emit( Element element )
    {
        for( Iterator i = elementProcessedListeners.iterator(); i.hasNext(); )
            ((ElementProcessedListener)i.next()).elementProcessed( element );
    }
}
