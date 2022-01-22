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
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

import com.google.common.eventbus.Subscribe;
import com.google.inject.assistedinject.Assisted;

import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.ProfileManager;
import net.sourceforge.fullsync.event.ProfileChanged;
import net.sourceforge.fullsync.event.ProfileListChanged;

class NiceListViewProfileListComposite extends ProfileListComposite {
	private final Map<String, NiceListViewItem> profilesToItems = new HashMap<>();
	private final ProfileListControlHandler handler;
	private final ProfileManager profileManager;
	private final NiceListView profileList;
	private final NiceListViewItemFactory niceListViewItemFactory;

	@Inject
	public NiceListViewProfileListComposite(@Assisted Composite parent, @Assisted ProfileListControlHandler handler,
		ProfileManager profileManager, ImageRepository imageRepository, NiceListViewItemFactory niceListViewItemFactory) {
		super(parent);
		this.handler = handler;
		this.profileManager = profileManager;
		this.niceListViewItemFactory = niceListViewItemFactory;
		var scrollPane = new ScrolledComposite(this, SWT.BORDER | SWT.V_SCROLL);
		profileList = new NiceListView(scrollPane);
		scrollPane.setExpandHorizontal(true);
		scrollPane.setExpandVertical(false);
		scrollPane.setAlwaysShowScrollBars(true);
		scrollPane.setContent(profileList);
		scrollPane.setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		scrollPane.getVerticalBar().setIncrement(20);
		profileList.pack();
		setLayout(new FillLayout());
		layout();
		populateProfileList();
	}

	private void populateProfileList() {
		if (!isDisposed()) {
			profilesToItems.clear();
			setItemsMenu(null);
			profileList.clear();
			for (Profile p : profileManager.getProfiles()) {
				NiceListViewItem item = null;
				try {
					item = niceListViewItemFactory.create(profileList, handler);
					item.setMenu(getMenu());
					item.update(p);
					profilesToItems.put(p.getId(), item);
				}
				catch (Exception e) {
					e.printStackTrace();
					if (null != item) {
						item.dispose();
					}
				}
			}
			profileList.pack();
		}
	}

	@Override
	public Profile getSelectedProfile() {
		var item = profileList.getSelectedItem();
		if (null != item) {
			return item.getProfile();
		}
		return null;
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

	@Subscribe
	private void profileListChanged(ProfileListChanged profileListChanged) {
		// use something like a de-bounced setTimeout
		getDisplay().syncExec(this::populateProfileList);
	}

	@Subscribe
	private void profileChanged(ProfileChanged pc) {
		getDisplay().syncExec(() -> {
			if (!isDisposed()) {
				var p = pc.getProfile();
				var item = profilesToItems.get(p.getId());
				if (null == item) {
					populateProfileList();
				}
				else {
					item.update(p);
				}
			}
		});
	}
}
