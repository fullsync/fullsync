/*
 * Created on 16.10.2004
 */
package net.sourceforge.fullsync.schedule;



/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class IntervalSchedule implements Schedule 
{
    long firstInterval;
    long interval;
	long next;
		
	public IntervalSchedule( long firstInterval, long interval )
	{
	    this.firstInterval = firstInterval;
	    this.interval = interval;
	    
		this.next = System.currentTimeMillis() + firstInterval;
	}
		
	public long getNextOccurrence( long now )
	{
		return next>now?next:now;
	}
	
	public void update()
	{
	    // TODO umpf !?... this might result into many execs at once
		this.next = next + interval;
	}
	
	public long getFirstInterval()
	{
	    return firstInterval;
	}
	
	public long getInterval()
	{
	    return interval;
	}
}
