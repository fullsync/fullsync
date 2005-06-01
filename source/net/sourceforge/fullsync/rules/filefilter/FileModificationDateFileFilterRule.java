/*
 * Created on May 29, 2005
 */
package net.sourceforge.fullsync.rules.filefilter;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.sourceforge.fullsync.fs.File;

/**
 * @author Michele Aiello
 */
public class FileModificationDateFileFilterRule implements FileFilterRule {

	private static final String ruleType = "File modification date";
	
	public static final int OP_IS = 0;
	public static final int OP_ISNT = 1;
	public static final int OP_IS_BEFORE = 2;
	public static final int OP_IS_AFTER = 3;
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
	
	private static final String[] allOperators = new String[] {
			"is",
			"isn't",
			"is before",
			"is after"
	};

	private long millis;
	private int op;
	
	public static String[] getAllOperators() {
		return allOperators;
	}
	
	public FileModificationDateFileFilterRule(long millis, int operator) {
		this.millis = millis;
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
		return new Long(millis);
	}

	public boolean match(File file) {
		long lastModified = file.getFileAttributes().getLastModified();
		switch (op) {
			case OP_IS:
				return (Math.floor(lastModified/1000.0)) == (Math.floor(millis/1000.0));
				
			case OP_ISNT:
				return (Math.floor(lastModified/1000.0)) != (Math.floor(millis/1000.0));
				
			case OP_IS_BEFORE:
				return (Math.floor(lastModified/1000.0)) > (Math.floor(millis/1000.0));
				
			case OP_IS_AFTER:
				return (Math.floor(lastModified/1000.0)) < (Math.floor(millis/1000.0));
		}
		return false;
	}

	public String toString() {
		StringBuffer buff = new StringBuffer(30);
		
		buff.append("file modification date ");
		buff.append(allOperators[op]);
		buff.append(" '");
		buff.append(dateFormat.format(new Date(millis)));
		buff.append('\'');
		return buff.toString();
	}

}
