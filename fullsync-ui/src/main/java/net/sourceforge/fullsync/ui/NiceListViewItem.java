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
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import net.sourceforge.fullsync.Profile;

public class NiceListViewItem extends Canvas implements Listener {
	private NiceListView list;

	private Label labelIcon;
	private Label labelCaption;
	private Label labelStatus;
	private Composite compositeContent;

	private ProfileListControlHandler handler;
	private Profile profile;

	private boolean mouseOver;
	private boolean selected;

	public NiceListViewItem(NiceListView parent) {
		super(parent, SWT.NULL);
		this.list = parent;

		GridData layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.horizontalAlignment = SWT.FILL;
		this.setLayoutData(layoutData);

		GridLayout thisLayout = new GridLayout();
		this.addListener(SWT.MouseEnter, this);
		this.addListener(SWT.MouseExit, this);
		this.addListener(SWT.MouseUp, this);
		this.addListener(SWT.MouseDown, this);
		this.addListener(SWT.MouseDoubleClick, this);
		this.addListener(SWT.KeyDown, this);
		this.addListener(SWT.FocusIn, this);
		this.addListener(SWT.FocusOut, this);
		this.setLayout(thisLayout);
		thisLayout.numColumns = 3;
		thisLayout.marginHeight = 3;
		thisLayout.marginWidth = 3;

		// icon
		labelIcon = new Label(this, SWT.TRANSPARENT);
		labelIcon.setSize(16, 16);
		GridData labelIconLData = new GridData();
		labelIconLData.grabExcessVerticalSpace = true;
		labelIconLData.verticalAlignment = GridData.BEGINNING;
		labelIconLData.widthHint = 16;
		labelIconLData.heightHint = 16;
		labelIcon.setLayoutData(labelIconLData);
		labelIcon.addListener(SWT.MouseEnter, this);
		labelIcon.addListener(SWT.MouseExit, this);
		labelIcon.addListener(SWT.MouseUp, this);
		labelIcon.addListener(SWT.MouseDown, this);
		labelIcon.addListener(SWT.MouseDoubleClick, this);

		// profile name
		labelCaption = new Label(this, SWT.TRANSPARENT);
		labelCaption.setFont(GuiController.getInstance().getFont("Tahoma", 9, 1)); //$NON-NLS-1$
		GridData labelCaptionLData = new GridData();
		labelCaptionLData.widthHint = -1;
		labelCaption.setLayoutData(labelCaptionLData);
		labelCaption.addListener(SWT.MouseEnter, this);
		labelCaption.addListener(SWT.MouseExit, this);
		labelCaption.addListener(SWT.MouseUp, this);
		labelCaption.addListener(SWT.MouseDown, this);
		labelCaption.addListener(SWT.MouseDoubleClick, this);

		labelStatus = new Label(this, SWT.TRANSPARENT);
		GridData labelStatusLData = new GridData();
		labelStatusLData.grabExcessHorizontalSpace = true;
		labelStatusLData.horizontalAlignment = SWT.FILL;
		labelStatusLData.horizontalIndent = 10;
		labelStatus.setLayoutData(labelStatusLData);
		labelStatus.addListener(SWT.MouseEnter, this);
		labelStatus.addListener(SWT.MouseExit, this);
		labelStatus.addListener(SWT.MouseUp, this);
		labelStatus.addListener(SWT.MouseDown, this);
		labelStatus.addListener(SWT.MouseDoubleClick, this);

		this.setBackground(parent.getColorDefault());
		this.setForeground(parent.getForeground());
		this.layout();
	}

	@Override
	public void handleEvent(final Event event) {
		switch (event.type) {
			case SWT.MouseEnter:
				mouseOver = true;
				updateBackground();
				break;
			case SWT.MouseExit:
				mouseOver = false;
				updateBackground();
				break;
			case SWT.MouseDown:
				list.setSelected(NiceListViewItem.this);
				break;
			case SWT.MouseUp:
				if (event.button == 3) {
					getMenu().setVisible(true);
				}
				break;
			case SWT.MouseDoubleClick:
				handler.editProfile(profile);
				break;
			case SWT.KeyDown:
				list.keyPressed(new KeyEvent(event));
				break;
			default:
				break;
		}
	}

	@Override
	public void setBackground(Color color) {
		super.setBackground(color);

		for (Control element : this.getChildren()) {
			element.setBackground(color);
		}
	}

	@Override
	public void setForeground(Color color) {
		super.setForeground(color);

		for (Control element : this.getChildren()) {
			element.setForeground(color);
		}
	}

	public void updateBackground() {
		if (selected) {
			setBackground(list.getColorSelected());
		}
		else {
			setBackground(mouseOver ? list.getColorHover() : list.getColorDefault());
		}
		setForeground(selected ? list.getColorSelectedForegroud() : list.getColorForeground());
	}

	public void setImage(Image image) {
		labelIcon.setImage(image);
	}

	public void setText(String text) {
		labelCaption.setText(text);
		labelCaption.pack();
		layout();
	}

	public void setStatusText(String status) {
		if ((null == status) || status.isEmpty()) {
			labelStatus.setText(""); //$NON-NLS-1$
		}
		else {
			labelStatus.setText("(" + status + ")"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		labelStatus.pack();
	}

	public void setSelected(boolean selected) {
		if (selected) {
			forceFocus();
			compositeContent.setVisible(true);
			Point size = compositeContent.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			((GridData) compositeContent.getLayoutData()).widthHint = size.x;
			((GridData) compositeContent.getLayoutData()).heightHint = size.y;
		}
		else {
			compositeContent.setVisible(false);
			((GridData) compositeContent.getLayoutData()).widthHint = 0;
			((GridData) compositeContent.getLayoutData()).heightHint = 0;
		}
		this.selected = selected;
		updateBackground();
		this.layout();
	}

	public Composite getContent() {
		return compositeContent;
	}

	/**
	 * @param content
	 *            The composite that will be shown when the item is
	 *            selectes. ATTENTION: this composite should not have
	 *            any more composites, as the background color and
	 *            mouselisteners are set only on all direct children
	 *            of this composite
	 */
	public void setContent(Composite content) {
		this.compositeContent = content;
		GridData compositeContentLData = new GridData();
		compositeContentLData.horizontalSpan = 3;
		compositeContentLData.horizontalIndent = labelIcon.getBounds().width + 4;
		compositeContentLData.grabExcessHorizontalSpace = true;
		compositeContentLData.horizontalAlignment = SWT.FILL;
		compositeContentLData.heightHint = 0;
		compositeContentLData.widthHint = 0;
		compositeContent.setLayoutData(compositeContentLData);
		compositeContent.setVisible(false);
		compositeContent.addListener(SWT.MouseDoubleClick, this);
		compositeContent.addListener(SWT.MouseUp, this);
		compositeContent.addListener(SWT.MouseDown, this);
		Control[] children = compositeContent.getChildren();
		for (Control element : children) {
			element.addListener(SWT.MouseDoubleClick, this);
			element.addListener(SWT.MouseUp, this);
			element.addListener(SWT.MouseDown, this);
		}
	}

	public ProfileListControlHandler getHandler() {
		return handler;
	}

	public void setHandler(ProfileListControlHandler handler) {
		this.handler = handler;
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}
}
