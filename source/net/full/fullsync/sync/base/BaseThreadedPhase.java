package net.full.fullsync.sync.base;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import net.full.fullsync.sync.Element;
import net.full.fullsync.sync.ElementProcessedListener;
import net.full.fullsync.sync.PhaseExecutionException;
import net.full.fullsync.sync.PhaseState;
import net.full.util.TimeSpan;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class BaseThreadedPhase extends AbstractPhase implements Runnable
{
    protected Thread worker;
    protected PhaseLogic logic;
    protected List inputQueue;
    protected boolean inputFinished;
    protected int processedElements; // input
    protected int emittedElements;   // output
    
    protected TimeSpan elapsedTime;
    protected long startTime;
    
    public BaseThreadedPhase( PhaseLogic logic )
    {
        this.logic = logic;
        this.inputQueue = Collections.synchronizedList( new LinkedList() );
        this.elapsedTime = new TimeSpan( 0 );
        this.startTime = 0;
        
        setState( PhaseState.Initializing );
        
        this.worker = new Thread( this );
        this.worker.start();
    }
    
    public void run()
    {
        boolean active = true;
        while( active )
        {
            try {
                if( getState() == PhaseState.Initializing ) {
                    if( logic.initialize() )
                        setState( PhaseState.Ready );
                } else if( getState() == PhaseState.StartPending ) {
                    if( logic.prepareStart() ) {
                        setState( PhaseState.Active );
                        startTimer();
                    }
                } else if( getState() == PhaseState.Active ) {
                    if( inputQueue.isEmpty() )
                    {
                        if( inputFinished )
                             setState( PhaseState.FinishPending );
                        else setState( PhaseState.Idle );
                    } else {
                        Element e = (Element)inputQueue.get( 0 );
                        if( logic.process( e ) ) {
                            inputQueue.remove( 0 );
                            processedElements++;
                        }
                    }
                } else if( getState() == PhaseState.Idle ) {
                    Thread.sleep( 60000 );
                } else if( getState() == PhaseState.Paused || getState() == PhaseState.Ready ) {
                    Thread.sleep( 60000 );
                } else if( getState() == PhaseState.Cancelled || getState() == PhaseState.Finished ) {
                    active = false;
                } else if( getState() == PhaseState.PausePending ) {
                    if( logic.pause() ) {
                        setState( PhaseState.Paused );
                        stopTimer();
                    }
                } else if( getState() == PhaseState.ResumePending ) {
                    if( logic.resume() ) {
                        setState( PhaseState.Active );
                        startTimer();
                    }
                } else if( getState() == PhaseState.FinishPending ) {
                    if( logic.finish() ) {
                        setState( PhaseState.Finished );
                        stopTimer();
                    }
                } else if( getState() == PhaseState.CancelPending ) {
                    if( logic.cancel() ) {
                        setState( PhaseState.Cancelled );
                        stopTimer();
                    }
                }
            } catch( InterruptedException ie ) {
            } catch( PhaseExecutionException ex ) {
                ex.printStackTrace();
                cancel();
            }
        }
    }
    
    protected void unidle()
    {
        if( this.getState() == PhaseState.Idle )
        {
            setState( PhaseState.Active );
            worker.interrupt();
        }
    }
    
    protected void doStart() throws IllegalStateException
    {
        setState( PhaseState.StartPending );
        worker.interrupt();
    }
    protected void doResume() throws IllegalStateException
    {
        setState( PhaseState.ResumePending );
        worker.interrupt();
    }
    protected void doPause() throws IllegalStateException
    {
        setState( PhaseState.PausePending );
        worker.interrupt();
    }
    protected void doCancel() throws IllegalStateException
    {
        setState( PhaseState.CancelPending );
        worker.interrupt();
    }
    
    public void elementProcessingStarted()
    {
        if( !inputQueue.isEmpty() )
            throw new IllegalStateException( "cannot start new input when there are elements in queue" );
        inputFinished = false;
        inputQueue.clear();
        
        processedElements = 0;
    }
    
    public void elementProcessed( Element element )
    {
        if( inputFinished )
            throw new IllegalStateException( "got new element, but input was already finished" );
        inputQueue.add( element );
        unidle();
    }
    
    public void elementProcessingFinished()
    {
        inputFinished = true;
    }
    
    protected void startTimer()
    {
        startTime = new Date().getTime();
    }
    
    protected void stopTimer()
    {
        elapsedTime = getElapsedRunningTime();
        startTime = 0;
    }
    
    public int getEstimatedElementCount()
    {
        return -1;
    }
    public int getProcessedElementCount()
    {
        return processedElements;
    }
    public TimeSpan getEstimatedRunningTime()
    {
        // TODO Auto-generated method stub
        return null;
    }
    public TimeSpan getElapsedRunningTime()
    {
        if( startTime > 0 )
             return elapsedTime.add( new Date().getTime() - startTime );
        else return elapsedTime;
    }
    
    public void addElementProcessedListener( ElementProcessedListener listener )
    {
        logic.addElementProcessedListener( listener );
    }
    public void removeElementProcessedListener( ElementProcessedListener listener )
    {
        logic.removeElementProcessedListener( listener );
    }
}
