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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.google.common.eventbus.Subscribe;
import com.google.inject.assistedinject.Assisted;

import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.ProfileManager;
import net.sourceforge.fullsync.event.ProfileChanged;
import net.sourceforge.fullsync.event.ProfileListChanged;

class ListViewProfileListComposite extends ProfileListComposite {
	private final ProfileManager profileManager;
	private final Table tableProfiles;
	private final TableColumn tableColumnName;
	private final TableColumn tableColumnLastUpdate;
	private final TableColumn tableColumnNextUpdate;
	private final TableColumn tableColumnSource;
	private final TableColumn tableColumnDestination;

	@Inject
	public ListViewProfileListComposite(@Assisted Composite parent, @Assisted ProfileListControlHandler handler,
		ProfileManager profileManager) {
		super(parent);
		this.profileManager = profileManager;

		tableProfiles = new Table(this, SWT.FULL_SELECTION | SWT.BORDER);

		tableColumnName = new TableColumn(tableProfiles, SWT.NONE);
		tableColumnName.setText(Messages.getString("ListViewProfileListComposite.Name")); //$NON-NLS-1$
		tableColumnName.setWidth(100);

		tableColumnLastUpdate = new TableColumn(tableProfiles, SWT.NONE);
		tableColumnLastUpdate.setText(Messages.getString("ListViewProfileListComposite.LastUpdate")); //$NON-NLS-1$
		tableColumnLastUpdate.setWidth(100);

		tableColumnNextUpdate = new TableColumn(tableProfiles, SWT.NONE);
		tableColumnNextUpdate.setText(Messages.getString("ListViewProfileListComposite.NextUpdate")); //$NON-NLS-1$
		tableColumnNextUpdate.setWidth(100);

		tableColumnSource = new TableColumn(tableProfiles, SWT.NONE);
		tableColumnSource.setText(Messages.getString("ListViewProfileListComposite.Source")); //$NON-NLS-1$
		tableColumnSource.setWidth(200);

		tableColumnDestination = new TableColumn(tableProfiles, SWT.NONE);
		tableColumnDestination.setText(Messages.getString("ListViewProfileListComposite.Destination")); //$NON-NLS-1$
		tableColumnDestination.setWidth(200);

		tableProfiles.setHeaderVisible(true);
		tableProfiles.setLinesVisible(false);

		setLayout(new FillLayout());
		layout();
		populateProfileList();
	}

	private void populateProfileList() {
		if (!isDisposed()) {
			tableProfiles.clearAll();
			tableProfiles.setItemCount(0);
			for (Profile p : profileManager.getProfiles()) {
				var cells = new String[5];
				cells[0] = p.getName();
				cells[1] = p.getLastUpdateText();
				cells[2] = p.getNextUpdateText();
				cells[3] = p.getSource().toString();
				cells[4] = p.getDestination().toString();
				var item = new TableItem(tableProfiles, SWT.NULL);
				item.setText(cells);
				item.setData(p);
			}
			tableColumnName.pack();
			tableColumnLastUpdate.pack();
			tableColumnNextUpdate.pack();
			tableColumnSource.pack();
			tableColumnDestination.pack();
		}
	}

	@Override
	public Profile getSelectedProfile() {
		Profile p = null;
		var sel = tableProfiles.getSelection();
		if (sel.length > 0) {
			p = (Profile) sel[0].getData();
		}
		return p;
	}

	@Override
	public void setMenu(Menu menu) {
		tableProfiles.setMenu(menu);
	}

	@Override
	public Menu getMenu() {
		return tableProfiles.getMenu();
	}

	@Subscribe
	private void profileChanged(ProfileChanged p) {
		getDisplay().asyncExec(this::populateProfileList);
	}

	@Subscribe
	private void profileListChanged(ProfileListChanged event) {
		getDisplay().asyncExec(this::populateProfileList);
	}
}
