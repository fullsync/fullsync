package net.full.fullsync.sync;

import net.full.util.TimeSpan;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface Process
{
    public String getName();
    
    public PhaseState getState();
    public void start() throws IllegalStateException;
    public void cancel() throws IllegalStateException;
    public void pause() throws IllegalStateException;
    public void resume() throws IllegalStateException;
    
    public void addStateChangeListener( PhaseStateChangeListener listener );
    public void removeStateChangeListener( PhaseStateChangeListener listener );
    
    public TimeSpan getEstimatedRunningTime(); 
    public TimeSpan getElapsedRunningTime();
}
