package net.sourceforge.fullsync.rules;

import net.sourceforge.fullsync.fs.File;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface Rule
{
    public boolean accepts( File node );
}
