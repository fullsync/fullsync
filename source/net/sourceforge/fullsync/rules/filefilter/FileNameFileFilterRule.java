/*
 * Created on May 28, 2005
 */
package net.sourceforge.fullsync.rules.filefilter;

import java.io.File;
import java.util.regex.Pattern;

/**
 * @author Michele Aiello
 */
public class FileNameFileFilterRule implements FileFilterRule {
	
	public static final int OP_IS = 0;
	public static final int OP_ISNT = 1;
	public static final int OP_CONTAINS = 2;
	public static final int OP_DOESNT_CONTAINS = 3;
	public static final int OP_BEGINS_WITH = 4;
	public static final int OP_DOESNT_BEGINS_WITH = 5;
	public static final int OP_ENDS_WITH = 6;
	public static final int OP_DOESNT_ENDS_WITH = 7;
	public static final int OP_REGEXP = 8;

	private static final String[] allOperators = new String[] {
			"is",
			"isn't",
			"contains",
			"doesn't contains",
			"begins with",
			"doesn't begins with",
			"ends with",
			"doesnt' ends with",
			"matches regexp"
	};
	
	private String pattern;
	private int op;
	
	private Pattern regexppattern;
	
	public static String[] getAllOperators() {
		return allOperators;
	}
	
	public FileNameFileFilterRule(String pattern, int operator) {
		this.pattern = pattern;
		this.op = operator;
		
		if (operator == OP_REGEXP) {
			this.regexppattern = Pattern.compile(this.pattern);
		}
	}
	
	public boolean match(File file) {
		String name = file.getName();
		
		switch(op) {
		case OP_IS:
			return name.equals(pattern);
			
		case OP_ISNT:
			return !name.equals(pattern);
			
		case OP_CONTAINS:
			return (name.indexOf(pattern) >= 0);
			
		case OP_DOESNT_CONTAINS:
			return (name.indexOf(pattern) < 0);
			
		case OP_BEGINS_WITH:
			return name.startsWith(pattern);
			
		case OP_DOESNT_BEGINS_WITH:
			return !name.startsWith(pattern);

		case OP_ENDS_WITH:
			return name.endsWith(pattern);
			
		case OP_DOESNT_ENDS_WITH:
			return !name.endsWith(pattern);

		case OP_REGEXP:
			return regexppattern.matcher(name).matches();
			
		default:
			return false;
		}
	}

	public int getOperator() {
		return op;
	}
	
	public String getPattern() {
		return pattern;
	}
	
	public String toString() {
		StringBuffer buff = new StringBuffer(30);
		
		buff.append("file name ");
		buff.append(allOperators[op]);
		buff.append(" "+pattern);
		
		return buff.toString();
	}
}
