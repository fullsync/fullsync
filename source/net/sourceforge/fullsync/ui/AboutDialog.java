/**
 * @license
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA  02110-1301, USA.
 *
 * For information about the authors of this project Have a look
 * at the AUTHORS file in the root of this project.
 */
package net.sourceforge.fullsync.ui;

import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.jar.Attributes;

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.Util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
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
public class AboutDialog extends org.eclipse.swt.widgets.Dialog implements DisposeListener {

	private Shell dialogShell;
	private Label labelPicture;
	private Label labelSeparator;
	private Composite compositeBottom;
	private Label label2;
	private Label labelThanks;
	private Composite composite1;
	private Button buttonOk;
	private Button buttonWebsite;

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
			dialogShell.setBackground(new Color(null, 255, 255, 255));

			dialogShell.addDisposeListener(this);

			GridLayout dialogShellLayout = new GridLayout();
			dialogShell.setLayout(dialogShellLayout);
			dialogShellLayout.verticalSpacing = 0;
			dialogShellLayout.marginHeight = 0;
			dialogShellLayout.marginWidth = 0;
			dialogShellLayout.horizontalSpacing = 0;
			dialogShell.setText(Messages.getString("AboutDialog.About_FullSync")); //$NON-NLS-1$
			// the fullsync picture
			labelPicture = new Label(dialogShell, SWT.NONE);
			GridData labelPictureLData = new GridData();
			labelPictureLData.grabExcessHorizontalSpace = true;
			labelPictureLData.grabExcessVerticalSpace = true;
			labelPictureLData.horizontalAlignment = GridData.FILL;
			labelPictureLData.verticalAlignment = GridData.FILL;
			labelPicture.setLayoutData(labelPictureLData);
			Image aboutImg = GuiController.getInstance().getImage("About.png"); //$NON-NLS-1$
			Rectangle r = aboutImg.getBounds();
			labelPicture.setSize(r.width, r.height);
			labelPicture.setImage(aboutImg);
			// version label
			Font smallfont = new Font(null, new FontData("Sans Serif", 7, SWT.ITALIC));
			Label labelVersion = new Label(dialogShell, SWT.FILL);
			labelVersion.setBackground(UISettings.COLOR_WHITE);
			labelVersion.setForeground(UISettings.COLOR_LIGHT_GREY);
			labelVersion.setFont(smallfont);
			labelVersion.setText("Version: <unknown devel version>");
			GridData lvd = new GridData(SWT.FILL);
			lvd.grabExcessHorizontalSpace = true;
			lvd.horizontalIndent = 17;
			labelVersion.setLayoutData(lvd);
			try {
				URL fileurl = AboutDialog.class.getProtectionDomain().getCodeSource().getLocation();
				URL jarurl = new URL("jar:" + fileurl.toString() + "!/");
				JarURLConnection urlc = (JarURLConnection) jarurl.openConnection();
				urlc.connect();
				Attributes jarattrs = urlc.getManifest().getMainAttributes();
				labelVersion.setText("Version: " + jarattrs.getValue("FullSync-Version"));
			}
			catch (Exception e) {
				/* this will happen during debugging, might happen at runtime too; ignore */
				//FIXME: log this exception to some warn log
			}
			String copyright = "<unable to read copyright of jar file>";
			InputStream copyrightIS = AboutDialog.class.getResourceAsStream("/jar-copyright.txt");
			if (null != copyrightIS) {
				copyright = Util.readStreamAsString(copyrightIS);
			}
			// copyright text
			Label labelCopyright = new Label(dialogShell, SWT.FILL);
			labelCopyright.setBackground(UISettings.COLOR_WHITE);
			labelCopyright.setForeground(UISettings.COLOR_LIGHT_GREY);
			labelCopyright.setFont(smallfont);
			labelCopyright.setText(copyright);
			GridData lcd = new GridData(SWT.FILL);
			lcd.grabExcessHorizontalSpace = true;
			lcd.horizontalIndent = 17;
			labelCopyright.setLayoutData(lcd);
			// separator
			labelSeparator = new Label(dialogShell, SWT.SEPARATOR | SWT.HORIZONTAL);
			GridData labelSeparatorLData = new GridData();
			labelSeparatorLData.horizontalAlignment = GridData.FILL;
			labelSeparatorLData.grabExcessHorizontalSpace = true;
			labelSeparator.setLayoutData(labelSeparatorLData);
			// credits background
			composite1 = new Composite(dialogShell, SWT.NONE);
			composite1.setLayout(new FillLayout());
			GridLayout composite1Layout = new GridLayout(1, true);
			GridData composite1LData = new GridData();
			composite1LData.horizontalAlignment = GridData.FILL;
			composite1LData.heightHint = 57;
			composite1.setLayoutData(composite1LData);
			composite1.setLayout(composite1Layout);
			composite1.setBackground(UISettings.COLOR_WHITE);
			// credits
			labelThanks = new Label(composite1, SWT.CENTER);
			GridData labelThanksLData = new GridData();
			labelThanksLData.grabExcessHorizontalSpace = true;
			labelThanksLData.horizontalAlignment = GridData.CENTER;
			labelThanks.setLayoutData(labelThanksLData);
			labelThanks.setBackground(UISettings.COLOR_WHITE);
			labelThanks.setText("\n\n\n");
			labelThanks.setAlignment(SWT.CENTER);
			stTimer = new Timer(false);
			final String[] specialThanks;
			InputStream specialThanksIS = AboutDialog.class.getResourceAsStream("/jar-special-thanks.txt");
			if (null != specialThanksIS) {
				String sp = Util.readStreamAsString(specialThanksIS);
				String[] res = sp.split("\n");
				if (null != res) {
					specialThanks = res;
				}
				else {
					specialThanks = new String[] {"", "", ""};
				}
			}
			else {
				specialThanks = new String[] {"", "", ""};
			}
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
							dialogShell.layout(true);
							composite1.layout(true);
							stIndex++;
							stIndex %= specialThanks.length;
						}
					});
				}
			}, delay, delay);
			// separator
			label2 = new Label(dialogShell, SWT.SEPARATOR | SWT.HORIZONTAL);
			GridData label2LData = new GridData();
			label2LData.grabExcessHorizontalSpace = true;
			label2LData.horizontalAlignment = GridData.FILL;
			label2.setLayoutData(label2LData);
			label2.setText("label2");
			// buttons composite
			compositeBottom = new Composite(dialogShell, SWT.NONE);
			GridLayout compositeBottomLayout = new GridLayout();
			GridData compositeBottomLData = new GridData();
			compositeBottomLData.horizontalAlignment = GridData.FILL;
			compositeBottom.setLayoutData(compositeBottomLData);
			compositeBottomLayout.makeColumnsEqualWidth = true;
			compositeBottomLayout.numColumns = 2;
			compositeBottom.setLayout(compositeBottomLayout);
			// website button
			buttonWebsite = new Button(compositeBottom, SWT.PUSH | SWT.CENTER);
			buttonWebsite.setText(Messages.getString("AboutDialog.WebSite")); //$NON-NLS-1$
			GridData buttonWebsiteLData = new GridData();
			buttonWebsite.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent evt) {
					Program.launch("http://fullsync.sourceforge.net"); //$NON-NLS-1$
				}
			});
			buttonWebsiteLData.widthHint = UISettings.BUTTON_WIDTH;
			buttonWebsiteLData.heightHint = UISettings.BUTTON_HEIGHT;
			buttonWebsiteLData.grabExcessHorizontalSpace = true;
			buttonWebsite.setLayoutData(buttonWebsiteLData);
			// ok button
			buttonOk = new Button(compositeBottom, SWT.PUSH | SWT.CENTER);
			buttonOk.setText("Ok"); //$NON-NLS-1$
			GridData buttonOkLData = new GridData();
			buttonOk.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent evt) {
					dialogShell.close();
				}
			});
			buttonOkLData.horizontalAlignment = GridData.END;
			buttonOkLData.heightHint = UISettings.BUTTON_HEIGHT;
			buttonOkLData.widthHint = UISettings.BUTTON_WIDTH;
			buttonOkLData.grabExcessHorizontalSpace = true;
			buttonOk.setLayoutData(buttonOkLData);
			// layout the dialog and show it
			dialogShell.pack();
			dialogShell.layout(true);
			dialogShell.open();
			Display display = dialogShell.getDisplay();
			while (!dialogShell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		}
		catch (Exception e) {
			ExceptionHandler.reportException(e);
		}
	}

	@Override
	public final void widgetDisposed(final DisposeEvent e) {
		if (stTimer != null) {
			stTimer.cancel();
		}
	}

}
