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
package net.sourceforge.fullsync.ui.profiledetails;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.widgets.DirectoryDialog;

import net.sourceforge.fullsync.ConnectionDescription;

class FileSpecificComposite extends ProtocolSpecificComposite {
	@Override
	public void setConnectionDescription(ConnectionDescription connection) {
		super.setConnectionDescription(connection);
		if (null != connection) {
			textPath.setText(connection.getDisplayPath());
		}
		else {
			textPath.setText(""); //$NON-NLS-1$
		}
	}

	@Override
	public void onBrowse() {
		var desc = getConnectionDescription().build();
		var d = new DirectoryDialog(m_parent.getShell());
		if (null != desc) {
			d.setFilterPath(desc.getPath());
		}
		var dir = d.open();
		if (null != dir) {
			var f = new File(dir);
			try {
				setPath(f.getCanonicalPath());
			}
			catch (IOException e) {
				setPath(""); //$NON-NLS-1$
				e.printStackTrace();
			}
		}
	}
}
