package net.sourceforge.fullsync.ui;

import net.sourceforge.fullsync.ExceptionHandler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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
public class ChooseDstBufferInitWindow extends org.eclipse.swt.widgets.Composite {

	private Button buttonAbort;
	private Button buttonOk;
	private Button button3;
	private Button button2;
	private Button button1;
	private Label label2;
	private Label label1;
	public ChooseDstBufferInitWindow(Composite parent, int style) {
		super(parent, style);
		initGUI();
	}

	/**
	* Initializes the GUI.
	* Auto-generated code - any changes you make will disappear.
	*/
	public void initGUI(){
		try {
			preInitGUI();
	
			label1 = new Label(this,SWT.NULL);
			label2 = new Label(this,SWT.WRAP);
			button1 = new Button(this,SWT.RADIO| SWT.LEFT);
			button2 = new Button(this,SWT.RADIO| SWT.LEFT);
			button3 = new Button(this,SWT.RADIO| SWT.LEFT);
			buttonOk = new Button(this,SWT.PUSH| SWT.CENTER);
			buttonAbort = new Button(this,SWT.PUSH| SWT.CENTER);
	
			this.setSize(new org.eclipse.swt.graphics.Point(560,157));
	
			GridData label1LData = new GridData();
			label1LData.verticalAlignment = GridData.CENTER;
			label1LData.horizontalAlignment = GridData.BEGINNING;
			label1LData.widthHint = -1;
			label1LData.heightHint = -1;
			label1LData.horizontalIndent = 0;
			label1LData.horizontalSpan = 2;
			label1LData.verticalSpan = 1;
			label1LData.grabExcessHorizontalSpace = false;
			label1LData.grabExcessVerticalSpace = false;
			label1.setLayoutData(label1LData);
			label1.setText("There was no destination buffer found.");
	
			GridData label2LData = new GridData();
			label2LData.verticalAlignment = GridData.BEGINNING;
			label2LData.horizontalAlignment = GridData.FILL;
			label2LData.widthHint = -1;
			label2LData.heightHint = 26;
			label2LData.horizontalIndent = 0;
			label2LData.horizontalSpan = 2;
			label2LData.verticalSpan = 1;
			label2LData.grabExcessHorizontalSpace = true;
			label2LData.grabExcessVerticalSpace = false;
			label2.setLayoutData(label2LData);
			label2.setText("It is most likely that you are synchronizing with a new site or the buffer file was deleted. Please choose one of the following options:");
			label2.setSize(new org.eclipse.swt.graphics.Point(544,26));
	
			GridData button1LData = new GridData();
			button1LData.verticalAlignment = GridData.CENTER;
			button1LData.horizontalAlignment = GridData.BEGINNING;
			button1LData.widthHint = -1;
			button1LData.heightHint = -1;
			button1LData.horizontalIndent = 20;
			button1LData.horizontalSpan = 2;
			button1LData.verticalSpan = 1;
			button1LData.grabExcessHorizontalSpace = false;
			button1LData.grabExcessVerticalSpace = false;
			button1.setLayoutData(button1LData);
			button1.setText("There is nothing at the destination. (don't overwrite, build buffer while copying)");
	
			GridData button2LData = new GridData();
			button2LData.verticalAlignment = GridData.CENTER;
			button2LData.horizontalAlignment = GridData.BEGINNING;
			button2LData.widthHint = -1;
			button2LData.heightHint = -1;
			button2LData.horizontalIndent = 20;
			button2LData.horizontalSpan = 2;
			button2LData.verticalSpan = 1;
			button2LData.grabExcessHorizontalSpace = false;
			button2LData.grabExcessVerticalSpace = false;
			button2.setLayoutData(button2LData);
			button2.setText("There is something at the destination but its not sure its the same as in source. (don't ask overwrite)");
	
			GridData button3LData = new GridData();
			button3LData.verticalAlignment = GridData.CENTER;
			button3LData.horizontalAlignment = GridData.BEGINNING;
			button3LData.widthHint = 357;
			button3LData.heightHint = 16;
			button3LData.horizontalIndent = 20;
			button3LData.horizontalSpan = 2;
			button3LData.verticalSpan = 1;
			button3LData.grabExcessHorizontalSpace = false;
			button3LData.grabExcessVerticalSpace = false;
			button3.setLayoutData(button3LData);
			button3.setText("There is the same version in destination. (build buffer from filesystem)");
			button3.setSize(new org.eclipse.swt.graphics.Point(357,16));
	
			GridData buttonOkLData = new GridData();
			buttonOkLData.verticalAlignment = GridData.BEGINNING;
			buttonOkLData.horizontalAlignment = GridData.END;
			buttonOkLData.widthHint = 50;
			buttonOkLData.heightHint = -1;
			buttonOkLData.horizontalIndent = 0;
			buttonOkLData.horizontalSpan = 1;
			buttonOkLData.verticalSpan = 1;
			buttonOkLData.grabExcessHorizontalSpace = true;
			buttonOkLData.grabExcessVerticalSpace = false;
			buttonOk.setLayoutData(buttonOkLData);
			buttonOk.setText("Ok");
	
			GridData buttonAbortLData = new GridData();
			buttonAbortLData.verticalAlignment = GridData.CENTER;
			buttonAbortLData.horizontalAlignment = GridData.BEGINNING;
			buttonAbortLData.widthHint = 50;
			buttonAbortLData.heightHint = -1;
			buttonAbortLData.horizontalIndent = 0;
			buttonAbortLData.horizontalSpan = 1;
			buttonAbortLData.verticalSpan = 1;
			buttonAbortLData.grabExcessHorizontalSpace = true;
			buttonAbortLData.grabExcessVerticalSpace = false;
			buttonAbort.setLayoutData(buttonAbortLData);
			buttonAbort.setText("Cancel");
			GridLayout thisLayout = new GridLayout(2, true);
			this.setLayout(thisLayout);
			thisLayout.marginWidth = 8;
			thisLayout.marginHeight = 8;
			thisLayout.numColumns = 2;
			thisLayout.makeColumnsEqualWidth = false;
			thisLayout.horizontalSpacing = 5;
			thisLayout.verticalSpacing = 5;
			this.layout();
	
			postInitGUI();
		} catch (Exception e) {
			ExceptionHandler.reportException( e );
		}
	}
	/** Add your pre-init code in here 	*/
	public void preInitGUI(){
	}

	/** Add your post-init code in here 	*/
	public void postInitGUI(){
	}

	/**
	* This static method creates a new instance of this class and shows
	* it inside a new Shell.
	*
	* It is a convenience method for showing the GUI, but it can be
	* copied and used as a basis for your own code.	*
	* It is auto-generated code - the body of this method will be
	* re-generated after any changes are made to the GUI.
	* However, if you delete this method it will not be re-created.	*/
	public static void showGUI(){
		try {
			Display display = Display.getDefault();
			Shell shell = new Shell(display);
			ChooseDstBufferInitWindow inst = new ChooseDstBufferInitWindow(shell, SWT.NULL);
			shell.setLayout(new org.eclipse.swt.layout.FillLayout());
			Rectangle shellBounds = shell.computeTrim(0,0,560,157);
			shell.setSize(shellBounds.width, shellBounds.height);
			shell.open();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		} catch (Exception e) {
			ExceptionHandler.reportException( e );
		}
	}
}
