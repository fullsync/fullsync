package net.sourceforge.fullsync;

import java.io.IOException;
import java.net.URISyntaxException;

import net.sourceforge.fullsync.fs.Site;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 * 
 * TODO call me TaskGenerator ?
 */
public interface TaskGenerator extends Phase
{
    // main functionallity
    public TaskTree execute( Profile profile )
        throws FileSystemException, DataParseException, URISyntaxException, IOException;
    public TaskTree execute( Site source, Site destination, ActionDecider actionDecider, RuleSet initialRules ) 
    	throws FileSystemException, DataParseException, IOException;
    
    // listeners
    public void addTaskGenerationListener( TaskGenerationListener listener );
    public void removeTaskGenerationListener( TaskGenerationListener listener );
}