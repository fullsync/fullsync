package net.sourceforge.fullsync.ui;

import net.sourceforge.fullsync.Preferences;

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
public class PreferencesPage implements WizardPage
{
    private WizardDialog dialog;
    private PreferencesComposite composite;
    private Preferences preferences;

    public PreferencesPage( WizardDialog dialog, Preferences preferences )
    {
        this.dialog = dialog;
        dialog.setPage( this );
        this.preferences = preferences;
    }
    public String getTitle()
    {
        return "Preferences";
    }

    public String getCaption()
    {
        return "Preferences";
    }

    public String getDescription()
    {
        return "";
    }

    public Image getIcon()
    {
        return null;
    }
    
    public Image getImage()
    {
        return null;
    }

    public void createContent( Composite content )
    {
        composite = new PreferencesComposite(content, SWT.NULL, preferences);
    }

    public void createBottom( Composite bottom )
    {
        bottom.setLayout( new GridLayout( 2, false ) );
        
        Button okButton = new Button( bottom, SWT.PUSH );
        okButton.setText( "Ok" );
        okButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected( SelectionEvent e )
            {
                composite.apply();
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
        
        bottom.getShell().setDefaultButton(okButton);
    }

}
