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
		return Messages.getString("ConnectionPage.Connection"); //$NON-NLS-1$
	}

	public String getCaption() {
		return Messages.getString("ConnectionPage.ConnectToARemoteServer"); //$NON-NLS-1$
	}

	public String getDescription() {
		return Messages.getString("ConnectionPage.ChooseTarget"); //$NON-NLS-1$
	}

	public Image getIcon() {
		return GuiController.getInstance().getImage( "Remote_Connect.png" ); //$NON-NLS-1$
    }

	public Image getImage() {
		return GuiController.getInstance().getImage( "Remote_Wizard.png" );	} //$NON-NLS-1$

	public void createContent(Composite content) {
		composite = new ConnectionComposite(content, SWT.NULL);
	}

	public void createBottom(Composite bottom) {
        bottom.setLayout( new GridLayout( 2, false ) );
        
        Button okButton = new Button( bottom, SWT.PUSH );
        okButton.setText( Messages.getString("ConnectionPage.Ok") ); //$NON-NLS-1$
        okButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected( SelectionEvent e )
            {
                composite.apply();
                dialog.dispose();
            }
        });
        okButton.setLayoutData( new GridData( GridData.END, GridData.CENTER, true, true ) );
        
        Button cancelButton = new Button( bottom, SWT.PUSH );
        cancelButton.setText( Messages.getString("ConnectionPage.Cancel") ); //$NON-NLS-1$
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
