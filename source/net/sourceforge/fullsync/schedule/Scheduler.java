/*
 * Created on 16.10.2004
 */
package net.sourceforge.fullsync.schedule;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface Scheduler 
{
	public void start();
	public void refresh();
	public void stop();
	
	public boolean isRunning();
	public boolean isEnabled();

	public void setSource( ScheduleTaskSource source );
	public ScheduleTaskSource getSource();
	
	public void addSchedulerChangeListener( SchedulerChangeListener listener );
	public void removeSchedulerChangeListener( SchedulerChangeListener listener );
}
