package net.sourceforge.fullsync.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
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
public class StatusLine extends org.eclipse.swt.widgets.Composite 
{
    private ProgressBar progressBar;
    private Label labelIcon;
    private Label labelMessage;

	/**
	* Auto-generated main method to display this 
	* org.eclipse.swt.widgets.Composite inside a new Shell.
	*/
	public static void main(String[] args) {
		showGUI();
	}
		
	/**
	* Auto-generated method to display this 
	* org.eclipse.swt.widgets.Composite inside a new Shell.
	*/
	public static void showGUI() {
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		StatusLine inst = new StatusLine(shell, SWT.NULL);
		Point size = inst.getSize();
		shell.setLayout(new FillLayout());
		shell.layout();
		if(size.x == 0 && size.y == 0) {
			inst.pack();
			shell.pack();
		} else {
			Rectangle shellBounds = shell.computeTrim(0, 0, size.x, size.y);
			int MENU_HEIGHT = 22;
			if (shell.getMenuBar() != null)
				shellBounds.height -= MENU_HEIGHT;
			shell.setSize(shellBounds.width, shellBounds.height);
		}
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	public StatusLine(org.eclipse.swt.widgets.Composite parent, int style) 
	{
		super(parent, style);
		initGUI();
		progressBar.setVisible( false );
	}

	private void initGUI() {
		try {
			GridLayout thisLayout = new GridLayout();
			this.setSize(400, 19);
			thisLayout.marginHeight = 1;
			thisLayout.marginWidth = 2;
			thisLayout.numColumns = 3;
			this.setLayout(thisLayout);
			{
                labelIcon = new Label(this, SWT.NONE);
                GridData labelIconLData = new GridData();
                labelIconLData.widthHint = 16;
                labelIconLData.heightHint = 16;
                labelIconLData.verticalAlignment = GridData.END;
                labelIcon.setLayoutData(labelIconLData);
            }
            {
                labelMessage = new Label(this, SWT.NONE);
                GridData labelMessageLData = new GridData();
                labelMessageLData.heightHint = 16;
                labelMessageLData.grabExcessHorizontalSpace = true;
                labelMessageLData.horizontalAlignment = GridData.FILL;
                labelMessageLData.verticalAlignment = GridData.END;
                labelMessage.setLayoutData(labelMessageLData);
            }
            {
                progressBar = new ProgressBar(this, SWT.NONE);
                GridData progressBarLData = new GridData();
                progressBarLData.horizontalAlignment = GridData.END;
                progressBarLData.widthHint = 150;
                progressBarLData.heightHint = 17;
                progressBar.setLayoutData(progressBarLData);
            }
			this.layout();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setMessage( final String message )
	{
	    getDisplay().asyncExec( new Runnable() {
            public void run()
            {
                labelIcon.setImage( null );
        	    //labelIcon.setVisible( false );
        		labelMessage.setText( message );
        		layout();
            }
        } );
	}
	public void setMessage( final Image icon, final String message )
	{
	    getDisplay().asyncExec( new Runnable() {
            public void run()
            {
			    labelIcon.setImage( icon );
			    //labelIcon.setVisible( true );
			    labelMessage.setText( message );
			    layout();
            }
	    } );
	}

}
