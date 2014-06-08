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

import java.util.HashMap;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.ProfileListChangeListener;
import net.sourceforge.fullsync.ProfileManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class NiceListViewProfileListComposite extends ProfileListComposite implements ProfileListChangeListener {
	class ContentComposite extends Composite {
		private Profile profile;

		private Label lSource;
		private Label lDestination;
		private Label lLastUpdate;
		private Label lNextUpdate;

		public ContentComposite(Composite parent, int style) {
			super(parent, style);
			GridLayout layout = new GridLayout(2, false);
			layout.marginHeight = 1;
			layout.marginWidth = 1;
			layout.verticalSpacing = 2;
			layout.horizontalSpacing = 2;
			this.setLayout(layout);

			Composite cSourceDestination = new Composite(this, SWT.FILL);
			GridLayout sourceDestinationLayout = new GridLayout(2, false);
			sourceDestinationLayout.marginHeight = 0;
			sourceDestinationLayout.marginWidth = 0;
			cSourceDestination.setLayout(sourceDestinationLayout);
			GridData cSourceDestinationData = new GridData(SWT.FILL, SWT.CENTER, true, false);
			cSourceDestinationData.horizontalSpan = 2;
			cSourceDestination.setLayoutData(cSourceDestinationData);
			cSourceDestination.setBackgroundMode(SWT.INHERIT_DEFAULT);
			cSourceDestination.setEnabled(false); // passes any events up to the parent

			// source label
			Label labelSource = new Label(cSourceDestination, SWT.NULL);
			labelSource.setText(Messages.getString("NiceListViewProfileListComposite.Source") + ":"); //$NON-NLS-1$ //$NON-NLS-2$
			lSource = new Label(cSourceDestination, SWT.NULL);
			GridData lSourceData = new GridData(SWT.FILL, SWT.CENTER, true, false);
			lSource.setLayoutData(lSourceData);

			// destination label
			Label labelDestination = new Label(cSourceDestination, SWT.NULL);
			labelDestination.setText(Messages.getString("NiceListViewProfileListComposite.Destination") + ":"); //$NON-NLS-1$ //$NON-NLS-2$
			lDestination = new Label(cSourceDestination, SWT.NULL);
			GridData lDestinationData = new GridData(SWT.FILL, SWT.CENTER, true, false);
			lDestination.setLayoutData(lDestinationData);

			// last / next update
			Composite cUpdate = new Composite(this, SWT.FILL);
			GridLayout updateLayout = new GridLayout(2, false);
			updateLayout.marginHeight = 0;
			updateLayout.marginWidth = 0;
			cUpdate.setLayout(updateLayout);
			GridData cUpdateData = new GridData(SWT.FILL, SWT.CENTER, true, false);
			cUpdateData.grabExcessHorizontalSpace = true;
			cUpdateData.horizontalAlignment = SWT.FILL;
			cUpdate.setLayoutData(cUpdateData);
			cUpdate.setBackgroundMode(SWT.INHERIT_DEFAULT);
			cUpdate.setEnabled(false); // passes any events up to the parent

			// last update
			Label labelLastUpdate = new Label(cUpdate, SWT.NULL);
			labelLastUpdate.setText(Messages.getString("NiceListViewProfileListComposite.LastUpdate") + ":"); //$NON-NLS-1$ //$NON-NLS-2$
			lLastUpdate = new Label(cUpdate, SWT.NULL);
			lLastUpdate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

			// next update
			Label labelNextUpdate = new Label(cUpdate, SWT.NULL);
			labelNextUpdate.setText(Messages.getString("NiceListViewProfileListComposite.NextUpdate") + ":"); //$NON-NLS-1$ //$NON-NLS-2$
			lNextUpdate = new Label(cUpdate, SWT.NULL);
			lNextUpdate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

			// buttons
			ToolBar toolbar = new ToolBar(this, SWT.FLAT);
			GridData d = new GridData(SWT.END, SWT.CENTER, true, false);
			d.verticalSpan = 2;
			toolbar.setLayoutData(d);

			ToolItem t = new ToolItem(toolbar, SWT.PUSH);
			t.setImage(imageRun);
			t.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent e) {
					handler.runProfile(profile, true);
				}
			});

			t = new ToolItem(toolbar, SWT.PUSH);
			t.setImage(imageRunNonInter);
			t.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent e) {
					handler.runProfile(profile, false);
				}
			});

			t = new ToolItem(toolbar, SWT.PUSH);
			t.setImage(imageEdit);
			t.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent e) {
					handler.editProfile(profile);
				}
			});

			t = new ToolItem(toolbar, SWT.PUSH);
			t.setImage(imageDelete);
			t.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent e) {
					handler.deleteProfile(profile);
				}
			});
		}

		public void updateComponent() {
			ConnectionDescription desc = profile.getSource();
			lSource.setText((null != desc) ? desc.getDisplayPath() : "null"); //TODO: null is not really user friendly
			desc = profile.getDestination();
			lDestination.setText((null != desc) ? desc.getDisplayPath() : "null"); //TODO: null is not really user friendly
			lLastUpdate.setText(profile.getLastUpdateText());
			lNextUpdate.setText(profile.getNextUpdateText());
			layout();
		}

		public void setProfile(Profile profile) {
			this.profile = profile;
			updateComponent();
		}

		public Profile getProfile() {
			return profile;
		}

		@Override
		public void setBackground(Color color) {
			super.setBackground(color);
			for (Control c : this.getChildren()) {
				if (c instanceof Composite) {
					for (Control child : ((Composite)c).getChildren()) {
						child.setBackground(color);
					}
				}
				c.setBackground(color);
			}
		}

		@Override
		public void setForeground(Color color) {
			super.setForeground(color);
			for (Control c : this.getChildren()) {
				if (c instanceof Composite) {
					for (Control child : ((Composite)c).getChildren()) {
						child.setForeground(color);
					}
				}
				c.setForeground(color);
			}
		}
	}

	private ScrolledComposite scrollPane;
	private NiceListView profileList;
	private HashMap<Profile, NiceListViewItem> profilesToItems;

	private ProfileManager profileManager;
	private ProfileListControlHandler handler;

	private Image imageProfileDefault;
	private Image imageProfileScheduled;
	private Image imageProfileError;
	private Image imageProfileErrorScheduled;
	private Image imageRun;
	private Image imageRunNonInter;
	private Image imageEdit;
	private Image imageDelete;

	public NiceListViewProfileListComposite(Composite parent, int style) {
		super(parent, style);
		loadImages();
		scrollPane = new ScrolledComposite(this, SWT.BORDER | SWT.V_SCROLL);
		profileList = new NiceListView(scrollPane, SWT.TRANSPARENT);
		scrollPane.setExpandHorizontal(true);
		scrollPane.setExpandVertical(false);
		scrollPane.setAlwaysShowScrollBars(true);
		scrollPane.setContent(profileList);
		scrollPane.setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		scrollPane.getVerticalBar().setIncrement(20);
		profileList.pack();
		this.setLayout(new FillLayout());
		this.layout();
	}

	private void loadImages() {
		GuiController gc = GuiController.getInstance();
		imageProfileDefault = gc.getImage("Profile_Default.png"); //$NON-NLS-1$
		imageProfileScheduled = gc.getImage("Profile_Default_Scheduled.png"); //$NON-NLS-1$
		imageProfileError = gc.getImage("Profile_Default_Error.png"); //$NON-NLS-1$
		imageProfileErrorScheduled = gc.getImage("Profile_Default_Error_Scheduled.png"); //$NON-NLS-1$

		imageRun = gc.getImage("Profile_Run.png"); //$NON-NLS-1$
		imageRunNonInter = gc.getImage("Profile_Run_Non_Inter.png"); //$NON-NLS-1$
		imageEdit = gc.getImage("Profile_Edit.png"); //$NON-NLS-1$
		imageDelete = gc.getImage("Profile_Delete.png"); //$NON-NLS-1$
	}

	@Override
	public void dispose() {
		profileManager.removeProfilesChangeListener(this);

		super.dispose();
	}

	private void updateItem(NiceListViewItem item, Profile profile) {
		if (profile.isEnabled() && (profile.getSchedule() != null)) {
			// scheduled
			if (profile.getLastErrorLevel() > 0) {
				item.setImage(imageProfileErrorScheduled);
			}
			else {
				item.setImage(imageProfileScheduled);
			}
		}
		else {
			// not scheduled
			if (profile.getLastErrorLevel() > 0) {
				item.setImage(imageProfileError);
			}
			else {
				item.setImage(imageProfileDefault);
			}
		}

		item.setText(profile.getName());

		if (profile.getLastErrorLevel() > 0) {
			item.setStatusText(profile.getLastErrorString());
		}
		else {
			String desc = profile.getDescription();
			if ((desc != null) && !"".equals(desc)) {
				item.setStatusText(desc);
			}
			else if (profile.isEnabled() && (profile.getSchedule() != null)) {
				item.setStatusText(profile.getNextUpdateText());
			}
			else {
				item.setStatusText(""); //$NON-NLS-1$
			}
		}
	}

	private void populateProfileList() {
		if (getProfileManager() != null) {
			profilesToItems = new HashMap<Profile, NiceListViewItem>();
			setItemsMenu(null);
			profileList.clear();
			for (Profile p : getProfileManager().getProfiles()) {
				NiceListViewItem item = null;
				try {
					item = new NiceListViewItem(profileList, SWT.NULL);
					ContentComposite content = new ContentComposite(item, SWT.NULL);
					content.setProfile(p);
					item.setContent(content);
					item.setMenu(getMenu());
					item.setHandler(handler);
					item.setProfile(p);
					updateItem(item, p);

					profilesToItems.put(p, item);
				}
				catch (Exception e) {
					e.printStackTrace();
					if (item != null) {
						item.dispose();
					}
				}
			}
			profileList.pack();
		}
	}

	@Override
	public Profile getSelectedProfile() {
		ContentComposite content = (ContentComposite) profileList.getSelectedContent();
		if (content != null) {
			return content.getProfile();
		}
		else {
			return null;
		}
	}

	@Override
	public void setProfileManager(ProfileManager profileManager) {
		if (this.profileManager != null) {
			profileManager.removeProfilesChangeListener(this);

		}
		this.profileManager = profileManager;
		if (this.profileManager != null) {
			profileManager.addProfilesChangeListener(this);
		}
		populateProfileList();
	}

	@Override
	public ProfileManager getProfileManager() {
		return profileManager;
	}

	@Override
	public ProfileListControlHandler getHandler() {
		return handler;
	}

	@Override
	public void setHandler(ProfileListControlHandler handler) {
		this.handler = handler;
	}

	public void setItemsMenu(Menu menu) {
		for (Control item : profileList.getChildren()) {
			item.setMenu(menu);
		}
	}

	@Override
	public void setMenu(Menu menu) {
		setItemsMenu(menu);
		super.setMenu(menu);
	}

	@Override
	public void profileListChanged() {
		// use something like a de-bounced setTimeout
		getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				populateProfileList();
			}
		});
	}

	@Override
	public void profileChanged(final Profile p) {
		getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				Object composite = profilesToItems.get(p);
				if (composite == null) {
					populateProfileList();
				}
				else {
					NiceListViewItem item = (NiceListViewItem) composite;
					ContentComposite content = (ContentComposite) item.getContent();
					updateItem(item, content.getProfile());
					content.updateComponent();
				}
			}
		});
	}
}
