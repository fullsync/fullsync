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
import net.sourceforge.fullsync.fs.File;

/**
 * An ActionDecider for source buffered publish/update. 
 * 
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class PublishSBActionDecider implements ActionDecider
{

    public Task getTask( File src, File dst, StateDecider sd,
            BufferStateDecider bsd ) throws DataParseException
    {
        Vector actions = new Vector( 3 );
        State state = sd.getState( src, dst );
        
        switch( state.getType() )
        {
        case State.Orphan:
            if( state.getLocation() == Location.Source )
            {
                if( !bsd.getState( src ).equals( State.Orphan, Location.Buffer ) )
                     actions.add( new Action( Action.Add, Location.Destination, BufferUpdate.Source, "Add" ) );
                else actions.add( new Action( Action.Nothing, Location.None, BufferUpdate.None, "In Sync" ) );
            } else if( state.getLocation() == Location.Destination ) {
                if( !bsd.getState( src ).equals( State.Orphan, Location.FileSystem ) )
                     actions.add( new Action( Action.Delete, Location.Destination, BufferUpdate.Source, "Deletion", false ) );
                else {
                    // we have to update buffer
                	src.refreshBuffer();
                    Task t = getTask( src, dst, sd, bsd );
                    return t;
                }
            }
        	break;
        case State.DirHereFileThere:
            State buff = bsd.getState( src );
            if( buff.equals( State.Orphan, Location.Buffer ) ) {
                actions.add( new Action( Action.Delete, Location.getOpposite( state.getLocation() ), BufferUpdate.Source, "There was a node in buff, but its orphan, so delete" ) );
            } else if( buff.equals( State.DirHereFileThere, state.getLocation() ) ) {
                if( state.getLocation() == Location.Source )
                     actions.add( new Action( Action.Nothing, Location.None, BufferUpdate.None, "dirherefilethere, but there is a dir instead of file, so its in sync" ) ); // FIXME may be out of sync anyways sync
                else actions.add( new Action( Action.UnexpectedChangeError, Location.Destination, BufferUpdate.None, "dirherefilethere, but there is a file instead of dir, so unexpected change") );
            } else {
                actions.add( new Action( Action.DirHereFileThereError, state.getLocation(), BufferUpdate.None, "cant update, dir here file there error occured" ) );
            }
        	break;
        case State.FileChange:
            if( bsd.getState( src ).equals( State.NodeInSync, Location.Both ) )
            {
                actions.add( new Action( Action.UnexpectedChangeError, Location.Destination, BufferUpdate.Source, "remote file changed" ) );
            } else {
                actions.add( new Action( Action.UnexpectedChangeError, Location.Destination, BufferUpdate.Source, "Source changed, but changed remotely too" ) );
            }
        	actions.add( new Action( Action.Update, Location.Destination, BufferUpdate.Source, "overwrite destination changes" ) );
        	break;
        case State.NodeInSync:
            if( bsd.getState( src ).equals( State.NodeInSync, Location.Both ) ) {
                actions.add( new Action( Action.Nothing, Location.None, BufferUpdate.None, "In Sync" ) );
            } else {
                // Update buffer and check
            	src.refreshBuffer();
                Task t = getTask( src, dst, sd, bsd );
                return t;
            }
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
