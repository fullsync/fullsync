package net.sourceforge.fullsync.ui;

import net.sourceforge.fullsync.ProfileManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class ProfileDetailsPage implements WizardPage
{
    private WizardDialog dialog;
    private ProfileManager profileManager;
    private String profileName;
    
    private ProfileDetails details;
    
    public ProfileDetailsPage( WizardDialog dialog, ProfileManager profileManager, String profileName )
    {
        dialog.setPage( this );
        this.dialog = dialog;
        this.profileManager = profileManager;
        this.profileName = profileName;
    }
    public String getTitle()
    {
        return "Profile "+profileName;
    }
    public String getCaption()
    {
        return "Profile Details";
    }
    public String getDescription()
    {
        return "";
    }
    public Image getIcon()
    {
        return new Image( dialog.getDisplay(), "images/Profile_Default.gif" );
    }
    public Image getImage()
    {
        return new Image( dialog.getDisplay(), "images/Profile_Wizard.png" ); 
    }
    public void createContent( Composite content )
    {
        details = new ProfileDetails( content, SWT.NULL );
        details.setProfileManager( profileManager );
        details.setProfileName( profileName );
    }
    public void createBottom( Composite bottom )
    {
        bottom.setLayout( new GridLayout( 2, false ) );
        
        Button okButton = new Button( bottom, SWT.PUSH );
        okButton.setText( "Ok" );
        okButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected( SelectionEvent e )
            {
                details.apply();
                dialog.dispose();
            }
        });
        okButton.setLayoutData( new GridData( GridData.END, GridData.CENTER, true, true ) );
        
        Button cancelButton = new Button( bottom, SWT.PUSH );
        cancelButton.setText( "Cancel" );
        cancelButton.addSelectionListener( new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e)
            {
                dialog.dispose();
            }
        } );
        cancelButton.setLayoutData( new GridData( GridData.END, GridData.CENTER, false, true ) );
    }
}
