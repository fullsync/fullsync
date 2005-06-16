package net.full.fullsync.sync.files;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.full.fullsync.sync.Element;
import net.full.fullsync.sync.PhaseExecutionException;
import net.full.fullsync.sync.base.BasePhaseLogic;
import net.sourceforge.fullsync.RuleSet;
import net.sourceforge.fullsync.fs.File;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class TwoSideTraveller extends BasePhaseLogic
{
    private FileSyncInputElement input;
    private List nodesToProcess;
    private List elementProcessedListeners;

    public TwoSideTraveller()
    {
        this.nodesToProcess = new LinkedList();
    }
    public boolean process( Element element ) throws PhaseExecutionException
    {
        if( this.input == null ) {
            this.input = (FileSyncInputElement)element;
            FileSyncElement root = 
                new FileSyncElement( input, 
                        input.getSource().getRoot(), 
                        input.getDestination().getRoot() );
            nodesToProcess.add( root );
        }
        
        while( !Thread.interrupted() )
        {
            try {
                try { Thread.sleep( 200 ); } catch( InterruptedException e ) { break; };
                if( nodesToProcess.size() > 0 )
                     processNode( (FileSyncElement)nodesToProcess.remove( 0 ) );
                else return true;
            } catch( Exception ioe ) {
                throw new PhaseExecutionException( ioe );
            }
        }
        return false;
    }
    public void enqueueChildren( File source, File destination )
        throws IOException
    {
        Collection srcFiles = source.getChildren();
        Collection dstFiles = new ArrayList();

        Collection dstRealFiles = destination.getChildren();
        if( dstRealFiles != null )
            dstFiles.addAll( dstRealFiles ); 

        
        // iterate through source nodes and remove them 
        // from our copy of the destination files
        if( srcFiles != null )
        for( Iterator i = srcFiles.iterator(); i.hasNext();  )
        {
            File sfile = (File)i.next();
            File dfile = destination.getChild( sfile.getName() );
            if( dfile == null )
                 dfile = destination.createChild( sfile.getName(), sfile.isDirectory() );
            else dstFiles.remove( dfile );
            
            nodesToProcess.add( new FileSyncElement( input, sfile, dfile ) );            
        }
        
        // iterate through the remaining destination files
        for( Iterator i = dstFiles.iterator(); i.hasNext();  )
        {
            File dfile = (File)i.next();
            File sfile = source.getChild( dfile.getName() );
            if( sfile == null )
                sfile = source.createChild( dfile.getName(), dfile.isDirectory() );
            
            nodesToProcess.add( new FileSyncElement( input, sfile, dfile ) );            
        }        
    }
    
    public void processNode( FileSyncElement syncElement )
        throws IOException
    {
        RuleSet rules = input.getRulesProvider().getRuleSet( syncElement.getRelativePath() );
        RuleSet filter = rules;

        File source = syncElement.getSourceFile();
        File destination = syncElement.getDestinationFile();

        if( filter.isNodeIgnored( source/*, Location.Source*/ ) )
            source.setFiltered ( true );
        if( filter.isNodeIgnored( destination/*, Location.Destination*/ ) )
            destination.setFiltered ( true );
        
        // check filter
        // emit generation started event
        //for( int i = 0; i < taskGenerationListeners.size(); i++ )
        //    ((TaskGenerationListener)taskGenerationListeners.get(i))
        //        .taskGenerationStarted(src, dst);

        // emit generation finished
        //for( int i = 0; i < taskGenerationListeners.size(); i++ )
        //    ((TaskGenerationListener)taskGenerationListeners.get(i))
        //        .taskGenerationFinished(task);
            
        // recurse, if recusion is enabled
        
        if( rules.isUsingRecursion() )
        {
            if( source.isDirectory() && (source.isFiltered() == false || rules.isUsingRecursionOnIgnore()) )
                enqueueChildren( source, destination );
            else if( destination.isDirectory() && (destination.isFiltered() == false || rules.isUsingRecursionOnIgnore()) )
                enqueueChildren( source, destination );
        }
            
        // update element structure
        //parent.addChild(task);
            
        // Enqueue ignore action ?
        
        emit( syncElement );
    }
}
