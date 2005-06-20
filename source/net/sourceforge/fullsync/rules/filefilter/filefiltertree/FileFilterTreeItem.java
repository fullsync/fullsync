/*
 * Created on Jun 20, 2005
 */
package net.sourceforge.fullsync.rules.filefilter.filefiltertree;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.sourceforge.fullsync.rules.filefilter.FileFilter;

/**
 * @author Michele Aiello
 */
class FileFilterTreeItem {

	private FileFilter filter;
	
	private HashMap childrenMap;
	
	public FileFilterTreeItem() {
		this.filter = null;
		this.childrenMap = new HashMap();
	}

	public FileFilterTreeItem(FileFilter filter) {
		this.filter = filter;
		this.childrenMap = new HashMap();
	}
	
	public void setFilter(FileFilter filter) {
		this.filter = filter;
	}
	
	public FileFilter getFilter() {
		return this.filter;
	}
	
	public void addChildren(String key, FileFilterTreeItem item) {
		childrenMap.put(key, item);
	}
	
	public FileFilterTreeItem getChildren(String key) {
		return (FileFilterTreeItem)childrenMap.get(key);
	}
	
	public void removeChildren(String key) {
		childrenMap.remove(key);
	}
	
	public String toString() {
		// TODO not implemented yet!
		StringBuffer buffer = new StringBuffer();
		
		Set entrySet = childrenMap.entrySet();
		Iterator it = entrySet.iterator();
		buffer.append("[ ");
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry)it.next();
			buffer.append(entry.getKey());
			buffer.append("->");
			buffer.append(entry.getValue());
			buffer.append(" ");
		}
		buffer.append("]");
		
		return buffer.toString();
	}
	
}
