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

import jakarta.inject.Inject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.google.inject.assistedinject.Assisted;

import net.sourceforge.fullsync.Profile;

class NiceListViewItem extends Composite implements Listener {
	private final NiceListView list;
	private final ImageRepository imageRepository;
	private final Label labelIcon;
	private final Label labelCaption;
	private final Label labelStatus;
	private final Composite compositeContent;
	private final Label labelSource;
	private final Label labelDestination;
	private final Label labelLastUpdate;
	private final Label labelNextUpdate;
	private final ProfileListControlHandler handler;
	private Profile profile;
	private boolean mouseOver;
	private boolean selected;

	@Inject
	NiceListViewItem(ImageRepository imageRepository, FontRepository fontRepository, @Assisted NiceListView parent,
		@Assisted ProfileListControlHandler handler) {
		super(parent, SWT.NULL);
		this.imageRepository = imageRepository;
		this.list = parent;
		this.handler = handler;
		this.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		var thisLayout = new GridLayout(3, false);
		this.addListener(SWT.MouseEnter, this);
		this.addListener(SWT.MouseExit, this);
		this.addListener(SWT.MouseUp, this);
		this.addListener(SWT.MouseDown, this);
		this.addListener(SWT.MouseDoubleClick, this);
		this.addListener(SWT.KeyDown, this);
		this.addListener(SWT.FocusIn, this);
		this.addListener(SWT.FocusOut, this);
		this.setLayout(thisLayout);
		thisLayout.marginHeight = 3;
		thisLayout.marginWidth = 3;

		// icon
		labelIcon = new Label(this, SWT.TRANSPARENT);
		labelIcon.setSize(16, 16);
		var labelIconLData = new GridData(GridData.CENTER, GridData.BEGINNING, false, true);
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
		labelCaption.setFont(fontRepository.getFont("Tahoma", 9, 1)); //$NON-NLS-1$
		var labelCaptionLData = new GridData();
		labelCaptionLData.widthHint = -1;
		labelCaption.setLayoutData(labelCaptionLData);
		labelCaption.addListener(SWT.MouseEnter, this);
		labelCaption.addListener(SWT.MouseExit, this);
		labelCaption.addListener(SWT.MouseUp, this);
		labelCaption.addListener(SWT.MouseDown, this);
		labelCaption.addListener(SWT.MouseDoubleClick, this);

		labelStatus = new Label(this, SWT.TRANSPARENT);
		var labelStatusLData = new GridData(GridData.FILL, GridData.CENTER, true, false);
		labelStatusLData.horizontalIndent = 10;
		labelStatus.setLayoutData(labelStatusLData);
		labelStatus.addListener(SWT.MouseEnter, this);
		labelStatus.addListener(SWT.MouseExit, this);
		labelStatus.addListener(SWT.MouseUp, this);
		labelStatus.addListener(SWT.MouseDown, this);
		labelStatus.addListener(SWT.MouseDoubleClick, this);

		this.setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		this.setForeground(parent.getForeground());
		this.layout();
		compositeContent = new Composite(this, SWT.NULL);
		compositeContent.setBackgroundMode(SWT.INHERIT_FORCE);

		var layout = new GridLayout(2, false);
		layout.marginHeight = 1;
		layout.marginWidth = 1;
		layout.verticalSpacing = 2;
		layout.horizontalSpacing = 2;
		compositeContent.setLayout(layout);

		var cSourceDestination = new Composite(compositeContent, SWT.FILL);
		var sourceDestinationLayout = new GridLayout(2, false);
		sourceDestinationLayout.marginHeight = 0;
		sourceDestinationLayout.marginWidth = 0;
		cSourceDestination.setLayout(sourceDestinationLayout);
		cSourceDestination.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		cSourceDestination.setEnabled(false); // passes any events up to the parent

		// source label
		new Label(cSourceDestination, SWT.NULL).setText(Messages.getString("NiceListViewProfileListComposite.Source")); //$NON-NLS-1$
		labelSource = new Label(cSourceDestination, SWT.NULL);
		labelSource.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		// destination label
		new Label(cSourceDestination, SWT.NULL).setText(Messages.getString("NiceListViewProfileListComposite.Destination")); //$NON-NLS-1$
		labelDestination = new Label(cSourceDestination, SWT.NULL);
		labelDestination.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		// last / next update
		var cUpdate = new Composite(compositeContent, SWT.FILL);
		var updateLayout = new GridLayout(2, false);
		updateLayout.marginHeight = 0;
		updateLayout.marginWidth = 0;
		cUpdate.setLayout(updateLayout);
		cUpdate.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		cUpdate.setEnabled(false); // passes any events up to the parent

		// last update
		new Label(cUpdate, SWT.NULL).setText(Messages.getString("NiceListViewProfileListComposite.LastUpdate")); //$NON-NLS-1$
		labelLastUpdate = new Label(cUpdate, SWT.NULL);
		labelLastUpdate.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		// next update
		new Label(cUpdate, SWT.NULL).setText(Messages.getString("NiceListViewProfileListComposite.NextUpdate")); //$NON-NLS-1$
		labelNextUpdate = new Label(cUpdate, SWT.NULL);
		labelNextUpdate.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		// buttons
		var toolbar = new ToolBar(compositeContent, SWT.FLAT);
		toolbar.setLayoutData(new GridData(GridData.END, GridData.CENTER, true, false, 1, 2));

		var runProfile = new ToolItem(toolbar, SWT.PUSH);
		runProfile.setImage(imageRepository.getImage("Profile_Run.png")); //$NON-NLS-1$
		runProfile.addListener(SWT.Selection, e -> handler.runProfile(getProfile(), true));

		var runProfileNonInteractive = new ToolItem(toolbar, SWT.PUSH);
		runProfileNonInteractive.setImage(imageRepository.getImage("Profile_Run_Non_Inter.png")); //$NON-NLS-1$
		runProfileNonInteractive.addListener(SWT.Selection, e -> handler.runProfile(getProfile(), false));

		var editProfile = new ToolItem(toolbar, SWT.PUSH);
		editProfile.setImage(imageRepository.getImage("Profile_Edit.png")); //$NON-NLS-1$
		editProfile.addListener(SWT.Selection, e -> handler.editProfile(getProfile()));

		var deleteProfile = new ToolItem(toolbar, SWT.PUSH);
		deleteProfile.setImage(imageRepository.getImage("Profile_Delete.png")); //$NON-NLS-1$
		deleteProfile.addListener(SWT.Selection, e -> handler.deleteProfile(getProfile()));
		var compositeContentLData = new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1);
		compositeContentLData.horizontalIndent = labelIcon.getBounds().width + 4;
		compositeContentLData.heightHint = 0;
		compositeContentLData.widthHint = 0;
		compositeContent.setLayoutData(compositeContentLData);
		compositeContent.setVisible(false);
		compositeContent.addListener(SWT.MouseDoubleClick, this);
		compositeContent.addListener(SWT.MouseUp, this);
		compositeContent.addListener(SWT.MouseDown, this);
		var children = compositeContent.getChildren();
		for (Control element : children) {
			element.addListener(SWT.MouseDoubleClick, this);
			element.addListener(SWT.MouseUp, this);
			element.addListener(SWT.MouseDown, this);
		}
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

