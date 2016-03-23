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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class LogoHeaderComposite extends Composite {
	private Color headerBackgroundColor;
	public LogoHeaderComposite(final Composite parent, final int style) {
		super(parent, style);
		addDisposeListener(e -> headerBackgroundColor.dispose());
		headerBackgroundColor = new Color(getDisplay(), 192, 204, 214);
		GridLayout headerLayout = new GridLayout(2, false);
		headerLayout.marginRight = 14; // 14px padding as reserved in the About.png
		setLayout(headerLayout);
		setBackground(headerBackgroundColor);
		setBackgroundMode(SWT.INHERIT_DEFAULT);

		Label labelPicture = new Label(this, SWT.NONE);
		GridData labelPictureLData = new GridData();
		labelPictureLData.horizontalAlignment = SWT.FILL;
		labelPicture.setLayoutData(labelPictureLData);
		Image aboutImg = GuiController.getInstance().getImage("About.png"); //$NON-NLS-1$
		Rectangle r = aboutImg.getBounds();
		labelPicture.setSize(r.width, r.height);
		labelPicture.setImage(aboutImg);

		Label labelLogo = new Label(this, SWT.TRANSPARENT);
		GridData labelLogoLData = new GridData();
		labelLogoLData.grabExcessHorizontalSpace = true;
		labelLogoLData.horizontalAlignment = GridData.END;
		labelLogoLData.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;
		labelLogo.setLayoutData(labelLogoLData);
		Image logoImg = GuiController.getInstance().getImage("fullsync72.png"); //$NON-NLS-1$
		labelLogo.setImage(logoImg);
	}
}
