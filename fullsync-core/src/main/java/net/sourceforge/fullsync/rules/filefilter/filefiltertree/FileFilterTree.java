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

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Stream;

import net.sourceforge.fullsync.rules.filefilter.FileFilter;

public class FileFilterTree {
	private static final String FILE_SEPARATOR = "/"; //$NON-NLS-1$

	private final FileFilterTreeItem root = new FileFilterTreeItem();
	private final Map<String, FileFilter> itemsMap = new HashMap<>();

	public FileFilterTree(Map<String, FileFilter> filters) {
		for (Map.Entry<String, FileFilter> e : filters.entrySet()) {
			addFileFilter(e.getKey(), e.getValue());
		}
	}

	private void addFileFilter(String key, FileFilter filter) {
		StringTokenizer tokenizer = new StringTokenizer(key, FILE_SEPARATOR);
		FileFilterTreeItem item = root;
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			FileFilterTreeItem children = item.getChildren(token);
			if (null == children) {
				children = new FileFilterTreeItem();
			}
			item.addChildren(token, children);
			item = children;
		}
		item.setFilter(filter);
		itemsMap.put(key, filter);
	}

	public FileFilter getFilter(String key) {
		FileFilter filter = null;
		FileFilter parentFilter = null;
		StringTokenizer tokenizer = new StringTokenizer(key, FILE_SEPARATOR);
		FileFilterTreeItem item = root;
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			parentFilter = filter;

			FileFilterTreeItem children = item.getChildren(token);
			if (null == children) {
				return parentFilter;
			}

			FileFilter childFilter = children.getFilter();
			if (null != childFilter) {
				filter = childFilter;
			}
			item = children;
		}

		return parentFilter;
	}

	public Stream<Map.Entry<String, FileFilter>> stream() {
		return itemsMap.entrySet().stream();
	}

	@Override
	public String toString() {
		return root.toString();
	}
}
