package net.sourceforge.fullsync;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface ProfileListChangeListener
{
    public void profileChanged( Profile p );
    public void profileListChanged();
}
