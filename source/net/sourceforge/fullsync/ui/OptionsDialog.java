package net.sourceforge.fullsync.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
* This code was generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a
* for-profit company or business) then you should purchase
* a license - please visit www.cloudgarden.com for details.
*/
public class OptionsDialog extends org.eclipse.swt.widgets.Dialog implements SelectionListener
{
	private Composite compositeButtons;
	private Label labelImage;
	private Label labelMessage;
	private Shell dialogShell;
	
	private String message;
	private String[] options;
	private String result;

	public OptionsDialog(Shell parent, int style) 
	{
		super(parent,style);
	}

	/**
	* Opens the Dialog Shell.
	* Auto-generated code - any changes you make will disappear.
	*/
	public String open()
	{
		try {
		    result = null;
		    
			Shell parent = getParent();
			dialogShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
			dialogShell.setText(getText());
			labelImage = new Label(dialogShell,SWT.NULL);
			labelMessage = new Label(dialogShell,SWT.NULL);
			compositeButtons = new Composite(dialogShell,SWT.NULL);
	
			dialogShell.setSize(new org.eclipse.swt.graphics.Point(418,139));
	
			Image i = parent.getDisplay().getSystemImage( getStyle() );
			labelImage.setImage( i );
	
			GridData labelMessageLData = new GridData();
			labelMessageLData.verticalAlignment = GridData.CENTER;
			labelMessageLData.horizontalAlignment = GridData.FILL;
			labelMessageLData.widthHint = -1;
			labelMessageLData.heightHint = -1;
			labelMessageLData.horizontalIndent = 0;
			labelMessageLData.horizontalSpan = 1;
			labelMessageLData.verticalSpan = 1;
			labelMessageLData.grabExcessHorizontalSpace = true;
			labelMessageLData.grabExcessVerticalSpace = false;
			labelMessage.setLayoutData(labelMessageLData);
			labelMessage.setText(getMessage());
	
			GridData compositeButtonsLData = new GridData();
			compositeButtonsLData.verticalAlignment = GridData.CENTER;
			compositeButtonsLData.horizontalAlignment = GridData.CENTER;
			compositeButtonsLData.widthHint = -1;
			compositeButtonsLData.heightHint = -1;
			compositeButtonsLData.horizontalIndent = 0;
			compositeButtonsLData.horizontalSpan = 2;
			compositeButtonsLData.verticalSpan = 1;
			compositeButtonsLData.grabExcessHorizontalSpace = true;
			compositeButtonsLData.grabExcessVerticalSpace = false;
			compositeButtons.setLayoutData(compositeButtonsLData);
			RowLayout compositeButtonsLayout = new RowLayout(256);
			compositeButtons.setLayout(compositeButtonsLayout);
			compositeButtonsLayout.type = SWT.HORIZONTAL;
			compositeButtonsLayout.marginWidth = 0;
			compositeButtonsLayout.marginHeight = 0;
			compositeButtonsLayout.spacing = 3;
			compositeButtonsLayout.wrap = true;
			compositeButtonsLayout.pack = true;
			compositeButtonsLayout.fill = false;
			compositeButtonsLayout.justify = false;
			compositeButtonsLayout.marginLeft = 3;
			compositeButtonsLayout.marginTop = 3;
			compositeButtonsLayout.marginRight = 3;
			compositeButtonsLayout.marginBottom = 3;
			for( int c = 0; c < options.length; c++ )
		    {
		        Button b = new Button( compositeButtons, SWT.PUSH );
		        b.setText( options[c] );
		        b.addSelectionListener( this );
		    }
			compositeButtons.layout();
			GridLayout dialogShellLayout = new GridLayout(2, true);
			dialogShell.setLayout(dialogShellLayout);
			dialogShellLayout.marginWidth = 14;
			dialogShellLayout.marginHeight = 14;
			dialogShellLayout.numColumns = 2;
			dialogShellLayout.makeColumnsEqualWidth = false;
			dialogShellLayout.horizontalSpacing = 14;
			dialogShellLayout.verticalSpacing = 14;
			dialogShell.layout();
			dialogShell.pack();
			dialogShell.open();
			Display display = dialogShell.getDisplay();
			while (!dialogShell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public void setMessage( String message )
	{
	    this.message = message;
	}
	public String getMessage()
	{
	    return message;
	}
	public void setOptions( String[] newOptions )
	{
	    this.options = newOptions;
	}
	public String[] getOptions()
	{
	    return options;
	}
	

    public void widgetDefaultSelected( SelectionEvent arg0 )
    {
        result = options[0];
        dialogShell.close();
        dialogShell.dispose();
    }
    public void widgetSelected( SelectionEvent arg0 )
    {
        result = ((Button)arg0.widget).getText();
        dialogShell.close();
        dialogShell.dispose();
    }
}
