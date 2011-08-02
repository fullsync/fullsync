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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class OptionsDialog extends Dialog implements SelectionListener {
	private Label labelImage;
	private Label labelMessage;
	private Shell dialogShell;

	private String message;
	private String[] options;
	private String result;

	public OptionsDialog(Shell parent, int style) {
		super(parent, style);
	}

	public String open() {
		try {
			result = null;

			Shell parent = getParent();
			dialogShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
			dialogShell.setText(getText());

			dialogShell.setSize(new Point(500, 200));

			labelImage = new Label(dialogShell, SWT.NULL);
			Image i = parent.getDisplay().getSystemImage(getStyle());
			labelImage.setImage(i);

			labelMessage = new Label(dialogShell, SWT.NULL);
			labelMessage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			labelMessage.setText(getMessage());

			Composite compositeButtons = new Composite(dialogShell, SWT.NULL);
			GridData compositeButtonsData = new GridData(SWT.CENTER, SWT.CENTER, true, false);
			compositeButtonsData.horizontalSpan = 2;
			compositeButtons.setLayoutData(compositeButtonsData);
			GridLayout compositeButtonsLayout = new GridLayout(options.length, true);
			compositeButtons.setLayout(compositeButtonsLayout);
			for (String option : options) {
				Button b = new Button(compositeButtons, SWT.PUSH);
				b.setText(option);
				b.addSelectionListener(this);
				b.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			}

			dialogShell.setLayout(new GridLayout(2, false));
			dialogShell.pack();
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
		return result;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setOptions(String[] newOptions) {
		this.options = newOptions;
	}

	public String[] getOptions() {
		return options;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent arg0) {
		result = options[0];
		dialogShell.close();
		dialogShell.dispose();
	}

	@Override
	public void widgetSelected(SelectionEvent evt) {
		result = ((Button) evt.widget).getText();
		dialogShell.close();
		dialogShell.dispose();
	}
}
