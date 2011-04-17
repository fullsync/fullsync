package net.sourceforge.fullsync.ui;

import java.util.Timer;
import java.util.TimerTask;

import net.sourceforge.fullsync.ExceptionHandler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
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
	private Label label2;
	private Label labelThanks;
	private Composite composite1;
	private Button buttonOk;
	private Button buttonWebsite;

	private static final String[] specialThanks = {"", "", "", "Credits",
			"",
			"Localizations",
			"Deutsch: Jan Kopcsek",
			"Español: Angela Verastegui Desouza",
			"Français: Pascal Conil-lacoste",
			"Italiano: Michele Aiello",
			"",
			"Testing",
			"Antonio Denegri",
			"",
			"",
			""};

	private static final long delay = 750;

	private int stIndex = 0;
	private Timer stTimer;

	public AboutDialog(Shell parent, int style)
	{
		super(parent, style);
	}

	public void open() {
		try {
			Shell parent = getParent();
			dialogShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

			dialogShell.addDisposeListener(new DisposeListener() {
				@Override
				public void widgetDisposed(DisposeEvent arg0) {
					if (stTimer != null) {
						stTimer.cancel();
					}
				}
			});

			GridLayout dialogShellLayout = new GridLayout();
			dialogShell.setLayout(dialogShellLayout);
			dialogShellLayout.verticalSpacing = 0;
			dialogShellLayout.marginHeight = 0;
			dialogShellLayout.marginWidth = 0;
			dialogShellLayout.horizontalSpacing = 0;
			dialogShell.setText(Messages.getString("AboutDialog.About_FullSync")); //$NON-NLS-1$
			dialogShell.setSize(308, 458);
            {
                labelPicture = new Label(dialogShell, SWT.NONE);
                GridData labelPictureLData = new GridData();
                labelPictureLData.grabExcessHorizontalSpace = true;
                labelPictureLData.grabExcessVerticalSpace = true;
                labelPictureLData.horizontalAlignment = GridData.FILL;
                labelPictureLData.verticalAlignment = GridData.FILL;
                labelPicture.setLayoutData(labelPictureLData);
                labelPicture.setSize(300, 342);
        		labelPicture.setImage( GuiController.getInstance().getImage( "About.png" ) ); //$NON-NLS-1$
            }
            {
                labelSeparator = new Label(dialogShell, SWT.SEPARATOR | SWT.HORIZONTAL);
                GridData labelSeparatorLData = new GridData();
                labelSeparatorLData.horizontalAlignment = GridData.FILL;
                labelSeparatorLData.grabExcessHorizontalSpace = true;
                labelSeparator.setLayoutData(labelSeparatorLData);
            }
			{
				composite1 = new Composite(dialogShell, SWT.NONE);
				composite1.setLayout(new FillLayout());
				GridLayout composite1Layout = new GridLayout();
				composite1Layout.makeColumnsEqualWidth = true;
				GridData composite1LData = new GridData();
				composite1LData.horizontalSpan = 3;
				composite1LData.horizontalAlignment = GridData.FILL;
				composite1LData.heightHint = 50;
				composite1LData.widthHint = 300;
				composite1.setLayoutData(composite1LData);
				composite1.setLayout(composite1Layout);
				composite1.setBackground(new Color(null, 255, 255, 255));
				{
					labelThanks = new Label(composite1, SWT.WRAP);
					GridData labelThanksLData = new GridData();
					labelThanksLData.grabExcessHorizontalSpace = true;
					labelThanksLData.horizontalAlignment = GridData.FILL;
					labelThanksLData.verticalAlignment = GridData.FILL;
					labelThanksLData.grabExcessVerticalSpace = true;
					//labelThanks.setSize(300, 50);
					labelThanks.setBackground(new Color(null, 255, 0, 0));
					labelThanks.setLayoutData(labelThanksLData);
					//labelThanks.setBackground(new Color(null, 255, 255, 255));
					labelThanks.setText("");
					labelThanks.setAlignment(SWT.CENTER);
					stTimer = new Timer(false);
					stTimer.scheduleAtFixedRate(new TimerTask() {
						@Override
						public void run() {
							Display display = Display.getDefault();
							display.syncExec(new Runnable() {
								@Override
								public void run() {
									int firstLine = (stIndex) % specialThanks.length;
									int secondLine = (stIndex + 1) % specialThanks.length;
									int thirdLine = (stIndex + 2) % specialThanks.length;

									labelThanks.setText(specialThanks[firstLine] + '\n'
											+ specialThanks[secondLine] + '\n'
											+ specialThanks[thirdLine]);
									stIndex++;
									stIndex %= specialThanks.length;
								}
							});
						}
					}, delay, delay);
				}
			}
			{
				label2 = new Label(dialogShell, SWT.SEPARATOR | SWT.HORIZONTAL);
				GridData label2LData = new GridData();
				label2LData.heightHint = 3;
				label2LData.grabExcessHorizontalSpace = true;
				label2LData.horizontalAlignment = GridData.FILL;
				label2.setLayoutData(label2LData);
				label2.setText("label2");
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
                    buttonWebsite.setText(Messages.getString("AboutDialog.WebSite")); //$NON-NLS-1$
                    GridData buttonWebsiteLData = new GridData();
                    buttonWebsite.addSelectionListener(new SelectionAdapter() {
                        @Override
						public void widgetSelected(SelectionEvent evt) {
                            Program.launch( "http://fullsync.sourceforge.net" ); //$NON-NLS-1$
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
                    buttonOk.setText("Ok"); //$NON-NLS-1$
                    GridData buttonOkLData = new GridData();
                    buttonOk.addSelectionListener(new SelectionAdapter() {
                        @Override
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
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			ExceptionHandler.reportException( e );
		}
	}

}
