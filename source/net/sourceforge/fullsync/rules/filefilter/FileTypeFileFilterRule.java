/*
 * Created on May 28, 2005
 */
package net.sourceforge.fullsync.rules.filefilter;

import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.rules.filefilter.values.OperandValue;
import net.sourceforge.fullsync.rules.filefilter.values.TypeValue;

/**
 * @author Michele Aiello
 */
public class FileTypeFileFilterRule extends FileFilterRule {
	
	public static String typeName = "File type";
	
	public static final int OP_IS = 0;
	public static final int OP_ISNT = 1;

	private static final String[] allOperators = new String[] {
			"is",
			"isn't"
	};
		
	private TypeValue type;
	private int op;
		
	public String getRuleType() {
		return typeName;
	}

	public static String[] getAllOperators() {
		return allOperators;
	}
	
	public static String[] getAllOperands() {
		return TypeValue.getAllTypes();
	}
		
	public FileTypeFileFilterRule(TypeValue type, int operator) {
		this.type = type;
		this.op = operator;		
	}

	public int getOperator() {
		return op;
	}

	public String getOperatorName() {
		return allOperators[op];
	}
	
	public OperandValue getValue() {
		return type;
	}

	public boolean match(File file) {				
		switch(op) {
		case OP_IS:
			return (((type.isFile()) && file.isFile()) ||
					((type.isDirectory()) && file.isDirectory()));
			
		case OP_ISNT:
			return !(((type.isFile()) && file.isFile()) ||
					((type.isDirectory()) && file.isDirectory()));
						
		default:
			return false;
		}
	}
		
	public String toString() {
		StringBuffer buff = new StringBuffer(30);
		
		buff.append("file type ");
		buff.append(allOperators[op]);
		buff.append(" '");
		buff.append(type.toString());
		buff.append('\'');
		
		return buff.toString();
	}
}
