/*
 * Created on Dec 1, 2004
 */
package net.sourceforge.fullsync.schedule;

/**
 * @author Michele Aiello
 * 
 * Interface for listener interested in changes in the timer status.
 */
public interface SchedulerChangeListener 
{
	void schedulerStatusChanged(boolean status);
}