	private void updateBackground() {
		var display = getDisplay();
		if (selected) {
			setBackground(display.getSystemColor(SWT.COLOR_LIST_SELECTION));
			setForeground(display.getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT));
		}
		else {
			if (mouseOver) {
				setBackground(display.getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
			}
			else {
				setBackground(display.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
			}
			setForeground(display.getSystemColor(SWT.COLOR_LIST_FOREGROUND));
		}
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
			labelStatus.setText(String.format("(%s)", status)); //$NON-NLS-1$
		}
		labelStatus.pack();
	}

	public void setSelected(boolean selected) {
		if (selected) {
			forceFocus();
			compositeContent.setVisible(true);
			var size = compositeContent.computeSize(SWT.DEFAULT, SWT.DEFAULT);
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

	public Profile getProfile() {
		return profile;
	}

	void update(Profile profile) {
		this.profile = profile;
		var isError = profile.getLastErrorLevel() > 0;
		var isScheduled = profile.isSchedulingEnabled() && (null != profile.getSchedule());
		if (isScheduled) {
			setImage(isError
				? imageRepository.getImage("Profile_Default_Error_Scheduled.png") //$NON-NLS-1$
				: imageRepository.getImage("Profile_Default_Scheduled.png")); //$NON-NLS-1$
		}
		else {
			setImage(isError ? imageRepository.getImage("Profile_Default_Error.png") : imageRepository.getImage("Profile_Default.png")); //$NON-NLS-1$ //$NON-NLS-2$
		}

		setText(profile.getName());

		if (isError) {
			setStatusText(profile.getLastErrorString());
		}
		else {
			var desc = profile.getDescription();
			if ((null != desc) && !desc.isEmpty()) {
				setStatusText(desc);
			}
			else if (isScheduled) {
				setStatusText(profile.getNextUpdateText());
			}
			else {
				setStatusText(""); //$NON-NLS-1$
			}
		}
		var src = profile.getSource();
		labelSource.setText(null != src ? src.getDisplayPath() : ""); //$NON-NLS-1$
		var dst = profile.getDestination();
		labelDestination.setText(null != dst ? dst.getDisplayPath() : ""); //$NON-NLS-1$
		labelLastUpdate.setText(profile.getLastUpdateText());
		labelNextUpdate.setText(profile.getNextUpdateText());
		compositeContent.layout();
	}
}
