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
public interface FileFilterRule extends Serializable {

	public String getRuleType();
	
	public int getOperator();
	
	public String getOperatorName();
		
	public OperandValue getValue();
	
	public boolean match(File file);
	
	public abstract String toString();

}
