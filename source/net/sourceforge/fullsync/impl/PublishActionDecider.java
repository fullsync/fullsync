package net.sourceforge.fullsync.impl;

import java.io.IOException;
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
 * An ActionDecider for destination buffered Publish/Update.
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class PublishActionDecider implements ActionDecider
{

    private static final Action addDestination = new Action( Action.Add, Location.Destination, BufferUpdate.Destination, "Add" );
    private static final Action ignoreDestinationExists = new Action( Action.UnexpectedChangeError, Location.Destination, BufferUpdate.None, "will not add, destination already exists" );
    private static final Action overwriteSource = new Action( Action.Update, Location.Source, BufferUpdate.Destination, "overwrite source" );
    private static final Action overwriteDestination = new Action( Action.Update, Location.Destination, BufferUpdate.Destination, "overwrite destination" );
    private static final Action updateDestination = new Action( Action.Update, Location.Destination, BufferUpdate.Destination, "Source changed" );
    private static final Action deleteDestination = new Action( Action.Delete, Location.Destination, BufferUpdate.Destination, "Delete destination file", false );
    private static final Action unexpectedDestinationChanged = new Action( Action.UnexpectedChangeError, Location.Destination, BufferUpdate.None, "will not delete, destination has changed" );
    private static final Action unexpectedBothChanged = new Action( Action.UnexpectedChangeError, Location.Destination, BufferUpdate.None, "Source changed, but changed remotely too" );
    private static final Action inSync = new Action( Action.Nothing, Location.None, BufferUpdate.None, "In Sync" );
    private static final Action ignore = new Action( Action.Nothing, Location.None, BufferUpdate.None, "Ignore" );
    
    public TraversalType getTraversalType()
    {
        return new TraversalType();
    }
    
    /*public Action getDefaultAction( File src, File dst, StateDecider sd, BufferStateDecider bsd ) throws DataParseException 
    {
        return getPossibleActions( src, dst, sd, bsd )[0];
    }
*/
    public Task getTask( File src, File dst, StateDecider sd, BufferStateDecider bsd ) 
    	throws DataParseException, IOException
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
                    actions.add( addDestination );
                } else {
                    actions.add( ignoreDestinationExists );
                    actions.add( overwriteDestination );
                }
            } else if( state.getLocation() == Location.Destination ) {
                if( !bsd.getState( dst ).equals( State.NodeInSync, Location.Both ) )
                {
                    actions.add( unexpectedDestinationChanged );
                }
                actions.add( deleteDestination );
            }
        	break;
        case State.DirHereFileThere:
            State buff = bsd.getState( dst );
            if( buff.equals( State.Orphan, Location.Buffer ) ) {
                actions.add( new Action( Action.Add, Location.getOpposite( state.getLocation() ), BufferUpdate.Destination, "There was a node in buff, but its orphan, so add" ) );
            } else if( buff.equals( State.DirHereFileThere, state.getLocation() ) ) {
                 if( state.getLocation() == Location.Source )
                     actions.add( new Action( Action.Nothing, Location.None, BufferUpdate.None, "dirherefilethere, but there is a dir instead of file, so its in sync" ) );
                 else actions.add( new Action( Action.UnexpectedChangeError, Location.Destination, BufferUpdate.None, "dirherefilethere, but there is a file instead of dir, so unexpected change") );
                 // TODO ^ recompare here
            } else {
                actions.add( new Action( Action.DirHereFileThereError, state.getLocation(), BufferUpdate.None, "cant update, dir here file there error occured" ) );
            }
        	break;
        case State.FileChange:
            if( bsd.getState( dst ).equals( State.NodeInSync, Location.Both ) )
            {
                actions.add( updateDestination );
            } else {
                actions.add( unexpectedBothChanged );
                actions.add( overwriteDestination );
            }
        	break;
        case State.NodeInSync:
            // TODO this check is not neccessary, check rules whether to do or not 
            //if( bsd.getState( dst ).equals( State.NodeInSync, Location.Both ) || bsd.getState( dst ).equals( State.NodeInSync, Location.None ) )
            {
                actions.add( inSync );
                actions.add( overwriteDestination );
                actions.add( overwriteSource );
            } /*else {
                actions.add( new Action( Action.UnexpectedChangeError, Location.Destination, BufferUpdate.None, "no local change, but changed remotely" ) );
                actions.add( new Action( Action.Update, Location.Destination, BufferUpdate.Destination, "overwrite destination changes" ) );
            }*/
        	break;
        default:
            actions.add( new Action( Action.NotDecidableError, Location.None, BufferUpdate.None, "no rule found" ) );
        	break;
        }
        
        actions.add( ignore );
        
        Action[] as = new Action[actions.size()];
        actions.toArray(as);
        return new Task( src, dst, state, as );
    }

}
