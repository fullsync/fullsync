package net.full.fullsync.sync.files;

import java.util.Arrays;

import net.full.fullsync.sync.Element;
import net.full.fullsync.sync.PhaseExecutionException;
import net.full.fullsync.sync.base.BasePhaseLogic;
import net.sourceforge.fullsync.ActionDecider;
import net.sourceforge.fullsync.RuleSet;
import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.impl.BufferStateDecider;
import net.sourceforge.fullsync.impl.StateDecider;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class ActionGenerator extends BasePhaseLogic
{
    private ActionDecider ad;
    
    public ActionGenerator( ActionDecider ad )
    {
        this.ad = ad;
    }
    
    public boolean process( Element element ) throws PhaseExecutionException
    {
        try {
            try { Thread.sleep( 200 ); } catch( InterruptedException e ) { return false; };
            FileSyncElement e = (FileSyncElement)element;
            RuleSet rules = e.getInput().getRulesProvider().getRuleSet(e.getRelativePath());
            Task t = ad.getTask( e.getSourceFile(), e.getDestinationFile(), new StateDecider( rules ), new BufferStateDecider( rules ) );
            e.setActions( Arrays.asList( t.getActions() ) );
            e.setCurrentAction( t.getCurrentAction() );
            emit( e );
            return true;
        } catch( Exception ioe ) {
            throw new PhaseExecutionException( ioe );
        }
    }
}
