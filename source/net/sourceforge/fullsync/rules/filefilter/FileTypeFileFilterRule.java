/*
 * Created on May 28, 2005
 */
package net.sourceforge.fullsync.rules.filefilter;

import net.sourceforge.fullsync.fs.File;

/**
 * @author Michele Aiello
 */
public class FileTypeFileFilterRule implements FileFilterRule {
	
	private static final String ruleType = "File type";
	
	public static final int OP_IS = 0;
	public static final int OP_ISNT = 1;

	public static final int FILE_TYPE = 0;
	public static final int DIRECTORY_TYPE = 1;

	private static final String[] allOperators = new String[] {
			"is",
			"isn't"
	};
	
	private static final String[] allOperands = new String[] {
		"file",
		"directory"
	};
	
	private int type;
	private int op;
		
	public static String[] getAllOperators() {
		return allOperators;
	}
	
	public static String[] getAllOperands() {
		return allOperands;
	}
		
	public FileTypeFileFilterRule(String typeName, int operator) {
		this.type = -1;
		for (int i = 0; i < allOperands.length; i++) {
			if (allOperands[i].equals(typeName)) {
				this.type = i;
				break;
			}
		}
		this.op = operator;		
	}

	public FileTypeFileFilterRule(int type, int operator) {
		this.type = type;
		this.op = operator;		
	}

	public String getRuleType() {
		return ruleType;
	}

	public int getOperator() {
		return op;
	}

	public String getOperatorName() {
		return allOperators[op];
	}
	
	public Object getValue() {
		return allOperands[type];
	}

	public boolean match(File file) {				
		switch(op) {
		case OP_IS:
			return (((type == 0) && file.isFile()) ||
					((type == 1) && file.isDirectory()));
			
		case OP_ISNT:
			return !(((type == 0) && file.isFile()) ||
					((type == 1) && file.isDirectory()));
						
		default:
			return false;
		}
	}
		
	public String toString() {
		StringBuffer buff = new StringBuffer(30);
		
		buff.append("file type ");
		buff.append(allOperators[op]);
		buff.append(" '");
		buff.append(allOperands[type]);
		buff.append('\'');
		
		return buff.toString();
	}
}
