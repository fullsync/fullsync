package net.sourceforge.fullsync.rules;

import net.sourceforge.fullsync.fs.Node;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface Rule
{
    public boolean accepts( Node node );
}
