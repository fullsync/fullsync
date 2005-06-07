/*
 * Created on Jun 6, 2005
 */
package net.sourceforge.fullsync.rules.filefilter.values;

/**
 * @author Michele Aiello
 */
public interface OperandValue {

	public void fromString(String value);
	
	public String toString();
	
}
