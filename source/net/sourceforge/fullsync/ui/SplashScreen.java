/*
 * Created on Nov 25, 2004
 */
package net.sourceforge.fullsync.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Michele Aiello
 */
public class SplashScreen extends Composite {

	private Label labelPicture;
	private String imageFileName;
	
	public static void main(String args[]) throws InterruptedException {
		Display display = Display.getDefault();
		Shell mainShell = new Shell(display, SWT.NONE);
		SplashScreen splashScreen = new SplashScreen(mainShell, "./images/About.png");
		splashScreen.show();
		Thread.sleep(3000);
		splashScreen.hide();
	}
	
	public static SplashScreen createSplashScreenSWT(String filename) {
		Display display = Display.getDefault();
    	Shell mainShell = new Shell(display, SWT.NONE);

    	return new SplashScreen(mainShell, filename);
	}
	
	private SplashScreen(Composite parent, String filename) {
		super(parent, SWT.NONE);
		this.imageFileName = filename;
		initGUI();
	}
	
	private void initGUI() {
		GridLayout thisLayout = new GridLayout();
		thisLayout.numColumns = 1;
		thisLayout.horizontalSpacing = 0;
		thisLayout.verticalSpacing = 0;
		thisLayout.marginHeight = 0;
		thisLayout.marginWidth = 0;
		
		this.setLayout(thisLayout);
		
		Image image = null;
		
        {
            labelPicture = new Label(this, SWT.NONE);
            GridData labelPictureLData = new GridData();
            labelPictureLData.grabExcessHorizontalSpace = true;
            labelPictureLData.grabExcessVerticalSpace = true;
            labelPictureLData.horizontalAlignment = GridData.FILL;
            labelPictureLData.verticalAlignment = GridData.FILL;
            labelPicture.setLayoutData(labelPictureLData);
            image = new Image(this.getDisplay(), imageFileName);
    		labelPicture.setImage( image );
            labelPicture.setBounds(0, 0, image.getBounds().width, image.getBounds().height);
        }
        
        this.setSize(image.getBounds().width, image.getBounds().height);
		this.pack();
        this.layout();
	}
	
	public void setHideOnClick() {
        final Shell mainShell = this.getShell();
        MouseAdapter adapter = new MouseAdapter() {
			public void mouseDown(MouseEvent e) {
				mainShell.dispose();
			}
        };
        
        this.addMouseListener(adapter);
        labelPicture.addMouseListener(adapter);
	}
	
	public void show() {
		final Shell mainShell = this.getShell();
		final Display display = mainShell.getDisplay();
		final Monitor monitor = mainShell.getMonitor();
		
		mainShell.pack();
		mainShell.layout();
		
		int screenWidth = monitor.getBounds().width;
		int screenHeight = monitor.getBounds().height;
		
		int shellWidth = mainShell.getBounds().width;
		int shellHeight = mainShell.getBounds().height;
		
		int posX = (screenWidth - shellWidth) / 2;
		int posY = (screenHeight- shellHeight) / 2;
		
		mainShell.setBounds(posX, posY, shellWidth, shellHeight);
		
		mainShell.setVisible(true);
		
		display.asyncExec(new Runnable() {
			public void run() {
				while( !mainShell.isDisposed() ) {
					if (!display.readAndDispatch())
						display.sleep();
				}
			}
		});
	}
	
	public void hide() {
		if (!this.isDisposed()) {
			Shell mainShell = this.getShell();
			if (!mainShell.isDisposed()) {
				mainShell.dispose();
			}
		}
	}
	
	public void dispose()
	{
	    super.dispose();
	    labelPicture.getImage().dispose();
	}
}
