package net.sourceforge.fullsync;

import java.io.IOException;
import java.rmi.RemoteException;

import net.sourceforge.fullsync.buffer.BlockBuffer;
import net.sourceforge.fullsync.impl.FillBufferTaskExecutor;
import net.sourceforge.fullsync.impl.TaskGeneratorImpl;
import net.sourceforge.fullsync.remote.RemoteManager;

import org.apache.log4j.Logger;


/**
 * This class should provide wrappers for most common synchronization tasks
 * like synchronizing a profile or perfoming a task tree.
 * 
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class Synchronizer
{
    private TaskGenerator taskGenerator;
    private RemoteManager remoteManager;

    public Synchronizer()
    {
        taskGenerator = new TaskGeneratorImpl();
    }
    // TODO we should hide the taskgenerator we use
    public TaskGenerator getTaskGenerator()
    {
        return taskGenerator;
    }
    public synchronized TaskTree executeProfile( Profile profile )
    {
    	if (remoteManager != null) {
    		try {
    			return remoteManager.executeProfile(profile.getName());
    		} catch( Exception e ) {
    			ExceptionHandler.reportException( e );
    		}    		
    	}
    	else {
    		try {
    			return taskGenerator.execute( profile );
    		} catch( Exception e ) {
    			ExceptionHandler.reportException( e );
    		}
    	}
    	return null;
    }
    public synchronized TaskTree executeProfile( Profile profile, TaskGenerationListener taskGenerationListener )
    {
        taskGenerator.addTaskGenerationListener( taskGenerationListener );
        TaskTree tree = executeProfile( profile );
        taskGenerator.removeTaskGenerationListener( taskGenerationListener );
        
        return tree;
    }
    
    /**
     * @return Returns the ErrorLevel
     */
    public int performActions( TaskTree taskTree )
    {
        return performActions( taskTree, null );
    }
    /**
     * TODO if we add some listener/feedback receiver here we could
     * easily use this for visual action performing as well.
     *   -- done ?
     * 
     * now we still need the action count info before everything is performed
     * and later we'll need to cancel/stop the whole process
     * looks like we really need to single thread the whole class ! 
	 * @return Returns the ErrorLevel
     */
    public int performActions( TaskTree taskTree, TaskFinishedListener listener )
    {
        Logger logger = Logger.getLogger( "FullSync" );
    	if (remoteManager != null) {
    		logger.info("Remote Synchronization started");
    		try {
    			remoteManager.performActions(taskTree, listener);
    	        logger.info( "synchronization successful" ); // TODO ...with x errors and y warnings
    	        logger.info( "------------------------------------------------------------" );
    			return 0;
    		} catch (RemoteException e) {
    			ExceptionHandler.reportException(e);
                logger.error( "An Exception occured while performing actions", e);
    	        logger.info( "synchronization failed" );
    	        logger.info( "------------------------------------------------------------" );
    			return 1;
    		}
    	}
    	else {
            try {
    	        logger.info( "Synchronization started" );
    	        logger.info( "  source:      "+taskTree.getSource().getUri().toString() );
    	        logger.info( "  destination: "+taskTree.getDestination().getUri().toString() );
    	        
    	        BlockBuffer buffer = new BlockBuffer( logger );
    	        TaskExecutor queue = new FillBufferTaskExecutor(buffer);
    	        
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
    	        return 0;
            } catch( IOException ioe ) {
                logger.error( "An Exception occured while performing actions", ioe );
    	        logger.info( "synchronization failed" );
    	        logger.info( "------------------------------------------------------------" );
    	        return 1;
            }    		
    	}
    }
    
	public void setRemoteConnection(RemoteManager remoteManager) {
		this.remoteManager = remoteManager;
	}

    public void disconnectRemote() 
    {
    	remoteManager = null;
    }

    public IoStatistics getIoStatistics(TaskTree taskTree) {
        // HACK omg, that's not the way io stats are intended to be generated / used
    	Logger logger = Logger.getLogger("FullSync");
    	BlockBuffer buffer = new BlockBuffer(logger);
    	TaskExecutor queue = new FillBufferTaskExecutor(buffer);
		return queue.createStatistics(taskTree);
	}
}
