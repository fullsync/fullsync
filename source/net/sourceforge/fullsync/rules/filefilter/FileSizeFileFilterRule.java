/*
 * Created on May 29, 2005
 */
package net.sourceforge.fullsync.rules.filefilter;

import java.util.regex.Pattern;

import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.rules.filefilter.values.SizeValue;
import net.sourceforge.fullsync.rules.filefilter.values.OperandValue;

/**
 * @author Michele Aiello
 */
public class FileSizeFileFilterRule implements FileFilterRule {
	
	public static final String typeName = "File size";
	
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

	
	private SizeValue size;
	private int op;
		
	private Pattern regexppattern;
	
	public static String[] getAllOperators() {
		return allOperators;
	}
	
	public static String[] getAllUnits() {
		return SizeValue.getAllUnits();
	}
	
	public FileSizeFileFilterRule(SizeValue size, int operator) {
		this.size = size;
		this.op = operator;
	}
	
	public String getRuleType() {
		return typeName;
	}

	public int getOperator() {
		return op;
	}

	public String getOperatorName() {
		return allOperators[op];
	}
	
	public OperandValue getValue() {
		return size;
	}

	public boolean match(File file) {
		long filesize = file.getFileAttributes().getLength();
		switch (op) {
			case OP_IS:
				return filesize == size.getBytes();
				
			case OP_ISNT:
				return filesize != size.getBytes();
				
			case OP_IS_GREATER_THAN:
				return filesize > size.getBytes();
				
			case OP_IS_LESS_THAN:
				return filesize < size.getBytes();
		}
		return false;
	}

	public String toString() {
		StringBuffer buff = new StringBuffer(30);
		
		buff.append("file size ");
		buff.append(allOperators[op]);
		buff.append(" '");
		buff.append(size.toString());
		buff.append("'");
		
		return buff.toString();
	}
}
