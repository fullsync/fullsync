package net.sourceforge.fullsync.impl;

import java.io.IOException;

import net.sourceforge.fullsync.Action;
import net.sourceforge.fullsync.ActionQueue;
import net.sourceforge.fullsync.Location;
import net.sourceforge.fullsync.buffer.Buffer;
import net.sourceforge.fullsync.fs.Directory;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.Node;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class FillBufferActionQueue implements ActionQueue
{
    private Buffer buffer;
    
    public FillBufferActionQueue( Buffer buffer )
    {
        this.buffer = buffer;
    }

    public void enqueue( Action action, Node source, Node destination )
    {
        System.out.println( action );
        switch( action.getType() )
        {
        case Action.Add:
        case Action.Update:
            if( action.getLocation() == Location.Destination )
            {
                try {
	                if( source.isDirectory() )
	                     buffer.storeEntry( new DirCreationEntryDescriptor( (Directory)destination ) );
	                else buffer.storeEntry( new FileCopyEntryDescriptor( (File)source, (File)destination ) );
                } catch(IOException ioe) {
                    // TODO throw unified exception here
                    ioe.printStackTrace();
                }
            }
            break;
        case Action.Delete:
            if( action.getLocation() == Location.Destination )
            {
                try {
	                buffer.storeEntry( new DeleteNodeEntryDescriptor( destination ) );
	            } catch(IOException ioe) {
                    // TODO throw unified exception here
                    ioe.printStackTrace();
                }
            }
            break;
        }
    }
    public void flush()
    {
        try {
            buffer.flush();
        } catch( IOException e ) {
            // TODO throw unified exception here
            e.printStackTrace();
        }
    }
}
