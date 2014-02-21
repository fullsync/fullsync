/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA 02110-1301, USA.
 *
 * For information about the authors of this project Have a look
 * at the AUTHORS file in the root of this project.
 */

package net.sourceforge.fullsync.ui;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import net.sourceforge.fullsync.Util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;



public class WelcomeScreen extends Dialog {

	private Shell dialogShell;
	private Label labelPicture;
	private Composite compositeBottom;
	private Button buttonOk;

	public Boolean welcomeScreenShown;

	public WelcomeScreen(Shell parent) {
		super(parent);
		welcomeScreenShown = true;
		dialogShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialogShell.setBackground(UISettings.COLOR_WHITE);
		dialogShell.setBackgroundMode(SWT.INHERIT_DEFAULT);

		GridLayout dialogShellLayout = new GridLayout();
		dialogShell.setLayout(dialogShellLayout);
		dialogShellLayout.verticalSpacing = 0;
		dialogShellLayout.marginHeight = 0;
		dialogShellLayout.marginWidth = 0;
		dialogShellLayout.horizontalSpacing = 0;
		dialogShell.setText(Messages.getString("WelcomeScreen.WelcomeMessage"));

		labelPicture = new Label(dialogShell, SWT.NONE);
		GridData labelPictureLData = new GridData();
		labelPictureLData.grabExcessHorizontalSpace = true;
		labelPictureLData.grabExcessVerticalSpace = true;
		labelPictureLData.horizontalAlignment = SWT.FILL;
		labelPictureLData.verticalAlignment = SWT.FILL;
		labelPicture.setLayoutData(labelPictureLData);
		Image aboutImg = GuiController.getInstance().getImage("About.png");
		Rectangle r = aboutImg.getBounds();
		labelPicture.setSize(r.width, r.height);
		labelPicture.setImage(aboutImg);

		// version label
		Label labelVersion = new Label(dialogShell, SWT.FILL);
		labelVersion.setForeground(UISettings.COLOR_LIGHT_GREY);
		labelVersion.setText(Messages.getString("WelcomeScreen.WelcomeMessage") + " - " + getVersion());
		GridData lvd = new GridData(SWT.FILL);
		lvd.grabExcessHorizontalSpace = true;
		lvd.horizontalIndent = 17;
		lvd.widthHint = 400;
		lvd.heightHint = 18;
		labelVersion.setLayoutData(lvd);

		//separator
		Label labelSeparator2 = new Label(dialogShell, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData labelSeparator2LData = new GridData();
		labelSeparator2LData.grabExcessHorizontalSpace = true;
		labelSeparator2LData.horizontalAlignment = SWT.FILL;
		labelSeparator2.setLayoutData(labelSeparator2LData);

		//releases label
		Label labelReleases = new Label(dialogShell, SWT.FILL | SWT.WRAP);
		labelReleases.setForeground(UISettings.COLOR_LIGHT_GREY);
		labelReleases.setText(Messages.getString("WelcomeScreen.NewReleases")+":"+getReleases());
		GridData lrel = new GridData(SWT.FILL | SWT.WRAP);
		lrel.grabExcessHorizontalSpace = true;
		lrel.horizontalIndent = 17;
		lrel.widthHint = 400;
		labelReleases.setLayoutData(lrel);

		//separator
		Label labelSeparator3 = new Label(dialogShell, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData labelSeparator3LData = new GridData();
		labelSeparator3LData.grabExcessHorizontalSpace = true;
		labelSeparator3LData.horizontalAlignment = SWT.FILL;
		labelSeparator3.setLayoutData(labelSeparator2LData);

		// buttons composite
		compositeBottom = new Composite(dialogShell, SWT.NONE);
		GridLayout compositeBottomLayout = new GridLayout();
		GridData compositeBottomLData = new GridData();
		compositeBottomLData.horizontalAlignment = SWT.FILL;
		compositeBottom.setLayoutData(compositeBottomLData);
		compositeBottomLayout.makeColumnsEqualWidth = true;
		compositeBottomLayout.numColumns = 2;
		compositeBottom.setLayout(compositeBottomLayout);

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

	private String getVersion(){
		String version = null;
		String url = Util.getWebsiteURL();
		try{
			Document doc = Jsoup.connect(url).get();
			Elements el = doc.body().select("li");
			version = parseVersion(el);
		}catch(IOException e){
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader("CHANGELOG"));
				version = br.readLine();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			finally{
				try {
					br.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		return version;
	}

	private String parseVersion(Elements el){
		String temp = el.toString();
		String version = "";
		int start=0;
		for(int i = 0; i<temp.length(); i++){
			if(temp.charAt(i)=='>'){
				start = i;
				break;
			}
		}
		int i = 1;
		while(temp.charAt(start+i)!=':'){
			version += temp.charAt(start+i);
			i++;
		}
		return version;
	}

	private String getReleases(){
		String releases  = "";
		String url = Util.getWebsiteURL();
		try{
			Document doc = Jsoup.connect(url).get();
			releases = parseReleases(doc.body().select("li").select("ul"));
		}catch(IOException e){
			releases = "connection error";
		}
		return releases;
	}


	private String parseReleases(Elements el){
		String releasesText = "";
		String allReleases = el.toString();
		String tokens[] = allReleases.split(" ");
		int i = 1;
		while(!tokens[i].contains("</ul>")){
			if(tokens[i].contains("<li>")){
				String temp = tokens[i].replace("<li>","-");
				releasesText += temp + " ";
			}
			else if(tokens[i].contains("</li>")){
				String temp = tokens[i].replace("</li>", "");
				releasesText += temp;
			}else{
				releasesText += tokens[i] + " ";
			}
			i++;
		}
		return releasesText;
	}




}
