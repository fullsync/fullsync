package net.sourceforge.fullsync.impl;

import java.io.IOException;

import net.sourceforge.fullsync.Action;
import net.sourceforge.fullsync.ActionQueue;
import net.sourceforge.fullsync.Location;
import net.sourceforge.fullsync.buffer.Buffer;
import net.sourceforge.fullsync.fs.File;

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

    public void enqueue( Action action, File source, File destination )
    {
        try {
	        System.out.println( action );
	        switch( action.getType() )
	        {
	        case Action.Add:
	        case Action.Update:
	            if( action.getLocation() == Location.Destination )
	            {
	                if( source.isDirectory() )
	                     buffer.storeEntry( new DirCreationEntryDescriptor( destination ) );
	                else buffer.storeEntry( new FileCopyEntryDescriptor( source, destination ) );
	            }
	            break;
	        case Action.Delete:
	            if( action.getLocation() == Location.Destination )
	            {
	                buffer.storeEntry( new DeleteNodeEntryDescriptor( destination ) );
	            }
	            break;
	        }
	        if( action.getBufferUpdate() > 0 )
	            buffer.storeEntry( new BufferUpdateEntryDescriptor( source, destination, action.getBufferUpdate() ) );
        } catch( IOException ioe ) {
            ioe.printStackTrace();
        }
    }
    public void flush()
    {
        try {
            buffer.flush();
        } catch( IOException e ) {
            e.printStackTrace();
        }
    }
}
