/*
 * Created on Dec 1, 2004
 */
package net.sourceforge.fullsync;

/**
 * @author Michele Aiello
 * 
 * Interface for listener interested in changes in the timer status.
 */
public interface TimerUpdateListener {

	void timerStatusChanged(boolean status);
	
}
