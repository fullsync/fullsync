/*
 * Created on May 29, 2005
 */
package net.sourceforge.fullsync.rules.filefilter;

import java.io.File;

/**
 * @author Michele Aiello
 */
public class FileSizeFileFilterRule implements FileFilterRule {
	
	public static final int OP_IS = 0;
	public static final int OP_ISNT = 1;
	public static final int OP_IS_GREATER_THAN = 2;
	public static final int OP_IS_LESS_THAN = 3;

	private long value;
	private int op;
	
	public FileSizeFileFilterRule(int value, int operator) {
		this.value = value;
		this.op = operator;
	}
	
	public boolean match(File file) {
		long size = file.length();
		switch (op) {
			case OP_IS:
				return size == value;
				
			case OP_ISNT:
				return size != value;
				
			case OP_IS_GREATER_THAN:
				return size > value;
				
			case OP_IS_LESS_THAN:
				return size < value;
		}
		return false;
	}

}
