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
package net.sourceforge.fullsync.pipeline;

import java.util.HashSet;

import net.sourceforge.fullsync.FileFilter;
import net.sourceforge.fullsync.fs.File;

public class DefaultIgnoreFilter extends AbstractFileFilter {
	private static HashSet<String> ignoredFiles;
	private static HashSet<String> ignoredSubdirs;

	static {
		// TODO: read them from a config file (probably user editable)
		ignoredFiles = new HashSet<String>();
		ignoredFiles.add("pagefile.sys");
		ignoredFiles.add("hiberfil.sys");
		ignoredSubdirs = new HashSet<String>();
		ignoredSubdirs.add("System Volume Information");
		ignoredSubdirs.add("lost+found");
	}

	public DefaultIgnoreFilter() {
	}

	@Override
	public FilterState getState(File file) {
		//TODO: add logging
		if (file.isDirectory()) {
			if (ignoredSubdirs.contains(file.getName())) {
				return FilterState.DROP;
			}
		}
		else {
			if (ignoredFiles.contains(file.getName())) {
				return FilterState.DROP;
			}
		}
		return FileFilter.FilterState.UNDECIDED;
	}
}
