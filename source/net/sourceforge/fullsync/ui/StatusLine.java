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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

class StatusLine extends Composite {
	private Label labelMessage;
	private GUIUpdateQueue<String> updateQueue;

	public StatusLine(Composite parent, int style) {
		super(parent, style);
		GridLayout thisLayout = new GridLayout(3, false);
		thisLayout.marginHeight = 1;
		thisLayout.marginWidth = 2;
		this.setLayout(thisLayout);

		labelMessage = new Label(this, SWT.NONE);
		GridData labelMessageLData = new GridData(SWT.FILL, SWT.END, true, false);
		labelMessage.setLayoutData(labelMessageLData);
		this.layout();
		updateQueue = new GUIUpdateQueue<String>(parent.getDisplay(), (display, items) -> {
			String message = items.getLast();
			if (null == message) {
				message = ""; //$NON-NLS-1$
			}
			labelMessage.setText(message);
		});
	}

	public void setMessage(final String message) {
		updateQueue.add(message);
	}
}
