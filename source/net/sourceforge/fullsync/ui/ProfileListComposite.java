package net.sourceforge.fullsync.ui;

import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.ProfileManager;

import org.eclipse.swt.widgets.Composite;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public abstract class ProfileListComposite extends Composite
{
    public ProfileListComposite( Composite parent, int style )
    {
        super( parent, style );
    }

    public abstract Profile getSelectedProfile();
    public abstract void setProfileManager( ProfileManager manager );
    public abstract ProfileManager getProfileManager();
    
    public abstract void setHandler( ProfileListControlHandler handler );
    public abstract ProfileListControlHandler getHandler();
}
