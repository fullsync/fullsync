package net.sourceforge.fullsync;


import java.util.EventListener;

import net.sourceforge.fullsync.fs.File;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface TaskGenerationListener extends EventListener
{
    public void taskTreeStarted( TaskTree tree );
    public void taskGenerationStarted( File source, File destination );
    public void taskGenerationFinished( Task task );
    public void taskTreeFinished( TaskTree tree );
}
