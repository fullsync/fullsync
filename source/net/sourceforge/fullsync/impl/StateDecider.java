package net.sourceforge.fullsync.impl;

import net.sourceforge.fullsync.DataParseException;
import net.sourceforge.fullsync.FileComparer;
import net.sourceforge.fullsync.Location;
import net.sourceforge.fullsync.State;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.Node;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class StateDecider
{
    private FileComparer comparer;
    
    public StateDecider( FileComparer comparer )
    {
        this.comparer = comparer;
    }
    
    public State getState( Node source, Node destination )
    	throws DataParseException
    {
        if( !source.exists() )
             if( !destination.exists() )
                  return new State( State.NodeInSync, Location.None );
             else return new State( State.Orphan, Location.Destination );
        else if( !destination.exists() )
             return new State( State.Orphan, Location.Source );
        
        if( source.isDirectory() )
             if( destination.isDirectory() )
                  return new State( State.NodeInSync, Location.Both );
             else return new State( State.DirHereFileThere, Location.Source );
        else if( destination.isDirectory() )
             return new State( State.DirHereFileThere, Location.Destination );

        return comparer.compareFiles( (File)source, (File)destination );	
    }
}
