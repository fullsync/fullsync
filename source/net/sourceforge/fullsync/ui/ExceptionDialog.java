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

import java.io.PrintWriter;
import java.io.StringWriter;

import net.sourceforge.fullsync.ExceptionHandler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


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
public class ExceptionDialog extends Dialog
{
	private Shell dialogShell;
	private Label labelImage;
	private Text textLog;
	private Button buttonOk;
	private Composite compositeExtension;
	private Composite compositeBase;
	private Button buttonDetails;
	private Label labelMessage;

	private boolean expanded;

	private final String message;
	private final Throwable throwable;

	/**
	* Auto-generated main method to display this
	* org.eclipse.swt.widgets.Dialog inside a new Shell.
	*/
	public ExceptionDialog( Shell parent, String message, Throwable throwable )
	{
		super( parent, SWT.NULL );
		this.message = message;
		this.throwable = throwable;
	}

	public void open() {
		try {
		    Shell parent = getParent();
			Display display = parent.getDisplay();

			dialogShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
			dialogShell.setText( Messages.getString("ExceptionDialog.Exception") ); //$NON-NLS-1$

			GridLayout dialogShellLayout = new GridLayout();
			dialogShellLayout.marginHeight = 0;
			dialogShellLayout.marginWidth = 0;
			dialogShellLayout.horizontalSpacing = 0;
			dialogShellLayout.verticalSpacing = 0;
			dialogShell.setLayout(dialogShellLayout);
			dialogShell.setSize(650, 350);
            {
                compositeBase = new Composite(dialogShell, SWT.NONE);
                GridLayout compositeBaseLayout = new GridLayout();
                GridData compositeBaseLData = new GridData();
                compositeBaseLData.heightHint = 116;
                compositeBaseLData.grabExcessHorizontalSpace = true;
                compositeBaseLData.horizontalAlignment = GridData.FILL;
                compositeBase.setLayoutData(compositeBaseLData);
    			dialogShell.setLayout(dialogShellLayout);
    			compositeBaseLayout.numColumns = 3;
    			compositeBaseLayout.marginHeight = 10;
    			compositeBaseLayout.marginWidth = 10;
                compositeBase.setLayout(compositeBaseLayout);
                {
                    labelImage = new Label(compositeBase, SWT.NONE);
                    GridData labelImageLData = new GridData();
                    labelImageLData.widthHint = 48;
                    labelImageLData.heightHint = 48;
                    labelImageLData.verticalSpan = 2;
                    labelImageLData.verticalAlignment = GridData.BEGINNING;
                    labelImage.setLayoutData(labelImageLData);
                    labelImage.setImage(display.getSystemImage(SWT.ICON_ERROR));
                }
                {
                    labelMessage = new Label(compositeBase, SWT.NONE);
                    labelMessage.setText( message );
                    GridData labelMessageLData = new GridData();
                    labelMessageLData.horizontalAlignment = GridData.FILL;
                    labelMessageLData.horizontalIndent = 20;
                    labelMessageLData.verticalAlignment = GridData.FILL;
                    labelMessageLData.horizontalSpan = 2;
                    labelMessage.setLayoutData(labelMessageLData);
                }
                {
                    buttonOk = new Button(compositeBase, SWT.PUSH | SWT.CENTER);
                    buttonOk.setText(Messages.getString("ExceptionDialog.Ok")); //$NON-NLS-1$
                    GridData buttonOkLData = new GridData();
                    buttonOk.addSelectionListener(new SelectionAdapter() {
                        @Override
						public void widgetSelected(SelectionEvent evt) {
                            dialogShell.dispose();
                        }
                    });
                    buttonOkLData.horizontalAlignment = GridData.END;
                    buttonOkLData.heightHint = 23;
                    buttonOkLData.verticalAlignment = GridData.END;
                    buttonOkLData.grabExcessHorizontalSpace = true;
                    buttonOkLData.widthHint = 80;
                    buttonOk.setLayoutData(buttonOkLData);
                }
                {
                    buttonDetails = new Button(compositeBase, SWT.PUSH
                        | SWT.CENTER);
                    buttonDetails.setText(Messages.getString("ExceptionDialog.Details")); //$NON-NLS-1$
                    GridData buttonDetailsLData = new GridData();
                    buttonDetails.addSelectionListener(new SelectionAdapter() {
                        @Override
						public void widgetSelected(SelectionEvent evt) {
                            toggleExpansion();
                        }
                    });
                    buttonDetailsLData.horizontalAlignment = GridData.END;
                    buttonDetailsLData.grabExcessVerticalSpace = true;
                    buttonDetailsLData.verticalAlignment = GridData.END;
                    buttonDetailsLData.widthHint = 80;
                    buttonDetailsLData.heightHint = 23;
                    buttonDetails.setLayoutData(buttonDetailsLData);
                }
            }
            {
                compositeExtension = new Composite(dialogShell, SWT.NONE);
                GridLayout compositeExtensionLayout = new GridLayout();
                GridData compositeExtensionLData = new GridData();
                compositeExtensionLData.grabExcessHorizontalSpace = true;
                compositeExtensionLData.horizontalAlignment = GridData.FILL;
                compositeExtensionLData.grabExcessVerticalSpace = true;
                compositeExtensionLData.verticalAlignment = GridData.FILL;
                compositeExtension.setLayoutData(compositeExtensionLData);
                compositeExtensionLayout.makeColumnsEqualWidth = true;
                compositeExtension.setLayout(compositeExtensionLayout);
                {
                    textLog = new Text(compositeExtension, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
                    GridData textLogLData = new GridData();
                    textLogLData.grabExcessHorizontalSpace = true;
                    textLogLData.grabExcessVerticalSpace = true;
                    textLogLData.horizontalAlignment = GridData.FILL;
                    textLogLData.verticalAlignment = GridData.FILL;
                    textLog.setLayoutData(textLogLData);

                    StringWriter writer = new StringWriter();
                    throwable.printStackTrace( new PrintWriter( writer ) );
                    textLog.setText( writer.getBuffer().toString() );
                }
            }
			dialogShell.layout();
			//dialogShell.pack();
            expanded = true;
            toggleExpansion();
            dialogShell.open();
			while (!dialogShell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		} catch (Exception e) {
			ExceptionHandler.reportException( e );
		}
	}

	private void toggleExpansion()
	{
	    if( expanded )
	    {
	        Rectangle r = dialogShell.computeTrim( 0, 0, compositeBase.getSize().x, compositeBase.getSize().y );
            dialogShell.setSize( r.width, r.height );
            buttonDetails.setText( Messages.getString("ExceptionDialog.Details")+" >>" ); //$NON-NLS-1$ //$NON-NLS-2$
            expanded = false;
	    } else {
	        dialogShell.setSize( dialogShell.getSize().x, dialogShell.getSize().y+200 );
            buttonDetails.setText( Messages.getString("ExceptionDialog.Details")+" <<" ); //$NON-NLS-1$ //$NON-NLS-2$
	        expanded = true;
	    }
	}

}
