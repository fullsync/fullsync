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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.Util;
import net.sourceforge.fullsync.changelog.ChangeLogEntry;
import net.sourceforge.fullsync.changelog.ChangeLogLoader;

class ChangeLogBox extends StyledText {
	private final String lastFullSyncVersion;

	ChangeLogBox(Composite parent, String _lastFullSyncVersion, BackgroundExecutor backgroundExecutor) {
		super(parent, SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL);
		setAlwaysShowScrollBars(false);
		lastFullSyncVersion = _lastFullSyncVersion;
		backgroundExecutor.runAsync(this::calculateChangeLog, this::updateUI, this::changelogCalculationFailed);
	}

	private List<ChangeLogEntry> calculateChangeLog() throws Exception {
		var loader = new ChangeLogLoader();
		var directory = new File(Util.getInstalllocation(), "versions"); //$NON-NLS-1$
		return ChangeLogLoader.filterAfter(loader.load(directory, ".+\\.html"), lastFullSyncVersion); //$NON-NLS-1$
	}

	private void updateUI(List<ChangeLogEntry> changelog) {
		if (!isDisposed()) {
			var sw = new StringWriter();
			var dateFormat = DateTimeFormatter.ofPattern(Messages.getString("ChangeLogBox.ReleaseDatePattern")); //$NON-NLS-1$
			var releaseHeading = Messages.getString("ChangeLogBox.ReleaseHeading"); //$NON-NLS-1$
			var releaseBulletPoint = Messages.getString("ChangeLogBox.ReleaseBulletPoint"); //$NON-NLS-1$
			for (ChangeLogEntry entry : changelog) {
				entry.write(releaseHeading, releaseBulletPoint, sw, dateFormat);
			}
			sw.flush();
			setText(sw.toString());
		}
	}

	private void changelogCalculationFailed(Exception ex) {
		if (!isDisposed()) {
			setText(Messages.getString("ChangeLogBox.LoadErrorMessage")); //$NON-NLS-1$
			ExceptionHandler.reportException(ex);
		}
	}
}
