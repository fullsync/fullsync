package net.sourceforge.fullsync;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface TaskDecider extends Phase
{
    public TaskTree modifyTaskTree( TaskTree tree );
}
