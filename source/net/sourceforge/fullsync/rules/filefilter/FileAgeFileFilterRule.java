/*
 * Created on May 29, 2005
 */
package net.sourceforge.fullsync.rules.filefilter;

import net.sourceforge.fullsync.SystemDate;
import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.fs.FileAttributes;
import net.sourceforge.fullsync.rules.filefilter.values.AgeValue;
import net.sourceforge.fullsync.rules.filefilter.values.OperandValue;

/**
 * @author Michele Aiello
 */
public class FileAgeFileFilterRule implements FileFilterRule {

	public static final String typeName = "File age";
	
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

	private AgeValue age;
	private int op;
	
	public static String[] getAllOperators() {
		return allOperators;
	}
	
	public FileAgeFileFilterRule(AgeValue age, int operator) {
		this.age = age;
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
		return age;
	}

	public boolean match(File file) throws FilterRuleNotAppliableException {
		FileAttributes attrs = file.getFileAttributes();
		if (attrs == null) {
			throw new FilterRuleNotAppliableException("The file doesn't have any size attribute");
		}

		long lastModified = attrs.getLastModified();
		long now = SystemDate.getInstance().currentTimeMillis();
		double delta = (Math.floor(now/1000.0) - Math.floor(lastModified/1000.0));
		switch (op) {
			case OP_IS:
				return delta == age.getSeconds();
				
			case OP_ISNT:
				return delta != age.getSeconds();
				
			case OP_IS_GREATER_THAN:
				return delta > age.getSeconds();
				
			case OP_IS_LESS_THAN:
				return delta < age.getSeconds();
		}
		return false;
	}

	public String toString() {
		StringBuffer buff = new StringBuffer(30);
		
		buff.append("file age ");
		buff.append(allOperators[op]);
		buff.append(" '");
		buff.append(age);
		buff.append("'");
		return buff.toString();
	}

}
