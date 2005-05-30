/*
 * Created on May 29, 2005
 */
package net.sourceforge.fullsync.rules.filefilter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Michele Aiello
 */
public class FileModificationDateFileFilterRule implements FileFilterRule {

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
	
	public boolean match(File file) {
		long lastModified = file.lastModified();
		switch (op) {
			case OP_IS:
				return ((lastModified+999)/1000) == ((millis+999)/1000);
				
			case OP_ISNT:
				return ((lastModified+999)/1000) != ((millis+999)/1000);
				
			case OP_IS_BEFORE:
				return lastModified > millis;
				
			case OP_IS_AFTER:
				return lastModified < millis;
		}
		return false;
	}

	public int getOperator() {
		return op;
	}
	
	public long getModificationDate() {
		return millis;
	}

	public String toString() {
		StringBuffer buff = new StringBuffer(30);
		
		buff.append("file modification date ");
		buff.append(allOperators[op]);
		buff.append(' ');
		buff.append(dateFormat.format(new Date(millis)));
		
		return buff.toString();
	}

}
