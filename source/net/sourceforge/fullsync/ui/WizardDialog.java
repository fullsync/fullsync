/**
 *	@license
 *	This program is free software; you can redistribute it and/or
 *	modify it under the terms of the GNU General Public License
 *	as published by the Free Software Foundation; either version 2
 *	of the License, or (at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program; if not, write to the Free Software
 *	Foundation, Inc., 51 Franklin Street, Fifth Floor,
 *	Boston, MA  02110-1301, USA.
 *
 *	---
 *	@copyright Copyright (C) 2005, Jan Kopcsek <codewright@gmx.net>
 *	@copyright Copyright (C) 2011, Obexer Christoph <cobexer@gmail.com>
 */
package net.sourceforge.fullsync.ui;

import java.util.Vector;

import net.sourceforge.fullsync.ExceptionHandler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class WizardDialog {

	private Shell dialogShell;
	private Composite compositeTop;
	private Composite compositeBottom;
	private Label labelImage;
	private Label labelSeparatorBottom;
	private Label labelSeparatorTop;
	private Label labelDescription;
	private Label labelCaption;
	private Composite compositeContent;

	private final Shell parent;
	private final int style;
	private Font captionFont;

	private final Vector dialogListeners;

	private WizardPage wizardPage;

	public WizardDialog( Shell parent, int style )
	{
	    this.parent = parent;
	    this.style = SWT.DIALOG_TRIM | style;
	    this.dialogListeners = new Vector();
	}

	public void setPage( WizardPage page )
	{
	    this.wizardPage = page;
	}

	public void show() {
		try {
			dialogShell = new Shell(parent, style );
			Display display = dialogShell.getDisplay();

			Color white = display.getSystemColor( SWT.COLOR_WHITE );

			GridLayout dialogShellLayout = new GridLayout();
			dialogShellLayout.horizontalSpacing = 0;
			dialogShellLayout.marginHeight = 0;
			dialogShellLayout.marginWidth = 0;
			dialogShellLayout.verticalSpacing = 0;
			dialogShell.setLayout(dialogShellLayout);
            {
                compositeTop = new Composite(dialogShell, SWT.NONE);
                compositeTop.setBackground(white);
                FormLayout compositeTopLayout = new FormLayout();
                GridData compositeTopLData = new GridData();
                compositeTopLData.grabExcessHorizontalSpace = true;
                compositeTopLData.horizontalAlignment = GridData.FILL;
                compositeTop.setLayoutData(compositeTopLData);
                compositeTop.setLayout(compositeTopLayout);
                {
                    labelImage = new Label(compositeTop, SWT.NONE);
                    labelImage.setBackground(white);
                    FormData labelImageLData = new FormData();
                    labelImage.setBounds(386, 0, 64, 64);
                    labelImageLData.width = 64;
                    labelImageLData.height = 64;
                    labelImageLData.right =  new FormAttachment(1000, 1000, 0);
                    labelImageLData.top =  new FormAttachment(0, 1000, 0);
                    labelImage.setLayoutData(labelImageLData);
                }
                {
                    labelCaption = new Label(compositeTop, SWT.NONE);
                    labelCaption.setBackground(white);
                    captionFont = new Font( display, "Tohama", 9, SWT.BOLD ); //$NON-NLS-1$
                    labelCaption.setFont( captionFont );
                    FormData labelCaptionLData = new FormData();
                    labelCaptionLData.width = 330;
                    labelCaptionLData.height = 13;
                    labelCaptionLData.left =  new FormAttachment(0, 1000, 10);
                    labelCaptionLData.top =  new FormAttachment(0, 1000, 10);
                    labelCaptionLData.right = new FormAttachment( labelImage, 20 );
                    labelCaption.setLayoutData(labelCaptionLData);
                }
                {
                    labelDescription = new Label(compositeTop, SWT.NULL);
                    labelDescription.setBackground(white);
                    FormData labelDescriptionLData = new FormData();
                    labelDescriptionLData.width = 330;
                    labelDescriptionLData.height = 26;
                    labelDescriptionLData.left =  new FormAttachment(0, 1000, 20);
                    labelDescriptionLData.top =  new FormAttachment(0, 1000, 30);
                    labelDescriptionLData.right = new FormAttachment( labelImage, 20 );
                    labelDescription.setLayoutData(labelDescriptionLData);
                }
            }
            {
                labelSeparatorTop = new Label(dialogShell, SWT.SEPARATOR
                    | SWT.HORIZONTAL);
                GridData labelSeparatorTopLData = new GridData();
                labelSeparatorTopLData.horizontalAlignment = GridData.FILL;
                labelSeparatorTop.setLayoutData(labelSeparatorTopLData);
            }
            {
                compositeContent = new Composite(dialogShell, SWT.NONE);
                //GridLayout compositeContentLayout = new GridLayout();
                GridData compositeContentLData = new GridData();
                compositeContentLData.grabExcessHorizontalSpace = true;
                compositeContentLData.horizontalAlignment = GridData.FILL;
                compositeContentLData.verticalAlignment = GridData.FILL;
                compositeContentLData.grabExcessVerticalSpace = true;
                compositeContent.setLayoutData(compositeContentLData);
                //compositeContentLayout.makeColumnsEqualWidth = true;
                compositeContent.setLayout(new GridLayout());
            }
            {
                labelSeparatorBottom = new Label(dialogShell, SWT.SEPARATOR
                    | SWT.HORIZONTAL);
                GridData labelSeparatorBottomLData = new GridData();
                labelSeparatorBottomLData.horizontalAlignment = GridData.FILL;
                labelSeparatorBottom.setLayoutData(labelSeparatorBottomLData);
            }
            {
                compositeBottom = new Composite(dialogShell, SWT.NONE);
                GridLayout compositeBottomLayout = new GridLayout();
                GridData compositeBottomLData = new GridData();
                compositeBottomLData.grabExcessHorizontalSpace = true;
                compositeBottomLData.horizontalAlignment = GridData.FILL;
                compositeBottom.setLayoutData(compositeBottomLData);
                compositeBottomLayout.makeColumnsEqualWidth = true;
                compositeBottom.setLayout(compositeBottomLayout);
            }

            updateTop();
            wizardPage.createContent( compositeContent );
            wizardPage.createBottom( compositeBottom );
            compositeContent.pack();
            dialogShell.layout();
            Point size = dialogShell.computeSize( SWT.DEFAULT, SWT.DEFAULT );
            if( size.x < 500 )
                size.x = 500;
            if( size.y < 400 )
                size.y = 400;

            if (size.x > display.getBounds().width - dialogShell.getBounds().x) {
            	size.x = display.getBounds().width - dialogShell.getBounds().x - 50;
            }
            if (size.y > display.getBounds().height - dialogShell.getBounds().y) {
            	size.y = display.getBounds().height - dialogShell.getBounds().y - 50;
            }

            dialogShell.setSize( size );
			dialogShell.open();
			dialogOpened();
			while( !dialogShell.isDisposed() ) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		} catch (Exception e) {
			ExceptionHandler.reportException( e );
		}
	}

	protected void dialogOpened() {
        for( int i = 0; i < dialogListeners.size(); i++ )
            ((WizardDialogListener)dialogListeners.get( i )).dialogOpened( this );

	}

	public void dispose()
	{
        Control[] controls = compositeContent.getChildren();
        for( int i = 0; i < controls.length; i++ )
            controls[i].dispose();

	    dialogShell.dispose();
	    captionFont.dispose();

	    // TODO dispose images ?
        // TODO dispose wizard page stuff
	}
	public Display getDisplay()
	{
	    return parent.getDisplay();
	}
	public Shell getShell()
	{
	    return dialogShell;
	}
	/*
	protected void setDialogTitle( String title )
	{
	    dialogShell.setText( title );
	}
	protected void setCaption( String caption )
	{
	    labelCaption.setText( caption );
	}
	protected void setDescription( String description )
	{
	    labelDescription.setText( description );
	}
	protected void setImage( Image image )
	{
	    labelImage.setImage( image );
	}
	*/
	public void updateTop()
	{
	    dialogShell.setImage( wizardPage.getIcon() );
	    dialogShell.setText( wizardPage.getTitle() );
        labelCaption.setText( wizardPage.getCaption() );
        labelDescription.setText( wizardPage.getDescription() );
        labelImage.setImage( wizardPage.getImage() );
	}

	public void addWizardDialogListener(WizardDialogListener listener) {
		if ((listener != null) && (!dialogListeners.contains(listener))) {
			dialogListeners.add(listener);
		}
	}

	public void removeWizardDialogListener(WizardDialogListener listener) {
		dialogListeners.remove(listener);
	}
}
