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

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

class ExceptionDialog {
	private Shell dialogShell;
	private Composite compositeBase;
	private Button buttonDetails;

	private boolean expanded;

	ExceptionDialog(Shell parent, String message, Throwable throwable) {
		try {
			Display display = parent.getDisplay();
			Shell p = display.getActiveShell();
			p = null == p ? parent : p;

			dialogShell = new Shell(p, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
			dialogShell.setText(Messages.getString("ExceptionDialog.Exception")); //$NON-NLS-1$
			dialogShell.addListener(SWT.Close, e -> display.asyncExec(() -> dialogShell.dispose()));

			GridLayout dialogShellLayout = new GridLayout();
			dialogShellLayout.marginHeight = 0;
			dialogShellLayout.marginWidth = 0;
			dialogShellLayout.horizontalSpacing = 0;
			dialogShellLayout.verticalSpacing = 0;
			dialogShell.setLayout(dialogShellLayout);
			dialogShell.setSize(650, 350);

			// base layout
			compositeBase = new Composite(dialogShell, SWT.NONE);
			GridLayout compositeBaseLayout = new GridLayout(3, false);
			compositeBaseLayout.marginHeight = 10;
			compositeBaseLayout.marginWidth = 10;
			compositeBase.setLayout(compositeBaseLayout);
			GridData compositeBaseLData = new GridData();
			compositeBaseLData.heightHint = 116;
			compositeBaseLData.grabExcessHorizontalSpace = true;
			compositeBaseLData.horizontalAlignment = SWT.FILL;
			compositeBase.setLayoutData(compositeBaseLData);

			// error icon
			Image img = display.getSystemImage(SWT.ICON_ERROR);
			Label labelImage = new Label(compositeBase, SWT.NONE);
			GridData labelImageLData = new GridData();
			Rectangle rect = new Rectangle(0, 0, 0, 0);
			if (null != img) {
				rect = img.getBounds();
				labelImage.setImage(img);
			}
			labelImageLData.widthHint = rect.width;
			labelImageLData.heightHint = rect.height;
			labelImageLData.verticalSpan = 2;
			labelImageLData.verticalAlignment = GridData.BEGINNING;
			labelImage.setLayoutData(labelImageLData);

			// message
			Label labelMessage = new Label(compositeBase, SWT.NONE);
			labelMessage.setText(message);
			GridData labelMessageLData = new GridData();
			labelMessageLData.horizontalAlignment = SWT.FILL;
			labelMessageLData.verticalAlignment = SWT.FILL;
			labelMessageLData.horizontalSpan = 2;
			labelMessage.setLayoutData(labelMessageLData);

			// buttons
			Button buttonOk = new Button(compositeBase, SWT.PUSH | SWT.CENTER);
			buttonOk.setText(Messages.getString("ExceptionDialog.Ok")); //$NON-NLS-1$
			GridData buttonOkLData = new GridData();
			buttonOk.addListener(SWT.Selection, e -> dialogShell.close());
			buttonOkLData.horizontalAlignment = SWT.END;
			buttonOkLData.heightHint = UISettings.BUTTON_HEIGHT;
			buttonOkLData.widthHint = UISettings.BUTTON_WIDTH;
			buttonOkLData.grabExcessHorizontalSpace = true;
			buttonOkLData.verticalAlignment = SWT.END;
			buttonOk.setLayoutData(buttonOkLData);

			// details button
			buttonDetails = new Button(compositeBase, SWT.PUSH | SWT.CENTER);
			buttonDetails.setText(Messages.getString("ExceptionDialog.Details")); //$NON-NLS-1$
			GridData buttonDetailsLData = new GridData();
			buttonDetails.addListener(SWT.Selection, e -> {
				if (expanded) {
					Rectangle r = dialogShell.computeTrim(0, 0, compositeBase.getSize().x, compositeBase.getSize().y);
					dialogShell.setSize(r.width, r.height);
				}
				else {
					dialogShell.setSize(dialogShell.getSize().x, dialogShell.getSize().y + 350);
				}
				expanded = !expanded;
				buttonDetails.setText(Messages.getString("ExceptionDialog.Details") + (expanded ? " <<" : " >>")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			});
			buttonDetailsLData.horizontalAlignment = SWT.END;
			buttonDetailsLData.grabExcessVerticalSpace = true;
			buttonDetailsLData.verticalAlignment = SWT.END;
			buttonDetailsLData.widthHint = UISettings.BUTTON_WIDTH;
			buttonDetailsLData.heightHint = UISettings.BUTTON_HEIGHT;
			buttonDetails.setLayoutData(buttonDetailsLData);

			// stack trace container
			Composite compositeExtension = new Composite(dialogShell, SWT.NONE);
			GridLayout compositeExtensionLayout = new GridLayout();
			GridData compositeExtensionLData = new GridData();
			compositeExtensionLData.grabExcessHorizontalSpace = true;
			compositeExtensionLData.horizontalAlignment = SWT.FILL;
			compositeExtensionLData.grabExcessVerticalSpace = true;
			compositeExtensionLData.verticalAlignment = SWT.FILL;
			compositeExtension.setLayoutData(compositeExtensionLData);
			compositeExtension.setLayout(compositeExtensionLayout);

			// stack trace
			Text textLog = new Text(compositeExtension, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
			GridData textLogLData = new GridData();
			textLogLData.grabExcessHorizontalSpace = true;
			textLogLData.grabExcessVerticalSpace = true;
			textLogLData.horizontalAlignment = SWT.FILL;
			textLogLData.verticalAlignment = SWT.FILL;
			textLog.setLayoutData(textLogLData);

			StringWriter writer = new StringWriter();
			throwable.printStackTrace(new PrintWriter(writer));
			textLog.setText(writer.getBuffer().toString());

			dialogShell.layout();
			expanded = true;
			buttonDetails.notifyListeners(SWT.Selection, null);
			dialogShell.open();
			while (!dialogShell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		}
		catch (Exception e) {
			// using this dialog here would likely cause an endless loop
			e.printStackTrace();
			// TODO: show a message box
		}
	}
}
