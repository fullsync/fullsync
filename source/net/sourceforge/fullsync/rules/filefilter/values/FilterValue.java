/*
 * Created on Jun 6, 2005
 */
package net.sourceforge.fullsync.rules.filefilter.values;

import net.sourceforge.fullsync.rules.filefilter.FileFilter;

/**
 * @author Michele Aiello
 */
public class FilterValue implements OperandValue {

	private FileFilter value;
		
	public FilterValue(FileFilter value) {
		this.value = value;
	}
	
	public void fromString(String value) {
		this.value = new FileFilter();
	}
	
	public void setValue(FileFilter value) {
		this.value = value;
	}
	
	public FileFilter getValue() {
		return value;
	}
	
	public String toString() {
		return value.toString();
	}
	
}
