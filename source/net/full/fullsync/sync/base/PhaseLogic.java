package net.full.fullsync.sync.base;

import net.full.fullsync.sync.Element;
import net.full.fullsync.sync.ElementProcessedListener;
import net.full.fullsync.sync.PhaseExecutionException;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface PhaseLogic
{
    public boolean initialize() throws PhaseExecutionException;
    public boolean prepareStart() throws PhaseExecutionException;
    public boolean process( Element element ) throws PhaseExecutionException;
    public boolean pause() throws PhaseExecutionException;
    public boolean resume() throws PhaseExecutionException;
    public boolean finish() throws PhaseExecutionException;
    public boolean cancel() throws PhaseExecutionException;
    
    public void addElementProcessedListener( ElementProcessedListener listener );
    public void removeElementProcessedListener( ElementProcessedListener listener );
}
