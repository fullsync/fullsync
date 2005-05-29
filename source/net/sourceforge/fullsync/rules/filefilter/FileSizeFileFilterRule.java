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

	private long size;
	private int op;
	
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
		switch(op) {
		case OP_IS:
			buff.append("is");
			break;
			
		case OP_ISNT:
			buff.append("isn't");
			break;
			
		case OP_IS_GREATER_THAN:
			buff.append("is greater than");
			break;
			
		case OP_IS_LESS_THAN:
			buff.append("is less than");
			break;
			
		default:
			buff.append("?");
		break;
		}

		buff.append(' ');
		buff.append(size);
		buff.append(" bytes");
		
		return buff.toString();
	}
}
