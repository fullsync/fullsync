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
public class SystemStatusPage implements WizardPage
{
    private WizardDialog dialog;
    private SystemStatusComposite systemStatusComposite;
    
    public SystemStatusPage( WizardDialog dialog )
    {
        this.dialog = dialog;
        dialog.setPage( this );
    }
    public String getTitle()
    {
        return "System Status";
    }
    
    public String getCaption()
    {
        return "System Status";
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
    
    public void createBottom( Composite bottom )
    {
        bottom.setLayout( new GridLayout( 1, false ) );
        
        Button okButton = new Button( bottom, SWT.PUSH );
        okButton.setText( "Ok" );
        okButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected( SelectionEvent e )
            {
                dialog.dispose();
            }
        });
        okButton.setLayoutData( new GridData( GridData.END, GridData.CENTER, true, true ) );
    }
    public void createContent( Composite content )
    {
        systemStatusComposite = new SystemStatusComposite( content, SWT.NULL );
    }
}
