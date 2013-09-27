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

import java.util.Vector;

import net.sourceforge.fullsync.ExceptionHandler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public abstract class WizardDialog {

	private Shell dialogShell;
	private Composite compositeTop;
	private Composite compositeBottom;
	private Label labelImage;
	private Label labelDescription;
	private Label labelCaption;
	private Composite compositeContent;
	private Button okButton;
	private Button cancelButton;

	private final Shell parent;
	private final int style;

	private final Vector<WizardDialogListener> dialogListeners;

	public WizardDialog(Shell parent) {
		this.parent = parent;
		this.style = SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL;
		this.dialogListeners = new Vector<WizardDialogListener>();
	}

	public boolean checkAndApply() {
		boolean applied = apply();
		if (applied) {
			dialogShell.dispose();
		}
		return applied;
	}

	public boolean checkAndCancel() {
		boolean closed = cancel();
		if (closed) {
			dialogShell.dispose();
		}
		return closed;
	}

	public void show() {
		try {
			dialogShell = new Shell(parent, style);
			dialogShell.addListener(SWT.Close, e -> e.doit = !checkAndCancel());
			Display display = dialogShell.getDisplay();

			Color white = display.getSystemColor(SWT.COLOR_WHITE);

			GridLayout dialogShellLayout = new GridLayout();
			dialogShellLayout.horizontalSpacing = 0;
			dialogShellLayout.marginHeight = 0;
			dialogShellLayout.marginWidth = 0;
			dialogShellLayout.verticalSpacing = 0;
			dialogShell.setLayout(dialogShellLayout);

			// top area
			compositeTop = new Composite(dialogShell, SWT.NONE);
			compositeTop.setBackground(white);
			GridData compositeTopLData = new GridData();
			compositeTopLData.grabExcessHorizontalSpace = true;
			compositeTopLData.horizontalAlignment = SWT.FILL;
			compositeTopLData.horizontalIndent = 0;
			compositeTopLData.verticalIndent = 0;
			compositeTop.setLayoutData(compositeTopLData);
			GridLayout compositeTopLayout = new GridLayout(2, false);
			compositeTopLayout.horizontalSpacing = 0;
			compositeTopLayout.marginRight = 0;
			compositeTopLayout.marginBottom = 0;
			compositeTopLayout.marginTop = 0;
			compositeTopLayout.marginLeft = 0;
			compositeTopLayout.marginHeight = 0;
			compositeTopLayout.marginWidth = 0;
			compositeTop.setLayout(compositeTopLayout);

			Composite topTextComposite = new Composite(compositeTop, SWT.NONE);
			topTextComposite.setBackground(white);
			GridLayout topTextCompositeLayout = new GridLayout(1, false);
			topTextCompositeLayout.marginHeight = 10;
			topTextCompositeLayout.marginWidth = 10;
			topTextComposite.setLayout(topTextCompositeLayout);
			GridData topTextCompositeData = new GridData();
			topTextCompositeData.grabExcessHorizontalSpace = true;
			topTextCompositeData.horizontalAlignment = SWT.FILL;
			topTextComposite.setLayoutData(topTextCompositeData);

			// top image
			labelImage = new Label(compositeTop, SWT.NONE);
			labelImage.setBackground(white);
			labelImage.setSize(64, 64);
			GridData labelImageLData = new GridData();
			labelImageLData.horizontalIndent = 0;
			labelImageLData.verticalIndent = 0;
			labelImage.setLayoutData(labelImageLData);

			// wizard caption
			labelCaption = new Label(topTextComposite, SWT.NONE);
			labelCaption.setBackground(white);
			labelCaption.setFont(GuiController.getInstance().getFont("Tohama", 9, SWT.BOLD)); //$NON-NLS-1$
			GridData labelCaptionLData = new GridData();
			labelCaptionLData.grabExcessHorizontalSpace = true;
			labelCaption.setLayoutData(labelCaptionLData);

			// wizard description
			labelDescription = new Label(topTextComposite, SWT.NULL);
			labelDescription.setBackground(white);
			GridData labelDescriptionLData = new GridData();
			labelDescriptionLData.grabExcessHorizontalSpace = true;
			labelDescription.setLayoutData(labelDescriptionLData);

			// line below the header
			Label labelSeparatorTop = new Label(dialogShell, SWT.SEPARATOR | SWT.HORIZONTAL);
			GridData labelSeparatorTopLData = new GridData();
			labelSeparatorTopLData.horizontalAlignment = SWT.FILL;
			labelSeparatorTop.setLayoutData(labelSeparatorTopLData);

			// composite for the wizard content
			compositeContent = new Composite(dialogShell, SWT.NONE);
			GridData compositeContentData = new GridData();
			compositeContentData.grabExcessHorizontalSpace = true;
			compositeContentData.horizontalAlignment = SWT.FILL;
			compositeContentData.verticalAlignment = SWT.FILL;
			compositeContentData.grabExcessVerticalSpace = true;
			compositeContent.setLayoutData(compositeContentData);
			compositeContent.setLayout(new GridLayout());

			// line below wizard content
			Label labelSeparatorBottom = new Label(dialogShell, SWT.SEPARATOR | SWT.HORIZONTAL);
			GridData labelSeparatorBottomData = new GridData();
			labelSeparatorBottomData.horizontalAlignment = SWT.FILL;
			labelSeparatorBottom.setLayoutData(labelSeparatorBottomData);

			// button bar at the bottom
			compositeBottom = new Composite(dialogShell, SWT.NONE);
			GridData compositeBottomData = new GridData();
			compositeBottomData.grabExcessHorizontalSpace = true;
			compositeBottomData.horizontalAlignment = SWT.FILL;
			compositeBottom.setLayoutData(compositeBottomData);
			GridLayout compositeBottomLayout = new GridLayout();
			compositeBottomLayout.makeColumnsEqualWidth = true;
			compositeBottom.setLayout(compositeBottomLayout);

			// fill in wizard page
			updateTop();
			createContent(compositeContent);

			// bottom area
			compositeBottom.setLayout(new GridLayout(2, false));
			okButton = new Button(compositeBottom, SWT.PUSH);
			okButton.setText(Messages.getString("ProfileDetailsPage.Ok")); //$NON-NLS-1$
			okButton.addListener(SWT.Selection, e -> checkAndApply());
			GridData okButtonLayoutData = new GridData(SWT.END, SWT.CENTER, true, true);
			okButtonLayoutData.widthHint = UISettings.BUTTON_WIDTH;
			okButtonLayoutData.heightHint = UISettings.BUTTON_HEIGHT;
			okButton.setLayoutData(okButtonLayoutData);

			cancelButton = new Button(compositeBottom, SWT.PUSH);
			cancelButton.setText(Messages.getString("ProfileDetailsPage.Cancel")); //$NON-NLS-1$
			cancelButton.addListener(SWT.Selection, e -> checkAndCancel());
			GridData cancelButtonLayoutData = new GridData(SWT.END, SWT.CENTER, false, true);
			cancelButtonLayoutData.widthHint = UISettings.BUTTON_WIDTH;
			cancelButtonLayoutData.heightHint = UISettings.BUTTON_HEIGHT;
			cancelButton.setLayoutData(cancelButtonLayoutData);
			dialogShell.setDefaultButton(okButton);

			compositeContent.pack();
			dialogShell.layout();

			Point size = dialogShell.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			size.x = Math.max(size.x, 500);
			size.y = Math.max(size.y, 400);

			if (size.x > (display.getBounds().width - dialogShell.getBounds().x)) {
				size.x = display.getBounds().width - dialogShell.getBounds().x - 50;
			}
			if (size.y > (display.getBounds().height - dialogShell.getBounds().y)) {
				size.y = display.getBounds().height - dialogShell.getBounds().y - 50;
			}

			dialogShell.setSize(size);
			dialogShell.open();
			dialogOpened();

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

	protected void dialogOpened() {
		for (WizardDialogListener listener : dialogListeners) {
			listener.dialogOpened();
		}
	}

	public void dispose() {
		for (Control control : compositeContent.getChildren()) {
			control.dispose();
		}
		dialogShell.dispose();
		// TODO dispose images ?
		// TODO dispose wizard page stuff
	}

	public Display getDisplay() {
		return parent.getDisplay();
	}

	public Shell getShell() {
		return dialogShell;
	}

	public void updateTop() {
		dialogShell.setImage(getIcon());
		dialogShell.setText(getTitle());
		labelCaption.setText(getCaption());
		labelDescription.setText(getDescription());
		labelImage.setImage(getImage());
	}

	public void addWizardDialogListener(WizardDialogListener listener) {
		if ((listener != null) && (!dialogListeners.contains(listener))) {
			dialogListeners.add(listener);
		}
	}

	/**
	 * enable or disable the ok button.
	 * @param enabled
	 */
	public final void setOkButtonEnabled(final boolean enabled) {
		okButton.setEnabled(enabled);
	}

	/**
	 * enable or disable the cancel button.
	 * @param enabled
	 */
	public final void setCancelButtonEnabled(final boolean enabled) {
		cancelButton.setEnabled(enabled);
	}

	public abstract String getTitle();

	public abstract String getCaption();

	public abstract String getDescription();

	public abstract Image getIcon();

	public abstract Image getImage();

	public abstract void createContent(Composite content);

	public abstract boolean apply();

	public abstract boolean cancel();

}
