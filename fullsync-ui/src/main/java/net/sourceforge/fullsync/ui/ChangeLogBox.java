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

import java.io.File;
import java.io.StringWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;

import net.sourceforge.fullsync.Util;
import net.sourceforge.fullsync.changelog.ChangeLogEntry;
import net.sourceforge.fullsync.changelog.ChangeLogLoader;

public class ChangeLogBox extends StyledText implements AsyncUIUpdate {
	private String lastFullSyncVersion;
	private List<ChangeLogEntry> changelog;

	public ChangeLogBox(Composite parent, String _lastFullSyncVersion, ScheduledExecutorService scheduledExecutorService) {
		super(parent, SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL);
		setAlwaysShowScrollBars(false);
		lastFullSyncVersion = _lastFullSyncVersion;
		scheduledExecutorService.submit(ExecuteBackgroundJob.create(this, getDisplay()));
	}

	@Override
	public void execute() throws Exception {
		ChangeLogLoader loader = new ChangeLogLoader();
		File directory = new File(Util.getInstalllocation(), "versions"); //$NON-NLS-1$
		changelog = ChangeLogLoader.filterAfter(loader.load(directory, ".+\\.html"), lastFullSyncVersion); //$NON-NLS-1$
	}

	@Override
	public void updateUI(boolean succeeded) {
		if (!isDisposed()) {
			if (succeeded) {
				StringWriter sw = new StringWriter();
				DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MMMM d, uuuu");
				for (ChangeLogEntry entry : changelog) {
					entry.write("FullSync %s released on %s", " - %s", sw, dateFormat);
				}
				sw.flush();
				setText(sw.toString());
			}
			else {
				setText("Failed to load Changelogs.");
			}
		}
	}
}
