package net.sourceforge.fullsync;

import java.util.EventListener;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface ProfileChangeListener extends EventListener
{
    public void profileChanged( Profile profile );
}
