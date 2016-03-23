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

import net.sourceforge.fullsync.ExceptionHandler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class StatusLine extends Composite {
	private Label labelIcon;
	private Label labelMessage;

	public StatusLine(Composite parent, int style) {
		super(parent, style);
		try {
			GridLayout thisLayout = new GridLayout();
			thisLayout.marginHeight = 1;
			thisLayout.marginWidth = 2;
			thisLayout.numColumns = 3;
			this.setLayout(thisLayout);

			labelIcon = new Label(this, SWT.NONE);
			GridData labelIconLData = new GridData();
			labelIconLData.widthHint = 16;
			labelIconLData.heightHint = 16;
			labelIconLData.verticalAlignment = GridData.END;
			labelIcon.setLayoutData(labelIconLData);

			labelMessage = new Label(this, SWT.NONE);
			GridData labelMessageLData = new GridData();
			labelMessageLData.grabExcessHorizontalSpace = true;
			labelMessageLData.horizontalAlignment = SWT.FILL;
			labelMessageLData.verticalAlignment = SWT.END;
			labelMessage.setLayoutData(labelMessageLData);
			this.layout();
		}
		catch (Exception e) {
			ExceptionHandler.reportException(e);
		}
	}

	public void setMessage(final String message) {
		getDisplay().asyncExec(() -> {
			labelIcon.setImage(null);
			labelMessage.setText(message == null ? "" : message); //$NON-NLS-1$
		});
	}

	public void setMessage(final Image icon, final String message) {
		getDisplay().asyncExec(() -> {
			labelIcon.setImage(icon);
			labelMessage.setText(message);
		});
	}

}
