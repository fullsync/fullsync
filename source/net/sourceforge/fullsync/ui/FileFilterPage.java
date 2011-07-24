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
/*
 * Created on May 29, 2005
 */
package net.sourceforge.fullsync.ui;

import net.sourceforge.fullsync.rules.filefilter.FileFilter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

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
/**
 * @author Michele Aiello
 */
public class FileFilterPage implements WizardPage {
	private WizardDialog dialog;

	private FileFilterDetails details;
	private FileFilter filter;

	public FileFilterPage(WizardDialog dialog, FileFilter filter) {
		dialog.setPage(this);
		this.filter = filter;
		this.dialog = dialog;
	}

	public String getTitle() {
		return "File Filter";
	}

	public String getCaption() {
		return "Edit the file filter";
	}

	public String getDescription() {
		return "";
	}

	public Image getIcon() {
		return GuiController.getInstance().getImage("FileFilter_Default.png"); //$NON-NLS-1$
	}

	public Image getImage() {
		return GuiController.getInstance().getImage("FileFilter_Wizard.png"); //$NON-NLS-1$
	}

	public void createContent(Composite content) {
		details = new FileFilterDetails(content, SWT.NULL, filter);
	}

	public void createBottom(Composite bottom) {
		bottom.setLayout(new GridLayout(2, false));

		Button okButton = new Button(bottom, SWT.PUSH);
		okButton.setText("OK");
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				filter = details.getFileFilter();
				dialog.dispose();
			}
		});
		okButton.setLayoutData(new GridData(GridData.END, GridData.CENTER, true, true));

		Button cancelButton = new Button(bottom, SWT.PUSH);
		cancelButton.setText("Cancel");
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				dialog.dispose();
				filter = null;
			}
		});
		cancelButton.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, true));

		bottom.getShell().setDefaultButton(okButton);
	}

	public FileFilter getFileFilter() {
		return filter;
	}

}
