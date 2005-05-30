/*
 * Created on May 28, 2005
 */
package net.sourceforge.fullsync.rules.filefilter;

import net.sourceforge.fullsync.fs.File;

/**
 * @author Michele Aiello
 */
public interface FileFilterRule {

	public String getRuleType();
	
	public int getOperator();
	
	public String getOperatorName();
	
	public Object getValue();
	
	public boolean match(File file);
	
	public abstract String toString();

}
