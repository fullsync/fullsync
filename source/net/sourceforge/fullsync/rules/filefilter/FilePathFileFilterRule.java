/*
 * Created on May 28, 2005
 */
package net.sourceforge.fullsync.rules.filefilter;

import java.util.regex.Pattern;

import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.rules.filefilter.values.OperandValue;
import net.sourceforge.fullsync.rules.filefilter.values.TextValue;

/**
 * @author Michele Aiello
 */
public class FilePathFileFilterRule implements FileFilterRule {
	
	public static final String typeName = "File path";
	
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
			"doesn't ends with",
			"matches regexp"
	};
	
	private TextValue pattern;
	private int op;
	
	private Pattern regexppattern;
	
	public static String[] getAllOperators() {
		return allOperators;
	}
		
	public FilePathFileFilterRule(TextValue pattern, int operator) {
		this.pattern = pattern;
		this.op = operator;
		
		if (operator == OP_REGEXP) {
			this.regexppattern = Pattern.compile(this.pattern.getValue());
		}
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
		return pattern;
	}

	public boolean match(File file) {
		String name = file.getPath();
		
		switch(op) {
		case OP_IS:
			return name.equals(pattern);
			
		case OP_ISNT:
			return !name.equals(pattern);
			
		case OP_CONTAINS:
			return (name.indexOf(pattern.getValue()) >= 0);
			
		case OP_DOESNT_CONTAINS:
			return (name.indexOf(pattern.getValue()) < 0);
			
		case OP_BEGINS_WITH:
			return name.startsWith(pattern.getValue());
			
		case OP_DOESNT_BEGINS_WITH:
			return !name.startsWith(pattern.getValue());

		case OP_ENDS_WITH:
			return name.endsWith(pattern.getValue());
			
		case OP_DOESNT_ENDS_WITH:
			return !name.endsWith(pattern.getValue());

		case OP_REGEXP:
			return regexppattern.matcher(name).matches();
			
		default:
			return false;
		}
	}
		
	public String toString() {
		StringBuffer buff = new StringBuffer(30);
		
		buff.append("file path ");
		buff.append(allOperators[op]);
		buff.append(" '");
		buff.append(pattern.toString());
		buff.append('\'');
		
		return buff.toString();
	}
}
