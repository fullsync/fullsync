/*
 * Created on 16.10.2004
 */
package net.sourceforge.fullsync.schedule;

import java.util.Date;



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
		this.next = new Date().getTime() + interval;
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
