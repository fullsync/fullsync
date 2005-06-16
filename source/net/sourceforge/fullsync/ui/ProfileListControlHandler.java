package net.sourceforge.fullsync.ui;

import java.util.EventListener;

import net.sourceforge.fullsync.Profile;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface ProfileListControlHandler extends EventListener
{
    public void createNewProfile();
    public void runProfile( Profile profile, boolean interactive );
    public void editProfile( Profile profile );
    public void deleteProfile( Profile profile );
}
