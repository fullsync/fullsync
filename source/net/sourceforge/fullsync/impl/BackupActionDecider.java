package net.sourceforge.fullsync.impl;

import java.util.Vector;

import net.sourceforge.fullsync.Action;
import net.sourceforge.fullsync.ActionDecider;
import net.sourceforge.fullsync.BufferStateDecider;
import net.sourceforge.fullsync.BufferUpdate;
import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.Location;
import net.sourceforge.fullsync.State;
import net.sourceforge.fullsync.StateDecider;
import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.TraversalType;
import net.sourceforge.fullsync.fs.File;

/**
 * An ActionDecider for source to destination backup.
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class BackupActionDecider implements ActionDecider
{
    // TODO param keep orphans/exact copy
    
    public TraversalType getTraversalType()
    {
        return new TraversalType();
    }
    
    /*public Action getDefaultAction( File src, File dst, StateDecider sd, BufferStateDecider bsd ) throws DataParseException 
    {
        return getPossibleActions( src, dst, sd, bsd )[0];
    }
*/
    public Task getTask( File src, File dst, StateDecider sd, BufferStateDecider bsd ) throws DataParseException
    {
        Vector actions = new Vector( 3 );
        State state = sd.getState( src, dst );
        switch( state.getType() )
        {
        case State.Orphan:
            if( state.getLocation() == Location.Source )
            {
                if( !bsd.getState( dst ).equals( State.Orphan, Location.FileSystem ) )
                {
                    actions.add( new Action( Action.Add, Location.Destination, BufferUpdate.Destination, "Add" ) );
                } else {
                    actions.add( new Action( Action.Update, Location.Destination, BufferUpdate.Destination, "overwrite destination" ) );
                }
            } else if( state.getLocation() == Location.Destination ) {
                actions.add( new Action( Action.Nothing, Location.None, BufferUpdate.None, "Ignoring orphan backup" ) );
                actions.add( new Action( Action.Delete, Location.Destination, BufferUpdate.Destination, "Delete orphan backup", false ) );
            }
        	break;
        case State.DirHereFileThere:
            State buff = bsd.getState( dst );
            if( buff.equals( State.Orphan, Location.Buffer ) ) {
                actions.add( new Action( Action.Add, Location.Destination, BufferUpdate.Destination, "There was a node in buff, but its orphan, so add" ) );
            } else if( buff.equals( State.DirHereFileThere, state.getLocation() ) ) {
                 if( state.getLocation() == Location.Source )
                      actions.add( new Action( Action.Nothing, Location.None, BufferUpdate.Destination, "dirherefilethere, but there is a dir instead of file, so its in sync" ) );
                 else actions.add( new Action( Action.DirHereFileThereError, Location.Destination, BufferUpdate.None, "file changed from/to dir, can't overwrite") );
                 // TODO ^ recompare here
            } else {
                actions.add( new Action( Action.DirHereFileThereError, state.getLocation(), BufferUpdate.None, "cant update, dir here file there error occured" ) );
            }
        	break;
        case State.FileChange:
            if( bsd.getState( dst ).equals( State.NodeInSync, Location.Both ) )
            {
                actions.add( new Action( Action.Update, Location.Destination, BufferUpdate.Destination, "Source changed" ) );
            } else {
                actions.add( new Action( Action.Update, Location.Destination, BufferUpdate.Destination, "overwrite destination changes" ) );
            }
        	break;
        case State.NodeInSync:
            // TODO this check is not neccessary, check rules whether to do or not 
            //if( bsd.getState( dst ).equals( State.NodeInSync, Location.Both ) || bsd.getState( dst ).equals( State.NodeInSync, Location.None ) )
            {
                actions.add( new Action( Action.Nothing, Location.None, BufferUpdate.None, "In Sync" ) );
                actions.add( new Action( Action.Update, Location.Destination, BufferUpdate.Destination, "overwrite destination" ) );
            } /*else {
                actions.add( new Action( Action.UnexpectedChangeError, Location.Destination, BufferUpdate.None, "no local change, but changed remotely" ) );
                actions.add( new Action( Action.Update, Location.Destination, BufferUpdate.Destination, "overwrite destination changes" ) );
            }*/
        	break;
        default:
            actions.add( new Action( Action.NotDecidableError, Location.None, BufferUpdate.None, "no rule found" ) );
        	break;
        }
        
        actions.add( new Action( Action.Nothing, Location.None, BufferUpdate.None, "Ignore" ) );
        
        Action[] as = new Action[actions.size()];
        actions.toArray(as);
        return new Task( src, dst, state, as );
    }

}
