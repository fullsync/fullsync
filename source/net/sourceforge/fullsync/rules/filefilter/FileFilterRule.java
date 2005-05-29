/*
 * Created on May 28, 2005
 */
package net.sourceforge.fullsync.rules.filefilter;

import java.io.File;

/**
 * @author Michele Aiello
 */
public interface FileFilterRule {

	public boolean match(File file);
	
	public abstract String toString();

}
