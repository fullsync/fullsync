package net.sourceforge.fullsync;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface Phase
{
    // process interaction
    public boolean isActive();
    public void suspend();
    public void resume();
    public void cancel();
}
