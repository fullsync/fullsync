/*
 * Created on May 28, 2005
 */
package net.sourceforge.fullsync.rules.filefilter;

import java.io.Serializable;

import net.sourceforge.fullsync.fs.File;
import net.sourceforge.fullsync.rules.filefilter.values.OperandValue;

/**
 * @author Michele Aiello
 */
public abstract class FileFilterRule implements Serializable {
		
	public abstract int getOperator();
	
	public abstract String getOperatorName();
		
	public abstract OperandValue getValue();
	
	public abstract boolean match(File file) throws FilterRuleNotAppliableException;
	
	public abstract String toString();

	public abstract String getRuleType();

}
