package net.sourceforge.fullsync.impl;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import net.sourceforge.fullsync.Action;
import net.sourceforge.fullsync.ActionFinishedListener;
import net.sourceforge.fullsync.ActionQueue;
import net.sourceforge.fullsync.Location;
import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.TaskTree;
import net.sourceforge.fullsync.buffer.Buffer;
import net.sourceforge.fullsync.buffer.EntryDescriptor;
import net.sourceforge.fullsync.buffer.EntryFinishedListener;
import net.sourceforge.fullsync.fs.File;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class FillBufferActionQueue implements ActionQueue, EntryFinishedListener
{
    private Buffer buffer;
    private Vector listeners;
    
    public FillBufferActionQueue( Buffer buffer )
    {
        this.buffer = buffer;
        this.listeners = new Vector();
        buffer.addEntryFinishedListener( this );
    }

    public void enqueue( TaskTree tree )
    {
    	enqueue( tree.getRoot() );
    }
    protected void enqueueTaskChildren( Task t )
	{
	    for( Enumeration e = t.getChildren(); e.hasMoreElements(); )
	        enqueue( (Task)e.nextElement() );
	}
    public void enqueue( Task task )
    {
        if( !task.getCurrentAction().isBeforeRecursion() )
        	enqueueTaskChildren( task );

        enqueue( task.getCurrentAction(), task.getSource(), task.getDestination() );
    	
        if(  task.getCurrentAction().isBeforeRecursion() )
        	enqueueTaskChildren( task );
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
	        default:
	        	entryFinished();
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
    public void entryFinished()
    {
    	Enumeration e = listeners.elements();
    	while( e.hasMoreElements() )
    	{
    		((ActionFinishedListener)e.nextElement()).actionFinished();
    	}
    }
    public void entryFinished(EntryDescriptor entry) 
    {
    	if( entry instanceof BufferUpdateEntryDescriptor )
    		return;
    	Enumeration e = listeners.elements();
    	while( e.hasMoreElements() )
    	{
    		((ActionFinishedListener)e.nextElement()).actionFinished();
    	}
	}
    
    public void addActionFinishedListener( ActionFinishedListener listener )
    {
    	listeners.add( listener );
    }
    public void removeActionFinishedListener( ActionFinishedListener listener )
    {
    	listeners.remove( listener );
    }
}
