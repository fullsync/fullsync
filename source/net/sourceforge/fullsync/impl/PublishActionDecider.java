package net.sourceforge.fullsync.impl;

import java.util.Vector;

import net.sourceforge.fullsync.Action;
import net.sourceforge.fullsync.ActionDecider;
import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.Location;
import net.sourceforge.fullsync.State;
import net.sourceforge.fullsync.fs.Node;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class PublishActionDecider implements ActionDecider
{
    public Action getDefaultAction( State state, Node src, Node dst, BufferStateDecider bsd ) throws DataParseException 
    {
        return getPossibleActions( state, src, dst, bsd )[0];
    }

    public Action[] getPossibleActions( State state, Node src, Node dst, BufferStateDecider bsd ) throws DataParseException
    {
        Vector actions = new Vector( 3 );
        
        switch( state.getType() )
        {
        case State.Orphan:
            if( state.getLocation() == Location.Source )
            {
                if( !bsd.getState( dst ).equals( State.Orphan, Location.FileSystem ) )
                     actions.add( new Action( Action.Add, Location.Destination, "Add" ) );
                else actions.add( new Action( Action.UnexpectedChangeError, Location.Destination, "will not add, destination already exists" ) );
            } else if( state.getLocation() == Location.Destination ) {
                if( bsd.getState( dst ).equals( State.NodeInSync, Location.Both ) )
                    actions.add( new Action( Action.Delete, Location.Destination, "Deletion", false ) );
                else actions.add( new Action( Action.UnexpectedChangeError, Location.Destination, "will not delete, destination has changed" ) );
            }
        	break;
        case State.DirHereFileThere:
            State buff = bsd.getState( dst );
            if( buff.equals( State.Orphan, Location.Buffer ) ) {
                actions.add( new Action( Action.Add, Location.getOpposite( state.getLocation() ), "There was a node in buff, but its orphan, so add" ) );
            } else if( buff.equals( State.DirHereFileThere, state.getLocation() ) ) {
                 if( state.getLocation() == Location.Source )
                     actions.add( new Action( Action.Nothing, Location.None, "dirherefilethere, but there is a dir instead of file, so its in sync" ) );
                 else actions.add( new Action( Action.UnexpectedChangeError, Location.Destination, "dirherefilethere, but there is a file instead of dir, so unexpected change") );
                 // TODO ^ recompare here
            } else {
                actions.add( new Action( Action.DirHereFileThereError, state.getLocation(), "cant update, dir here file there error occured" ) );
            }
        	break;
        case State.FileChange:
            if( bsd.getState( dst ).equals( State.NodeInSync, Location.Both ) )
            {
                actions.add( new Action( Action.Update, Location.Destination, "Source changed" ) );
            } else {
                actions.add( new Action( Action.UnexpectedChangeError, Location.Destination, "Source changed, but changed remotely too" ) );
                actions.add( new Action( Action.Update, Location.Destination, "overwrite destination changes" ) );
            }
        	break;
        case State.NodeInSync:
            // TODO this check is not neccessary, check rules whether to do or not 
            if( bsd.getState( dst ).equals( State.NodeInSync, Location.Both ) )
                 actions.add( new Action( Action.Nothing, Location.None, "In Sync" ) );
            else actions.add( new Action( Action.UnexpectedChangeError, Location.Destination, "no local change, but changed remotely" ) );
        	break;
        default:
            actions.add( new Action( Action.NotDecidableError, Location.None, "no rule found" ) );
        	break;
        }
        
        actions.add( new Action( Action.Nothing, Location.None, "Ignore" ) );
        
        Action[] as = new Action[actions.size()];
        actions.toArray(as);
        return as;
    }

}
