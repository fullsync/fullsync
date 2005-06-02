/*
 * Created on May 28, 2005
 */
package net.sourceforge.fullsync.rules.filefilter;

import java.io.Serializable;

import net.sourceforge.fullsync.fs.File;

/**
 * @author Michele Aiello
 */
public interface FileFilterRule extends Serializable {

	public String getRuleType();
	
	public int getOperator();
	
	public String getOperatorName();
		
	public Object getValue();
	
	public boolean match(File file);
	
	public abstract String toString();

}
