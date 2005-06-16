package net.full.fullsync.sync.files;

import net.full.fullsync.sync.Element;
import net.full.fullsync.sync.PhaseExecutionException;
import net.full.fullsync.sync.base.BasePhaseLogic;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class DebugActionExecutor extends BasePhaseLogic
{
    public boolean process( Element element ) throws PhaseExecutionException
    {
        try { Thread.sleep( 150 ); } catch( InterruptedException e ) { return false; };
        FileSyncElement e = (FileSyncElement)element;
        System.out.println( e.getRelativePath() + ": " + e.getCurrentAction() );
        return true;
    }
}
