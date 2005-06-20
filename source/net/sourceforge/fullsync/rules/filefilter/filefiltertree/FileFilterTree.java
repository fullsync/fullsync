/*
 * Created on Jun 20, 2005
 */
package net.sourceforge.fullsync.rules.filefilter.filefiltertree;

import java.util.StringTokenizer;

import net.sourceforge.fullsync.rules.filefilter.FileFilter;

/**
 * @author Michele Aiello
 */
public class FileFilterTree {
		
	private FileFilterTreeItem root;
	// TODO is this the correct path separator?
	private String separator = "/";
	
	public FileFilterTree() {
		this.root = new FileFilterTreeItem();
	}

	public FileFilterTree(String separator) {
		this.root = new FileFilterTreeItem();
		this.separator = separator;
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
	
	
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(root.toString());
		
		return buffer.toString();
	}
	
}
