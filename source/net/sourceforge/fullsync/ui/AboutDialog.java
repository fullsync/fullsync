package net.sourceforge.fullsync.ui;

import net.sourceforge.fullsync.ExceptionHandler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

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
public class AboutDialog extends org.eclipse.swt.widgets.Dialog {

	private Shell dialogShell;
	private Label labelPicture;
	private Label labelSeparator;
	private Composite compositeBottom;
	private Button buttonOk;
	private Button buttonWebsite;
	private Image imageAbout;

	/**
	* Auto-generated main method to display this 
	* org.eclipse.swt.widgets.Dialog inside a new Shell.
	*/
	public static void main(String[] args) {
		try {
			Display display = Display.getDefault();
			Shell shell = new Shell(display);
			AboutDialog inst = new AboutDialog(shell, SWT.NULL);
			inst.open();
		} catch (Exception e) {
			ExceptionHandler.reportException( e );
		}
	}

	public AboutDialog(Shell parent, int style) 
	{
		super(parent, style);
	}

	public void open() {
		try {
			Shell parent = getParent();
			dialogShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

			GridLayout dialogShellLayout = new GridLayout();
			dialogShell.setLayout(dialogShellLayout);
			dialogShellLayout.verticalSpacing = 0;
			dialogShellLayout.marginHeight = 0;
			dialogShellLayout.marginWidth = 0;
			dialogShellLayout.horizontalSpacing = 0;
			dialogShell.setText("About FullSync");
			dialogShell.setSize(308, 406);
            {
                labelPicture = new Label(dialogShell, SWT.NONE);
                GridData labelPictureLData = new GridData();
                labelPictureLData.grabExcessHorizontalSpace = true;
                labelPictureLData.grabExcessVerticalSpace = true;
                labelPictureLData.horizontalAlignment = GridData.FILL;
                labelPictureLData.verticalAlignment = GridData.FILL;
                labelPicture.setLayoutData(labelPictureLData);
                imageAbout = GuiController.getInstance().getImage( "About.png" );
        		labelPicture.setImage( imageAbout );
            }
            {
                labelSeparator = new Label(dialogShell, SWT.SEPARATOR | SWT.HORIZONTAL);
                GridData labelSeparatorLData = new GridData();
                labelSeparatorLData.horizontalAlignment = GridData.FILL;
                labelSeparatorLData.grabExcessHorizontalSpace = true;
                labelSeparator.setLayoutData(labelSeparatorLData);
            }
            {
                compositeBottom = new Composite(dialogShell, SWT.NONE);
                GridLayout compositeBottomLayout = new GridLayout();
                GridData compositeBottomLData = new GridData();
                compositeBottomLData.horizontalAlignment = GridData.FILL;
                compositeBottom.setLayoutData(compositeBottomLData);
                compositeBottomLayout.makeColumnsEqualWidth = true;
                compositeBottomLayout.numColumns = 2;
                compositeBottom.setLayout(compositeBottomLayout);
                {
                    buttonWebsite = new Button(compositeBottom, SWT.PUSH
                        | SWT.CENTER);
                    buttonWebsite.setText("Website");
                    GridData buttonWebsiteLData = new GridData();
                    buttonWebsite.addSelectionListener(new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent evt) {
                            Program.launch( "http://fullsync.sourceforge.net" );
                        }
                    });
                    buttonWebsiteLData.widthHint = 80;
                    buttonWebsiteLData.heightHint = 23;
                    buttonWebsiteLData.grabExcessHorizontalSpace = true;
                    buttonWebsite.setLayoutData(buttonWebsiteLData);
                }
                {
                    buttonOk = new Button(compositeBottom, SWT.PUSH
                        | SWT.CENTER);
                    buttonOk.setText("Ok");
                    GridData buttonOkLData = new GridData();
                    buttonOk.addSelectionListener(new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent evt) {
                            dialogShell.close();
                        }
                    });
                    buttonOkLData.horizontalAlignment = GridData.END;
                    buttonOkLData.heightHint = 23;
                    buttonOkLData.widthHint = 80;
                    buttonOkLData.grabExcessHorizontalSpace = true;
                    buttonOk.setLayoutData(buttonOkLData);
                }
            }
			dialogShell.layout();
			dialogShell.open();
			Display display = dialogShell.getDisplay();
			while (!dialogShell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
			imageAbout.dispose();
			imageAbout = null;
		} catch (Exception e) {
			ExceptionHandler.reportException( e );
		}
	}
	
}
