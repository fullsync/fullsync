package net.sourceforge.fullsync.ui;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;


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
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;

//FIXME [Michele] This might be the ugliest About Box EVER!!!
public class AboutDialog extends org.eclipse.swt.widgets.Dialog {

	private Shell dialogShell;
	private StyledText textAbout;
	private Button buttonCloseAbout;

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
			e.printStackTrace();
		}
	}

	public AboutDialog(Shell parent, int style) {
		super(parent, style);
	}

	public void open() {
		try {
			Shell parent = getParent();
			dialogShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

			GridLayout dialogShellLayout = new GridLayout();
			dialogShell.setLayout(dialogShellLayout);
			dialogShellLayout.verticalSpacing = 10;
			dialogShell.setText("About FullSync");
			dialogShell.layout();
			dialogShell.pack();
			dialogShell.setSize(161, 103);
			{
				textAbout = new StyledText(dialogShell, SWT.MULTI | SWT.READ_ONLY | SWT.NO_BACKGROUND | SWT.NO_FOCUS);
				textAbout.setText("FullSync 0.8.0\n by Jan Kopcsek");
				textAbout.setOrientation(SWT.HORIZONTAL);
				GridData textAboutLData = new GridData();
				textAbout.setEnabled(false);
				textAbout.setDoubleClickEnabled(false);
				textAbout.setBackground(new Color( null, 236, 233, 216 ));
				textAboutLData.horizontalAlignment = GridData.CENTER;
				textAboutLData.grabExcessHorizontalSpace = true;
				textAbout.setLayoutData(textAboutLData);
			}
			{
				buttonCloseAbout = new Button(dialogShell, SWT.PUSH
					| SWT.CENTER);
				buttonCloseAbout.setText("OK");
				GridData buttonCloseAboutLData = new GridData();
				buttonCloseAbout.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent evt) {
						dialogShell.dispose();
					}
				});
				buttonCloseAboutLData.horizontalAlignment = GridData.CENTER;
				buttonCloseAbout.setLayoutData(buttonCloseAboutLData);
			}
			dialogShell.open();
			Display display = dialogShell.getDisplay();
			while (!dialogShell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
