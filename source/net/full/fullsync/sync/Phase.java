package net.full.fullsync.sync;

import net.full.util.TimeSpan;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface Phase extends ElementProcessedListener
{
    public String getName();
    
    public PhaseState getState();
    public void start() throws IllegalStateException;
    public void cancel() throws IllegalStateException;
    public void pause() throws IllegalStateException;
    public void resume() throws IllegalStateException;
    
    public void addStateChangeListener( PhaseStateChangeListener listener );
    public void removeStateChangeListener( PhaseStateChangeListener listener );
    public void addElementProcessedListener( ElementProcessedListener listener );
    public void removeElementProcessedListener( ElementProcessedListener listener );
    
    public void attach( Phase phase );
    public void detach( Phase phase );
    
    public int getEstimatedElementCount();
    public int getProcessedElementCount(); // emitted?
    public TimeSpan getEstimatedRunningTime(); 
    public TimeSpan getElapsedRunningTime();
}
