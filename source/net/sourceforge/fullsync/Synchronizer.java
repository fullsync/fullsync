package net.sourceforge.fullsync;

import java.io.IOException;

import net.sourceforge.fullsync.buffer.BlockBuffer;
import net.sourceforge.fullsync.impl.FillBufferActionQueue;
import net.sourceforge.fullsync.impl.ProcessorImpl;

import org.apache.log4j.Logger;


/**
 * This class should provide wrappers for most common synchronization tasks
 * like synchronizing a profile or perfoming a task tree.
 * 
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class Synchronizer
{
    private Processor processor;
    
    public Synchronizer()
    {
        processor = new ProcessorImpl();
    }
    // TODO we should hide the processor we use
    public Processor getProcessor()
    {
        return processor;
    }
    public TaskTree executeProfile( Profile profile )
    {
        try {
            return processor.execute( profile );
        } catch( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void performActions( TaskTree taskTree )
    {
        performActions( taskTree, null );
    }
    /**
     * TODO if we add some listener/feedback receiver here we could
     * easily use this for visual action performing as well.
     *   -- done ?
     * 
     * now we still need the action count info before everything is performed
     * and later we'll need to cancel/stop the whole process
     * looks like we really need to single thread the whole class ! 
     */
    public void performActions( TaskTree taskTree, TaskFinishedListener listener )
    {
        Logger logger = Logger.getLogger( "FullSync" );
        try {
	        logger.info( "Synchronization started" );
	        logger.info( "  source:      "+taskTree.getSource().getUri().toString() );
	        logger.info( "  destination: "+taskTree.getDestination().getUri().toString() );
	        
	        BlockBuffer buffer = new BlockBuffer( logger );
	        ActionQueue queue = new FillBufferActionQueue(buffer);
	        
	        if( listener != null )
	            queue.addTaskFinishedListener( listener );
	        
	        buffer.load();
	        queue.enqueue( taskTree );
	        queue.flush();
	        buffer.unload();
	        
	        taskTree.getSource().flush();
	        taskTree.getDestination().flush();
	        taskTree.getSource().close();
	        taskTree.getDestination().close();
	        logger.info( "synchronization successful" ); // TODO ...with x errors and y warnings
	        logger.info( "------------------------------------------------------------" );
        } catch( IOException ioe ) {
            logger.error( "An Exception occured while performing actions", ioe );
	        logger.info( "synchronization failed" );
	        logger.info( "------------------------------------------------------------" );
        }
    }
}
