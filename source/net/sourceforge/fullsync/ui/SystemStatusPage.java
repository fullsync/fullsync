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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class SystemStatusPage implements WizardPage {
	private WizardDialog dialog;
	private SystemStatusComposite systemStatusComposite;

	public SystemStatusPage(WizardDialog dialog) {
		this.dialog = dialog;
		dialog.setPage(this);
	}

	@Override
	public String getTitle() {
		return "System Status";
	}

	@Override
	public String getCaption() {
		return "System Status";
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public Image getIcon() {
		return null;
	}

	@Override
	public Image getImage() {
		return null;
	}

	@Override
	public void createBottom(Composite bottom) {
		bottom.setLayout(new GridLayout(1, false));

		Button okButton = new Button(bottom, SWT.PUSH);
		okButton.setText("Ok");
		okButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dialog.dispose();
			}
		});
		okButton.setLayoutData(new GridData(GridData.END, GridData.CENTER, true, true));
	}

	@Override
	public void createContent(Composite content) {
		systemStatusComposite = new SystemStatusComposite(content, SWT.NULL);
	}
}
