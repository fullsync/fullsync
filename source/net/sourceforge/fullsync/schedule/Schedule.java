/*
 * Created on 16.10.2004
 */
package net.sourceforge.fullsync.schedule;

import java.io.Serializable;


/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface Schedule extends Serializable
{
	public long getNextOccurrence( long now );
	// TODO instead of update we should use set last occurrence
	public void update();
}
