/*
 * Created on 16.10.2004
 */
package net.sourceforge.fullsync.schedule;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface ScheduleTask extends Runnable
{
	public long getExecutionTime();
}
