/*
 * Created on Nov 19, 2004
 */
package net.sourceforge.fullsync.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Michele Aiello
 */
public class ConnectionPage implements WizardPage {
	
    private WizardDialog dialog;
    private ConnectionComposite composite;

    public ConnectionPage(WizardDialog dialog)
    {
        this.dialog = dialog;
        dialog.setPage( this );
    }
    
	public String getTitle() {
		return "Connection...";
	}

	public String getCaption() {
		return "Connect to a Remote Server";
	}

	public String getDescription() {
		return "You can connect to remote instance of FullSync.";
	}

	public Image getIcon() {
		return new Image( dialog.getDisplay(), "images/Remote_Connect.gif" );
    }

	public Image getImage() {
		return new Image( dialog.getDisplay(), "images/Remote_Wizard.png" );	}

	public void createContent(Composite content) {
		composite = new ConnectionComposite(content, SWT.NULL);
	}

	public void createBottom(Composite bottom) {
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
