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
import org.eclipse.swt.widgets.Label;

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
public class StatusLine extends org.eclipse.swt.widgets.Composite {
	private Label labelIcon;
	private Label labelMessage;

	// private ProgressBar progressBar;

	public StatusLine(org.eclipse.swt.widgets.Composite parent, int style) {
		super(parent, style);
		initGUI();
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
			/*
			 * {
			 * progressBar = new ProgressBar(this, SWT.NONE);
			 * GridData progressBarLData = new GridData();
			 * progressBarLData.horizontalAlignment = GridData.END;
			 * progressBarLData.widthHint = 150;
			 * progressBarLData.heightHint = 17;
			 * progressBar.setLayoutData(progressBarLData);
			 * }
			 */
			this.layout();
		}
		catch (Exception e) {
			ExceptionHandler.reportException(e);
		}
	}

	public void setMessage(final String message) {
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				labelIcon.setImage(null);
				labelMessage.setText(message == null ? "" : message); //$NON-NLS-1$
			}
		});
	}

	public void setMessage(final Image icon, final String message) {
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				labelIcon.setImage(icon);
				labelMessage.setText(message);
			}
		});
	}

}
