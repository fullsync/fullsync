package net.sourceforge.fullsync.ui;

import java.io.PrintWriter;
import java.io.StringWriter;

import net.sourceforge.fullsync.ExceptionHandler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/**
* This code was generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* *************************************
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED
* for this machine, so Jigloo or this code cannot be used legally
* for any corporate or commercial purpose.
* *************************************
*/
public class ExceptionDialog extends Dialog 
{
	private Shell dialogShell;
	private Label labelImage;
	private Text textLog;
	private Button buttonOk;
	private Composite compositeExtension;
	private Composite compositeBase;
	private Button buttonDetails;
	private Label labelMesage;
	
	private boolean expanded;
	
	private String message;
	private Throwable throwable;

	/**
	* Auto-generated main method to display this 
	* org.eclipse.swt.widgets.Dialog inside a new Shell.
	*/
	public ExceptionDialog( Shell parent, String message, Throwable throwable ) 
	{
		super( parent, SWT.NULL );
		this.message = message;
		this.throwable = throwable;
	}

	public void open() {
		try {
		    Shell parent = getParent();
			Display display = parent.getDisplay();
			
			dialogShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
			dialogShell.setText( "Exception" );
			
			GridLayout dialogShellLayout = new GridLayout();
			dialogShellLayout.marginHeight = 0;
			dialogShellLayout.marginWidth = 0;
			dialogShellLayout.horizontalSpacing = 0;
			dialogShellLayout.verticalSpacing = 0;
			dialogShell.setLayout(dialogShellLayout);
			dialogShell.setSize(466, 324);
            {
                compositeBase = new Composite(dialogShell, SWT.NONE);
                GridLayout compositeBaseLayout = new GridLayout();
                GridData compositeBaseLData = new GridData();
                compositeBaseLData.heightHint = 116;
                compositeBaseLData.grabExcessHorizontalSpace = true;
                compositeBaseLData.horizontalAlignment = GridData.FILL;
                compositeBase.setLayoutData(compositeBaseLData);
    			dialogShell.setLayout(dialogShellLayout);
    			compositeBaseLayout.numColumns = 3;
    			compositeBaseLayout.marginHeight = 10;
    			compositeBaseLayout.marginWidth = 10;
                compositeBase.setLayout(compositeBaseLayout);
                {
                    labelImage = new Label(compositeBase, SWT.NONE);
                    GridData labelImageLData = new GridData();
                    labelImageLData.widthHint = 48;
                    labelImageLData.heightHint = 48;
                    labelImageLData.verticalSpan = 2;
                    labelImageLData.verticalAlignment = GridData.BEGINNING;
                    labelImage.setLayoutData(labelImageLData);
                    labelImage.setImage(display.getSystemImage(SWT.ICON_ERROR));
                }
                {
                    labelMesage = new Label(compositeBase, SWT.NONE);
                    labelMesage.setText( message );
                    GridData labelMesageLData = new GridData();
                    labelMesageLData.heightHint = 13;
                    labelMesageLData.horizontalAlignment = GridData.FILL;
                    labelMesageLData.horizontalIndent = 20;
                    labelMesageLData.verticalAlignment = GridData.BEGINNING;
                    labelMesageLData.horizontalSpan = 2;
                    labelMesage.setLayoutData(labelMesageLData);
                }
                {
                    buttonOk = new Button(compositeBase, SWT.PUSH | SWT.CENTER);
                    buttonOk.setText("Ok");
                    GridData buttonOkLData = new GridData();
                    buttonOk.addSelectionListener(new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent evt) {
                            dialogShell.dispose();
                        }
                    });
                    buttonOkLData.horizontalAlignment = GridData.END;
                    buttonOkLData.heightHint = 23;
                    buttonOkLData.verticalAlignment = GridData.END;
                    buttonOkLData.grabExcessHorizontalSpace = true;
                    buttonOkLData.widthHint = 80;
                    buttonOk.setLayoutData(buttonOkLData);
                }
                {
                    buttonDetails = new Button(compositeBase, SWT.PUSH
                        | SWT.CENTER);
                    buttonDetails.setText("Details");
                    GridData buttonDetailsLData = new GridData();
                    buttonDetails.addSelectionListener(new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent evt) {
                            toggleExpansion();
                        }
                    });
                    buttonDetailsLData.horizontalAlignment = GridData.END;
                    buttonDetailsLData.grabExcessVerticalSpace = true;
                    buttonDetailsLData.verticalAlignment = GridData.END;
                    buttonDetailsLData.widthHint = 80;
                    buttonDetailsLData.heightHint = 23;
                    buttonDetails.setLayoutData(buttonDetailsLData);
                }
            }
            {
                compositeExtension = new Composite(dialogShell, SWT.NONE);
                GridLayout compositeExtensionLayout = new GridLayout();
                GridData compositeExtensionLData = new GridData();
                compositeExtensionLData.grabExcessHorizontalSpace = true;
                compositeExtensionLData.horizontalAlignment = GridData.FILL;
                compositeExtensionLData.grabExcessVerticalSpace = true;
                compositeExtensionLData.verticalAlignment = GridData.FILL;
                compositeExtension.setLayoutData(compositeExtensionLData);
                compositeExtensionLayout.makeColumnsEqualWidth = true;
                compositeExtension.setLayout(compositeExtensionLayout);
                {
                    textLog = new Text(compositeExtension, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
                    textLog.setText("text1");
                    GridData textLogLData = new GridData();
                    textLogLData.grabExcessHorizontalSpace = true;
                    textLogLData.grabExcessVerticalSpace = true;
                    textLogLData.horizontalAlignment = GridData.FILL;
                    textLogLData.verticalAlignment = GridData.FILL;
                    textLog.setLayoutData(textLogLData);
                    
                    StringWriter writer = new StringWriter();
                    throwable.printStackTrace( new PrintWriter( writer ) );
                    textLog.setText( writer.getBuffer().toString() );
                }
            }
			dialogShell.layout();
			//dialogShell.pack();
            expanded = true;
            toggleExpansion();
            dialogShell.open();
			while (!dialogShell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		} catch (Exception e) {
			ExceptionHandler.reportException( e );
		}
	}

	private void toggleExpansion()
	{
	    if( expanded )
	    {
	        Rectangle r = dialogShell.computeTrim( 0, 0, compositeBase.getSize().x, compositeBase.getSize().y );
            dialogShell.setSize( r.width, r.height );
            buttonDetails.setText( "Details >>" );
            expanded = false;
	    } else {
	        dialogShell.setSize( dialogShell.getSize().x, dialogShell.getSize().y+200 );
            buttonDetails.setText( "Details <<" );
	        expanded = true;
	    }
	}
	
	protected void doReportException( String message, Throwable exception )
    {
        // TODO Auto-generated method stub

    }
	protected void doReportException( Throwable exception )
    {
        // TODO Auto-generated method stub

    }
}
