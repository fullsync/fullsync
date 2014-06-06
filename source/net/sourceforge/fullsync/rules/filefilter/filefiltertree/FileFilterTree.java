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
import java.util.StringTokenizer;

import net.sourceforge.fullsync.rules.filefilter.FileFilter;

public class FileFilterTree implements Serializable {
	private static final long serialVersionUID = 2L;

	private FileFilterTreeItem root;
	private HashMap<String, FileFilter> itemsMap;

	// TODO is this the correct path separator?
	private String separator = "/";

	public FileFilterTree() {
		this.root = new FileFilterTreeItem();
		this.itemsMap = new HashMap<String, FileFilter>();
	}

	public void addFileFilter(String key, FileFilter filter) {
		StringTokenizer tokenizer = new StringTokenizer(key, separator);
		FileFilterTreeItem item = root;
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			FileFilterTreeItem children = item.getChildren(token);
			if (children == null) {
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
		StringTokenizer tokenizer = new StringTokenizer(key, separator);
		FileFilterTreeItem item = root;
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			parentFilter = filter;

			FileFilterTreeItem children = item.getChildren(token);
			if (children == null) {
				return parentFilter;
			}

			FileFilter childFilter = children.getFilter();
			if (childFilter != null) {
				filter = childFilter;
			}
			item = children;
		}

		return parentFilter;
	}

	public HashMap<String, FileFilter> getItemsMap() {
		return itemsMap;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();

		buffer.append(root.toString());

		return buffer.toString();
	}

}
