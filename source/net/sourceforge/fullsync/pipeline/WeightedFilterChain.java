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

import java.util.SortedSet;
import java.util.TreeSet;

import net.sourceforge.fullsync.FileFilter;
import net.sourceforge.fullsync.FileFilterChain;
import net.sourceforge.fullsync.Profile;
import net.sourceforge.fullsync.fs.File;

public class WeightedFilterChain implements FileFilterChain {
	private SortedSet<FileFilter> filters;

	public WeightedFilterChain(FileFilter ..._filters) {
		filters = new TreeSet<FileFilter>();
		for (FileFilter filter : _filters) {
			filters.add(filter);
		}
	}

	public static WeightedFilterChain fromProfile(Profile p) {
		//FIXME: add filters from the profile
		return new WeightedFilterChain(new DefaultIgnoreFilter());
	}

	@Override
	public void addFilter(FileFilter filter) {
		filters.add(filter);
	}

	@Override
	public boolean accept(File file) {
		for (FileFilter filter : filters) {
			switch(filter.getState(file)) {
				case ACCEPT:
					return true;
				case DROP:
					return false;
				case UNDECIDED:
					continue;
			}
		}
		return true;
	}
}
