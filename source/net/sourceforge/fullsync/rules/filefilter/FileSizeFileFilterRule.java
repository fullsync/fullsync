/*
 * Created on May 29, 2005
 */
package net.sourceforge.fullsync.rules.filefilter;

import java.io.File;
import java.util.regex.Pattern;

/**
 * @author Michele Aiello
 */
public class FileSizeFileFilterRule implements FileFilterRule {
	
	public static final int OP_IS = 0;
	public static final int OP_ISNT = 1;
	public static final int OP_IS_GREATER_THAN = 2;
	public static final int OP_IS_LESS_THAN = 3;

	private static final String[] allOperators = new String[] {
			"is",
			"isn't",
			"is greater than",
			"is less than"
	};

	private long size;
	private int op;
		
	private Pattern regexppattern;
	
	public static String[] getAllOperators() {
		return allOperators;
	}
	
	public FileSizeFileFilterRule(long size, int operator) {
		this.size = size;
		this.op = operator;
	}
	
	public boolean match(File file) {
		long filesize = file.length();
		switch (op) {
			case OP_IS:
				return filesize == size;
				
			case OP_ISNT:
				return filesize != size;
				
			case OP_IS_GREATER_THAN:
				return filesize > size;
				
			case OP_IS_LESS_THAN:
				return filesize < size;
		}
		return false;
	}

	public int getOperator() {
		return op;
	}
	
	public long getSize() {
		return size;
	}

	public String toString() {
		StringBuffer buff = new StringBuffer(30);
		
		buff.append("file size ");
		buff.append(allOperators[op]);
		buff.append(' ');
		buff.append(size);
		buff.append(" bytes");
		
		return buff.toString();
	}
}
