package net.full.fullsync.sync;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class PhaseState
{
    public final static PhaseState Ready            = new PhaseState( 1, "Ready" );
    public final static PhaseState Active           = new PhaseState( 2, "Active" );
    public final static PhaseState Idle             = new PhaseState( 3, "Idle" );
    public final static PhaseState Paused           = new PhaseState( 4, "Paused" );
    public final static PhaseState Finished         = new PhaseState( 5, "Finished" );
    public final static PhaseState Cancelled        = new PhaseState( 6, "Cancelled" );
    
    public final static PhaseState Initializing     = new PhaseState( 11, "Initializing" );
    public final static PhaseState StartPending     = new PhaseState( 12, "StartPending" );
    public final static PhaseState ResumePending    = new PhaseState( 13, "ResumePending" );
    public final static PhaseState PausePending     = new PhaseState( 14, "PausePending" );
    public final static PhaseState FinishPending    = new PhaseState( 15, "FinishPending" );
    public final static PhaseState CancelPending    = new PhaseState( 16, "CancelPending" );
    
    
    private final int id;
    private final String name;
    
    protected PhaseState( int id, String name )
    {
        this.id = id;
        this.name = name;
    }
    public final int getId()
    {
        return id;
    }
    public final String getName()
    {
        return name;
    }
    public String toString()
    {
        return name;
    }
}
