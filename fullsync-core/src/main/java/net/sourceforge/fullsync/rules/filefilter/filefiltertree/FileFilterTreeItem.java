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
package net.sourceforge.fullsync.rules.filefilter.filefiltertree;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sourceforge.fullsync.rules.filefilter.FileFilter;

class FileFilterTreeItem implements Serializable {

	private static final long serialVersionUID = 2L;

	private FileFilter filter;

	private Map<String, FileFilterTreeItem> childrenMap = new HashMap<>();

	public void setFilter(final FileFilter filter) {
		this.filter = filter;
	}

	public FileFilter getFilter() {
		return this.filter;
	}

	public void addChildren(final String key, final FileFilterTreeItem item) {
		childrenMap.put(key, item);
	}

	public FileFilterTreeItem getChildren(final String key) {
		return childrenMap.get(key);
	}

	public void removeChildren(final String key) {
		childrenMap.remove(key);
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("[ ");
		for (Entry<String, FileFilterTreeItem> entry : childrenMap.entrySet()) {
			buffer.append(entry.getKey());
			buffer.append("->");
			buffer.append(entry.getValue());
			buffer.append(" ");
		}
		buffer.append("]");

		return buffer.toString();
	}
}
