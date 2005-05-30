/*
 * Created on May 29, 2005
 */
package net.sourceforge.fullsync.ui;

import net.sourceforge.fullsync.rules.filefilter.FileFilter;

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
public class FileFilterPage implements WizardPage {
    private WizardDialog dialog;
    
    private FileFilterDetails details;
    private FileFilter filter;
    
    public FileFilterPage(WizardDialog dialog)
    {
        dialog.setPage(this);
        this.dialog = dialog;
    }
    
    public String getTitle()
    {
        return "File Filter";
    }
    public String getCaption()
    {
        return "Edit the file filter";
    }
    public String getDescription()
    {
        return "";
    }
    public Image getIcon()
    {
        return GuiController.getInstance().getImage( "FileFilter_Default.png" ); //$NON-NLS-1$
    }
    public Image getImage()
    {
        return GuiController.getInstance().getImage( "FileFilter_Wizard.png" );  //$NON-NLS-1$
    }
    public void createContent( Composite content )
    {
        details = new FileFilterDetails( content, SWT.NULL );
    }
    public void createBottom( Composite bottom )
    {
        bottom.setLayout( new GridLayout( 2, false ) );
        
        Button okButton = new Button( bottom, SWT.PUSH );
        okButton.setText("OK");
        okButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected( SelectionEvent e )
            {
            	filter = details.getFileFilter();
                dialog.dispose();
            }
        });
        okButton.setLayoutData( new GridData( GridData.END, GridData.CENTER, true, true ) );
        
        Button cancelButton = new Button( bottom, SWT.PUSH );
        cancelButton.setText("Cancel");
        cancelButton.addSelectionListener( new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e)
            {
                dialog.dispose();
                filter = null;
            }
        } );
        cancelButton.setLayoutData( new GridData( GridData.END, GridData.CENTER, false, true ) );
        
        bottom.getShell().setDefaultButton(okButton);
    }

    public FileFilter getFileFilter() {
    	return filter;
    }
    
}
