/*
 * Created on May 29, 2005
 */
package net.sourceforge.fullsync.rules.filefilter;

import net.sourceforge.fullsync.SystemDate;
import net.sourceforge.fullsync.fs.File;

/**
 * @author Michele Aiello
 */
public class FileAgeFileFilterRule implements FileFilterRule {

	static final String typeName = "File age";
	
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

	private long deltaMillis;
	private int op;
	
	public static String[] getAllOperators() {
		return allOperators;
	}
	
	public FileAgeFileFilterRule(long deltaMillis, int operator) {
		this.deltaMillis= deltaMillis;
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
	
	public Object getValue() {
		return new Long(deltaMillis);
	}

	public boolean match(File file) {
		long lastModified = file.getFileAttributes().getLastModified();
		long now = SystemDate.getInstance().currentTimeMillis();
		switch (op) {
			case OP_IS:
				return (Math.floor(now/1000.0) - Math.floor(lastModified/1000.0)) == (Math.floor(deltaMillis/1000.0));
				
			case OP_ISNT:
				return (Math.floor(now/1000.0) - Math.floor(lastModified/1000.0)) != (Math.floor(deltaMillis/1000.0));
				
			case OP_IS_GREATER_THAN:
				return (Math.floor(now/1000.0) - Math.floor(lastModified/1000.0)) > (Math.floor(deltaMillis/1000.0));
				
			case OP_IS_LESS_THAN:
				return (Math.floor(now/1000.0) - Math.floor(lastModified/1000.0)) < (Math.floor(deltaMillis/1000.0));
		}
		return false;
	}

	public String toString() {
		StringBuffer buff = new StringBuffer(30);
		
		buff.append("file age ");
		buff.append(allOperators[op]);
		buff.append(" '");
		buff.append(deltaMillis);
		buff.append('\'');
		return buff.toString();
	}

}
