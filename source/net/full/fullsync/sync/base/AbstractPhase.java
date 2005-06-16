package net.full.fullsync.sync.base;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.full.fullsync.sync.Element;
import net.full.fullsync.sync.ElementProcessedListener;
import net.full.fullsync.sync.Phase;
import net.full.fullsync.sync.PhaseState;
import net.full.fullsync.sync.PhaseStateChangeListener;
import net.full.util.TimeSpan;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public abstract class AbstractPhase implements Phase
{
    private String name;
    private PhaseState state;
    private List stateChangeListeners;


    public AbstractPhase()
    {
        this.stateChangeListeners = new ArrayList();
    }
    
    public String getName()
    {
        return name;
    }
    public void setName( String name )
    {
        this.name = name;
    }
    
    public PhaseState getState()
    {
        return state;
    }
    protected synchronized void setState( PhaseState newState )
    {
        PhaseState oldState = state;
        state = newState;
        
        Iterator i = stateChangeListeners.iterator();
        while( i.hasNext() )
        {
            ((PhaseStateChangeListener)i.next()).phaseStateChanged( this, oldState, newState );
        }
    }
    public void addStateChangeListener( PhaseStateChangeListener listener )
    {
        stateChangeListeners.add( listener );
    }
    public void removeStateChangeListener( PhaseStateChangeListener listener )
    {
        stateChangeListeners.remove( listener );
    }
    
    public synchronized void start() throws IllegalStateException
    {
        if( state != PhaseState.Ready )
            throw new IllegalStateException( "Phase can not be started as it is not ready." );
        doStart();
    }
    public synchronized void pause() throws IllegalStateException
    {
        if( state != PhaseState.Active && state != PhaseState.Idle )
            throw new IllegalStateException( "Phase can not be paused as it is not active." );
        doPause();
    }
    public synchronized void resume() throws IllegalStateException
    {
        if( state != PhaseState.Paused )
            throw new IllegalStateException( "Phase can not be resumed as it is not paused." );
        doResume();
    }
    public synchronized void cancel() throws IllegalStateException
    {
        //if( state != PhaseState.Ready && state != PhaseState.Paused && state != PhaseState.Active )
        //    throw new IllegalStateException( "Phase can not be cancelled as it is not ready, active or paused." );
        doCancel();
    }
    
    protected abstract void doStart() throws IllegalStateException;
    protected abstract void doPause() throws IllegalStateException;
    protected abstract void doResume() throws IllegalStateException;
    protected abstract void doCancel() throws IllegalStateException;
    
    public void attach( Phase phase )
    {
        addElementProcessedListener( phase );        
    }

    public void detach( Phase phase )
    {
        removeElementProcessedListener( phase );        
    }
}
