package net.sourceforge.fullsync;

import java.io.IOException;
import java.net.URISyntaxException;

import net.sourceforge.fullsync.fs.Site;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 * 
 * TODO call me TaskGenerator ?
 */
public interface Processor
{
    // main functionallity
    public TaskTree execute( Profile profile )
        throws FileSystemException, DataParseException, URISyntaxException, IOException;
    public TaskTree execute( Site source, Site destination, RuleSet initialRules ) 
    	throws FileSystemException, DataParseException, IOException;
    
    // listeners
    public void addTaskGenerationListener( TaskGenerationListener listener );
    public void removeTaskGenerationListener( TaskGenerationListener listener );
    
    // process interaction
    public boolean isActive();
    public void suspend();
    public void resume();
    public void cancel();
}